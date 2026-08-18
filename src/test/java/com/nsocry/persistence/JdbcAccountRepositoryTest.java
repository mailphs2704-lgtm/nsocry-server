package com.nsocry.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nsocry.authentication.AccountCredential;
import com.nsocry.authentication.AccountStatus;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;

class JdbcAccountRepositoryTest {
    @Test
    void mapsCredentialAndBindsUsername() {
        FakeJdbc jdbc = new FakeJdbc();
        jdbc.row.put("id", 7L);
        jdbc.row.put("username", "Cry");
        jdbc.row.put("password_hash", "encoded");
        jdbc.row.put("status", 0);
        jdbc.row.put("activated", true);
        jdbc.row.put("locked_until", null);
        Optional<AccountCredential> found = new JdbcAccountRepository(jdbc.dataSource()).findByUsername("Cry");
        assertTrue(found.isPresent());
        assertEquals(7L, found.orElseThrow().id());
        assertEquals(AccountStatus.ACTIVE, found.orElseThrow().status());
        assertEquals("Cry", jdbc.parameters.get(1));
    }

    @Test
    void returnsEmptyWhenUsernameDoesNotExist() {
        FakeJdbc jdbc = new FakeJdbc();
        assertTrue(new JdbcAccountRepository(jdbc.dataSource()).findByUsername("missing").isEmpty());
    }

    @Test
    void bindsSuccessfulLoginUpdate() {
        FakeJdbc jdbc = new FakeJdbc();
        Instant instant = Instant.parse("2026-08-18T10:00:00Z");
        new JdbcAccountRepository(jdbc.dataSource()).recordSuccessfulLogin(9, instant);
        assertEquals(Timestamp.from(instant), jdbc.parameters.get(1));
        assertEquals(9L, jdbc.parameters.get(2));
    }

    private static final class FakeJdbc {
        final Map<String, Object> row = new HashMap<>();
        final Map<Integer, Object> parameters = new HashMap<>();

        DataSource dataSource() {
            return (DataSource) Proxy.newProxyInstance(getClass().getClassLoader(),
                    new Class<?>[] {DataSource.class}, (proxy, method, args) ->
                            method.getName().equals("getConnection") ? connection() : defaultValue(method.getReturnType()));
        }

        private Connection connection() {
            return (Connection) Proxy.newProxyInstance(getClass().getClassLoader(),
                    new Class<?>[] {Connection.class}, (proxy, method, args) ->
                            method.getName().equals("prepareStatement") ? statement() : defaultValue(method.getReturnType()));
        }

        private PreparedStatement statement() {
            return (PreparedStatement) Proxy.newProxyInstance(getClass().getClassLoader(),
                    new Class<?>[] {PreparedStatement.class}, (proxy, method, args) -> {
                        if (method.getName().startsWith("set")) {
                            parameters.put((Integer) args[0], args[1]);
                            return null;
                        }
                        if (method.getName().equals("executeQuery")) return resultSet();
                        if (method.getName().equals("executeUpdate")) return 1;
                        return defaultValue(method.getReturnType());
                    });
        }

        private ResultSet resultSet() {
            boolean[] advanced = {false};
            return (ResultSet) Proxy.newProxyInstance(getClass().getClassLoader(),
                    new Class<?>[] {ResultSet.class}, (proxy, method, args) -> {
                        if (method.getName().equals("next")) {
                            if (advanced[0] || row.isEmpty()) return false;
                            advanced[0] = true;
                            return true;
                        }
                        if (method.getName().startsWith("get")) return row.get((String) args[0]);
                        return defaultValue(method.getReturnType());
                    });
        }

        private static Object defaultValue(Class<?> type) {
            if (!type.isPrimitive()) return null;
            if (type == boolean.class) return false;
            if (type == int.class) return 0;
            if (type == long.class) return 0L;
            return 0;
        }
    }
}
