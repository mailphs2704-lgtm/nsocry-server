package com.nsocry.assets;

import java.util.HexFormat;
import java.util.Locale;
import java.util.Objects;

/** Manifest xác định cho một MAP seed candidate đã encode. */
public record MapAssetSeedManifest(
        byte version,
        int mapCount,
        int npcCount,
        int mobCount,
        int payloadLength,
        String payloadSha256) {

    /** Kiểm tra đúng count wire và chuẩn hóa SHA-256 về chữ thường. */
    public MapAssetSeedManifest {
        requireRange(mapCount, 0, 255, "mapCount");
        requireRange(npcCount, 0, 127, "npcCount");
        requireRange(mobCount, 0, 32_767, "mobCount");
        requireRange(payloadLength, 0, Integer.MAX_VALUE, "payloadLength");
        Objects.requireNonNull(payloadSha256, "payloadSha256");
        payloadSha256 = payloadSha256.toLowerCase(Locale.ROOT);
        if (payloadSha256.length() != 64) {
            throw new IllegalArgumentException("SHA-256 phải có 64 ký tự hex");
        }
        HexFormat.of().parseHex(payloadSha256);
    }

    private static void requireRange(int value, int minimum, int maximum, String name) {
        if (value < minimum || value > maximum) {
            throw new IllegalArgumentException(name + " ngoài giới hạn");
        }
    }
}
