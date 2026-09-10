package com.nsocry.bootstrap;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nsocry.assets.AtomicDataAssetRuntimeSnapshotStore;
import com.nsocry.assets.ClientAssetSourceException;
import com.nsocry.assets.ClientGraphicBlock;
import com.nsocry.assets.DataAssetBundle;
import com.nsocry.assets.DataAssetSeedArtifact;
import com.nsocry.assets.DataAssetSeedArtifactGenerator;
import com.nsocry.assets.ProgressionTable;
import com.nsocry.assets.TaskRouteAsset;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import org.junit.jupiter.api.Test;

class DataAssetServerStartupReadinessTest {
    @Test
    void publishesAndRequiresExactSnapshotBeforeReturning() {
        DataAssetBundle bundle = bundle();
        DataAssetSeedArtifact artifact = DataAssetSeedArtifactGenerator.generate(bundle);
        AtomicDataAssetRuntimeSnapshotStore store = new AtomicDataAssetRuntimeSnapshotStore();
        DataAssetServerStartupReadiness readiness = new DataAssetServerStartupReadiness(
                () -> bundle, artifact.manifest(), store);

        readiness.verify();

        assertSame(store.currentSnapshot().orElseThrow(),
                store.requireCurrent(artifact.manifest().version(),
                        artifact.manifest().payloadSha256()));
    }

    @Test
    void sourceFailureIsFailClosedAndLeavesStoreEmpty() {
        DataAssetSeedArtifact artifact = DataAssetSeedArtifactGenerator.generate(bundle());
        AtomicDataAssetRuntimeSnapshotStore store = new AtomicDataAssetRuntimeSnapshotStore();
        DataAssetServerStartupReadiness readiness = new DataAssetServerStartupReadiness(
                () -> { throw new ClientAssetSourceException("database unavailable", null); },
                artifact.manifest(), store);

        assertThrows(IllegalStateException.class, readiness::verify);
        assertThrows(IllegalStateException.class, () -> store.requireCurrent(
                artifact.manifest().version(), artifact.manifest().payloadSha256()));
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
