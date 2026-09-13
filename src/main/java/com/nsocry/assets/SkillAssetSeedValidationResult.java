package com.nsocry.assets;

import com.nsocry.assets.conversion.SkillRawByteDifference;
import java.util.List;
import java.util.Objects;

/** Metadata SKILL đã xác minh từ read model, payload và manifest. */
public record SkillAssetSeedValidationResult(
        byte version,
        SkillAssetValidationReport structure,
        List<SkillRawByteDifference> rawByteDifferences,
        int payloadLength,
        String payloadSha256) {

    /** Giữ danh sách difference bất biến và từ chối metadata null. */
    public SkillAssetSeedValidationResult {
        Objects.requireNonNull(structure, "structure");
        rawByteDifferences = List.copyOf(Objects.requireNonNull(rawByteDifferences, "rawByteDifferences"));
        Objects.requireNonNull(payloadSha256, "payloadSha256");
    }
}
