package com.nsocry.assets;

import java.util.Arrays;
import java.util.Objects;

/** Snapshot SKILL bất biến đã vượt gate version, cấu trúc và checksum trước khi publish. */
public final class SkillAssetRuntimeSnapshot {
    private final byte version;
    private final SkillAssetValidationReport structure;
    private final int payloadLength;
    private final String payloadSha256;
    private final byte[] payload;

    /** Tạo snapshot từ kết quả validation và payload tương ứng. */
    public SkillAssetRuntimeSnapshot(SkillAssetSeedValidationResult validation, byte[] payload) {
        Objects.requireNonNull(validation, "validation");
        this.payload = Arrays.copyOf(Objects.requireNonNull(payload, "payload"), payload.length);
        if (payload.length != validation.payloadLength()) {
            throw new IllegalArgumentException("SKILL payload length không khớp validation");
        }
        version = validation.version();
        structure = validation.structure();
        payloadLength = validation.payloadLength();
        payloadSha256 = validation.payloadSha256();
    }

    public byte version() { return version; }
    public SkillAssetValidationReport structure() { return structure; }
    public int payloadLength() { return payloadLength; }
    public String payloadSha256() { return payloadSha256; }

    /** Trả bản sao để session không thể sửa payload đang được dùng chung. */
    public byte[] payload() { return Arrays.copyOf(payload, payload.length); }
}
