package com.nsocry.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nsocry.assets.ItemAssetBundle;
import com.nsocry.assets.ItemAssetSeedArtifact;
import com.nsocry.assets.ItemAssetSeedArtifactGenerator;
import com.nsocry.assets.ItemAssetValidationResult;
import com.nsocry.assets.ItemOptionAsset;
import com.nsocry.assets.ItemTemplateAsset;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;

class JdbcItemAssetSeedImporterTest {
    @Test
    void validatesThenReplacesRowsWithPreparedBatches() throws Exception {
        FakeJdbc jdbc = new FakeJdbc();
        ItemAssetSeedArtifact artifact = ItemAssetSeedArtifactGenerator.generate(fixture());

        ItemAssetValidationResult result = new JdbcItemAssetSeedImporter(jdbc.dataSource())
                .importSeed(artifact.payload(), artifact.manifestText());

        assertEquals(1, result.optionCount());
        assertEquals(1, result.itemCount());
        assertEquals("Tang HP", jdbc.optionRows.get(0).get(2));
        assertEquals("Kiem", jdbc.itemRows.get(0).get(4));
        assertEquals(-1, jdbc.itemRows.get(0).get(8));
        assertTrue(jdbc.committed);
        assertFalse(jdbc.rolledBack);
        assertFalse(jdbc.autoCommit);
        assertEquals(Connection.TRANSACTION_SERIALIZABLE, jdbc.isolation);
    }

    @Test
    void invalidArtifactDoesNotOpenDatabaseConnection() throws Exception {
        FakeJdbc jdbc = new FakeJdbc();
        ItemAssetSeedArtifact artifact = ItemAssetSeedArtifactGenerator.generate(fixture());
        byte[] changed = artifact.payload();
        changed[changed.length - 1] ^= 1;

        assertThrows(ItemAssetSeedImportException.class,
                () -> new JdbcItemAssetSeedImporter(jdbc.dataSource())
                        .importSeed(changed, artifact.manifestText()));
        assertEquals(0, jdbc.connectionCount);
    }

    @Test
    void databaseFailureRollsBackWholeReplacement() throws Exception {
        FakeJdbc jdbc = new FakeJdbc();
        jdbc.failBatch = true;
        ItemAssetSeedArtifact artifact = ItemAssetSeedArtifactGenerator.generate(fixture());

        assertThrows(ItemAssetSeedImportException.class,
                () -> new JdbcItemAssetSeedImporter(jdbc.dataSource())
                        .importSeed(artifact.payload(), artifact.manifestText()));
        assertTrue(jdbc.rolledBack);
        assertFalse(jdbc.committed);
    }

    private static ItemAssetBundle fixture() {
        return new ItemAssetBundle(
                (byte) 26,
                List.of(new ItemOptionAsset("Tang HP", (byte) 2)),
                List.of(new ItemTemplateAsset(
                        (byte) 3, (byte) 1, "Kiem", "Mo ta",
                        (byte) 10, (short) 12, (short) -1, true)));
    }

    private static final class FakeJdbc {
        final List<Map<Integer, Object>> optionRows = new ArrayList<>();
        final List<Map<Integer, Object>> itemRows = new ArrayList<>();
        int connectionCount;
        boolean autoCommit = true;
        boolean committed;
        boolean rolledBack;
        boolean failBatch;
        int isolation;

        DataSource dataSource() {
            return (DataSource) Proxy.newProxyInstance(getClass().getClassLoader(),
                    new Class<?>[] {DataSource.class}, (proxy, method, args) -> {
                        if (method.getName().equals("getConnection")) {
                            connectionCount++;
                            return connection();
                        }
                        return defaultValue(method.getReturnType());
                    });
        }

        private Connection connection() {
            return (Connection) Proxy.newProxyInstance(getClass().getClassLoader(),
                    new Class<?>[] {Connection.class}, (proxy, method, args) -> switch (method.getName()) {
                        case "setAutoCommit" -> { autoCommit = (Boolean) args[0]; yield null; }
                        case "setTransactionIsolation" -> { isolation = (Integer) args[0]; yield null; }
                        case "prepareStatement" -> statement((String) args[0]);
                        case "commit" -> { committed = true; yield null; }
                        case "rollback" -> { rolledBack = true; yield null; }
                        default -> defaultValue(method.getReturnType());
                    });
        }

        private PreparedStatement statement(String sql) {
            Map<Integer, Object> current = new HashMap<>();
            List<Map<Integer, Object>> target = sql.startsWith("INSERT INTO client_item_options")
                    ? optionRows : sql.startsWith("INSERT INTO client_item_templates") ? itemRows : null;
            return (PreparedStatement) Proxy.newProxyInstance(getClass().getClassLoader(),
                    new Class<?>[] {PreparedStatement.class}, (proxy, method, args) -> {
                        if (method.getName().startsWith("set")) {
                            current.put((Integer) args[0], args[1]);
                            return null;
                        }
                        if (method.getName().equals("addBatch")) {
                            target.add(new HashMap<>(current));
                            return null;
                        }
                        if (method.getName().equals("executeBatch")) {
                            if (failBatch) throw new SQLException("batch failed");
                            int[] results = new int[target.size()];
                            Arrays.fill(results, 1);
                            return results;
                        }
                        if (method.getName().equals("executeUpdate")) return 1;
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
