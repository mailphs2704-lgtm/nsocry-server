package com.nsocry.assets.conversion;

import java.util.List;
import java.util.Objects;

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
        int signedByteOverflowValueCount,
        List<SkillRawByteDifference> rawByteDifferences) {

    /** Sao chép difference list và giữ count đồng bộ. */
    public SkillDumpInventoryReport {
        Objects.requireNonNull(rawByteDifferences, "rawByteDifferences");
        rawByteDifferences = List.copyOf(rawByteDifferences);
        if (signedByteOverflowValueCount != rawByteDifferences.size()) {
            throw new IllegalArgumentException("difference count không khớp danh sách");
        }
    }
}
