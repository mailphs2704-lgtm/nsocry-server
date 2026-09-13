package com.nsocry.assets.conversion;

import com.nsocry.assets.SkillAssetBundle;
import com.nsocry.assets.SkillClassAsset;
import com.nsocry.assets.SkillLevelAsset;
import com.nsocry.assets.SkillLevelOptionAsset;
import com.nsocry.assets.SkillTemplateAsset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Chuyển bốn nguồn SKILL trong dump thành read model đúng thứ tự wire client V7. */
public final class ReferenceSkillAssetConverter {
    private static final String CLASS_MARKER = "INSERT INTO `clazz` (`id`, `name`) VALUES";
    private static final String OPTION_MARKER = "INSERT INTO `skill_option` (`id`, `name`) VALUES";
    private static final String TEMPLATE_MARKER = "INSERT INTO `skill_template` (`id`, `class`, `name`, `max_point`, `type`, `icon`, `description`, `skillTemplates`) VALUES";
    private static final String LEVEL_MARKER = "INSERT INTO `skill` (`id`, `template_id`, `max_fight`, `level`, `mana_use`, `cooldown`, `point`, `dx`, `dy`, `options`) VALUES";
    private static final Pattern OPTION = Pattern.compile("\\{\\\"param\\\":(-?\\d+),\\\"id\\\":(-?\\d+)\\}");

    private ReferenceSkillAssetConverter() {
    }

    /** Validate toàn dump trước, sau đó dựng cây class → template → level → option. */
    public static SkillAssetConversionResult convert(byte version, String dump) {
        Objects.requireNonNull(dump, "dump");
        SkillDumpInventoryReport report = ReferenceSkillDumpInventoryParser.parse(dump);
        List<List<String>> classRows = ReferenceItemSqlDumpParser.parseValues(dump, CLASS_MARKER);
        List<List<String>> optionRows = ReferenceItemSqlDumpParser.parseValues(dump, OPTION_MARKER);
        List<List<String>> templateRows = ReferenceItemSqlDumpParser.parseValues(dump, TEMPLATE_MARKER);
        List<List<String>> levelRows = ReferenceItemSqlDumpParser.parseValues(dump, LEVEL_MARKER);

        List<String> optionNames = optionRows.stream().map(row -> row.get(1)).toList();
        List<SkillClassAsset> classes = new ArrayList<>(classRows.size());
        for (List<String> classRow : classRows) {
            int classId = integer(classRow.get(0), "class id");
            List<SkillTemplateAsset> templates = new ArrayList<>();
            for (List<String> templateRow : templateRows) {
                if (integer(templateRow.get(1), "template class") == classId) {
                    templates.add(template(templateRow, levelRows));
                }
            }
            classes.add(new SkillClassAsset(classRow.get(1), templates));
        }
        return new SkillAssetConversionResult(new SkillAssetBundle(version, optionNames, classes), report);
    }

    /** Dựng template; cột skillTemplates cũ là cache JSON và không phải nguồn authoritative. */
    private static SkillTemplateAsset template(List<String> row, List<List<String>> levelRows) {
        int templateId = integer(row.get(0), "template id");
        List<SkillLevelAsset> levels = new ArrayList<>();
        for (List<String> levelRow : levelRows) {
            if (integer(levelRow.get(1), "level template") == templateId) {
                levels.add(level(levelRow));
            }
        }
        return new SkillTemplateAsset(
                checkedSignedByte(templateId, "template id"), row.get(2),
                rawByte(integer(row.get(3), "max point"), "max point"),
                rawByte(integer(row.get(4), "template type"), "template type"),
                checkedShort(integer(row.get(5), "template icon"), "template icon"),
                row.get(6), levels);
    }

    /** Dựng level và giữ nguyên bit pattern của raw byte 128–255 khi ghi wire. */
    private static SkillLevelAsset level(List<String> row) {
        return new SkillLevelAsset(
                checkedShort(integer(row.get(0), "level id"), "level id"),
                rawByte(integer(row.get(6), "point"), "point"),
                rawByte(integer(row.get(3), "required level"), "required level"),
                checkedShort(integer(row.get(4), "mana use"), "mana use"),
                integer(row.get(5), "cooldown"),
                checkedShort(integer(row.get(7), "dx"), "dx"),
                checkedShort(integer(row.get(8), "dy"), "dy"),
                rawByte(integer(row.get(2), "max fight"), "max fight"),
                options(row.get(9)));
    }

    private static List<SkillLevelOptionAsset> options(String json) {
        List<SkillLevelOptionAsset> options = new ArrayList<>();
        Matcher matcher = OPTION.matcher(json);
        while (matcher.find()) {
            options.add(new SkillLevelOptionAsset(
                    checkedShort(integer(matcher.group(1), "option parameter"), "option parameter"),
                    checkedSignedByte(integer(matcher.group(2), "option id"), "option id")));
        }
        return options;
    }

    private static int integer(String value, String name) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(name + " không phải integer", exception);
        }
    }

    private static byte checkedSignedByte(int value, String name) {
        if (value < 0 || value > Byte.MAX_VALUE) {
            throw new IllegalArgumentException(name + " vượt signed byte không âm: " + value);
        }
        return (byte) value;
    }

    private static byte rawByte(int value, String name) {
        if (value < Byte.MIN_VALUE || value > 255) {
            throw new IllegalArgumentException(name + " vượt raw byte: " + value);
        }
        return (byte) value;
    }

    private static short checkedShort(int value, String name) {
        if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
            throw new IllegalArgumentException(name + " vượt short: " + value);
        }
        return (short) value;
    }
}
