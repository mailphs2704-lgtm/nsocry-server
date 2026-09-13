package com.nsocry.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nsocry.assets.SkillAssetBundle;
import com.nsocry.assets.SkillAssetSeedArtifactGenerator;
import com.nsocry.assets.SkillClassAsset;
import com.nsocry.assets.SkillLevelAsset;
import com.nsocry.assets.SkillLevelOptionAsset;
import com.nsocry.assets.SkillTemplateAsset;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;

class JdbcSkillAssetSeedImporterTest {
    @Test
    void replacesAllFiveTablesInOneSerializableTransaction() throws Exception {
        FakeJdbc jdbc = new FakeJdbc();
        var artifact = SkillAssetSeedArtifactGenerator.generate(fixture());

        var result = new JdbcSkillAssetSeedImporter(jdbc.dataSource())
                .importSeed(artifact.payload(), artifact.manifestText());

        assertEquals(1, result.structure().optionTemplateCount());
        assertEquals(5, jdbc.batches.size());
        assertEquals(List.of("client_skill_level_options", "client_skill_levels",
                "client_skill_templates", "client_skill_classes", "client_skill_options"), jdbc.deletedTables);
        assertTrue(jdbc.committed);
        assertFalse(jdbc.rolledBack);
        assertEquals(Connection.TRANSACTION_SERIALIZABLE, jdbc.isolation);
    }

    @Test
    void storesRawPointAsUnsignedDatabaseValue() throws Exception {
        FakeJdbc jdbc = new FakeJdbc();
        var artifact = SkillAssetSeedArtifactGenerator.generate(fixture());

        new JdbcSkillAssetSeedImporter(jdbc.dataSource()).importSeed(artifact.payload(), artifact.manifestText());

        assertEquals(150, jdbc.rows("client_skill_levels").get(0).get(4));
    }

    @Test
    void invalidArtifactDoesNotOpenConnection() {
        FakeJdbc jdbc = new FakeJdbc();
        var artifact = SkillAssetSeedArtifactGenerator.generate(fixture());
        byte[] changed = artifact.payload();
        changed[changed.length - 1] ^= 1;

        assertThrows(SkillAssetSeedImportException.class,
                () -> new JdbcSkillAssetSeedImporter(jdbc.dataSource()).importSeed(changed, artifact.manifestText()));
        assertEquals(0, jdbc.connectionCount);
    }

    @Test
    void failedBatchRollsBackWholeReplacement() {
        FakeJdbc jdbc = new FakeJdbc();
        jdbc.failBatch = true;
        var artifact = SkillAssetSeedArtifactGenerator.generate(fixture());

        assertThrows(SkillAssetSeedImportException.class,
                () -> new JdbcSkillAssetSeedImporter(jdbc.dataSource())
                        .importSeed(artifact.payload(), artifact.manifestText()));
        assertTrue(jdbc.rolledBack);
        assertFalse(jdbc.committed);
    }

    @Test
    void incompleteBatchRollsBackWholeReplacement() {
        FakeJdbc jdbc = new FakeJdbc();
        jdbc.incompleteBatch = true;
        var artifact = SkillAssetSeedArtifactGenerator.generate(fixture());

        assertThrows(SkillAssetSeedImportException.class,
                () -> new JdbcSkillAssetSeedImporter(jdbc.dataSource())
                        .importSeed(artifact.payload(), artifact.manifestText()));
        assertTrue(jdbc.rolledBack);
    }

    private static SkillAssetBundle fixture() {
        SkillLevelAsset level = new SkillLevelAsset((short) 0, (byte) 150, (byte) 10,
                (short) 20, 500, (short) 30, (short) 18, (byte) 1,
                List.of(new SkillLevelOptionAsset((short) 15, (byte) 0)));
        SkillTemplateAsset template = new SkillTemplateAsset((byte) 0, "Chiêu Cry",
                (byte) 12, (byte) 1, (short) 318, "Mô tả", List.of(level));
        return new SkillAssetBundle((byte) 26, List.of("Tấn công"),
                List.of(new SkillClassAsset("Ninja Cry", List.of(template))));
    }

    private static final class FakeJdbc {
        final Map<String, List<Map<Integer, Object>>> batches = new HashMap<>();
        final List<String> deletedTables = new ArrayList<>();
        int connectionCount;
        int isolation;
        boolean committed;
        boolean rolledBack;
        boolean failBatch;
        boolean incompleteBatch;

        List<Map<Integer, Object>> rows(String table) { return batches.get(table); }

        DataSource dataSource() {
            return (DataSource) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] {DataSource.class},
                    (proxy, method, args) -> {
                        if (method.getName().equals("getConnection")) { connectionCount++; return connection(); }
                        return defaultValue(method.getReturnType());
                    });
        }

        private Connection connection() {
            return (Connection) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] {Connection.class},
                    (proxy, method, args) -> switch (method.getName()) {
                        case "setTransactionIsolation" -> { isolation = (Integer) args[0]; yield null; }
                        case "prepareStatement" -> statement((String) args[0]);
                        case "commit" -> { committed = true; yield null; }
                        case "rollback" -> { rolledBack = true; yield null; }
                        default -> defaultValue(method.getReturnType());
                    });
        }

        private PreparedStatement statement(String sql) {
            String table = table(sql);
            Map<Integer, Object> current = new HashMap<>();
            List<Map<Integer, Object>> rows = batches.computeIfAbsent(table, ignored -> new ArrayList<>());
            return (PreparedStatement) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] {PreparedStatement.class},
                    (proxy, method, args) -> {
                        if (method.getName().startsWith("set")) { current.put((Integer) args[0], args[1]); return null; }
                        if (method.getName().equals("addBatch")) { rows.add(new HashMap<>(current)); return null; }
                        if (method.getName().equals("executeUpdate")) { deletedTables.add(table); return 1; }
                        if (method.getName().equals("executeBatch")) {
                            if (failBatch) throw new SQLException("batch failed");
                            int size = incompleteBatch && !rows.isEmpty() ? rows.size() - 1 : rows.size();
                            int[] results = new int[size]; Arrays.fill(results, Statement.SUCCESS_NO_INFO); return results;
                        }
                        return defaultValue(method.getReturnType());
                    });
        }

        private static String table(String sql) {
            String normalized = sql.strip();
            String prefix = normalized.startsWith("DELETE FROM ") ? "DELETE FROM " : "INSERT INTO ";
            return normalized.substring(prefix.length()).split("[ (\\n]", 2)[0];
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
