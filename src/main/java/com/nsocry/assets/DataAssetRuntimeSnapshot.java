package com.nsocry.assets;

import java.util.Arrays;
import java.util.Objects;

/** Snapshot DATA bất biến đã vượt version/count/length/checksum gate. */
public final class DataAssetRuntimeSnapshot {
    private final byte version;
    private final int taskGroupCount;
    private final int experienceCount;
    private final int payloadLength;
    private final String payloadSha256;
    private final byte[] payload;

    private DataAssetRuntimeSnapshot(DataAssetSeedValidationResult validation, byte[] payload) {
        Objects.requireNonNull(validation, "validation");
        this.payload = Arrays.copyOf(Objects.requireNonNull(payload, "payload"), payload.length);
        if (payload.length != validation.payloadLength()) {
            throw new IllegalArgumentException("DATA payload length không khớp validation");
        }
        if (!DataAssetSeedValidator.sha256(payload).equals(validation.payloadSha256())) {
            throw new IllegalArgumentException("DATA payload SHA-256 không khớp validation");
        }
        version = validation.version();
        taskGroupCount = validation.taskGroupCount();
        experienceCount = validation.experienceCount();
        payloadLength = validation.payloadLength();
        payloadSha256 = validation.payloadSha256();
    }

    /** Factory duy nhất cho payload đã validate. */
    static DataAssetRuntimeSnapshot verified(
            DataAssetSeedValidationResult validation, byte[] payload) {
        return new DataAssetRuntimeSnapshot(validation, payload);
    }

    public byte version() { return version; }
    public int taskGroupCount() { return taskGroupCount; }
    public int experienceCount() { return experienceCount; }
    public int payloadLength() { return payloadLength; }
    public String payloadSha256() { return payloadSha256; }
    /** Defensive copy để session không thể sửa snapshot dùng chung. */
    public byte[] payload() { return Arrays.copyOf(payload, payload.length); }
}
