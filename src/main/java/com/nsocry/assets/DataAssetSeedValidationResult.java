package com.nsocry.assets;

/** Metadata DATA đã decode, encode lại và đối chiếu thành công với manifest. */
public record DataAssetSeedValidationResult(
        byte version,
        int taskGroupCount,
        int experienceCount,
        int payloadLength,
        String payloadSha256) {
}
