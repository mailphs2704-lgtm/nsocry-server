package com.nsocry.session;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nsocry.assets.AtomicDataAssetRuntimeSnapshotStore;
import com.nsocry.assets.ClientGraphicBlock;
import com.nsocry.assets.DataAssetBundle;
import com.nsocry.assets.DataAssetRuntimePublishService;
import com.nsocry.assets.DataAssetSeedArtifact;
import com.nsocry.assets.DataAssetSeedArtifactGenerator;
import com.nsocry.assets.ProgressionTable;
import com.nsocry.assets.TaskRouteAsset;
import com.nsocry.protocol.compat.ProtocolFrame;
import java.io.IOException;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class PostLoginDataSyncServiceTest {
    @Test
    void respondsToDataRequestFromCurrentSnapshot() throws Exception {
        DataAssetBundle bundle = bundle();
        DataAssetSeedArtifact artifact = DataAssetSeedArtifactGenerator.generate(bundle);
        AtomicDataAssetRuntimeSnapshotStore store = new AtomicDataAssetRuntimeSnapshotStore();
        new DataAssetRuntimePublishService(
                () -> bundle, artifact.manifest(), store).rebuildAndPublish();
        PostLoginDataSyncService service =
                new PostLoginDataSyncService(store::currentSnapshot);

        ProtocolFrame response = service.respond(
                new ProtocolFrame((byte) -28, new byte[] {(byte) -122}));

        assertEquals(-28, response.command());
        byte[] expected = new byte[artifact.payload().length + 1];
        expected[0] = (byte) -122;
        System.arraycopy(artifact.payload(), 0, expected, 1, artifact.payload().length);
        assertArrayEquals(expected, response.payload());
    }

    @Test
    void failsClosedWhenSnapshotIsNotReady() {
        PostLoginDataSyncService service =
                new PostLoginDataSyncService(Optional::empty);

        assertThrows(IllegalStateException.class, () -> service.respond(
                new ProtocolFrame((byte) -28, new byte[] {(byte) -122})));
    }

    @Test
    void rejectsDatasetOwnedByAnotherService() {
        PostLoginDataSyncService service =
                new PostLoginDataSyncService(Optional::empty);

        assertThrows(IOException.class, () -> service.respond(
                new ProtocolFrame((byte) -28, new byte[] {(byte) -121})));
    }

    @Test
    void rejectsMalformedRequestBeforeReadingSnapshot() {
        PostLoginDataSyncService service = new PostLoginDataSyncService(
                () -> { throw new AssertionError("snapshot must not be read"); });

        assertThrows(IOException.class, () -> service.respond(
                new ProtocolFrame((byte) -28, new byte[] {(byte) -122, 0})));
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
