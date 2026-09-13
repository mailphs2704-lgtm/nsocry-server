package com.nsocry.assets;

import java.util.Arrays;
import java.util.Objects;

/** Appearance candidate bất biến đã khóa độ dài, SHA-256 và round-trip codec. */
public final class AppearanceAssetSeedArtifact {
    private final byte[] payload;
    private final String payloadSha256;

    AppearanceAssetSeedArtifact(byte[] payload, String payloadSha256) {
        this.payload = Arrays.copyOf(Objects.requireNonNull(payload, "payload"), payload.length);
        this.payloadSha256 = Objects.requireNonNull(payloadSha256, "payloadSha256");
    }

    /** Trả defensive copy của payload appearance đã kiểm định. */
    public byte[] payload() {
        return Arrays.copyOf(payload, payload.length);
    }

    /** Trả độ dài payload đã khóa. */
    public int payloadLength() {
        return payload.length;
    }

    /** Trả SHA-256 lowercase của payload. */
    public String payloadSha256() {
        return payloadSha256;
    }
}
