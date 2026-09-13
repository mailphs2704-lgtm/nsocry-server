package com.nsocry.bootstrap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nsocry.assets.AtomicSkillAssetRuntimeSnapshotStore;
import com.nsocry.assets.SkillAssetBundle;
import com.nsocry.assets.SkillAssetCodec;
import com.nsocry.assets.SkillAssetRuntimeSnapshot;
import com.nsocry.assets.SkillAssetStructureValidator;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import org.junit.jupiter.api.Test;

class SkillAssetRuntimePublishCommandTest {
    @Test
    void publishesValidatedJdbcEquivalentBundle() throws Exception {
        SkillAssetBundle bundle = new SkillAssetBundle((byte) 26, List.of(), List.of());
        byte[] payload = SkillAssetCodec.encode(bundle);
        AtomicSkillAssetRuntimeSnapshotStore store = new AtomicSkillAssetRuntimeSnapshotStore();

        SkillAssetRuntimeSnapshot snapshot = SkillAssetRuntimePublishCommand.publish(
                () -> bundle, manifestText(bundle, payload), store);

        assertSame(snapshot, store.currentSnapshot().orElseThrow());
        assertEquals(26, Byte.toUnsignedInt(snapshot.version()));
    }

    @Test
    void invalidChecksumDoesNotPublish() throws Exception {
        SkillAssetBundle bundle = new SkillAssetBundle((byte) 26, List.of(), List.of());
        byte[] payload = SkillAssetCodec.encode(bundle);
        String invalidManifest = manifestText(bundle, payload)
                .replace(sha256(payload), "0".repeat(64));
        AtomicSkillAssetRuntimeSnapshotStore store = new AtomicSkillAssetRuntimeSnapshotStore();

        assertThrows(IllegalArgumentException.class,
                () -> SkillAssetRuntimePublishCommand.publish(() -> bundle, invalidManifest, store));
        assertTrue(store.currentSnapshot().isEmpty());
    }

    @Test
    void reportStatesReadOnlyAndNotWiredToStartup() throws Exception {
        SkillAssetBundle bundle = new SkillAssetBundle((byte) 26, List.of(), List.of());
        byte[] payload = SkillAssetCodec.encode(bundle);
        AtomicSkillAssetRuntimeSnapshotStore store = new AtomicSkillAssetRuntimeSnapshotStore();
        SkillAssetRuntimeSnapshot snapshot = SkillAssetRuntimePublishCommand.publish(
                () -> bundle, manifestText(bundle, payload), store);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        SkillAssetRuntimePublishCommand.printReport(snapshot, store, new PrintStream(bytes));

        String report = bytes.toString(StandardCharsets.UTF_8);
        assertTrue(report.contains("runtimeSnapshotPublished=true"));
        assertTrue(report.contains("databaseChanged=false"));
        assertTrue(report.contains("serverStartupWired=false"));
    }

    @Test
    void reportRejectsSnapshotThatIsNotCurrent() throws Exception {
        SkillAssetBundle bundle = new SkillAssetBundle((byte) 26, List.of(), List.of());
        byte[] payload = SkillAssetCodec.encode(bundle);
        SkillAssetRuntimeSnapshot snapshot = SkillAssetRuntimePublishCommand.publish(
                () -> bundle, manifestText(bundle, payload), new AtomicSkillAssetRuntimeSnapshotStore());

        assertThrows(IllegalStateException.class, () -> SkillAssetRuntimePublishCommand.printReport(
                snapshot, new AtomicSkillAssetRuntimeSnapshotStore(), System.out));
    }

    private static String manifestText(SkillAssetBundle bundle, byte[] payload) {
        var structure = SkillAssetStructureValidator.validate(bundle);
        return """
                format=nsocry-skill-seed-v1
                version=%d
                optionTemplateCount=%d
                classCount=%d
                skillTemplateCount=%d
                skillLevelCount=%d
                skillLevelOptionCount=%d
                rawByteDifferenceCount=0
                payloadLength=%d
                sha256=%s
                """.formatted(
                Byte.toUnsignedInt(bundle.version()),
                structure.optionTemplateCount(), structure.classCount(),
                structure.skillTemplateCount(), structure.skillLevelCount(),
                structure.skillLevelOptionCount(), payload.length, sha256(payload));
    }

    private static String sha256(byte[] payload) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(payload));
        } catch (Exception exception) {
            throw new AssertionError(exception);
        }
    }
}
