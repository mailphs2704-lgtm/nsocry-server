package com.nsocry.bootstrap;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nsocry.assets.AtomicDataAssetRuntimeSnapshotStore;
import com.nsocry.assets.ClientGraphicBlock;
import com.nsocry.assets.DataAssetBundle;
import com.nsocry.assets.DataAssetSeedArtifact;
import com.nsocry.assets.DataAssetSeedArtifactGenerator;
import com.nsocry.assets.ProgressionTable;
import com.nsocry.assets.TaskRouteAsset;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import org.junit.jupiter.api.Test;

class DataAssetRuntimePublishCommandTest {
    @Test
    void publishesSourceAgainstArchiveManifest() throws Exception {
        DataAssetBundle bundle = bundle();
        DataAssetSeedArtifact artifact = DataAssetSeedArtifactGenerator.generate(bundle);
        AtomicDataAssetRuntimeSnapshotStore store = new AtomicDataAssetRuntimeSnapshotStore();

        var snapshot = DataAssetRuntimePublishCommand.publish(
                () -> bundle, artifact.manifestText(), store);

        assertSame(snapshot, store.currentSnapshot().orElseThrow());
    }

    @Test
    void invalidManifestDoesNotPublish() throws Exception {
        AtomicDataAssetRuntimeSnapshotStore store = new AtomicDataAssetRuntimeSnapshotStore();

        assertThrows(Exception.class, () ->
                DataAssetRuntimePublishCommand.publish(() -> bundle(), "invalid", store));
        assertTrue(store.currentSnapshot().isEmpty());
    }

    @Test
    void reportStatesIsolatedPublishAndNoStartupWiring() throws Exception {
        DataAssetBundle bundle = bundle();
        DataAssetSeedArtifact artifact = DataAssetSeedArtifactGenerator.generate(bundle);
        AtomicDataAssetRuntimeSnapshotStore store = new AtomicDataAssetRuntimeSnapshotStore();
        var snapshot = DataAssetRuntimePublishCommand.publish(
                () -> bundle, artifact.manifestText(), store);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        DataAssetRuntimePublishCommand.printReport(
                snapshot, store, new PrintStream(bytes, true, StandardCharsets.UTF_8));

        String report = bytes.toString(StandardCharsets.UTF_8);
        assertTrue(report.contains("DATA runtime snapshot PUBLISHED_ISOLATED"));
        assertTrue(report.contains("databaseChanged=false"));
        assertTrue(report.contains("runtimeSnapshotPublished=true"));
        assertTrue(report.contains("serverStartupWired=false"));
    }

    @Test
    void reportRejectsSnapshotDifferentFromCurrentStore() throws Exception {
        DataAssetBundle bundle = bundle();
        DataAssetSeedArtifact artifact = DataAssetSeedArtifactGenerator.generate(bundle);
        var firstStore = new AtomicDataAssetRuntimeSnapshotStore();
        var secondStore = new AtomicDataAssetRuntimeSnapshotStore();
        var first = DataAssetRuntimePublishCommand.publish(
                () -> bundle, artifact.manifestText(), firstStore);
        DataAssetRuntimePublishCommand.publish(
                () -> bundle, artifact.manifestText(), secondStore);

        assertThrows(IllegalStateException.class, () ->
                DataAssetRuntimePublishCommand.printReport(first, secondStore, System.out));
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
