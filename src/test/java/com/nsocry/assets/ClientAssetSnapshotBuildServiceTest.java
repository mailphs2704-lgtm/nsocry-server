package com.nsocry.assets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.EnumMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class ClientAssetSnapshotBuildServiceTest {
    @Test
    void loadsFiveSourcesThenPublishesCompleteSnapshot() throws Exception {
        AtomicInteger loadOrder = new AtomicInteger();
        AtomicClientAssetSnapshotProvider provider = new AtomicClientAssetSnapshotProvider(snapshot((byte) 11));
        ClientAssetSnapshotBuildService service = new ClientAssetSnapshotBuildService(
                () -> { assertEquals(0, loadOrder.getAndIncrement()); return data((byte) 1); },
                () -> { assertEquals(1, loadOrder.getAndIncrement()); return map((byte) 2); },
                () -> { assertEquals(2, loadOrder.getAndIncrement()); return skill((byte) 3); },
                () -> { assertEquals(3, loadOrder.getAndIncrement()); return item((byte) 4); },
                () -> { assertEquals(4, loadOrder.getAndIncrement()); return appearance(); },
                provider);

        ClientAssetSnapshot rebuilt = service.rebuild();

        assertSame(rebuilt, provider.currentSnapshot());
        assertEquals(5, loadOrder.get());
        assertEquals(1, rebuilt.manifest().dataVersion());
        assertEquals(4, rebuilt.manifest().itemVersion());
    }

    @Test
    void sourceFailureKeepsPublishedSnapshot() throws Exception {
        ClientAssetSnapshot initial = snapshot((byte) 11);
        AtomicClientAssetSnapshotProvider provider = new AtomicClientAssetSnapshotProvider(initial);
        ClientAssetSourceException failure = new ClientAssetSourceException("map unavailable", null);
        ClientAssetSnapshotBuildService service = new ClientAssetSnapshotBuildService(
                () -> data((byte) 1),
                () -> { throw failure; },
                () -> skill((byte) 3),
                () -> item((byte) 4),
                ClientAssetSnapshotBuildServiceTest::appearance,
                provider);

        assertSame(failure, assertThrows(ClientAssetSourceException.class, service::rebuild));
        assertSame(initial, provider.currentSnapshot());
    }

    @Test
    void invalidSourceResultKeepsPublishedSnapshot() throws Exception {
        ClientAssetSnapshot initial = snapshot((byte) 11);
        AtomicClientAssetSnapshotProvider provider = new AtomicClientAssetSnapshotProvider(initial);
        ClientAssetSnapshotBuildService service = new ClientAssetSnapshotBuildService(
                () -> data((byte) 1),
                () -> map((byte) 2),
                () -> null,
                () -> item((byte) 4),
                ClientAssetSnapshotBuildServiceTest::appearance,
                provider);

        assertThrows(NullPointerException.class, service::rebuild);
        assertSame(initial, provider.currentSnapshot());
    }

    @Test
    void constructorRejectsMissingPort() {
        assertThrows(NullPointerException.class, () -> new ClientAssetSnapshotBuildService(
                () -> data((byte) 1),
                () -> map((byte) 2),
                () -> skill((byte) 3),
                () -> item((byte) 4),
                ClientAssetSnapshotBuildServiceTest::appearance,
                null));
    }

    private static ClientAssetSnapshot snapshot(byte baseVersion) throws Exception {
        return ClientAssetSnapshotAssembler.assemble(
                data(baseVersion), map((byte) (baseVersion + 1)),
                skill((byte) (baseVersion + 2)), item((byte) (baseVersion + 3)), appearance());
    }

    private static DataAssetBundle data(byte version) {
        EnumMap<ClientGraphicBlock, byte[]> graphics = new EnumMap<>(ClientGraphicBlock.class);
        for (ClientGraphicBlock block : ClientGraphicBlock.values()) {
            graphics.put(block, new byte[0]);
        }
        EnumMap<ProgressionTable, int[]> progression = new EnumMap<>(ProgressionTable.class);
        for (ProgressionTable table : ProgressionTable.values()) {
            progression.put(table, new int[0]);
        }
        return new DataAssetBundle(version, graphics, List.of(), new long[0], progression, new byte[0]);
    }

    private static MapAssetBundle map(byte version) {
        return new MapAssetBundle(version, List.of(), List.of(), List.of());
    }

    private static SkillAssetBundle skill(byte version) {
        return new SkillAssetBundle(version, List.of(), List.of());
    }

    private static ItemAssetBundle item(byte version) {
        return new ItemAssetBundle(version, List.of(), List.of());
    }

    private static AppearanceAssetBundle appearance() {
        return new AppearanceAssetBundle(
                List.of(), List.of(), List.of(), List.of(),
                List.of(), List.of(), List.of(), List.of());
    }
}
