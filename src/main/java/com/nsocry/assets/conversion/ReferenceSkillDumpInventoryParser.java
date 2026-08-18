package com.nsocry.assets.conversion;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Parse và đối chiếu inventory SKILL tham chiếu mà chưa tạo seed hoặc truy cập database. */
public final class ReferenceSkillDumpInventoryParser {
    private static final String CLASS_MARKER = "INSERT INTO `clazz` (`id`, `name`) VALUES";
    private static final String OPTION_MARKER = "INSERT INTO `skill_option` (`id`, `name`) VALUES";
    private static final String TEMPLATE_MARKER = "INSERT INTO `skill_template` (`id`, `class`, `name`, `max_point`, `type`, `icon`, `description`, `skillTemplates`) VALUES";
    private static final String LEVEL_MARKER = "INSERT INTO `skill` (`id`, `template_id`, `max_fight`, `level`, `mana_use`, `cooldown`, `point`, `dx`, `dy`, `options`) VALUES";
    private static final Pattern OPTION = Pattern.compile("\\{\\\"param\\\":(-?\\d+),\\\"id\\\":(-?\\d+)\\}");

    private ReferenceSkillDumpInventoryParser() {
    }

    /** Kiểm tra count, ID liên tục và mọi reference class/template/option trong dump. */
    public static SkillDumpInventoryReport parse(String dump) {
        Objects.requireNonNull(dump, "dump");
        List<List<String>> classes = ReferenceItemSqlDumpParser.parseValues(dump, CLASS_MARKER);
        List<List<String>> options = ReferenceItemSqlDumpParser.parseValues(dump, OPTION_MARKER);
        List<List<String>> templates = ReferenceItemSqlDumpParser.parseValues(dump, TEMPLATE_MARKER);
        List<List<String>> levels = ReferenceItemSqlDumpParser.parseValues(dump, LEVEL_MARKER);
        requireMaximum(classes.size(), 255, "class count");
        requireMaximum(options.size(), 127, "skill option count");
        requireMaximum(templates.size(), 127, "skill template count");
        requireMaximum(levels.size(), 32_768, "skill level count");
        requireSequential(classes, 2, "class");
        requireSequential(options, 2, "skill option");
        requireSequential(templates, 8, "skill template");
        requireSequential(levels, 10, "skill level");

        Set<Integer> classIds = ids(classes);
        Set<Integer> templateIds = ids(templates);
        int signedByteOverflowCount = 0;
        for (List<String> template : templates) {
            int classId = integer(template.get(1), "template class");
            if (!classIds.contains(classId)) {
                throw new IllegalArgumentException("skill template tham chiếu class không tồn tại");
            }
            signedByteOverflowCount += checkedWireByte(integer(template.get(3), "max point"), "max point");
            signedByteOverflowCount += checkedWireByte(integer(template.get(4), "template type"), "template type");
            checkedShort(integer(template.get(5), "template icon"), "template icon");
        }

        int levelOptionCount = 0;
        int maximumOptions = 0;
        for (List<String> level : levels) {
            int templateId = integer(level.get(1), "level template");
            if (!templateIds.contains(templateId)) {
                throw new IllegalArgumentException("skill level tham chiếu template không tồn tại");
            }
            signedByteOverflowCount += checkedWireByte(integer(level.get(2), "max fight"), "max fight");
            signedByteOverflowCount += checkedWireByte(integer(level.get(3), "required level"), "required level");
            checkedShort(integer(level.get(4), "mana use"), "mana use");
            integer(level.get(5), "cooldown");
            signedByteOverflowCount += checkedWireByte(integer(level.get(6), "point"), "point");
            checkedShort(integer(level.get(7), "dx"), "dx");
            checkedShort(integer(level.get(8), "dy"), "dy");
            int count = validateOptions(level.get(9), options.size());
            requireMaximum(count, 127, "level option count");
            levelOptionCount += count;
            maximumOptions = Math.max(maximumOptions, count);
        }
        return new SkillDumpInventoryReport(
                classes.size(), options.size(), templates.size(), levels.size(), levelOptionCount,
                templates.isEmpty() ? 0 : 0, templates.isEmpty() ? 0 : templates.size() - 1,
                levels.isEmpty() ? 0 : 0, levels.isEmpty() ? 0 : levels.size() - 1,
                maximumOptions, signedByteOverflowCount);
    }

    /** Parse chính xác array option JSON có schema param/id và kiểm tra reference. */
    private static int validateOptions(String json, int optionCount) {
        if (!json.startsWith("[") || !json.endsWith("]")) {
            throw new IllegalArgumentException("skill options không phải JSON array");
        }
        String body = json.substring(1, json.length() - 1);
        if (body.isEmpty()) return 0;
        Matcher matcher = OPTION.matcher(body);
        int end = 0;
        int count = 0;
        while (matcher.find()) {
            String separator = body.substring(end, matcher.start());
            if (!separator.isEmpty() && !separator.equals(",")) {
                throw new IllegalArgumentException("skill option JSON ngoài schema");
            }
            checkedShort(integer(matcher.group(1), "option parameter"), "option parameter");
            int optionId = integer(matcher.group(2), "option id");
            if (optionId < 0 || optionId >= optionCount) {
                throw new IllegalArgumentException("skill option id không tồn tại");
            }
            count++;
            end = matcher.end();
        }
        if (count == 0 || end != body.length()) {
            throw new IllegalArgumentException("skill option JSON ngoài schema");
        }
        return count;
    }

    private static Set<Integer> ids(List<List<String>> rows) {
        Set<Integer> values = new HashSet<>();
        for (List<String> row : rows) values.add(integer(row.get(0), "id"));
        return values;
    }

    private static void requireSequential(List<List<String>> rows, int arity, String name) {
        for (int index = 0; index < rows.size(); index++) {
            List<String> row = rows.get(index);
            if (row.size() != arity || integer(row.get(0), name + " id") != index) {
                throw new IllegalArgumentException(name + " row/ID không liên tục từ 0");
            }
        }
    }

    private static int integer(String value, String name) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(name + " không phải integer", exception);
        }
    }

    private static void requireMaximum(int count, int maximum, String name) {
        if (count > maximum) throw new IllegalArgumentException(name + " vượt giới hạn " + maximum);
    }

    private static int checkedWireByte(int value, String name) {
        if (value < Byte.MIN_VALUE || value > 255) {
            throw new IllegalArgumentException(name + " vượt raw byte");
        }
        return value > Byte.MAX_VALUE ? 1 : 0;
    }

    private static short checkedShort(int value, String name) {
        if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
            throw new IllegalArgumentException(name + " vượt short");
        }
        return (short) value;
    }
}
