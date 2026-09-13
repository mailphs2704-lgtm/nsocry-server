package com.nsocry.assets.conversion;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nsocry.assets.ClientGraphicBlock;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.EnumMap;
import org.junit.jupiter.api.Test;

class ReferenceDataWireEncoderTest {
    @Test
    void encodesArrowEffectImageAndPartBlocksExactly() {
        EnumMap<ClientGraphicBlock, byte[]> blocks = ReferenceDataWireEncoder.encodeGraphics(validDump());

        assertArrayEquals(bytes(0, 1, 0, 1, 0, 2, 0, 3, 0, 4), blocks.get(ClientGraphicBlock.ARROW));
        assertArrayEquals(bytes(0, 1, 0, 1, 1, 0, 5, 128, 255), blocks.get(ClientGraphicBlock.EFFECT));
        assertArrayEquals(bytes(0, 1, 6, 0, 7, 0, 8, 0, 9, 0, 10), blocks.get(ClientGraphicBlock.IMAGE));
        assertArrayEquals(bytes(0, 1, 2, 0, 11, 12, 253), blocks.get(ClientGraphicBlock.PART));
    }

    @Test
    void encodesEverySkillFrameFieldInReferenceOrder() throws Exception {
        byte[] payload = ReferenceDataWireEncoder.encodeGraphics(validDump()).get(ClientGraphicBlock.SKILL_PAINT);
        try (DataInputStream input = new DataInputStream(new ByteArrayInputStream(payload))) {
            assertEquals(1, input.readUnsignedShort());
            assertEquals(20, input.readShort());
            assertEquals(21, input.readShort());
            assertEquals(22, input.readUnsignedByte());
            assertEquals(1, input.readUnsignedByte());
            assertEquals(23, input.readUnsignedByte());
            for (int expected = 24; expected <= 35; expected++) assertEquals(expected, input.readShort());
            assertEquals(0, input.readUnsignedByte());
            assertEquals(0, input.available());
        }
    }

    @Test
    void encodesPartWithJsonSimpleMissingObjectComma() {
        String dump = validDump().replace(
                "{\"id\":11,\"dx\":12,\"dy\":-3}",
                "{\"id\":11,\"dx\":12\"dy\":-3}");

        assertArrayEquals(bytes(0, 1, 2, 0, 11, 12, 253),
                ReferenceDataWireEncoder.encodeGraphics(dump).get(ClientGraphicBlock.PART));
    }

    @Test
    void encodesEffectTemplateTailWithModifiedUtfContract() {
        assertArrayEquals(bytes(1, 200, 255, 0, 4, 70, 111, 111, 100, 0, 10),
                ReferenceDataWireEncoder.encodeEffectTemplates(validDump()));
    }

    @Test
    void narrowsReferenceEffectImageToLowSixteenBits() {
        String dump = validDump().replace("\"imgId\":5", "\"imgId\":260910");

        assertArrayEquals(bytes(0, 1, 0, 1, 1, 251, 46, 128, 255),
                ReferenceDataWireEncoder.encodeGraphics(dump).get(ClientGraphicBlock.EFFECT));
    }

    @Test
    void acceptsLegacyEffectImageIdAlias() {
        String dump = validDump().replace("{\"imgId\":5,\"dx\":128,\"dy\":-1}",
                "{\"id\":5,\"dx\":128,\"dy\":-1}");

        assertArrayEquals(bytes(0, 1, 0, 1, 1, 0, 5, 128, 255),
                ReferenceDataWireEncoder.encodeGraphics(dump).get(ClientGraphicBlock.EFFECT));
    }

    @Test
    void rejectsMissingSkillFrameField() {
        String dump = validDump().replace(",\"ady\":35", "");
        assertThrows(IllegalArgumentException.class, () -> ReferenceDataWireEncoder.encodeGraphics(dump));
    }

    @Test
    void rejectsValueOutsideWireRange() {
        String dump = validDump().replace("\"dx\":128", "\"dx\":256");
        assertThrows(IllegalArgumentException.class, () -> ReferenceDataWireEncoder.encodeGraphics(dump));
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
                statement(ReferenceDataDumpInventoryParser.TASK_MARKER, "(0, '[1]', '[2]')"),
                statement(ReferenceDataDumpInventoryParser.OTHERS_MARKER, "(1, 'exp', '[100]')"),
                statement(ReferenceDataDumpInventoryParser.EFFECT_TEMPLATE_MARKER,
                        "(200, 'Food', 255, 10)"));
    }

    private static String statement(String marker, String rows) {
        return marker + "\n" + rows + ";";
    }

    private static byte[] bytes(int... values) {
        byte[] result = new byte[values.length];
        for (int index = 0; index < values.length; index++) result[index] = (byte) values[index];
        return result;
    }
}
