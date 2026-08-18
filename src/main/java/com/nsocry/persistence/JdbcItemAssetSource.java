package com.nsocry.persistence;

import com.nsocry.assets.ClientAssetSourceException;
import com.nsocry.assets.ItemAssetBundle;
import com.nsocry.assets.ItemAssetSource;
import com.nsocry.assets.ItemOptionAsset;
import com.nsocry.assets.ItemTemplateAsset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.sql.DataSource;

/** Adapter JDBC đọc read model ITEM tĩnh của NSOCry trong một transaction nhất quán. */
public final class JdbcItemAssetSource implements ItemAssetSource {
    private static final String READ_OPTIONS = """
            SELECT id, name, type
            FROM client_item_options
            ORDER BY id
            """;
    private static final String READ_ITEMS = """
            SELECT id, type, gender, name, description, required_level, icon_id, part_id, upgradable
            FROM client_item_templates
            ORDER BY id
            """;

    private final DataSource dataSource;
    private final byte version;

    /** Tạo source với DataSource ứng dụng và version sẽ ghi vào payload ITEM. */
    public JdbcItemAssetSource(DataSource dataSource, byte version) {
        this.dataSource = Objects.requireNonNull(dataSource, "dataSource");
        this.version = version;
    }

    /** Đọc option và template trên cùng một repeatable-read transaction. */
    @Override
    public ItemAssetBundle load() throws ClientAssetSourceException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setReadOnly(true);
            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            connection.setAutoCommit(false);
            try {
                List<ItemOptionAsset> options = readOptions(connection);
                List<ItemTemplateAsset> items = readItems(connection);
                connection.commit();
                return new ItemAssetBundle(version, options, items);
            } catch (SQLException | RuntimeException exception) {
                rollback(connection, exception);
                throw exception;
            }
        } catch (SQLException | RuntimeException exception) {
            throw new ClientAssetSourceException("Không thể đọc ITEM asset", exception);
        }
    }

    /** Đọc option theo ID liên tục vì ID không được ghi riêng trên wire. */
    private static List<ItemOptionAsset> readOptions(Connection connection) throws SQLException {
        List<ItemOptionAsset> options = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(READ_OPTIONS);
                ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                requireSequentialId(result.getInt("id"), options.size(), "client_item_options");
                options.add(new ItemOptionAsset(
                        Objects.requireNonNull(result.getString("name"), "option name"),
                        checkedByte(result.getInt("type"), "option type")));
            }
        }
        return options;
    }

    /** Đọc template theo ID liên tục và kiểm tra mọi số trước khi thu hẹp về kiểu wire. */
    private static List<ItemTemplateAsset> readItems(Connection connection) throws SQLException {
        List<ItemTemplateAsset> items = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(READ_ITEMS);
                ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                requireSequentialId(result.getInt("id"), items.size(), "client_item_templates");
                items.add(new ItemTemplateAsset(
                        checkedByte(result.getInt("type"), "item type"),
                        checkedByte(result.getInt("gender"), "item gender"),
                        Objects.requireNonNull(result.getString("name"), "item name"),
                        Objects.requireNonNull(result.getString("description"), "item description"),
                        checkedByte(result.getInt("required_level"), "required level"),
                        checkedShort(result.getInt("icon_id"), "icon id"),
                        checkedShort(result.getInt("part_id"), "part id"),
                        result.getBoolean("upgradable")));
            }
        }
        return items;
    }

    /** Bảo đảm vị trí danh sách luôn chính là ID mà client sử dụng. */
    private static void requireSequentialId(int actual, int expected, String table) throws SQLException {
        if (actual != expected) {
            throw new SQLException(table + " id phải liên tục từ 0");
        }
    }

    /** Thu hẹp số nguyên sang signed byte mà không cho phép tràn im lặng. */
    private static byte checkedByte(int value, String field) throws SQLException {
        if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
            throw new SQLException(field + " vượt giới hạn byte");
        }
        return (byte) value;
    }

    /** Thu hẹp số nguyên sang signed short mà không cho phép tràn im lặng. */
    private static short checkedShort(int value, String field) throws SQLException {
        if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
            throw new SQLException(field + " vượt giới hạn short");
        }
        return (short) value;
    }

    /** Rollback best-effort và giữ lỗi rollback trong suppressed exceptions. */
    private static void rollback(Connection connection, Exception original) {
        try {
            connection.rollback();
        } catch (SQLException rollbackFailure) {
            original.addSuppressed(rollbackFailure);
        }
    }
}
