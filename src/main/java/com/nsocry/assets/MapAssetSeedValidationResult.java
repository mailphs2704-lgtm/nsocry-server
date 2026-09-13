package com.nsocry.assets;

import java.util.Objects;

/** Kết quả kiểm định MAP candidate sau khi đối chiếu manifest với payload encode lại. */
public record MapAssetSeedValidationResult(
        byte version,
        int mapCount,
        int npcCount,
        int mobCount,
        int payloadLength,
        String payloadSha256) {

    /** Từ chối validation result thiếu checksum. */
    public MapAssetSeedValidationResult {
        Objects.requireNonNull(payloadSha256, "payloadSha256");
    }
}
