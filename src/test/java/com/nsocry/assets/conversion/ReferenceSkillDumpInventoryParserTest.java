package com.nsocry.assets.conversion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class ReferenceSkillDumpInventoryParserTest {
    @Test
    void inventoriesValidatedSkillRelationsAndOptions() {
        SkillDumpInventoryReport report = ReferenceSkillDumpInventoryParser.parse(validDump());

        assertEquals(1, report.classCount());
        assertEquals(1, report.optionTemplateCount());
        assertEquals(1, report.skillTemplateCount());
        assertEquals(1, report.skillLevelCount());
        assertEquals(1, report.skillLevelOptionCount());
        assertEquals(1, report.maximumOptionsPerLevel());
        assertEquals(0, report.signedByteOverflowValueCount());
        assertEquals(List.of(), report.rawByteDifferences());
    }

    @Test
    void rejectsTemplateReferencingMissingClass() {
        assertThrows(IllegalArgumentException.class,
                () -> ReferenceSkillDumpInventoryParser.parse(validDump().replace(
                        "(0, 0, 'Chiêu Cry'", "(0, 9, 'Chiêu Cry'")));
    }

    @Test
    void rejectsLevelOptionReferencingMissingTemplate() {
        assertThrows(IllegalArgumentException.class,
                () -> ReferenceSkillDumpInventoryParser.parse(validDump().replace(
                        "[{\"param\":10,\"id\":0}]", "[{\"param\":10,\"id\":9}]")));
    }

    @Test
    void rejectsOptionJsonOutsideExpectedSchema() {
        assertThrows(IllegalArgumentException.class,
                () -> ReferenceSkillDumpInventoryParser.parse(validDump().replace(
                        "[{\"param\":10,\"id\":0}]", "[{\"id\":0,\"param\":10}]")));
    }

    @Test
    void rejectsGapInImplicitIds() {
        assertThrows(IllegalArgumentException.class,
                () -> ReferenceSkillDumpInventoryParser.parse(validDump().replace(
                        "(0, 'Tấn công')", "(1, 'Tấn công')")));
    }

    @Test
    void capturesLevelPointOutsideSignedByteRange() {
        SkillDumpInventoryReport report = ReferenceSkillDumpInventoryParser.parse(
                validDump().replace(", 1, 30, 18, '[", ", 140, 30, 18, '["));

        assertEquals(List.of(new SkillRawByteDifference("level", 0, "point", 140)),
                report.rawByteDifferences());
    }

    @Test
    void capturesLevelRequirementOutsideSignedByteRange() {
        SkillDumpInventoryReport report = ReferenceSkillDumpInventoryParser.parse(
                validDump().replace("(0, 0, 1, 10, 20", "(0, 0, 1, 200, 20"));

        assertEquals(List.of(new SkillRawByteDifference("level", 0, "requiredLevel", 200)),
                report.rawByteDifferences());
    }

    @Test
    void capturesTemplateMaxPointOutsideSignedByteRange() {
        SkillDumpInventoryReport report = ReferenceSkillDumpInventoryParser.parse(
                validDump().replace("'Chiêu Cry', 12, 1", "'Chiêu Cry', 130, 1"));

        assertEquals(List.of(new SkillRawByteDifference("template", 0, "maxPoint", 130)),
                report.rawByteDifferences());
    }

    @Test
    void rejectsValueOutsideRawByteRange() {
        assertThrows(IllegalArgumentException.class,
                () -> ReferenceSkillDumpInventoryParser.parse(
                        validDump().replace(", 1, 30, 18, '[", ", 256, 30, 18, '[")));
    }

    @Test
    void reportCopiesDifferenceListAndValidatesCount() {
        List<SkillRawByteDifference> differences = new ArrayList<>();
        differences.add(new SkillRawByteDifference("level", 0, "point", 140));
        SkillDumpInventoryReport report = new SkillDumpInventoryReport(
                1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, differences);

        differences.clear();
        assertEquals(1, report.rawByteDifferences().size());
        assertThrows(IllegalArgumentException.class,
                () -> new SkillDumpInventoryReport(1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0,
                        report.rawByteDifferences()));
    }

    private static String validDump() {
        return """
                INSERT INTO `clazz` (`id`, `name`) VALUES
                (0, 'Ninja Cry');
                INSERT INTO `skill_option` (`id`, `name`) VALUES
                (0, 'Tấn công');
                INSERT INTO `skill_template` (`id`, `class`, `name`, `max_point`, `type`, `icon`, `description`, `skillTemplates`) VALUES
                (0, 0, 'Chiêu Cry', 12, 1, 318, 'Mô tả', '[]');
                INSERT INTO `skill` (`id`, `template_id`, `max_fight`, `level`, `mana_use`, `cooldown`, `point`, `dx`, `dy`, `options`) VALUES
                (0, 0, 1, 10, 20, 500, 1, 30, 18, '[{\"param\":10,\"id\":0}]');
                """;
    }
}
