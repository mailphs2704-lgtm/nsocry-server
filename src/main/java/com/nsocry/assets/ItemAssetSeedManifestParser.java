package com.nsocry.assets;

import java.util.Objects;

/** Parse manifest ITEM seed v1 theo định dạng canonical, từ chối khóa thừa hoặc đổi thứ tự. */
public final class ItemAssetSeedManifestParser {
    private static final String FORMAT_LINE = "format=nsocry-item-seed-v1";

    private ItemAssetSeedManifestParser() {
    }

    /** Parse đúng sáu dòng key=value và yêu cầu newline LF kết thúc file. */
    public static ItemAssetSeedManifest parse(String text) throws ItemAssetSeedValidationException {
        Objects.requireNonNull(text, "text");
        String[] lines = text.split("\\n", -1);
        if (lines.length != 7 || !lines[6].isEmpty() || !FORMAT_LINE.equals(lines[0])) {
            throw new ItemAssetSeedValidationException("Manifest ITEM không đúng format v1 canonical");
        }
        try {
            int version = parseInt(lines[1], "version=");
            if (version < 0 || version > 255) {
                throw new IllegalArgumentException("version ngoài giới hạn unsigned byte");
            }
            int optionCount = parseInt(lines[2], "optionCount=");
            int itemCount = parseInt(lines[3], "itemCount=");
            int payloadLength = parseInt(lines[4], "payloadLength=");
            if (payloadLength < 0) {
                throw new IllegalArgumentException("payloadLength âm");
            }
            String sha256 = value(lines[5], "sha256=");
            return new ItemAssetSeedManifest((byte) version, optionCount, itemCount, payloadLength, sha256);
        } catch (IllegalArgumentException exception) {
            throw new ItemAssetSeedValidationException("Manifest ITEM chứa giá trị không hợp lệ", exception);
        }
    }

    /** Đọc một số nguyên decimal không cho phép đổi tên khóa. */
    private static int parseInt(String line, String prefix) {
        return Integer.parseInt(value(line, prefix));
    }

    /** Lấy value sau prefix bắt buộc và từ chối value rỗng. */
    private static String value(String line, String prefix) {
        if (!line.startsWith(prefix) || line.length() == prefix.length()) {
            throw new IllegalArgumentException("Thiếu khóa " + prefix);
        }
        return line.substring(prefix.length());
    }
}
