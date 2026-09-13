package com.nsocry.operations;

/** Metadata appearance archive đã kiểm định checksum và codec round-trip. */
public record AppearanceAssetArchiveValidationResult(
        int payloadLength,
        String payloadSha256,
        boolean roundTripVerified) {
}
