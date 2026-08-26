package com.nsocry.assets.conversion;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Kiểm kê tám nguồn SQL authoritative của DATA mà không thực thi dump. */
public final class ReferenceDataDumpInventoryParser {
    static final String ARROW_MARKER = "INSERT INTO `nj_arrow` (`id`, `imgId`) VALUES";
    static final String EFFECT_PAINT_MARKER = "INSERT INTO `nj_effect` (`id`, `info`) VALUES";
    static final String IMAGE_MARKER = "INSERT INTO `nj_image` (`id`, `smallImage`) VALUES";
    static final String PART_MARKER = "INSERT INTO `nj_part` (`id`, `type`, `part`) VALUES";
    static final String SKILL_PAINT_MARKER = "INSERT INTO `nj_skill` (`id`, `skillId`, `effId`, `numEff`, `skillStand`, `skillFly`) VALUES";
    static final String TASK_MARKER = "INSERT INTO `task` (`id`, `npcs`, `maps`) VALUES";
    static final String OTHERS_MARKER = "INSERT INTO `others` (`id`, `name`, `value`) VALUES";
    static final String EFFECT_TEMPLATE_MARKER = "INSERT INTO `effect` (`id`, `name`, `type`, `icon`) VALUES";
    private static final int MAX_SIGNED_BYTE_COUNT = 127;

    private ReferenceDataDumpInventoryParser() {
    }

    /** Parse đúng tám statement, kiểm tra wire-boundary và trả count phục vụ converter. */
    public static DataDumpInventoryReport parse(String dump) {
        Objects.requireNonNull(dump, "dump");
        List<List<String>> arrows = rows(dump, ARROW_MARKER, 2, "nj_arrow");
        List<List<String>> effects = rows(dump, EFFECT_PAINT_MARKER, 2, "nj_effect");
        List<List<String>> images = rows(dump, IMAGE_MARKER, 2, "nj_image");
        List<List<String>> parts = rows(dump, PART_MARKER, 3, "nj_part");
        List<List<String>> skills = rows(dump, SKILL_PAINT_MARKER, 6, "nj_skill");
        List<List<String>> tasks = rows(dump, TASK_MARKER, 3, "task");
        List<List<String>> others = rows(dump, OTHERS_MARKER, 3, "others");
        List<List<String>> templates = rows(dump, EFFECT_TEMPLATE_MARKER, 4, "effect");

        requireSignedCount(tasks.size(), "task group count");
        requireSignedCount(templates.size(), "effect template count");
        requireAscendingIds(arrows, "nj_arrow");
        requireAscendingIds(effects, "nj_effect");
        requireAscendingIds(images, "nj_image");
        requireAscendingIds(parts, "nj_part");
        requireAscendingIds(skills, "nj_skill");
        requireAscendingIds(tasks, "task");
        requireAscendingIds(templates, "effect");

        int rawDifferences = 0;
        for (List<String> row : arrows) {
            List<?> imageIds = array(row.get(1), "nj_arrow.imgId");
            requireSize(imageIds, 3, "nj_arrow.imgId");
            for (Object value : imageIds) checkedShort(number(value, "arrow image id"), "arrow image id");
        }
        for (List<String> row : effects) {
            List<?> frames = array(row.get(1), "nj_effect.info");
            requireSignedCount(frames.size(), "effect frame count");
            for (Object frame : frames) {
                Map<?, ?> object = object(frame, "effect frame");
                Object image = object.containsKey("imgId") ? object.get("imgId") : object.get("id");
                if (image == null) throw error("effect frame thiếu imgId/id");
                checkedShort(number(image, "effect image id"), "effect image id");
                rawDifferences += rawByte(number(required(object, "dx"), "effect dx"), "effect dx");
                rawDifferences += rawByte(number(required(object, "dy"), "effect dy"), "effect dy");
            }
        }
        for (List<String> row : images) {
            List<?> values = array(row.get(1), "nj_image.smallImage");
            requireSize(values, 5, "nj_image.smallImage");
            for (Object value : values) number(value, "small image value");
        }
        for (List<String> row : parts) {
            rawDifferences += rawByte(integer(row.get(1), "part type"), "part type");
            List<?> frames = array(row.get(2), "nj_part.part");
            requireSignedCount(frames.size(), "part frame count");
            for (Object frame : frames) {
                Map<?, ?> object = object(frame, "part frame");
                checkedShort(number(required(object, "id"), "part image id"), "part image id");
                rawDifferences += rawByte(number(required(object, "dx"), "part dx"), "part dx");
                rawDifferences += rawByte(number(required(object, "dy"), "part dy"), "part dy");
            }
        }
        for (List<String> row : skills) {
            checkedShort(integer(row.get(1), "skill id"), "skill id");
            checkedShort(integer(row.get(2), "skill effect id"), "skill effect id");
            rawDifferences += rawByte(integer(row.get(3), "skill effect count"), "skill effect count");
            requireSignedCount(array(row.get(4), "skillStand").size(), "skillStand count");
            requireSignedCount(array(row.get(5), "skillFly").size(), "skillFly count");
        }
        for (List<String> row : tasks) {
            List<?> npcs = array(row.get(1), "task.npcs");
            List<?> maps = array(row.get(2), "task.maps");
            if (npcs.size() != maps.size()) throw error("task npcs/maps lệch chiều dài");
            requireSignedCount(npcs.size(), "task route count");
            for (Object value : npcs) rawDifferences += rawByte(number(value, "task npc"), "task npc");
            for (Object value : maps) rawDifferences += rawByte(number(value, "task map"), "task map");
        }

        List<?> experience = null;
        for (List<String> row : others) {
            if ("exp".equals(row.get(1))) {
                if (experience != null) throw error("others phải có đúng một row exp");
                experience = array(row.get(2), "others.exp");
                for (Object value : experience) number(value, "experience");
            }
        }
        if (experience == null) throw error("others thiếu row exp");
        requireSignedCount(experience.size(), "experience count");

        for (List<String> row : templates) {
            rawDifferences += rawByte(integer(row.get(0), "effect id"), "effect id");
            rawDifferences += rawByte(integer(row.get(2), "effect type"), "effect type");
            checkedShort(integer(row.get(3), "effect icon"), "effect icon");
        }
        return new DataDumpInventoryReport(
                arrows.size(), effects.size(), images.size(), parts.size(), skills.size(),
                tasks.size(), experience.size(), templates.size(), rawDifferences);
    }

    private static List<List<String>> rows(String dump, String marker, int arity, String table) {
        List<List<String>> rows = ReferenceItemSqlDumpParser.parseValues(dump, marker);
        for (List<String> row : rows) {
            if (row.size() != arity) throw error(table + " row phải có " + arity + " cột");
        }
        return rows;
    }

    private static void requireAscendingIds(List<List<String>> rows, String table) {
        int previous = Integer.MIN_VALUE;
        for (List<String> row : rows) {
            int id = integer(row.get(0), table + " id");
            if (id <= previous) throw error(table + " id phải tăng nghiêm ngặt theo thứ tự dump");
            previous = id;
        }
    }

    private static List<?> array(String json, String field) {
        Object value = new JsonParser(json).parse();
        if (!(value instanceof List<?> list)) throw error(field + " phải là JSON array");
        return list;
    }

    private static Map<?, ?> object(Object value, String field) {
        if (!(value instanceof Map<?, ?> map)) throw error(field + " phải là JSON object");
        return map;
    }

    private static Object required(Map<?, ?> object, String key) {
        Object value = object.get(key);
        if (value == null) throw error("JSON object thiếu field " + key);
        return value;
    }

    private static long number(Object value, String field) {
        if (!(value instanceof Long number)) throw error(field + " phải là integer JSON");
        return number;
    }

    private static int integer(String value, String field) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(field + " không phải integer: " + value, exception);
        }
    }

    private static int rawByte(long value, String field) {
        if (value < Byte.MIN_VALUE || value > 255) throw error(field + " vượt raw byte: " + value);
        return value > Byte.MAX_VALUE ? 1 : 0;
    }

    private static void checkedShort(long value, String field) {
        if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) throw error(field + " vượt short: " + value);
    }

    private static void requireSignedCount(int count, String field) {
        if (count > MAX_SIGNED_BYTE_COUNT) throw error(field + " vượt giới hạn 127");
    }

    private static void requireSize(List<?> values, int expected, String field) {
        if (values.size() != expected) throw error(field + " phải có " + expected + " phần tử");
    }

    private static IllegalArgumentException error(String message) {
        return new IllegalArgumentException(message);
    }

    /** JSON parser giới hạn cho integer, string, array và object trong dump authoritative. */
    private static final class JsonParser {
        private final String source;
        private int index;

        JsonParser(String source) {
            this.source = Objects.requireNonNull(source, "json");
        }

        Object parse() {
            Object value = value();
            whitespace();
            if (index != source.length()) throw failure("JSON có byte dư");
            return value;
        }

        private Object value() {
            whitespace();
            if (index >= source.length()) throw failure("JSON thiếu value");
            return switch (source.charAt(index)) {
                case '[' -> array();
                case '{' -> object();
                case '"' -> string();
                default -> integer();
            };
        }

        private List<Object> array() {
            take('[');
            List<Object> values = new ArrayList<>();
            whitespace();
            if (takeIf(']')) return values;
            while (true) {
                values.add(value());
                whitespace();
                if (takeIf(']')) return values;
                take(',');
            }
        }

        private Map<String, Object> object() {
            take('{');
            Map<String, Object> values = new LinkedHashMap<>();
            whitespace();
            if (takeIf('}')) return values;
            while (true) {
                String key = string();
                whitespace();
                take(':');
                if (values.put(key, value()) != null) throw failure("JSON object trùng key");
                whitespace();
                if (takeIf('}')) return values;
                take(',');
            }
        }

        private String string() {
            whitespace();
            take('"');
            StringBuilder value = new StringBuilder();
            while (index < source.length()) {
                char current = source.charAt(index++);
                if (current == '"') return value.toString();
                if (current != '\\') {
                    if (current <= 0x1f) throw failure("JSON string có control character");
                    value.append(current);
                    continue;
                }
                if (index >= source.length()) throw failure("JSON escape thiếu ký tự");
                char escaped = source.charAt(index++);
                value.append(switch (escaped) {
                    case '"', '\\', '/' -> escaped;
                    case 'b' -> '\b';
                    case 'f' -> '\f';
                    case 'n' -> '\n';
                    case 'r' -> '\r';
                    case 't' -> '\t';
                    default -> throw failure("JSON escape không hỗ trợ");
                });
            }
            throw failure("JSON string chưa đóng");
        }

        private Long integer() {
            int start = index;
            if (takeIf('-') && index == source.length()) throw failure("JSON integer thiếu chữ số");
            while (index < source.length() && Character.isDigit(source.charAt(index))) index++;
            if (start == index || (source.charAt(start) == '-' && start + 1 == index)) {
                throw failure("JSON chỉ cho phép integer");
            }
            try {
                return Long.parseLong(source.substring(start, index));
            } catch (NumberFormatException exception) {
                throw failure("JSON integer vượt long");
            }
        }

        private boolean takeIf(char expected) {
            if (index < source.length() && source.charAt(index) == expected) {
                index++;
                return true;
            }
            return false;
        }

        private void take(char expected) {
            whitespace();
            if (!takeIf(expected)) throw failure("JSON cần ký tự " + expected);
        }

        private void whitespace() {
            while (index < source.length() && Character.isWhitespace(source.charAt(index))) index++;
        }

        private IllegalArgumentException failure(String message) {
            return error(message + " tại offset " + index);
        }
    }
}
