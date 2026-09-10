package com.nsocry.assets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import org.junit.jupiter.api.Test;

class DataAssetRuntimePublishServiceTest {
    @Test
    void publishesOnlyAfterAllManifestGatesPass() throws Exception {
        DataAssetBundle bundle = bundle();
        DataAssetSeedArtifact artifact = DataAssetSeedArtifactGenerator.generate(bundle);
        AtomicDataAssetRuntimeSnapshotStore store = new AtomicDataAssetRuntimeSnapshotStore();

        DataAssetRuntimeSnapshot snapshot = new DataAssetRuntimePublishService(
                () -> bundle, artifact.manifest(), store).rebuildAndPublish();

        assertSame(snapshot, store.currentSnapshot().orElseThrow());
        assertEquals(7, Byte.toUnsignedInt(snapshot.version()));
        assertEquals(1, snapshot.taskGroupCount());
        assertEquals(131, snapshot.experienceCount());
        assertArrayEquals(artifact.payload(), snapshot.payload());
    }

    @Test
    void checksumFailureKeepsPreviousSnapshot() throws Exception {
        DataAssetBundle bundle = bundle();
        DataAssetSeedArtifact artifact = DataAssetSeedArtifactGenerator.generate(bundle);
        AtomicDataAssetRuntimeSnapshotStore store = new AtomicDataAssetRuntimeSnapshotStore();
        DataAssetRuntimeSnapshot previous = new DataAssetRuntimePublishService(
                () -> bundle, artifact.manifest(), store).rebuildAndPublish();
        DataAssetSeedManifest invalid = new DataAssetSeedManifest(
                (byte) 7, 1, 131, artifact.payload().length, "0".repeat(64));

        assertThrows(IllegalArgumentException.class, () ->
                new DataAssetRuntimePublishService(() -> bundle, invalid, store).rebuildAndPublish());
        assertSame(previous, store.currentSnapshot().orElseThrow());
    }

    @Test
    void sourceFailureDoesNotPublish() {
        AtomicDataAssetRuntimeSnapshotStore store = new AtomicDataAssetRuntimeSnapshotStore();
        ClientAssetSourceException failure = new ClientAssetSourceException("database unavailable", null);
        DataAssetSeedManifest manifest = new DataAssetSeedManifest(
                (byte) 7, 0, 0, 0, "0".repeat(64));
        DataAssetRuntimePublishService service = new DataAssetRuntimePublishService(
                () -> { throw failure; }, manifest, store);

        assertSame(failure, assertThrows(ClientAssetSourceException.class, service::rebuildAndPublish));
        assertTrue(store.currentSnapshot().isEmpty());
    }

    @Test
    void snapshotPayloadIsDefensivelyCopied() throws Exception {
        DataAssetBundle bundle = bundle();
        DataAssetSeedArtifact artifact = DataAssetSeedArtifactGeneratornymous_generateDelegate.generate(bundle);
        DataAssetRuntimeSnapshot snapshot = new DataAssetRuntimePublishService(
                () -> bundle, artifact.manifest(), new AtomicDataAssetRuntimeSnapshotStore())
                .rebuildAndPublish();
        byte[] exposed = snapshot.payload();
        exposed[0] = 0;
        assertEquals(7, Byte.toUnsignedInt(snapshot.payload()[0]));
    }

    @Test
    void constructorRejectsMissingDependencies() {
        DataAssetSeedManifest manifest = new DataAssetSeedManifest(
                (byte) 7, 0, 0, 0, "0".repeat(64));
        AtomicDataAssetRuntimeSnapshotStore store = new AtomicDataAssetRuntimeSnapshotStore();
        assertThrows(NullPointerException.class, () -> new DataAssetRuntimePublishService(null, manifest, store));
        assertThrows(NullPointerException.class, () -> new DataAssetRuntimePublishService(() -> bundle(), null, store));
        assertThrows(NullPointerException.class, () -> new DataAssetRuntimePublishService(() -> bundle(), manifest, null));
    }

    @Test
    void snapshotRejectsSameLengthTamperedPayload() throws Exception {
        DataAssetBundle bundle = bundle();
        DataAssetSeedArtifact artifact = DataAssetSeedArtifactGenerator.generate(bundle);
        DataAssetSeedValidationResult validation =
                DataAssetSeedValidator.validate(bundle, artifact.manifest());
        byte[] changed = artifact.payload();
        changed[changed.length - 1] ^= 1;
        assertThrows(IllegalArgumentException.class,
                () -> DataAssetRuntimeSnapshot.verified(validation, changed));
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
        return new DataAssetBundle((byte) 7, graphics,
                List.of(List.of(new TaskRouteAsset((byte) 1, (byte) 2))),
                experience, progression, new byte[] {0});
    }

}
