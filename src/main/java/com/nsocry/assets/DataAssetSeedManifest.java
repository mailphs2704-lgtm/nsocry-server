package com.nsocry.assets;

import java.util.HexFormat;
import java.util.Locale;
import java.util.Objects;

/** Manifest xác định khóa metadata và SHA-256 của một DATA candidate. */
public record DataAssetSeedManifest(
        byte version,
        int taskGroupCount,
        int experienceCount,
        int payloadLength,
        String payloadSha256) {

    /** Kiểm tra count wire, payload length và chuẩn hóa SHA-256 chữ thường. */
    public DataAssetSeedManifest {
        requireRange(taskGroupCount, 0, 127, "taskGroupCount");
        requireRange(experienceCount, 0, 127, "experienceCount");
        requireRange(payloadLength, 0, Integer.MAX_VALUE, "payloadLength");
        Objects.requireNonNull(payloadSha256, "payloadSha256");
        payloadSha256 = payloadSha256.toLowerCase(Locale.ROOT);
        if (payloadSha256.length() != 64) {
            throw new IllegalArgumentException("SHA-256 phải có 64 ký tự hex");
        }
        HexFormat.of().parseHex(payloadSha256);
    }

    private static void requireRange(int value, int minimum, int maximum, String field) {
        if (value < minimum || value > maximum) {
            throw new IllegalArgumentException(field + " ngoài giới hạn");
        }
    }
}
