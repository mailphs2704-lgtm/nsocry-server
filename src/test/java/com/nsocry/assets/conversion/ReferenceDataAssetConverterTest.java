package com.nsocry.assets.conversion;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nsocry.assets.ClientGraphicBlock;
import com.nsocry.assets.DataAssetBundle;
import com.nsocry.assets.DataAssetCodec;
import com.nsocry.assets.ProgressionTable;
import java.util.EnumMap;
import org.junit.jupiter.api.Test;

class ReferenceDataAssetConverterTest {
    @Test
    void assemblesCompleteBundleAndPreservesRawTaskBytes() throws Exception {
        DataAssetBundle converted = ReferenceDataAssetConverter.convert(validDump(), 26, 0.25, progression());
        DataAssetBundle decoded = DataAssetCodec.decode(DataAssetCodec.encode(converted));

        assertEquals(27, Byte.toUnsignedInt(decoded.version()));
        assertEquals(1, decoded.taskRoutes().size());
        assertEquals(2, decoded.taskRoutes().get(0).size());
        assertEquals(200, Byte.toUnsignedInt(decoded.taskRoutes().get(0).get(0).npcId()));
        assertEquals(255, Byte.toUnsignedInt(decoded.taskRoutes().get(0).get(1).mapId()));
        assertArrayEquals(new long[] {100L, 200L}, decoded.experienceThresholds());
        assertArrayEquals(new int[] {12, 25}, decoded.progression(ProgressionTable.MAX_PERCENT));
        assertArrayEquals(converted.graphic(ClientGraphicBlock.SKILL_PAINT),
                decoded.graphic(ClientGraphicBlock.SKILL_PAINT));
        assertArrayEquals(converted.effectTemplates(), decoded.effectTemplates());
    }

    @Test
    void zeroPercentPreservesVersionAndMaxPercent() {
        DataAssetBundle bundle = ReferenceDataAssetConverter.convert(validDump(), 26, 0, progression());

        assertEquals(26, Byte.toUnsignedInt(bundle.version()));
        assertArrayEquals(new int[] {10, 20}, bundle.progression(ProgressionTable.MAX_PERCENT));
    }

    @Test
    void rejectsIncompleteProgressionSource() {
        EnumMap<ProgressionTable, int[]> tables = progression();
        tables.remove(ProgressionTable.GOLD_COST);

        assertThrows(NullPointerException.class,
                () -> ReferenceDataAssetConverter.convert(validDump(), 26, 0, tables));
    }

    @Test
    void rejectsVersionOverflowAfterPercentUpgrade() {
        assertThrows(IllegalArgumentException.class,
                () -> ReferenceDataAssetConverter.convert(validDump(), 255, 0.1, progression()));
    }

    private static EnumMap<ProgressionTable, int[]> progression() {
        EnumMap<ProgressionTable, int[]> tables = new EnumMap<>(ProgressionTable.class);
        for (ProgressionTable table : ProgressionTable.values()) {
            tables.put(table, table == ProgressionTable.MAX_PERCENT
                    ? new int[] {10, 20}
                    : new int[] {table.ordinal() + 1});
        }
        return tables;
    }

    private static String validDump() {
        String frame = "{\"status\":23,\"effS0Id\":24,\"e0dx\":25,\"e0dy\":26,"
                + "\"effS1Id\":27,\"e1dx\":28,\"e1dy\":29,\"effS2Id\":30,"
                + "\"e2dx\":31,\"e2dy\":32,\"arrowId\":33,\"adx\":34,\"ady\":35}";
        return String.join("\n",
                statement(ReferenceDataDumpInventoryParser.ARROW_MARKER, "(1, '[2,3,4]')"),
                statement(ReferenceDataDumpInventoryParser.EFFECT_PAINT_MARKER,
                        "(1, '[{\"imgId\":5,\"dx\":128,\"dy\":-1}]')"),
                statement(ReferenceDataDumpInventoryParser.IMAGE_MARKER, "(1, '[6,7,8,9,10]')"),
                statement(ReferenceDataDumpInventoryParser.PART_MARKER,
                        "(0, 2, '[{\"id\":11,\"dx\":12,\"dy\":-3}]')"),
                statement(ReferenceDataDumpInventoryParser.SKILL_PAINT_MARKER,
                        "(1, 20, 21, 22, '[" + frame + "]', '[]')"),
                statement(ReferenceDataDumpInventoryParser.TASK_MARKER, "(0, '[200,1]', '[2,255]')"),
                statement(ReferenceDataDumpInventoryParser.OTHERS_MARKER, "(1, 'exp', '[100,200]')"),
                statement(ReferenceDataDumpInventoryParser.EFFECT_TEMPLATE_MARKER,
                        "(200, 'Food', 255, 10)"));
    }

    private static String statement(String marker, String rows) {
        return marker + "\n" + rows + ";";
    }
}
