package com.nsocry.assets.conversion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
