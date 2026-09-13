package com.nsocry.operations;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nsocry.assets.DataAssetSeedValidationResult;
import com.nsocry.persistence.DataAssetOverwriteMode;
import com.nsocry.persistence.DataAssetSeedImportException;
import com.nsocry.persistence.JdbcDataAssetSeedImporter;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;

class JdbcDataAssetSeedImporterTest {
    private static final byte[] PAYLOAD = {7, 1, 2};
    private static final String MANIFEST = "version=7";
    private static final DataAssetSeedValidationResult VALIDATION =
            new DataAssetSeedValidationResult((byte) 7, 43, 131, 3, "a".repeat(64));

    @Test
    void insertsMissingVersionAndCommits() throws Exception {
        FakeJdbc jdbc = new FakeJdbc(false, false);
        var result = new JdbcDataAssetSeedImporter(jdbc.dataSource())
                .importSeed(archive(), DataAssetOverwriteMode.REJECT_EXISTING);
        assertFalse(result.overwritten());
        assertTrue(jdbc.committed);
        assertFalse(jdbc.rolledBack);
    }

    @Test
    void rejectsExistingVersionAndRollsBackWithoutWrite() {
        FakeJdbc jdbc = new FakeJdbc(true, false);
        assertThrows(DataAssetSeedImportException.class, () ->
                new JdbcDataAssetSeedImporter(jdbc.dataSource())
                        .importSeed(archive(), DataAssetOverwriteMode.REJECT_EXISTING));
        assertTrue(jdbc.rolledBack);
        assertFalse(jdbc.writeExecuted);
    }

    @Test
    void databaseFailureRollsBackReplacement() {
        FakeJdbc jdbc = new FakeJdbc(true, true);
        assertThrows(DataAssetSeedImportException.class, () ->
                new JdbcDataAssetSeedImporter(jdbc.dataSource())
                        .importSeed(archive(), DataAssetOverwriteMode.REPLACE_SAME_VERSION));
        assertTrue(jdbc.rolledBack);
        assertFalse(jdbc.committed);
    }

    private static ValidatedDataAssetSeedArchive archive() {
        return new ValidatedDataAssetSeedArchive(PAYLOAD, MANIFEST, VALIDATION);
    }

    private static final class FakeJdbc {
        final boolean exists;
        final boolean failWrite;
        boolean committed;
        boolean rolledBack;
        boolean writeExecuted;

        FakeJdbc(boolean exists, boolean failWrite) {
            this.exists = exists;
            this.failWrite = failWrite;
        }

        DataSource dataSource() {
            return (DataSource) Proxy.newProxyInstance(getClass().getClassLoader(),
                    new Class<?>[] {DataSource.class}, (proxy, method, args) ->
                            method.getName().equals("getConnection")
                                    ? connection() : defaultValue(method.getReturnType()));
        }

        private Connection connection() {
            return (Connection) Proxy.newProxyInstance(getClass().getClassLoader(),
                    new Class<?>[] {Connection.class}, (proxy, method, args) -> switch (method.getName()) {
                        case "prepareStatement" -> statement((String) args[0]);
                        case "commit" -> { committed = true; yield null; }
                        case "rollback" -> { rolledBack = true; yield null; }
                        default -> defaultValue(method.getReturnType());
                    });
        }

        private PreparedStatement statement(String sql) {
            return (PreparedStatement) Proxy.newProxyInstance(getClass().getClassLoader(),
                    new Class<?>[] {PreparedStatement.class}, (proxy, method, args) -> {
                        if (method.getName().equals("executeQuery")) return resultSet();
                        if (method.getName().equals("executeUpdate")) {
                            writeExecuted = true;
                            if (failWrite) throw new SQLException("write failed");
                            return 1;
                        }
                        return defaultValue(method.getReturnType());
                    });
        }

        private ResultSet resultSet() {
            return (ResultSet) Proxy.newProxyInstance(getClass().getClassLoader(),
                    new Class<?>[] {ResultSet.class}, new java.lang.reflect.InvocationHandler() {
                        boolean first = true;
                        public Object invoke(Object proxy, java.lang.reflect.Method method, Object[] args) {
                            if (method.getName().equals("next")) {
                                boolean answer = first && exists;
                                first = false;
                                return answer;
                            }
                            return defaultValue(method.getReturnType());
                        }
                    });
        }
    }

    private static Object defaultValue(Class<?> type) {
        if (!type.isPrimitive()) return null;
        if (type == boolean.class) return false;
        if (type == byte.class) return (byte) 0;
        if (type == short.class) return (short) 0;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == float.class) return 0F;
        if (type == double.class) return 0D;
        if (type == char.class) return (char) 0;
        return null;
    }
}
