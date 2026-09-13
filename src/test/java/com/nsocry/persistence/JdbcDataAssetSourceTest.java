package com.nsocry.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nsocry.assets.ClientAssetSourceException;
import com.nsocry.assets.ClientGraphicBlock;
import com.nsocry.assets.DataAssetBundle;
import com.nsocry.assets.DataAssetSeedArtifact;
import com.nsocry.assets.DataAssetSeedArtifactGenerator;
import com.nsocry.assets.ProgressionTable;
import com.nsocry.assets.TaskRouteAsset;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;

class JdbcDataAssetSourceTest {
    @Test
    void readsValidatedRowInReadOnlyRepeatableReadSnapshot() throws Exception {
        FakeJdbc jdbc = new FakeJdbc(artifact(), true);

        DataAssetBundle loaded = new JdbcDataAssetSource(jdbc.dataSource(), (byte) 7).load();

        assertEquals(7, Byte.toUnsignedInt(loaded.version()));
        assertEquals(1, loaded.taskRoutes().size());
        assertEquals(131, loaded.experienceThresholds().length);
        assertEquals(7, jdbc.requestedVersion);
        assertTrue(jdbc.readOnly);
        assertEquals(Connection.TRANSACTION_REPEATABLE_READ, jdbc.isolation);
        assertTrue(jdbc.committed);
        assertFalse(jdbc.rolledBack);
    }

    @Test
    void missingVersionRollsBackAndDoesNotReturnBundle() throws Exception {
        FakeJdbc jdbc = new FakeJdbc(artifact(), false);

        assertThrows(ClientAssetSourceException.class,
                () -> new JdbcDataAssetSource(jdbc.dataSource(), (byte) 7).load());

        assertTrue(jdbc.rolledBack);
        assertFalse(jdbc.committed);
    }

    @Test
    void metadataMismatchRollsBackSnapshot() throws Exception {
        FakeJdbc jdbc = new FakeJdbc(artifact(), true);
        jdbc.payloadLengthDelta = 1;

        assertThrows(ClientAssetSourceException.class,
                () -> new JdbcDataAssetSource(jdbc.dataSource(), (byte) 7).load());

        assertTrue(jdbc.rolledBack);
        assertFalse(jdbc.committed);
    }

    private static DataAssetSeedArtifact artifact() throws Exception {
        EnumMap<ClientGraphicBlock, byte[]> graphics = new EnumMap<>(ClientGraphicBlock.class);
        for (ClientGraphicBlock block : ClientGraphicBlock.values()) {
            graphics.put(block, new byte[] {(byte) block.ordinal()});
        }
        EnumMap<ProgressionTable, int[]> progression = new EnumMap<>(ProgressionTable.class);
        for (ProgressionTable table : ProgressionTable.values()) {
            progression.put(table, new int[] {table.ordinal() + 1});
        }
        long[] experience = new long[131];
        Arrays.setAll(experience, index -> index * 100L);
        return DataAssetSeedArtifactGenerator.generate(new DataAssetBundle(
                (byte) 7, graphics,
                List.of(List.of(new TaskRouteAsset((byte) 1, (byte) 2))),
                experience, progression, new byte[] {0}));
    }

    private static final class FakeJdbc {
        final DataAssetSeedArtifact artifact;
        final boolean exists;
        boolean readOnly;
        boolean committed;
        boolean rolledBack;
        int isolation;
        int requestedVersion;
        int payloadLengthDelta;

        FakeJdbc(DataAssetSeedArtifact artifact, boolean exists) {
            this.artifact = artifact;
            this.exists = exists;
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
                        case "setReadOnly" -> { readOnly = (Boolean) args[0]; yield null; }
                        case "setTransactionIsolation" -> { isolation = (Integer) args[0]; yield null; }
                        case "prepareStatement" -> statement();
                        case "commit" -> { committed = true; yield null; }
                        case "rollback" -> { rolledBack = true; yield null; }
                        default -> defaultValue(method.getReturnType());
                    });
        }

        private PreparedStatement statement() {
            return (PreparedStatement) Proxy.newProxyInstance(getClass().getClassLoader(),
                    new Class<?>[] {PreparedStatement.class}, (proxy, method, args) -> {
                        if (method.getName().equals("setInt")) {
                            requestedVersion = (Integer) args[1];
                            return null;
                        }
                        if (method.getName().equals("executeQuery")) return resultSet();
                        return defaultValue(method.getReturnType());
                    });
        }

        private ResultSet resultSet() {
            int[] nextCount = {0};
            return (ResultSet) Proxy.newProxyInstance(getClass().getClassLoader(),
                    new Class<?>[] {ResultSet.class}, (proxy, method, args) -> {
                        if (method.getName().equals("next")) return exists && nextCount[0]++ == 0;
                        if (method.getName().equals("getBytes")) return artifact.payload();
                        if (method.getName().equals("getString")) {
                            return switch ((String) args[0]) {
                                case "manifest_text" -> artifact.manifestText();
                                case "payload_sha256" -> artifact.manifest().payloadSha256();
                                default -> null;
                            };
                        }
                        if (method.getName().equals("getInt")) {
                            return switch ((String) args[0]) {
                                case "version" -> Byte.toUnsignedInt(artifact.manifest().version());
                                case "task_group_count" -> artifact.manifest().taskGroupCount();
                                case "experience_count" -> artifact.manifest().experienceCount();
                                case "payload_length" -> artifact.manifest().payloadLength() + payloadLengthDelta;
                                default -> 0;
                            };
                        }
                        return defaultValue(method.getReturnType());
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
