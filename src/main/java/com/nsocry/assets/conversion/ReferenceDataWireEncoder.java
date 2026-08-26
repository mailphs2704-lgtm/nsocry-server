package com.nsocry.assets.conversion;

import com.nsocry.assets.ClientGraphicBlock;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Tái tạo năm graphics block và effect-template tail theo byte contract DATA client V7. */
public final class ReferenceDataWireEncoder {
    private static final int MAX_SIGNED_BYTE_COUNT = 127;

    private ReferenceDataWireEncoder() {
    }

    /** Parse dump đã kiểm kê và encode đủ năm graphics block theo đúng thứ tự wire. */
    public static EnumMap<ClientGraphicBlock, byte[]> encodeGraphics(String dump) {
        Objects.requireNonNull(dump, "dump");
        ReferenceDataDumpInventoryParser.parse(dump);
        EnumMap<ClientGraphicBlock, byte[]> blocks = new EnumMap<>(ClientGraphicBlock.class);
        blocks.put(ClientGraphicBlock.ARROW, encode(output -> writeArrows(output, dump)));
        blocks.put(ClientGraphicBlock.EFFECT, encode(output -> writeEffects(output, dump)));
        blocks.put(ClientGraphicBlock.IMAGE, encode(output -> writeImages(output, dump)));
        blocks.put(ClientGraphicBlock.PART, encode(output -> writeParts(output, dump)));
        blocks.put(ClientGraphicBlock.SKILL_PAINT, encode(output -> writeSkills(output, dump)));
        return blocks;
    }

    /** Encode tail effect-template: signed-byte count, id/type raw byte, UTF name và icon short. */
    public static byte[] encodeEffectTemplates(String dump) {
        Objects.requireNonNull(dump, "dump");
        ReferenceDataDumpInventoryParser.parse(dump);
        return encode(output -> {
            List<List<String>> rows = rows(dump, ReferenceDataDumpInventoryParser.EFFECT_TEMPLATE_MARKER, 4, "effect");
            requireSignedCount(rows.size(), "effect template count");
            output.writeByte(rows.size());
            for (List<String> row : rows) {
                output.writeByte(wireByte(integer(row.get(0), "effect id"), "effect id"));
                output.writeByte(wireByte(integer(row.get(2), "effect type"), "effect type"));
                output.writeUTF(row.get(1));
                output.writeShort(wireShort(integer(row.get(3), "effect icon"), "effect icon"));
            }
        });
    }

    private static void writeArrows(DataOutputStream output, String dump) throws IOException {
        List<List<String>> rows = rows(dump, ReferenceDataDumpInventoryParser.ARROW_MARKER, 2, "nj_arrow");
        output.writeShort(rows.size());
        for (List<String> row : rows) {
            output.writeShort(wireShort(integer(row.get(0), "arrow id"), "arrow id"));
            List<?> images = array(row.get(1), "nj_arrow.imgId");
            for (Object image : images) {
                output.writeShort(wireShort(number(image, "arrow image id"), "arrow image id"));
            }
        }
    }

    private static void writeEffects(DataOutputStream output, String dump) throws IOException {
        List<List<String>> rows = rows(dump, ReferenceDataDumpInventoryParser.EFFECT_PAINT_MARKER, 2, "nj_effect");
        output.writeShort(rows.size());
        for (List<String> row : rows) {
            output.writeShort(wireShort(integer(row.get(0), "effect paint id"), "effect paint id"));
            List<?> frames = array(row.get(1), "nj_effect.info");
            requireSignedCount(frames.size(), "effect frame count");
            output.writeByte(frames.size());
            for (Object frame : frames) {
                Map<?, ?> object = object(frame, "effect frame");
                Object image = object.containsKey("imgId") ? object.get("imgId") : required(object, "id");
                output.writeShort(wireShort(number(image, "effect image id"), "effect image id"));
                output.writeByte(wireByte(number(required(object, "dx"), "effect dx"), "effect dx"));
                output.writeByte(wireByte(number(required(object, "dy"), "effect dy"), "effect dy"));
            }
        }
    }

    private static void writeImages(DataOutputStream output, String dump) throws IOException {
        List<List<String>> rows = rows(dump, ReferenceDataDumpInventoryParser.IMAGE_MARKER, 2, "nj_image");
        output.writeShort(rows.size());
        for (List<String> row : rows) {
            List<?> values = array(row.get(1), "nj_image.smallImage");
            output.writeByte(wireByte(number(values.get(0), "small image sheet"), "small image sheet"));
            for (int index = 1; index < values.size(); index++) {
                output.writeShort(wireShort(
                        number(values.get(index), "small image coordinate"), "small image coordinate"));
            }
        }
    }

    private static void writeParts(DataOutputStream output, String dump) throws IOException {
        List<List<String>> rows = rows(dump, ReferenceDataDumpInventoryParser.PART_MARKER, 3, "nj_part");
        output.writeShort(rows.size());
        for (List<String> row : rows) {
            output.writeByte(wireByte(integer(row.get(1), "part type"), "part type"));
            for (Object frame : array(row.get(2), "nj_part.part")) {
                Map<?, ?> object = object(frame, "part frame");
                output.writeShort(wireShort(number(required(object, "id"), "part image id"), "part image id"));
                output.writeByte(wireByte(number(required(object, "dx"), "part dx"), "part dx"));
                output.writeByte(wireByte(number(required(object, "dy"), "part dy"), "part dy"));
            }
        }
    }

    private static void writeSkills(DataOutputStream output, String dump) throws IOException {
        List<List<String>> rows = rows(dump, ReferenceDataDumpInventoryParser.SKILL_PAINT_MARKER, 6, "nj_skill");
        output.writeShort(rows.size());
        for (List<String> row : rows) {
            output.writeShort(wireShort(integer(row.get(1), "skill id"), "skill id"));
            output.writeShort(wireShort(integer(row.get(2), "skill effect id"), "skill effect id"));
            output.writeByte(wireByte(integer(row.get(3), "skill effect count"), "skill effect count"));
            writeSkillFrames(output, array(row.get(4), "skillStand"), "skillStand");
            writeSkillFrames(output, array(row.get(5), "skillFly"), "skillFly");
        }
    }

    private static void writeSkillFrames(DataOutputStream output, List<?> frames, String field) throws IOException {
        requireSignedCount(frames.size(), field + " count");
        output.writeByte(frames.size());
        for (Object frame : frames) {
            Map<?, ?> value = object(frame, field + " frame");
            output.writeByte(wireByte(number(required(value, "status"), field + ".status"), field + ".status"));
            writeShort(output, value, "effS0Id", field);
            writeShort(output, value, "e0dx", field);
            writeShort(output, value, "e0dy", field);
            writeShort(output, value, "effS1Id", field);
            writeShort(output, value, "e1dx", field);
            writeShort(output, value, "e1dy", field);
            writeShort(output, value, "effS2Id", field);
            writeShort(output, value, "e2dx", field);
            writeShort(output, value, "e2dy", field);
            writeShort(output, value, "arrowId", field);
            writeShort(output, value, "adx", field);
            writeShort(output, value, "ady", field);
        }
    }

    private static void writeShort(DataOutputStream output, Map<?, ?> value, String key, String field)
            throws IOException {
        output.writeShort(wireShort(number(required(value, key), field + "." + key), field + "." + key));
    }

    private static List<List<String>> rows(String dump, String marker, int arity, String table) {
        return ReferenceDataDumpInventoryParser.rows(dump, marker, arity, table);
    }

    private static List<?> array(String json, String field) {
        return ReferenceDataDumpInventoryParser.array(json, field);
    }

    private static Map<?, ?> object(Object value, String field) {
        return ReferenceDataDumpInventoryParser.object(value, field);
    }

    private static Object required(Map<?, ?> object, String key) {
        return ReferenceDataDumpInventoryParser.required(object, key);
    }

    private static long number(Object value, String field) {
        return ReferenceDataDumpInventoryParser.number(value, field);
    }

    private static int integer(String value, String field) {
        return ReferenceDataDumpInventoryParser.integer(value, field);
    }

    private static void requireSignedCount(int count, String field) {
        if (count > MAX_SIGNED_BYTE_COUNT) {
            throw new IllegalArgumentException(field + " vượt giới hạn 127");
        }
    }

    private static int wireByte(long value, String field) {
        if (value < Byte.MIN_VALUE || value > 255) {
            throw new IllegalArgumentException(field + " vượt raw byte: " + value);
        }
        return (int) value;
    }

    private static int wireShort(long value, String field) {
        if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
            throw new IllegalArgumentException(field + " vượt short: " + value);
        }
        return (int) value;
    }

    private static byte[] encode(WireWriter writer) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            try (DataOutputStream output = new DataOutputStream(buffer)) {
                writer.write(output);
            }
            return buffer.toByteArray();
        } catch (IOException exception) {
            throw new UncheckedIOException("Không thể encode DATA wire block", exception);
        }
    }

    @FunctionalInterface
    private interface WireWriter {
        void write(DataOutputStream output) throws IOException;
    }
}
