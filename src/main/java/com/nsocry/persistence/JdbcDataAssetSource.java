package com.nsocry.persistence;

import com.nsocry.assets.ClientAssetSourceException;
import com.nsocry.assets.DataAssetBundle;
import com.nsocry.assets.DataAssetCodec;
import com.nsocry.assets.DataAssetSeedManifest;
import com.nsocry.assets.DataAssetSeedManifestParser;
import com.nsocry.assets.DataAssetSeedValidationResult;
import com.nsocry.assets.DataAssetSeedValidator;
import com.nsocry.assets.DataAssetSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import javax.sql.DataSource;

/** Đọc row DATA version trong repeatable-read transaction và tự kiểm định metadata/checksum. */
public final class JdbcDataAssetSource implements DataAssetSource {
    private static final String SELECT = """
            SELECT version, task_group_count, experience_count, payload_length,
                   payload_sha256, payload, manifest_text
            FROM client_data_assets WHERE version = ?
            """;
    private final DataSource dataSource;
    private final byte version;

    /** Tạo source cho đúng raw version; chưa mở connection. */
    public JdbcDataAssetSource(DataSource dataSource, byte version) {
        this.dataSource = Objects.requireNonNull(dataSource, "dataSource");
        this.version = version;
    }

    /** Đọc đúng một row, validate trước khi commit snapshot chỉ đọc. */
    @Override
    public DataAssetBundle load() throws ClientAssetSourceException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setReadOnly(true);
            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(SELECT)) {
                statement.setInt(1, Byte.toUnsignedInt(version));
                try (ResultSet result = statement.executeQuery()) {
                    if (!result.next()) {
                        throw new SQLException("Không tìm thấy DATA version");
                    }
                    byte[] payload = Objects.requireNonNull(result.getBytes("payload"), "payload");
                    String manifestText = Objects.requireNonNull(
                            result.getString("manifest_text"), "manifest_text");
                    DataAssetSeedManifest manifest = DataAssetSeedManifestParser.parse(manifestText);
                    DataAssetBundle bundle = DataAssetCodec.decode(payload);
                    DataAssetSeedValidationResult validation =
                            DataAssetSeedValidator.validate(bundle, manifest);
                    require(result.getInt("version") == Byte.toUnsignedInt(validation.version()), "version");
                    require(result.getInt("task_group_count") == validation.taskGroupCount(), "task_group_count");
                    require(result.getInt("experience_count") == validation.experienceCount(), "experience_count");
                    require(result.getInt("payload_length") == validation.payloadLength(), "payload_length");
                    require(validation.payloadSha256().equals(result.getString("payload_sha256")), "payload_sha256");
                    connection.commit();
                    return bundle;
                }
            } catch (Exception exception) {
                rollback(connection, exception);
                throw exception;
            }
        } catch (Exception exception) {
            throw new ClientAssetSourceException("Không thể đọc DATA asset", exception);
        }
    }

    private static void require(boolean condition, String field) throws SQLException {
        if (!condition) throw new SQLException("DATA row không khớp " + field);
    }

    private static void rollback(Connection connection, Exception original) {
        try { connection.rollback(); } catch (SQLException failure) { original.addSuppressed(failure); }
    }
}
