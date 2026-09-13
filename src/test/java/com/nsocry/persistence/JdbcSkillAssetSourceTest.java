package com.nsocry.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nsocry.assets.ClientAssetSourceException;
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

class JdbcSkillAssetSourceTest {
    @Test
    void rebuildsNestedBundleAndUnsignedPointInOneSnapshot() throws Exception {
        FakeJdbc jdbc = FakeJdbc.valid();

        var bundle = new JdbcSkillAssetSource(jdbc.dataSource(), (byte) 26).load();

        var level = bundle.classes().get(0).templates().get(0).levels().get(0);
        assertEquals("Tấn công", bundle.optionTemplateNames().get(0));
        assertEquals("Ninja Cry", bundle.classes().get(0).name());
        assertEquals(150, Byte.toUnsignedInt(level.point()));
        assertEquals(15, level.options().get(0).parameter());
        assertTrue(jdbc.readOnly);
        assertEquals(Connection.TRANSACTION_REPEATABLE_READ, jdbc.isolation);
        assertTrue(jdbc.committed);
        assertFalse(jdbc.rolledBack);
    }

    @Test
    void rejectsIdGapAndRollsBackSnapshot() {
        FakeJdbc jdbc = FakeJdbc.valid();
        jdbc.options.get(0).put("id", 1);

        assertThrows(ClientAssetSourceException.class,
                () -> new JdbcSkillAssetSource(jdbc.dataSource(), (byte) 26).load());
        assertTrue(jdbc.rolledBack);
        assertFalse(jdbc.committed);
    }

    private static Map<String, Object> row(Object... values) {
        Map<String, Object> row = new HashMap<>();
        for (int index = 0; index < values.length; index += 2) row.put((String) values[index], values[index + 1]);
        return row;
    }

    private static final class FakeJdbc {
        final List<Map<String, Object>> options = new ArrayList<>();
        final List<Map<String, Object>> classes = new ArrayList<>();
        final List<Map<String, Object>> templates = new ArrayList<>();
        final List<Map<String, Object>> levels = new ArrayList<>();
        final List<Map<String, Object>> levelOptions = new ArrayList<>();
        boolean readOnly; boolean committed; boolean rolledBack; int isolation;

        static FakeJdbc valid() {
            FakeJdbc jdbc = new FakeJdbc();
            jdbc.options.add(row("id", 0, "name", "Tấn công"));
            jdbc.classes.add(row("id", 0, "name", "Ninja Cry"));
            jdbc.templates.add(row("id", 0, "class_id", 0, "sort_order", 0, "name", "Chiêu Cry",
                    "max_point", 12, "type", 1, "icon_id", 318, "description", "Mô tả"));
            jdbc.levels.add(row("id", 0, "template_id", 0, "sort_order", 0, "point", 150,
                    "required_level", 10, "mana_use", 20, "cooldown", 500, "dx", 30, "dy", 18, "max_fight", 1));
            jdbc.levelOptions.add(row("skill_level_id", 0, "sort_order", 0,
                    "parameter_value", 15, "option_template_id", 0));
            return jdbc;
        }

        DataSource dataSource() {
            return (DataSource) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] {DataSource.class},
                    (proxy, method, args) -> method.getName().equals("getConnection") ? connection() : defaultValue(method.getReturnType()));
        }

        private Connection connection() {
            return (Connection) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] {Connection.class},
                    (proxy, method, args) -> switch (method.getName()) {
                        case "setReadOnly" -> { readOnly = (Boolean) args[0]; yield null; }
                        case "setTransactionIsolation" -> { isolation = (Integer) args[0]; yield null; }
                        case "prepareStatement" -> statement(rows((String) args[0]));
                        case "commit" -> { committed = true; yield null; }
                        case "rollback" -> { rolledBack = true; yield null; }
                        default -> defaultValue(method.getReturnType());
                    });
        }

        private List<Map<String, Object>> rows(String sql) {
            if (sql.contains("client_skill_level_options")) return levelOptions;
            if (sql.contains("client_skill_levels")) return levels;
            if (sql.contains("client_skill_templates")) return templates;
            if (sql.contains("client_skill_classes")) return classes;
            return options;
        }

        private PreparedStatement statement(List<Map<String, Object>> rows) {
            return (PreparedStatement) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] {PreparedStatement.class},
                    (proxy, method, args) -> method.getName().equals("executeQuery") ? resultSet(rows) : defaultValue(method.getReturnType()));
        }

        private ResultSet resultSet(List<Map<String, Object>> rows) {
            int[] index = {-1};
            return (ResultSet) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] {ResultSet.class},
                    (proxy, method, args) -> {
                        if (method.getName().equals("next")) return ++index[0] < rows.size();
                        if (method.getName().startsWith("get")) return rows.get(index[0]).get((String) args[0]);
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
