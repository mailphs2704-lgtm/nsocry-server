package com.nsocry.assets;

import java.util.Objects;

/** Parse manifest DATA seed v1 canonical, từ chối field thừa, thiếu hoặc đổi thứ tự. */
public final class DataAssetSeedManifestParser {
    private static final String FORMAT_LINE = "format=nsocry-data-seed-v1";

    private DataAssetSeedManifestParser() {
    }

    /** Parse đúng sáu dòng key=value và yêu cầu newline LF kết thúc file. */
    public static DataAssetSeedManifest parse(String text) {
        Objects.requireNonNull(text, "text");
        String[] lines = text.split("\\n", -1);
        if (lines.length != 7 || !lines[6].isEmpty() || !FORMAT_LINE.equals(lines[0])) {
            throw new IllegalArgumentException("Manifest DATA không đúng format v1 canonical");
        }
        try {
            int version = parseInt(lines[1], "version=");
            if (version < 0 || version > 255) {
                throw new IllegalArgumentException("version ngoài giới hạn unsigned byte");
            }
            return new DataAssetSeedManifest(
                    (byte) version,
                    parseInt(lines[2], "taskGroupCount="),
                    parseInt(lines[3], "experienceCount="),
                    parseInt(lines[4], "payloadLength="),
                    value(lines[5], "sha256="));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Manifest DATA chứa số không hợp lệ", exception);
        }
    }

    private static int parseInt(String line, String prefix) {
        return Integer.parseInt(value(line, prefix));
    }

    private static String value(String line, String prefix) {
        if (!line.startsWith(prefix) || line.length() == prefix.length()) {
            throw new IllegalArgumentException("Manifest DATA thiếu khóa " + prefix);
        }
        return line.substring(prefix.length());
    }
}
