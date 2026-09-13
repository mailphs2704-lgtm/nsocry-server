package com.nsocry.assets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nsocry.protocol.compat.ClientDataSet;
import java.util.EnumMap;
import java.util.List;
import org.junit.jupiter.api.Test;

class ClientAssetSnapshotAssemblerTest {
    @Test
    void assemblesFiveEncodedProductsWithOneManifest() throws Exception {
        ClientAssetSnapshot snapshot = assemble((byte) 1);

        assertEquals(1, snapshot.manifest().dataVersion());
        assertEquals(2, snapshot.manifest().mapVersion());
        assertEquals(3, snapshot.manifest().skillVersion());
        assertEquals(4, snapshot.manifest().itemVersion());
        assertArrayEquals(DataAssetCodec.encode(data((byte) 1)), snapshot.payload(ClientDataSet.DATA));
        assertArrayEquals(AppearanceAssetCodec.encode(emptyAppearance()), snapshot.appearanceData());
    }

    @Test
    void atomicProviderPublishesWholeSnapshot() throws Exception {
        ClientAssetSnapshot first = assemble((byte) 1);
        ClientAssetSnapshot second = assemble((byte) 11);
        AtomicClientAssetSnapshotProvider provider = new AtomicClientAssetSnapshotProvider(first);

        provider.publish(second);

        assertSame(second, provider.currentSnapshot());
    }

    @Test
    void atomicProviderRejectsNullWithoutLosingCurrentSnapshot() throws Exception {
        ClientAssetSnapshot first = assemble((byte) 1);
        AtomicClientAssetSnapshotProvider provider = new AtomicClientAssetSnapshotProvider(first);

        assertThrows(NullPointerException.class, () -> provider.publish(null));
        assertSame(first, provider.currentSnapshot());
    }

    @Test
    void assemblerRejectsMissingBundle() {
        assertThrows(NullPointerException.class, () -> ClientAssetSnapshotAssembler.assemble(
                data((byte) 1), emptyMap((byte) 2), emptySkill((byte) 3), null, emptyAppearance()));
    }

    private static ClientAssetSnapshot assemble(byte baseVersion) throws Exception {
        return ClientAssetSnapshotAssembler.assemble(
                data(baseVersion),
                emptyMap((byte) (baseVersion + 1)),
                emptySkill((byte) (baseVersion + 2)),
                new ItemAssetBundle((byte) (baseVersion + 3), List.of(), List.of()),
                emptyAppearance());
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

    private static MapAssetBundle emptyMap(byte version) {
        return new MapAssetBundle(version, List.of(), List.of(), List.of());
    }

    private static SkillAssetBundle emptySkill(byte version) {
        return new SkillAssetBundle(version, List.of(), List.of());
    }

    private static AppearanceAssetBundle emptyAppearance() {
        return new AppearanceAssetBundle(
                List.of(), List.of(), List.of(), List.of(),
                List.of(), List.of(), List.of(), List.of());
    }
}
