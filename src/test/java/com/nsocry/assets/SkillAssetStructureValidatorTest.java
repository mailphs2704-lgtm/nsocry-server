package com.nsocry.assets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class SkillAssetStructureValidatorTest {
    @Test
    void validatesNestedReferencesAndReturnsCounts() {
        SkillAssetValidationReport report = SkillAssetStructureValidator.validate(validBundle());

        assertEquals(1, report.optionTemplateCount());
        assertEquals(1, report.classCount());
        assertEquals(1, report.skillTemplateCount());
        assertEquals(1, report.skillLevelCount());
        assertEquals(1, report.skillLevelOptionCount());
    }

    @Test
    void rejectsDuplicateTemplateIdAcrossClasses() {
        SkillTemplateAsset template = template((byte) 3, (short) 100, (byte) 0);
        SkillAssetBundle bundle = new SkillAssetBundle((byte) 26, List.of("Tấn công"), List.of(
                new SkillClassAsset("Kiếm", List.of(template)),
                new SkillClassAsset("Cung", List.of(template))));

        assertThrows(IllegalArgumentException.class, () -> SkillAssetStructureValidator.validate(bundle));
    }

    @Test
    void rejectsDuplicateLevelId() {
        SkillTemplateAsset first = template((byte) 3, (short) 100, (byte) 0);
        SkillTemplateAsset second = template((byte) 4, (short) 100, (byte) 0);
        SkillAssetBundle bundle = new SkillAssetBundle((byte) 26, List.of("Tấn công"),
                List.of(new SkillClassAsset("Kiếm", List.of(first, second))));

        assertThrows(IllegalArgumentException.class, () -> SkillAssetStructureValidator.validate(bundle));
    }

    @Test
    void rejectsReferenceToMissingOptionTemplate() {
        SkillAssetBundle bundle = new SkillAssetBundle((byte) 26, List.of("Tấn công"),
                List.of(new SkillClassAsset("Kiếm", List.of(template((byte) 3, (short) 100, (byte) 1)))));

        assertThrows(IllegalArgumentException.class, () -> SkillAssetStructureValidator.validate(bundle));
    }

    @Test
    void rejectsSignedCountOverflow() {
        List<String> options = new ArrayList<>();
        for (int index = 0; index < 128; index++) {
            options.add("option-" + index);
        }
        SkillAssetBundle bundle = new SkillAssetBundle((byte) 26, options, List.of());

        assertThrows(IllegalArgumentException.class, () -> SkillAssetStructureValidator.validate(bundle));
    }

    private static SkillAssetBundle validBundle() {
        return new SkillAssetBundle((byte) 26, List.of("Tấn công"),
                List.of(new SkillClassAsset("Kiếm", List.of(template((byte) 3, (short) 100, (byte) 0)))));
    }

    private static SkillTemplateAsset template(byte templateId, short levelId, byte optionId) {
        SkillLevelAsset level = new SkillLevelAsset(
                levelId, (byte) 1, (byte) 10, (short) 20,
                500, (short) 30, (short) 18, (byte) 1,
                List.of(new SkillLevelOptionAsset((short) 15, optionId)));
        return new SkillTemplateAsset(
                templateId, "Chiêu Cry", (byte) 12, (byte) 1,
                (short) 300, "Mô tả", List.of(level));
    }
}
