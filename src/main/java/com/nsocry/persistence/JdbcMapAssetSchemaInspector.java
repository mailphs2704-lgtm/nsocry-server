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

/** Đọc information_schema để kiểm tra MAP V004 mà không thay đổi database. */
public final class JdbcMapAssetSchemaInspector {
    private static final String READ_COLUMNS = """
            SELECT table_name, column_name, data_type, column_type, is_nullable
            FROM information_schema.columns
            WHERE table_schema = DATABASE()
              AND table_name IN ('client_map_names', 'client_npc_templates',
                  'client_npc_menu_entries', 'client_mob_templates')
            ORDER BY table_name, ordinal_position
            """;

    private final DataSource dataSource;

    /** Tạo inspector read-only cho đúng DataSource NSOCry; từ chối null. */
    public JdbcMapAssetSchemaInspector(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource, "dataSource");
    }

    /** Dùng connection read-only, chỉ đọc metadata và không thực hiện DDL/DML. */
    public MapAssetSchemaPreflightReport inspect() throws ClientAssetSourceException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setReadOnly(true);
            try (PreparedStatement statement = connection.prepareStatement(READ_COLUMNS);
                    ResultSet result = statement.executeQuery()) {
                List<MapAssetSchemaColumn> columns = new ArrayList<>();
                while (result.next()) {
                    columns.add(new MapAssetSchemaColumn(
                            result.getString("table_name"), result.getString("column_name"),
                            result.getString("data_type"), result.getString("column_type"),
                            "YES".equalsIgnoreCase(result.getString("is_nullable"))));
                }
                return MapAssetSchemaContract.evaluate(columns);
            }
        } catch (SQLException | RuntimeException exception) {
            throw new ClientAssetSourceException("Không thể kiểm tra schema MAP V004", exception);
        }
    }
}
