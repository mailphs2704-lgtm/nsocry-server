package com.nsocry.assets;

/** Count tổng hợp của SKILL bundle đã vượt structural validation. */
public record SkillAssetValidationReport(
        int optionTemplateCount,
        int classCount,
        int skillTemplateCount,
        int skillLevelCount,
        int skillLevelOptionCount) {
}
