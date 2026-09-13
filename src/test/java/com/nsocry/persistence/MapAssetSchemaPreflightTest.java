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

class MapAssetSchemaPreflightTest {
    @Test
    void acceptsExactV004ColumnContract() {
        var report = MapAssetSchemaContract.evaluate(validColumns());
        assertTrue(report.ready());
        assertTrue(report.differences().isEmpty());
    }

    @Test
    void reportsMissingAndUnexpectedColumns() {
        List<MapAssetSchemaColumn> columns = new ArrayList<>(validColumns());
        columns.removeIf(column -> column.columnName().equals("choice_order"));
        columns.add(column("client_map_names", "legacy_id", "int", "int", false));
        var report = MapAssetSchemaContract.evaluate(columns);
        assertFalse(report.ready());
        assertTrue(report.differences().stream().anyMatch(value -> value.contains("choice_order")));
        assertTrue(report.differences().stream().anyMatch(value -> value.contains("legacy_id")));
    }

    @Test
    void rejectsWrongUnsignedNullableAndDuplicateMetadata() {
        List<MapAssetSchemaColumn> columns = new ArrayList<>(validColumns());
        columns.set(0, column("client_map_names", "id", "tinyint", "tinyint", false));
        columns.set(1, column("client_map_names", "name", "varchar", "varchar(500)", true));
        columns.add(columns.get(2));
        var report = MapAssetSchemaContract.evaluate(columns);
        assertFalse(report.ready());
        assertTrue(report.differences().stream().anyMatch(value -> value.contains("bị trùng")));
    }

    @Test
    void jdbcInspectorReadsInformationSchemaWithoutMutation() throws Exception {
        FakeJdbc jdbc = new FakeJdbc(validColumns());
        var report = new JdbcMapAssetSchemaInspector(jdbc.dataSource()).inspect();
        assertTrue(report.ready());
        assertTrue(jdbc.readOnly);
        assertTrue(jdbc.sql.contains("information_schema.columns"));
    }

    private static List<MapAssetSchemaColumn> validColumns() {
        List<MapAssetSchemaColumn> columns = new ArrayList<>();
        add(columns, "client_map_names", new String[][] {
                {"id", "tinyint", "tinyint unsigned"}, {"name", "varchar", "varchar(500)"}});
        add(columns, "client_npc_templates", new String[][] {
                {"id", "tinyint", "tinyint unsigned"}, {"name", "varchar", "varchar(500)"},
                {"head", "smallint", "smallint"}, {"body", "smallint", "smallint"},
                {"leg", "smallint", "smallint"},
                {"menu_row_count", "tinyint", "tinyint unsigned"}});
        add(columns, "client_npc_menu_entries", new String[][] {
                {"npc_id", "tinyint", "tinyint unsigned"},
                {"row_order", "tinyint", "tinyint unsigned"},
                {"choice_order", "tinyint", "tinyint unsigned"},
                {"text", "varchar", "varchar(5000)"}});
        add(columns, "client_mob_templates", new String[][] {
                {"id", "smallint", "smallint unsigned"}, {"type", "tinyint", "tinyint"},
                {"name", "varchar", "varchar(500)"}, {"health", "int", "int"},
                {"move_range", "tinyint", "tinyint"}, {"speed", "tinyint", "tinyint"}});
        return List.copyOf(columns);
    }

    private static void add(List<MapAssetSchemaColumn> target, String table, String[][] definitions) {
        for (String[] definition : definitions) {
            target.add(column(table, definition[0], definition[1], definition[2], false));
        }
    }

    private static MapAssetSchemaColumn column(String table, String name, String dataType,
            String columnType, boolean nullable) {
        return new MapAssetSchemaColumn(table, name, dataType, columnType, nullable);
    }

    private static final class FakeJdbc {
        final List<Map<String, Object>> rows = new ArrayList<>();
        boolean readOnly;
        String sql = "";

        FakeJdbc(List<MapAssetSchemaColumn> columns) {
            for (MapAssetSchemaColumn column : columns) {
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
