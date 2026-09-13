package com.nsocry.assets;

/** Metadata của payload ITEM đã encode, parse lại và khớp manifest. */
public record ItemAssetValidationResult(
        byte version,
        int optionCount,
        int itemCount,
        int payloadLength,
        String payloadSha256) {
}
