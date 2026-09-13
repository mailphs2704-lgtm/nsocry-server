package com.nsocry.assets;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/** Kiểm tra quan hệ ID/count của SKILL read model trước seed/codec validation. */
public final class SkillAssetStructureValidator {
    private static final int MAX_SIGNED_COUNT = 127;
    private static final int MAX_CLASSES = 255;

    private SkillAssetStructureValidator() {
    }

    /** Từ chối ID trùng, reference option sai và count vượt wire; trả count report. */
    public static SkillAssetValidationReport validate(SkillAssetBundle bundle) {
        Objects.requireNonNull(bundle, "bundle");
        requireCount(bundle.optionTemplateNames().size(), MAX_SIGNED_COUNT, "skill option templates");
        requireCount(bundle.classes().size(), MAX_CLASSES, "skill classes");
        Set<Byte> templateIds = new HashSet<>();
        Set<Short> levelIds = new HashSet<>();
        int templateCount = 0;
        int levelCount = 0;
        int levelOptionCount = 0;
        for (SkillClassAsset skillClass : bundle.classes()) {
            requireCount(skillClass.templates().size(), MAX_SIGNED_COUNT, "class skill templates");
            for (SkillTemplateAsset template : skillClass.templates()) {
                requireNonNegative(template.id(), "skill template id");
                if (!templateIds.add(template.id())) {
                    throw new IllegalArgumentException("skill template id bị trùng");
                }
                templateCount++;
                requireCount(template.levels().size(), MAX_SIGNED_COUNT, "skill levels");
                for (SkillLevelAsset level : template.levels()) {
                    if (level.id() < 0 || !levelIds.add(level.id())) {
                        throw new IllegalArgumentException("skill level id âm hoặc bị trùng");
                    }
                    levelCount++;
                    requireCount(level.options().size(), MAX_SIGNED_COUNT, "skill level options");
                    for (SkillLevelOptionAsset option : level.options()) {
                        int optionId = option.optionTemplateId();
                        if (optionId < 0 || optionId >= bundle.optionTemplateNames().size()) {
                            throw new IllegalArgumentException("skill option template id không tồn tại");
                        }
                        levelOptionCount++;
                    }
                }
            }
        }
        return new SkillAssetValidationReport(
                bundle.optionTemplateNames().size(), bundle.classes().size(),
                templateCount, levelCount, levelOptionCount);
    }

    private static void requireCount(int count, int maximum, String name) {
        if (count > maximum) {
            throw new IllegalArgumentException(name + " vượt giới hạn " + maximum);
        }
    }

    private static void requireNonNegative(byte value, String name) {
        if (value < 0) {
            throw new IllegalArgumentException(name + " không được âm");
        }
    }
}
