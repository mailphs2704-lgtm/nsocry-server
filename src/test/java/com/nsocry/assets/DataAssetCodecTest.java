package com.nsocry.assets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.EOFException;
import java.util.EnumMap;
import java.util.List;
import org.junit.jupiter.api.Test;

class DataAssetCodecTest {
    @Test
    void roundTripsEveryContainerSection() throws Exception {
        DataAssetBundle expected = fixtureBundle();

        DataAssetBundle actual = DataAssetCodec.decode(DataAssetCodec.encode(expected));

        assertEquals(expected.version(), actual.version());
        for (ClientGraphicBlock block : ClientGraphicBlock.values()) {
            assertArrayEquals(expected.graphic(block), actual.graphic(block));
        }
        assertEquals(expected.taskRoutes(), actual.taskRoutes());
        assertArrayEquals(expected.experienceThresholds(), actual.experienceThresholds());
        for (ProgressionTable table : ProgressionTable.values()) {
            assertArrayEquals(expected.progression(table), actual.progression(table));
        }
        assertArrayEquals(expected.effectTemplates(), actual.effectTemplates());
    }

    @Test
    void rejectsGraphicLengthBeyondRemainingPayload() {
        byte[] invalid = {26, 0, 0, 0, 5, 1};

        assertThrows(EOFException.class, () -> DataAssetCodec.decode(invalid));
    }

    @Test
    void requiresAllProgressionTables() {
        EnumMap<ProgressionTable, int[]> incomplete = progression();
        incomplete.remove(ProgressionTable.MAX_PERCENT);

        assertThrows(NullPointerException.class, () -> new DataAssetBundle(
                (byte) 1, graphics(), List.of(), new long[0], incomplete, new byte[0]));
    }

    @Test
    void keepsInputArraysImmutable() {
        EnumMap<ClientGraphicBlock, byte[]> graphics = graphics();
        byte[] arrow = graphics.get(ClientGraphicBlock.ARROW);
        DataAssetBundle bundle = new DataAssetBundle(
                (byte) 1, graphics, List.of(), new long[0], progression(), new byte[0]);

        arrow[0] = 99;

        assertArrayEquals(new byte[] {1}, bundle.graphic(ClientGraphicBlock.ARROW));
    }

    private static DataAssetBundle fixtureBundle() {
        return new DataAssetBundle(
                (byte) 26,
                graphics(),
                List.of(List.of(new TaskRouteAsset((byte) 2, (byte) 3))),
                new long[] {0L, 100L},
                progression(),
                new byte[] {9, 10});
    }

    private static EnumMap<ClientGraphicBlock, byte[]> graphics() {
        EnumMap<ClientGraphicBlock, byte[]> values = new EnumMap<>(ClientGraphicBlock.class);
        byte value = 1;
        for (ClientGraphicBlock block : ClientGraphicBlock.values()) {
            values.put(block, new byte[] {value++});
        }
        return values;
    }

    private static EnumMap<ProgressionTable, int[]> progression() {
        EnumMap<ProgressionTable, int[]> values = new EnumMap<>(ProgressionTable.class);
        int value = 1;
        for (ProgressionTable table : ProgressionTable.values()) {
            values.put(table, new int[] {value++});
        }
        return values;
    }
}
