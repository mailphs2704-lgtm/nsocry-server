package com.nsocry.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nsocry.assets.ClientAssetSourceException;
import com.nsocry.assets.ItemAssetBundle;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;

class JdbcItemAssetSourceTest {
    @Test
    void mapsOrderedOptionsAndItemsInOneTransaction() throws Exception {
        FakeJdbc jdbc = new FakeJdbc();
        jdbc.options.add(row("id", 0, "name", "Tấn công +#", "type", 1));
        jdbc.items.add(row(
                "id", 0, "type", 26, "gender", 2, "name", "Đá Cry",
                "description", "Dùng nâng cấp", "required_level", 1,
                "icon_id", 188, "part_id", -1, "upgradable", true));

        ItemAssetBundle bundle = new JdbcItemAssetSource(jdbc.dataSource(), (byte) 26).load();

        assertEquals(26, bundle.version());
        assertEquals("Tấn công +#", bundle.options().get(0).name());
        assertEquals("Đá Cry", bundle.items().get(0).name());
        assertTrue(bundle.items().get(0).upgradable());
        assertTrue(jdbc.readOnly);
        assertEquals(Connection.TRANSACTION_REPEATABLE_READ, jdbc.isolation);
        assertFalse(jdbc.autoCommit);
        assertTrue(jdbc.committed);
        assertFalse(jdbc.rolledBack);
    }

    @Test
    void rejectsGapBecauseWireUsesListPositionAsId() {
        FakeJdbc jdbc = new FakeJdbc();
        jdbc.options.add(row("id", 1, "name", "Sai ID", "type", 0));

        assertThrows(ClientAssetSourceException.class,
                () -> new JdbcItemAssetSource(jdbc.dataSource(), (byte) 1).load());
        assertTrue(jdbc.rolledBack);
        assertFalse(jdbc.committed);
    }

    @Test
    void wrapsConnectionFailureAsSourceError() {
        DataSource failing = (DataSource) Proxy.newProxyInstance(
                getClass().getClassLoader(), new Class<?>[] {DataSource.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("getConnection")) {
                        throw new SQLException("offline");
                    }
                    return defaultValue(method.getReturnType());
                });

        assertThrows(ClientAssetSourceException.class,
                () -> new JdbcItemAssetSource(failing, (byte) 1).load());
    }

    private static Map<String, Object> row(Object... values) {
        Map<String, Object> row = new HashMap<>();
        for (int index = 0; index < values.length; index += 2) {
            row.put((String) values[index], values[index + 1]);
        }
        return row;
    }

    private static final class FakeJdbc {
        final List<Map<String, Object>> options = new ArrayList<>();
        final List<Map<String, Object>> items = new ArrayList<>();
        boolean readOnly;
        boolean autoCommit = true;
        boolean committed;
        boolean rolledBack;
        int isolation;

        DataSource dataSource() {
            return (DataSource) Proxy.newProxyInstance(getClass().getClassLoader(),
                    new Class<?>[] {DataSource.class}, (proxy, method, args) ->
                            method.getName().equals("getConnection")
                                    ? connection() : defaultValue(method.getReturnType()));
        }

        private Connection connection() {
            return (Connection) Proxy.newProxyInstance(getClass().getClassLoader(),
                    new Class<?>[] {Connection.class}, (proxy, method, args) -> {
                        return switch (method.getName()) {
                            case "setReadOnly" -> { readOnly = (Boolean) args[0]; yield null; }
                            case "setAutoCommit" -> { autoCommit = (Boolean) args[0]; yield null; }
                            case "setTransactionIsolation" -> { isolation = (Integer) args[0]; yield null; }
                            case "prepareStatement" -> statement((String) args[0]);
                            case "commit" -> { committed = true; yield null; }
                            case "rollback" -> { rolledBack = true; yield null; }
                            default -> defaultValue(method.getReturnType());
                        };
                    });
        }

        private PreparedStatement statement(String sql) {
            List<Map<String, Object>> rows = sql.contains("client_item_options") ? options : items;
            return (PreparedStatement) Proxy.newProxyInstance(getClass().getClassLoader(),
                    new Class<?>[] {PreparedStatement.class}, (proxy, method, args) ->
                            method.getName().equals("executeQuery")
                                    ? resultSet(rows) : defaultValue(method.getReturnType()));
        }

        private ResultSet resultSet(List<Map<String, Object>> rows) {
            int[] index = {-1};
            return (ResultSet) Proxy.newProxyInstance(getClass().getClassLoader(),
                    new Class<?>[] {ResultSet.class}, (proxy, method, args) -> {
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
