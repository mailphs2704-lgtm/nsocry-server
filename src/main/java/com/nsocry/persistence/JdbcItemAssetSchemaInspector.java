package com.nsocry.persistence;

import com.nsocry.assets.ClientAssetSourceException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.sql.DataSource;

/** Đọc information_schema để kiểm tra V002 mà không thay đổi database. */
public final class JdbcItemAssetSchemaInspector {
    private static final String READ_COLUMNS = """
            SELECT table_name, column_name, data_type, column_type, is_nullable
            FROM information_schema.columns
            WHERE table_schema = DATABASE()
              AND table_name IN ('client_item_options', 'client_item_templates')
            ORDER BY table_name, ordinal_position
            """;

    private final DataSource dataSource;

    /** Tạo inspector chỉ đọc từ DataSource NSOCry. */
    public JdbcItemAssetSchemaInspector(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource, "dataSource");
    }

    /** Đọc metadata rồi đối chiếu contract V002, không thực hiện DDL/DML. */
    public ItemAssetSchemaPreflightReport inspect() throws ClientAssetSourceException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setReadOnly(true);
            try (PreparedStatement statement = connection.prepareStatement(READ_COLUMNS);
                    ResultSet result = statement.executeQuery()) {
                List<ItemAssetSchemaColumn> columns = new ArrayList<>();
                while (result.next()) {
                    columns.add(new ItemAssetSchemaColumn(
                            result.getString("table_name"),
                            result.getString("column_name"),
                            result.getString("data_type"),
                            result.getString("column_type"),
                            "YES".equalsIgnoreCase(result.getString("is_nullable"))));
                }
                return ItemAssetSchemaContract.evaluate(columns);
            }
        } catch (SQLException | RuntimeException exception) {
            throw new ClientAssetSourceException("Không thể kiểm tra schema ITEM V002", exception);
        }
    }
}
