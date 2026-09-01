package com.nsocry.persistence;

import com.nsocry.assets.ItemAssetBundle;
import com.nsocry.assets.ItemAssetCodec;
import com.nsocry.assets.ItemAssetSeedManifest;
import com.nsocry.assets.ItemAssetSeedManifestParser;
import com.nsocry.assets.ItemAssetSeedValidator;
import com.nsocry.assets.ItemAssetValidationResult;
import com.nsocry.assets.ItemOptionAsset;
import com.nsocry.assets.ItemTemplateAsset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import javax.sql.DataSource;

/** Import seed ITEM đã kiểm định bằng một transaction và prepared batch. */
public final class JdbcItemAssetSeedImporter {
    private static final String DELETE_ITEMS = "DELETE FROM client_item_templates";
    private static final String DELETE_OPTIONS = "DELETE FROM client_item_options";
    private static final String INSERT_OPTION = """
            INSERT INTO client_item_options (id, name, type)
            VALUES (?, ?, ?)
            """;
    private static final String INSERT_ITEM = """
            INSERT INTO client_item_templates
                (id, type, gender, name, description, required_level, icon_id, part_id, upgradable)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

    private final DataSource dataSource;

    /** Tạo importer với DataSource của database NSOCry. */
    public JdbcItemAssetSeedImporter(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource, "dataSource");
    }

    /**
     * Kiểm định artifact trước khi mở connection, sau đó thay hai bảng trong một transaction.
     *
     * @return metadata của artifact đã commit
     */
    public ItemAssetValidationResult importSeed(byte[] payload, String manifestText)
            throws ItemAssetSeedImportException {
        Objects.requireNonNull(payload, "payload");
        Objects.requireNonNull(manifestText, "manifestText");
        final ItemAssetBundle bundle;
        final ItemAssetValidationResult validation;
        try {
            ItemAssetSeedManifest manifest = ItemAssetSeedManifestParser.parse(manifestText);
            bundle = ItemAssetCodec.decode(payload.clone());
            validation = ItemAssetSeedValidator.validate(bundle, manifest);
            if (payload.length != validation.payloadLength()) {
                throw new IllegalArgumentException("Độ dài payload không khớp manifest");
            }
        } catch (Exception exception) {
            throw new ItemAssetSeedImportException("ITEM artifact không hợp lệ", exception);
        }

        try (Connection connection = dataSource.getConnection()) {
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            connection.setAutoCommit(false);
            try {
                replaceRows(connection, bundle);
                connection.commit();
                return validation;
            } catch (SQLException | RuntimeException exception) {
                rollback(connection, exception);
                throw exception;
            }
        } catch (SQLException | RuntimeException exception) {
            throw new ItemAssetSeedImportException("Không thể ghi ITEM seed", exception);
        }
    }

    /** Xóa dữ liệu cũ và batch insert toàn bộ read model trên cùng transaction. */
    private static void replaceRows(Connection connection, ItemAssetBundle bundle) throws SQLException {
        try (PreparedStatement deleteItems = connection.prepareStatement(DELETE_ITEMS);
                PreparedStatement deleteOptions = connection.prepareStatement(DELETE_OPTIONS)) {
            deleteItems.executeUpdate();
            deleteOptions.executeUpdate();
        }
        insertOptions(connection, bundle);
        insertItems(connection, bundle);
    }

    /** Batch insert option bằng ID chính là vị trí trên wire. */
    private static void insertOptions(Connection connection, ItemAssetBundle bundle) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_OPTION)) {
            for (int id = 0; id < bundle.options().size(); id++) {
                ItemOptionAsset option = bundle.options().get(id);
                statement.setInt(1, id);
                statement.setString(2, option.name());
                statement.setByte(3, option.type());
                statement.addBatch();
            }
            requireSuccessfulBatch(statement.executeBatch(), bundle.options().size(), "item options");
        }
    }

    /** Batch insert item template bằng ID chính là vị trí trên wire. */
    private static void insertItems(Connection connection, ItemAssetBundle bundle) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_ITEM)) {
            for (int id = 0; id < bundle.items().size(); id++) {
                ItemTemplateAsset item = bundle.items().get(id);
                statement.setInt(1, id);
                statement.setByte(2, item.type());
                statement.setByte(3, item.gender());
                statement.setString(4, item.name());
                statement.setString(5, item.description());
                statement.setByte(6, item.level());
                statement.setShort(7, item.icon());
                statement.setShort(8, item.part());
                statement.setBoolean(9, item.upgradable());
                statement.addBatch();
            }
            requireSuccessfulBatch(statement.executeBatch(), bundle.items().size(), "item templates");
        }
    }

    /** Yêu cầu driver báo đủ số batch và không có statement thất bại. */
    private static void requireSuccessfulBatch(int[] results, int expected, String name) throws SQLException {
        if (results.length != expected) {
            throw new SQLException(name + " batch count không khớp");
        }
        for (int result : results) {
            if (result == Statement.EXECUTE_FAILED) {
                throw new SQLException(name + " batch có row thất bại");
            }
        }
    }

    /** Rollback best-effort và giữ lỗi rollback dưới dạng suppressed exception. */
    private static void rollback(Connection connection, Exception original) {
        try {
            connection.rollback();
        } catch (SQLException rollbackFailure) {
            original.addSuppressed(rollbackFailure);
        }
    }
}
