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

class ItemAssetSchemaPreflightTest {
    @Test
    void acceptsExactV002ColumnContract() {
        ItemAssetSchemaPreflightReport report = ItemAssetSchemaContract.evaluate(validColumns());

        assertTrue(report.ready());
        assertTrue(report.differences().isEmpty());
    }

    @Test
    void reportsMissingColumn() {
        List<ItemAssetSchemaColumn> columns = new ArrayList<>(validColumns());
        columns.removeIf(column -> column.columnName().equals("part_id"));

        ItemAssetSchemaPreflightReport report = ItemAssetSchemaContract.evaluate(columns);

        assertFalse(report.ready());
        assertTrue(report.differences().stream().anyMatch(value -> value.contains("part_id")));
    }

    @Test
    void rejectsWrongUnsignedOrNullableContract() {
        List<ItemAssetSchemaColumn> columns = new ArrayList<>(validColumns());
        columns.set(0, column("client_item_options", "id", "smallint", "smallint(5)", false));
        columns.set(1, column("client_item_options", "name", "varchar", "varchar(500)", true));

        ItemAssetSchemaPreflightReport report = ItemAssetSchemaContract.evaluate(columns);

        assertFalse(report.ready());
        assertTrue(report.differences().size() >= 2);
    }

    @Test
    void rejectsUnexpectedOrDuplicateMetadata() {
        List<ItemAssetSchemaColumn> columns = new ArrayList<>(validColumns());
        columns.add(columns.get(0));
        columns.add(column("client_item_templates", "price", "int", "int(11)", false));

        ItemAssetSchemaPreflightReport report = ItemAssetSchemaContract.evaluate(columns);

        assertFalse(report.ready());
        assertTrue(report.differences().stream().anyMatch(value -> value.contains("bị trùng")));
        assertTrue(report.differences().stream().anyMatch(value -> value.contains("price")));
    }

    @Test
    void jdbcInspectorReadsInformationSchemaWithoutMutation() throws Exception {
        FakeJdbc jdbc = new FakeJdbc(validColumns());

        ItemAssetSchemaPreflightReport report = new JdbcItemAssetSchemaInspector(jdbc.dataSource()).inspect();

        assertTrue(report.ready());
        assertTrue(jdbc.readOnly);
        assertTrue(jdbc.sql.contains("information_schema.columns"));
    }

    private static List<ItemAssetSchemaColumn> validColumns() {
        return List.of(
                column("client_item_options", "id", "smallint", "smallint(5) unsigned", false),
                column("client_item_options", "name", "varchar", "varchar(500)", false),
                column("client_item_options", "type", "tinyint", "tinyint(4)", false),
                column("client_item_templates", "id", "smallint", "smallint(5) unsigned", false),
                column("client_item_templates", "type", "tinyint", "tinyint(4)", false),
                column("client_item_templates", "gender", "tinyint", "tinyint(4)", false),
                column("client_item_templates", "name", "varchar", "varchar(500)", false),
                column("client_item_templates", "description", "varchar", "varchar(1000)", false),
                column("client_item_templates", "required_level", "tinyint", "tinyint(4)", false),
                column("client_item_templates", "icon_id", "smallint", "smallint(6)", false),
                column("client_item_templates", "part_id", "smallint", "smallint(6)", false),
                column("client_item_templates", "upgradable", "tinyint", "tinyint(1)", false));
    }

    private static ItemAssetSchemaColumn column(
            String table, String name, String dataType, String columnType, boolean nullable) {
        return new ItemAssetSchemaColumn(table, name, dataType, columnType, nullable);
    }

    private static final class FakeJdbc {
        final List<Map<String, Object>> rows = new ArrayList<>();
        boolean readOnly;
        String sql = "";

        FakeJdbc(List<ItemAssetSchemaColumn> columns) {
            for (ItemAssetSchemaColumn column : columns) {
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
                        if (method.getName().equals("getString")) return rows.get(index[0]).get((String) args[0]);
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
