package com.nsocry.persistence;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;

class DataAssetSchemaPreflightTest {
    @Test
    void acceptsExactV005ColumnContract() {
        var report = DataAssetSchemaContract.evaluate(validColumns());
        assertTrue(report.ready());
        assertTrue(report.differences().isEmpty());
    }

    @Test
    void reportsMissingUnexpectedAndWrongColumns() {
        List<DataAssetSchemaColumn> columns = new ArrayList<>(validColumns());
        columns.removeIf(column -> column.columnName().equals("payload_sha256"));
        columns.set(0, column("version", "tinyint", "tinyint", false));
        columns.add(column("legacy_id", "int", "int", false));
        columns.add(columns.get(1));

        var report = DataAssetSchemaContract.evaluate(columns);

        assertFalse(report.ready());
        assertTrue(report.differences().stream().anyMatch(value -> value.contains("payload_sha256")));
        assertTrue(report.differences().stream().anyMatch(value -> value.contains("version")));
        assertTrue(report.differences().stream().anyMatch(value -> value.contains("legacy_id")));
        assertTrue(report.differences().stream().anyMatch(value -> value.contains("bị trùng")));
    }

    @Test
    void jdbcInspectorReadsInformationSchemaWithoutMutation() throws Exception {
        FakeJdbc jdbc = new FakeJdbc(validColumns());
        var report = new JdbcDataAssetSchemaInspector(jdbc.dataSource()).inspect();
        assertTrue(report.ready());
        assertTrue(jdbc.readOnly);
        assertTrue(jdbc.sql.contains("information_schema.columns"));
        assertTrue(jdbc.sql.contains("client_data_assets"));
    }

    private static List<DataAssetSchemaColumn> validColumns() {
        return List.of(
                column("version", "tinyint", "tinyint unsigned", false),
                column("task_group_count", "tinyint", "tinyint unsigned", false),
                column("experience_count", "tinyint", "tinyint unsigned", false),
                column("payload_length", "int", "int unsigned", false),
                column("payload_sha256", "char", "char(64)", false),
                column("payload", "longblob", "longblob", false),
                column("manifest_text", "text", "text", false));
    }

    private static DataAssetSchemaColumn column(
            String name, String dataType, String columnType, boolean nullable) {
        return new DataAssetSchemaColumn(
                "client_data_assets", name, dataType, columnType, nullable);
    }

    private static final class FakeJdbc {
        final List<Map<String, Object>> rows = new ArrayList<>();
        boolean readOnly;
        String sql = "";

        FakeJdbc(List<DataAssetSchemaColumn> columns) {
            for (DataAssetSchemaColumn column : columns) {
                Map<String, Object> row = new HashMap<>();
                row.put("table_name", column.tableName());
                row.put("column_name", column.columnName());
                row.put("data_type", column.dataType());
                row.put("column_type", column.columnType());
                row.put("is_nullable", column.nullable() ? "YES" : "NO");
                rows.add(row);
            }
        }

        DataSource dataSource() {
            return (DataSource) Proxy.newProxyInstance(getClass().getClassLoader(),
                    new Class<?>[] {DataSource.class}, (proxy, method, args) ->
                            method.getName().equals("getConnection")
                                    ? connection() : defaultValue(method.getReturnType()));
        }

        private Connection connection() {
            return (Connection) Proxy.newProxyInstance(getClass().getClassLoader(),
                    new Class<?>[] {Connection.class}, (proxy, method, args) -> {
                        if (method.getName().equals("setReadOnly")) {
                            readOnly = (Boolean) args[0];
                            return null;
                        }
                        if (method.getName().equals("prepareStatement")) {
                            sql = (String) args[0];
                            return statement();
                        }
                        return defaultValue(method.getReturnType());
                    });
        }

        private PreparedStatement statement() {
            return (PreparedStatement) Proxy.newProxyInstance(getClass().getClassLoader(),
                    new Class<?>[] {PreparedStatement.class}, (proxy, method, args) ->
                            method.getName().equals("executeQuery")
                                    ? resultSet() : defaultValue(method.getReturnType()));
        }

        private ResultSet resultSet() {
            int[] index = {-1};
            return (ResultSet) Proxy.newProxyInstance(getClass().getClassLoader(),
                    new Class<?>[] {ResultSet.class}, (proxy, method, args) -> {
                        if (method.getName().equals("next")) return ++index[0] < rows.size();
                        if (method.getName().equals("getString")) {
                            return rows.get(index[0]).get((String) args[0]);
                        }
                        return defaultValue(method.getReturnType());
                    });
        }
    }

    private static Object defaultValue(Class<?> type) {
        if (!type.isPrimitive()) return null;
        if (type == boolean.class) return false;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        return 0;
    }
}
