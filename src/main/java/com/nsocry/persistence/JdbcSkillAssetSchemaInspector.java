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

/** Đọc information_schema để kiểm tra V003 mà không thay đổi database. */
public final class JdbcSkillAssetSchemaInspector {
    private static final String READ_COLUMNS = """
            SELECT table_name, column_name, data_type, column_type, is_nullable
            FROM information_schema.columns
            WHERE table_schema = DATABASE()
              AND table_name IN ('client_skill_options', 'client_skill_classes',
                  'client_skill_templates', 'client_skill_levels', 'client_skill_level_options')
            ORDER BY table_name, ordinal_position
            """;

    private final DataSource dataSource;

    public JdbcSkillAssetSchemaInspector(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource, "dataSource");
    }

    /** Dùng connection read-only, không thực hiện DDL/DML. */
    public SkillAssetSchemaPreflightReport inspect() throws ClientAssetSourceException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setReadOnly(true);
            try (PreparedStatement statement = connection.prepareStatement(READ_COLUMNS);
                    ResultSet result = statement.executeQuery()) {
                List<SkillAssetSchemaColumn> columns = new ArrayList<>();
                while (result.next()) {
                    columns.add(new SkillAssetSchemaColumn(
                            result.getString("table_name"), result.getString("column_name"),
                            result.getString("data_type"), result.getString("column_type"),
                            "YES".equalsIgnoreCase(result.getString("is_nullable"))));
                }
                return SkillAssetSchemaContract.evaluate(columns);
            }
        } catch (SQLException | RuntimeException exception) {
            throw new ClientAssetSourceException("Không thể kiểm tra schema SKILL V003", exception);
        }
    }
}
