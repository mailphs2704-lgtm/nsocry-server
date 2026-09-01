package com.nsocry.assets;

import java.util.HexFormat;
import java.util.Locale;
import java.util.Objects;

/** Kỳ vọng bất biến để nhận diện chính xác một candidate SKILL đã kiểm định. */
public record SkillAssetSeedManifest(
        byte version,
        int optionTemplateCount,
        int classCount,
        int skillTemplateCount,
        int skillLevelCount,
        int skillLevelOptionCount,
        int rawByteDifferenceCount,
        int payloadLength,
        String payloadSha256) {

    /** Kiểm tra miền count và chuẩn hóa SHA-256 về chữ thường. */
    public SkillAssetSeedManifest {
        requireRange(optionTemplateCount, 0, 127, "optionTemplateCount");
        requireRange(classCount, 0, 255, "classCount");
        requireRange(skillTemplateCount, 0, 128, "skillTemplateCount");
        requireRange(skillLevelCount, 0, 32_768, "skillLevelCount");
        requireRange(skillLevelOptionCount, 0, Integer.MAX_VALUE, "skillLevelOptionCount");
        requireRange(rawByteDifferenceCount, 0, Integer.MAX_VALUE, "rawByteDifferenceCount");
        requireRange(payloadLength, 0, Integer.MAX_VALUE, "payloadLength");
        Objects.requireNonNull(payloadSha256, "payloadSha256");
        payloadSha256 = payloadSha256.toLowerCase(Locale.ROOT);
        if (payloadSha256.length() != 64) throw new IllegalArgumentException("SHA-256 phải có 64 ký tự hex");
        HexFormat.of().parseHex(payloadSha256);
    }

    private static void requireRange(int value, int minimum, int maximum, String name) {
        if (value < minimum || value > maximum) throw new IllegalArgumentException(name + " ngoài giới hạn");
    }
}
