package com.nsocry.operations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nsocry.assets.ClientGraphicBlock;
import com.nsocry.assets.DataAssetBundle;
import com.nsocry.assets.DataAssetSeedArtifact;
import com.nsocry.assets.DataAssetSeedArtifactGenerator;
import com.nsocry.assets.ProgressionTable;
import com.nsocry.assets.TaskRouteAsset;
import com.nsocry.persistence.DataAssetSeedImportException;
import com.nsocry.persistence.JdbcDataAssetSeedVerifier;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;

class JdbcDataAssetSeedVerifierTest {
    @Test
    void verifiesEveryPersistedFieldOnReadOnlyConnection() throws Exception {
        DataAssetSeedArtifact artifact = DataAssetSeedArtifactGenerator.generate(bundle());
        FakeJdbc jdbc = new FakeJdbc(artifact, true);

        var result = new JdbcDataAssetSeedVerifier(jdbc.dataSource()).verify(archive(artifact));

        assertEquals(artifact.validation(), result);
        assertTrue(jdbc.readOnly);
        assertEquals(7, jdbc.requestedVersion);
    }

    @Test
    void rejectsMissingDatabaseVersion() throws Exception {
        DataAssetSeedArtifact artifact = DataAssetSeedArtifactGenerator.generate(bundle());
        FakeJdbc jdbc = new FakeJdbc(artifact, false);

        assertThrows(DataAssetSeedImportException.class,
                () -> new JdbcDataAssetSeedVerifier(jdbc.dataSource()).verify(archive(artifact)));
        assertTrue(jdbc.readOnly);
    }

    @Test
    void rejectsPayloadDifferentFromValidatedArchive() throws Exception {
        DataAssetSeedArtifact artifact = DataAssetSeedArtifactGenerator.generate(bundle());
        FakeJdbc jdbc = new FakeJdbc(artifact, true);
        jdbc.payload = artifact.payload();
        jdbc.payload[jdbc.payload.length - 1] ^= 1;

        assertThrows(DataAssetSeedImportException.class,
                () -> new JdbcDataAssetSeedVerifier(jdbc.dataSource()).verify(archive(artifact)));
    }

    private static ValidatedDataAssetSeedArchive archive(DataAssetSeedArtifact artifact) {
        return new ValidatedDataAssetSeedArchive(
                artifact.payload(), artifact.manifestText(), artifact.validation());
    }

    private static DataAssetBundle bundle() {
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
        return new DataAssetBundle(
                (byte) 7, graphics,
                List.of(List.of(new TaskRouteAsset((byte) 1, (byte) 2))),
                experience, progression, new byte[] {0});
    }

    private static final class FakeJdbc {
        final DataAssetSeedArtifact artifact;
        final boolean exists;
        boolean readOnly;
        int requestedVersion;
        byte[] payload;

        FakeJdbc(DataAssetSeedArtifact artifact, boolean exists) {
            this.artifact = artifact;
            this.exists = exists;
            this.payload = artifact.payload();
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
                        case "prepareStatement" -> statement();
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
            return (ResultSet) Proxy.newProxyInstance(getClass().getClassLoader(),
                    new Class<?>[] {ResultSet.class}, new java.lang.reflect.InvocationHandler() {
                        boolean first = true;
                        public Object invoke(Object proxy, java.lang.reflect.Method method, Object[] args) {
                            if (method.getName().equals("next")) {
                                boolean answer = first && exists;
                                first = false;
                                return answer;
                            }
                            if (method.getName().equals("getInt")) return switch ((String) args[0]) {
                                case "version" -> Byte.toUnsignedInt(artifact.validation().version());
                                case "task_group_count" -> artifact.validation().taskGroupCount();
                                case "experience_count" -> artifact.validation().experienceCount();
                                case "payload_length" -> artifact.validation().payloadLength();
                                default -> 0;
                            };
                            if (method.getName().equals("getString")) return switch ((String) args[0]) {
                                case "payload_sha256" -> artifact.validation().payloadSha256();
                                case "manifest_text" -> artifact.manifestText();
                                default -> null;
                            };
                            if (method.getName().equals("getBytes")) return payload.clone();
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
