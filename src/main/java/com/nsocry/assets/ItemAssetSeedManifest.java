package com.nsocry.assets;

import java.util.HexFormat;
import java.util.Locale;
import java.util.Objects;

/** Kỳ vọng bất biến dùng để nhận diện chính xác một bộ seed ITEM đã được phê duyệt. */
public record ItemAssetSeedManifest(
        byte version,
        int optionCount,
        int itemCount,
        int payloadLength,
        String payloadSha256) {

    /** Kiểm tra count và chuẩn hóa SHA-256 về chữ thường. */
    public ItemAssetSeedManifest {
        if (optionCount < 0 || optionCount > 255) {
            throw new IllegalArgumentException("optionCount ngoài giới hạn wire");
        }
        if (itemCount < 0 || itemCount > 32_767) {
            throw new IllegalArgumentException("itemCount ngoài giới hạn wire");
        }
        if (payloadLength < 0) {
            throw new IllegalArgumentException("payloadLength không được âm");
        }
        Objects.requireNonNull(payloadSha256, "payloadSha256");
        payloadSha256 = payloadSha256.toLowerCase(Locale.ROOT);
        if (payloadSha256.length() != 64) {
            throw new IllegalArgumentException("payloadSha256 phải có 64 ký tự hex");
        }
        try {
            HexFormat.of().parseHex(payloadSha256);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("payloadSha256 không phải chuỗi hex", exception);
        }
    }
}
