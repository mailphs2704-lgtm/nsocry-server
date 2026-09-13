package com.nsocry.persistence;

import com.nsocry.assets.DataAssetSeedValidationResult;
import com.nsocry.operations.ValidatedDataAssetSeedArchive;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import javax.sql.DataSource;

/** Ghi một DATA archive đã kiểm định trong transaction SERIALIZABLE và khóa overwrite rõ ràng. */
public final class JdbcDataAssetSeedImporter {
    private static final String LOCK_VERSION =
            "SELECT version FROM client_data_assets WHERE version = ? FOR UPDATE";
    private static final String INSERT = """
            INSERT INTO client_data_assets
                (version, task_group_count, experience_count, payload_length,
                 payload_sha256, payload, manifest_text)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
    private static final String UPDATE = """
            UPDATE client_data_assets
            SET task_group_count = ?, experience_count = ?, payload_length = ?,
                payload_sha256 = ?, payload = ?, manifest_text = ?
            WHERE version = ?
            """;

    private final DataSource dataSource;

    /** Tạo importer nhưng chưa mở connection. */
    public JdbcDataAssetSeedImporter(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource, "dataSource");
    }

    /**
     * Khóa row cùng version rồi insert hoặc update theo policy; mọi lỗi đều rollback.
     * Archive bắt buộc đã được DataAssetSeedArchiveService kiểm định trước.
     */
    public DataAssetSeedImportResult importSeed(
            ValidatedDataAssetSeedArchive archive,
            DataAssetOverwriteMode overwriteMode) throws DataAssetSeedImportException {
        Objects.requireNonNull(archive, "archive");
        Objects.requireNonNull(overwriteMode, "overwriteMode");
        DataAssetSeedValidationResult validation = archive.validation();
        byte[] payload = archive.payload();
        String manifestText = archive.manifestText();

        try (Connection connection = dataSource.getConnection()) {
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            connection.setAutoCommit(false);
            try {
                boolean exists = versionExistsForUpdate(connection, validation);
                if (exists && overwriteMode == DataAssetOverwriteMode.REJECT_EXISTING) {
                    throw new DataAssetSeedImportException(
                            "DATA version đã tồn tại; cần xác nhận REPLACE_SAME_VERSION");
                }
                int changed = exists
                        ? update(connection, validation, payload, manifestText)
                        : insert(connection, validation, payload, manifestText);
                if (changed != 1) {
                    throw new SQLException("DATA import phải thay đổi đúng một row, thực tế=" + changed);
                }
                connection.commit();
                return new DataAssetSeedImportResult(validation, exists);
            } catch (SQLException | RuntimeException | DataAssetSeedImportException exception) {
                rollback(connection, exception);
                throw exception;
            }
        } catch (DataAssetSeedImportException exception) {
            throw exception;
        } catch (SQLException | RuntimeException exception) {
            throw new DataAssetSeedImportException("Không thể ghi DATA seed", exception);
        }
    }

    private static boolean versionExistsForUpdate(
            Connection connection,
            DataAssetSeedValidationResult validation) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(LOCK_VERSION)) {
            statement.setInt(1, Byte.toUnsignedInt(validation.version()));
            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    private static int insert(
            Connection connection,
            DataAssetSeedValidationResult validation,
            byte[] payload,
            String manifestText) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT)) {
            statement.setInt(1, Byte.toUnsignedInt(validation.version()));
            statement.setInt(2, validation.taskGroupCount());
            statement.setInt(3, validation.experienceCount());
            statement.setInt(4, validation.payloadLength());
            statement.setString(5, validation.payloadSha256());
            statement.setBytes(6, payload);
            statement.setString(7, manifestText);
            return statement.executeUpdate();
        }
    }

    private static int update(
            Connection connection,
            DataAssetSeedValidationResult validation,
            byte[] payload,
            String manifestText) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE)) {
            statement.setInt(1, validation.taskGroupCount());
            statement.setInt(2, validation.experienceCount());
            statement.setInt(3, validation.payloadLength());
            statement.setString(4, validation.payloadSha256());
            statement.setBytes(5, payload);
            statement.setString(6, manifestText);
            statement.setInt(7, Byte.toUnsignedInt(validation.version()));
            return statement.executeUpdate();
        }
    }

    private static void rollback(Connection connection, Exception original) {
        try {
            connection.rollback();
        } catch (SQLException rollbackFailure) {
            original.addSuppressed(rollbackFailure);
        }
    }
}
