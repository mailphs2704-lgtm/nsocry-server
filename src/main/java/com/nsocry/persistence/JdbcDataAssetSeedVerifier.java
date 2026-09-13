package com.nsocry.persistence;

import com.nsocry.assets.DataAssetBundle;
import com.nsocry.assets.DataAssetCodec;
import com.nsocry.assets.DataAssetSeedManifest;
import com.nsocry.assets.DataAssetSeedManifestParser;
import com.nsocry.assets.DataAssetSeedValidationResult;
import com.nsocry.assets.DataAssetSeedValidator;
import com.nsocry.operations.ValidatedDataAssetSeedArchive;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;
import javax.sql.DataSource;

/** Đọc lại row DATA và đối chiếu metadata, payload, manifest cùng checksum canonical. */
public final class JdbcDataAssetSeedVerifier {
    private static final String SELECT = """
            SELECT version, task_group_count, experience_count, payload_length,
                   payload_sha256, payload, manifest_text
            FROM client_data_assets
            WHERE version = ?
            """;
    private final DataSource dataSource;

    /** Tạo verifier read-only cho database NSOCry. */
    public JdbcDataAssetSeedVerifier(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource, "dataSource");
    }

    /** Fail nếu thiếu row hoặc bất kỳ byte/metadata/checksum nào khác archive đã duyệt. */
    public DataAssetSeedValidationResult verify(ValidatedDataAssetSeedArchive archive)
            throws DataAssetSeedImportException {
        Objects.requireNonNull(archive, "archive");
        DataAssetSeedValidationResult expected = archive.validation();
        try (Connection connection = dataSource.getConnection()) {
            connection.setReadOnly(true);
            try (PreparedStatement statement = connection.prepareStatement(SELECT)) {
                statement.setInt(1, Byte.toUnsignedInt(expected.version()));
                try (ResultSet result = statement.executeQuery()) {
                    if (!result.next()) {
                        throw new DataAssetSeedImportException("Không tìm thấy DATA version trong database");
                    }
                    byte[] payload = result.getBytes("payload");
                    String manifestText = result.getString("manifest_text");
                    require(result.getInt("version") == Byte.toUnsignedInt(expected.version()), "version");
                    require(result.getInt("task_group_count") == expected.taskGroupCount(), "task_group_count");
                    require(result.getInt("experience_count") == expected.experienceCount(), "experience_count");
                    require(result.getInt("payload_length") == expected.payloadLength(), "payload_length");
                    require(expected.payloadSha256().equals(result.getString("payload_sha256")), "payload_sha256");
                    require(Arrays.equals(archive.payload(), payload), "payload");
                    require(archive.manifestText().equals(manifestText), "manifest_text");
                    DataAssetSeedManifest manifest = DataAssetSeedManifestParser.parse(manifestText);
                    DataAssetBundle bundle = DataAssetCodec.decode(payload);
                    return DataAssetSeedValidator.validate(bundle, manifest);
                }
            }
        } catch (DataAssetSeedImportException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new DataAssetSeedImportException("Không thể verify DATA seed trong database", exception);
        }
    }

    private static void require(boolean condition, String field) throws DataAssetSeedImportException {
        if (!condition) {
            throw new DataAssetSeedImportException("DATA database không khớp " + field);
        }
    }
}
