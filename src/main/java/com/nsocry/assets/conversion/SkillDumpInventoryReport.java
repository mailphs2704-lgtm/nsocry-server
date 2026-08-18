package com.nsocry.assets.conversion;

/** Inventory đã kiểm tra của bốn nguồn SKILL trong dump tham chiếu. */
public record SkillDumpInventoryReport(
        int classCount,
        int optionTemplateCount,
        int skillTemplateCount,
        int skillLevelCount,
        int skillLevelOptionCount,
        int minimumTemplateId,
        int maximumTemplateId,
        int minimumLevelId,
        int maximumLevelId,
        int maximumOptionsPerLevel,
        int signedByteOverflowValueCount) {
}
