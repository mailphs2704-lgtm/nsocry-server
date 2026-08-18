package com.nsocry.assets.conversion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nsocry.assets.SkillAssetCodec;
import com.nsocry.assets.SkillAssetStructureValidator;
import java.util.List;
import org.junit.jupiter.api.Test;

class ReferenceSkillAssetConverterTest {
    @Test
    void convertsNormalizedSkillTreeInWireOrder() {
        SkillAssetConversionResult result = ReferenceSkillAssetConverter.convert((byte) 26, validDump());
        var bundle = result.bundle();

        assertEquals(26, bundle.version());
        assertEquals(List.of("Tấn công"), bundle.optionTemplateNames());
        assertEquals("Ninja Cry", bundle.classes().get(0).name());
        assertEquals("Chiêu Cry", bundle.classes().get(0).templates().get(0).name());
        assertEquals(10, bundle.classes().get(0).templates().get(0).levels().get(0)
                .options().get(0).parameter());
    }

    @Test
    void preservesUnsignedRawPointAsWireBits() {
        SkillAssetConversionResult result = ReferenceSkillAssetConverter.convert(
                (byte) 26, validDump().replace(", 1, 30, 18, '[", ", 150, 30, 18, '["));
        byte point = result.bundle().classes().get(0).templates().get(0).levels().get(0).point();

        assertEquals(150, Byte.toUnsignedInt(point));
        assertEquals(-106, point);
        assertEquals(1, result.report().rawByteDifferences().size());
    }

    @Test
    void producesCodecRoundTripCandidate() throws Exception {
        var converted = ReferenceSkillAssetConverter.convert((byte) 26, validDump()).bundle();

        var decoded = SkillAssetCodec.decode(SkillAssetCodec.encode(converted));
        assertEquals(converted, decoded);
    }

    @Test
    void producesStructurallyValidCandidate() {
        var bundle = ReferenceSkillAssetConverter.convert((byte) 26, validDump()).bundle();

        var report = SkillAssetStructureValidator.validate(bundle);
        assertEquals(1, report.skillTemplateCount());
        assertEquals(1, report.skillLevelCount());
        assertEquals(1, report.skillLevelOptionCount());
    }

    @Test
    void rejectsInvalidReferenceBeforeConversion() {
        assertThrows(IllegalArgumentException.class,
                () -> ReferenceSkillAssetConverter.convert((byte) 26, validDump().replace(
                        "(0, 0, 1, 10", "(0, 9, 1, 10")));
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
