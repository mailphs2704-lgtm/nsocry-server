package com.nsocry.assets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import org.junit.jupiter.api.Test;

class SkillAssetRuntimePublishServiceTest {
    @Test
    void publishesOnlyAfterAllManifestGatesPass() throws Exception {
        SkillAssetBundle bundle = bundle((byte) 26);
        byte[] payload = SkillAssetCodec.encode(bundle);
        AtomicSkillAssetRuntimeSnapshotStore store = new AtomicSkillAssetRuntimeSnapshotStore();
        SkillAssetRuntimePublishService service = new SkillAssetRuntimePublishService(
                () -> bundle, manifest(payload), store);

        SkillAssetRuntimeSnapshot published = service.rebuildAndPublish();

        assertSame(published, store.currentSnapshot().orElseThrow());
        assertEquals(26, Byte.toUnsignedInt(published.version()));
        assertArrayEquals(payload, published.payload());
    }

    @Test
    void checksumFailureKeepsPreviousSnapshot() throws Exception {
        SkillAssetBundle bundle = bundle((byte) 26);
        byte[] payload = SkillAssetCodec.encode(bundle);
        AtomicSkillAssetRuntimeSnapshotStore store = new AtomicSkillAssetRuntimeSnapshotStore();
        SkillAssetRuntimeSnapshot previous = new SkillAssetRuntimePublishService(
                () -> bundle, manifest(payload), store).rebuildAndPublish();
        SkillAssetSeedManifest invalid = new SkillAssetSeedManifest(
                (byte) 26, 1, 1, 1, 1, 0, 0, payload.length, "0".repeat(64));

        assertThrows(IllegalArgumentException.class,
                () -> new SkillAssetRuntimePublishService(() -> bundle, invalid, store).rebuildAndPublish());
        assertSame(previous, store.currentSnapshot().orElseThrow());
    }

    @Test
    void sourceFailureDoesNotPublish() {
        AtomicSkillAssetRuntimeSnapshotStore store = new AtomicSkillAssetRuntimeSnapshotStore();
        ClientAssetSourceException failure = new ClientAssetSourceException("database unavailable", null);
        SkillAssetRuntimePublishService service = new SkillAssetRuntimePublishService(
                () -> { throw failure; }, manifest(new byte[] {26}), store);

        assertSame(failure, assertThrows(ClientAssetSourceException.class, service::rebuildAndPublish));
        assertTrue(store.currentSnapshot().isEmpty());
    }

    @Test
    void snapshotPayloadIsDefensivelyCopied() throws Exception {
        SkillAssetBundle bundle = bundle((byte) 26);
        byte[] payload = SkillAssetCodec.encode(bundle);
        SkillAssetRuntimeSnapshot snapshot = new SkillAssetRuntimePublishService(
                () -> bundle, manifest(payload), new AtomicSkillAssetRuntimeSnapshotStore())
                .rebuildAndPublish();

        byte[] first = snapshot.payload();
        first[0] = 0;

        assertEquals(26, Byte.toUnsignedInt(snapshot.payload()[0]));
    }

    @Test
    void constructorRejectsMissingDependencies() throws Exception {
        SkillAssetBundle bundle = bundle((byte) 26);
        SkillAssetSeedManifest manifest = manifest(SkillAssetCodec.encode(bundle));
        AtomicSkillAssetRuntimeSnapshotStore store = new AtomicSkillAssetRuntimeSnapshotStore();

        assertThrows(NullPointerException.class, () -> new SkillAssetRuntimePublishService(null, manifest, store));
        assertThrows(NullPointerException.class, () -> new SkillAssetRuntimePublishService(() -> bundle, null, store));
        assertThrows(NullPointerException.class, () -> new SkillAssetRuntimePublishService(() -> bundle, manifest, null));
    }

    private static SkillAssetBundle bundle(byte version) {
        SkillTemplateAsset template = new SkillTemplateAsset(
                (byte) 0, "Cry Strike", (byte) 1, (byte) 0, (short) 7, "skill",
                List.of(new SkillLevelAsset((short) 0, (byte) 1, (byte) 1,
                        (short) 2, 3, (short) 4, (short) 5, (byte) 1, List.of())));
        return new SkillAssetBundle(version, List.of("Power"),
                List.of(new SkillClassAsset("Cry", List.of(template))));
    }

    private static SkillAssetSeedManifest manifest(byte[] payload) {
        return new SkillAssetSeedManifest((byte) 26, 1, 1, 1, 1, 0, 0,
                payload.length, sha256(payload));
    }

    private static String sha256(byte[] payload) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(payload));
        } catch (Exception exception) {
            throw new AssertionError(exception);
        }
    }
}
