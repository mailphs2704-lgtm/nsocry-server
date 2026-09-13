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

class SkillAssetSchemaPreflightTest {
    @Test
    void acceptsExactV003ColumnContract() {
        var report = SkillAssetSchemaContract.evaluate(validColumns());
        assertTrue(report.ready());
        assertTrue(report.differences().isEmpty());
    }

    @Test
    void reportsMissingColumn() {
        List<SkillAssetSchemaColumn> columns = new ArrayList<>(validColumns());
        columns.removeIf(column -> column.columnName().equals("point"));
        var report = SkillAssetSchemaContract.evaluate(columns);
        assertFalse(report.ready());
        assertTrue(report.differences().stream().anyMatch(value -> value.contains("point")));
    }

    @Test
    void rejectsWrongUnsignedOrNullableContract() {
        List<SkillAssetSchemaColumn> columns = new ArrayList<>(validColumns());
        columns.set(0, column("client_skill_options", "id", "tinyint", "tinyint(4)", false));
        columns.set(1, column("client_skill_options", "name", "varchar", "varchar(500)", true));
        var report = SkillAssetSchemaContract.evaluate(columns);
        assertFalse(report.ready());
        assertTrue(report.differences().size() >= 2);
    }

    @Test
    void rejectsUnexpectedOrDuplicateMetadata() {
        List<SkillAssetSchemaColumn> columns = new ArrayList<>(validColumns());
        columns.add(columns.get(0));
        columns.add(column("client_skill_levels", "damage", "int", "int(11)", false));
        var report = SkillAssetSchemaContract.evaluate(columns);
        assertFalse(report.ready());
        assertTrue(report.differences().stream().anyMatch(value -> value.contains("bị trùng")));
        assertTrue(report.differences().stream().anyMatch(value -> value.contains("damage")));
    }

    @Test
    void jdbcInspectorReadsInformationSchemaWithoutMutation() throws Exception {
        FakeJdbc jdbc = new FakeJdbc(validColumns());
        var report = new JdbcSkillAssetSchemaInspector(jdbc.dataSource()).inspect();
        assertTrue(report.ready());
        assertTrue(jdbc.readOnly);
        assertTrue(jdbc.sql.contains("information_schema.columns"));
    }

    private static List<SkillAssetSchemaColumn> validColumns() {
        List<SkillAssetSchemaColumn> columns = new ArrayList<>();
        add(columns, "client_skill_options", new String[][] {{"id", "tinyint", "tinyint unsigned"}, {"name", "varchar", "varchar(500)"}});
        add(columns, "client_skill_classes", new String[][] {{"id", "tinyint", "tinyint unsigned"}, {"name", "varchar", "varchar(500)"}});
        add(columns, "client_skill_templates", new String[][] {{"id", "tinyint", "tinyint unsigned"}, {"class_id", "tinyint", "tinyint unsigned"}, {"sort_order", "tinyint", "tinyint unsigned"}, {"name", "varchar", "varchar(500)"}, {"max_point", "tinyint", "tinyint"}, {"type", "tinyint", "tinyint"}, {"icon_id", "smallint", "smallint"}, {"description", "varchar", "varchar(5000)"}});
        add(columns, "client_skill_levels", new String[][] {{"id", "smallint", "smallint unsigned"}, {"template_id", "tinyint", "tinyint unsigned"}, {"sort_order", "tinyint", "tinyint unsigned"}, {"point", "smallint", "smallint unsigned"}, {"required_level", "tinyint", "tinyint"}, {"mana_use", "smallint", "smallint"}, {"cooldown", "int", "int"}, {"dx", "smallint", "smallint"}, {"dy", "smallint", "smallint"}, {"max_fight", "tinyint", "tinyint"}});
        add(columns, "client_skill_level_options", new String[][] {{"skill_level_id", "smallint", "smallint unsigned"}, {"sort_order", "tinyint", "tinyint unsigned"}, {"parameter_value", "smallint", "smallint"}, {"option_template_id", "tinyint", "tinyint unsigned"}});
        return List.copyOf(columns);
    }

    private static void add(List<SkillAssetSchemaColumn> target, String table, String[][] definitions) {
        for (String[] definition : definitions) target.add(column(table, definition[0], definition[1], definition[2], false));
    }

    private static SkillAssetSchemaColumn column(String table, String name, String dataType,
            String columnType, boolean nullable) {
        return new SkillAssetSchemaColumn(table, name, dataType, columnType, nullable);
    }

    private static final class FakeJdbc {
        final List<Map<String, Object>> rows = new ArrayList<>();
        boolean readOnly;
        String sql = "";

        FakeJdbc(List<SkillAssetSchemaColumn> columns) {
            for (SkillAssetSchemaColumn column : columns) {
                Map<String, Object> row = new HashMap<>();
                row.put("table_name", column.tableName()); row.put("column_name", column.columnName());
                row.put("data_type", column.dataType()); row.put("column_type", column.columnType());
                row.put("is_nullable", column.nullable() ? "YES" : "NO"); rows.add(row);
            }
        }

        DataSource dataSource() {
            return (DataSource) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] {DataSource.class},
                    (proxy, method, args) -> method.getName().equals("getConnection") ? connection() : defaultValue(method.getReturnType()));
        }

        private Connection connection() {
            return (Connection) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] {Connection.class},
                    (proxy, method, args) -> {
                        if (method.getName().equals("setReadOnly")) { readOnly = (Boolean) args[0]; return null; }
                        if (method.getName().equals("prepareStatement")) { sql = (String) args[0]; return statement(); }
                        return defaultValue(method.getReturnType());
                    });
        }

        private PreparedStatement statement() {
            return (PreparedStatement) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] {PreparedStatement.class},
                    (proxy, method, args) -> method.getName().equals("executeQuery") ? resultSet() : defaultValue(method.getReturnType()));
        }

        private ResultSet resultSet() {
            int[] index = {-1};
            return (ResultSet) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] {ResultSet.class},
                    (proxy, method, args) -> {
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
