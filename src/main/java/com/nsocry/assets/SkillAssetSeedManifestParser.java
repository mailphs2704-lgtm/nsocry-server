package com.nsocry.assets;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/** Parse manifest SKILL key=value với schema đóng và không chấp nhận field lạ. */
public final class SkillAssetSeedManifestParser {
    private static final String FORMAT = "nsocry-skill-seed-v1";

    private SkillAssetSeedManifestParser() {
    }

    /** Parse manifest schema đóng và từ chối field thiếu, lạ, trùng hoặc sai kiểu. */
    public static SkillAssetSeedManifest parse(String text) {
        Objects.requireNonNull(text, "text");
        Map<String, String> values = new HashMap<>();
        for (String line : text.split("\\n")) {
            if (line.isEmpty()) continue;
            int separator = line.indexOf('=');
            if (separator <= 0 || values.put(line.substring(0, separator), line.substring(separator + 1)) != null) {
                throw new IllegalArgumentException("SKILL manifest sai cấu trúc hoặc trùng field");
            }
        }
        if (values.size() != 10 || !FORMAT.equals(values.remove("format"))) {
            throw new IllegalArgumentException("SKILL manifest sai format/schema");
        }
        SkillAssetSeedManifest manifest = new SkillAssetSeedManifest(
                version(values.remove("version")),
                integer(values.remove("optionTemplateCount"), "optionTemplateCount"),
                integer(values.remove("classCount"), "classCount"),
                integer(values.remove("skillTemplateCount"), "skillTemplateCount"),
                integer(values.remove("skillLevelCount"), "skillLevelCount"),
                integer(values.remove("skillLevelOptionCount"), "skillLevelOptionCount"),
                integer(values.remove("rawByteDifferenceCount"), "rawByteDifferenceCount"),
                integer(values.remove("payloadLength"), "payloadLength"),
                Objects.requireNonNull(values.remove("sha256"), "sha256"));
        if (!values.isEmpty()) throw new IllegalArgumentException("SKILL manifest chứa field lạ");
        return manifest;
    }

    private static int integer(String value, String name) {
        try {
            return Integer.parseInt(Objects.requireNonNull(value, name));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("SKILL manifest field " + name + " không phải integer", exception);
        }
    }

    private static byte version(String value) {
        int parsed = integer(value, "version");
        if (parsed < 0 || parsed > 255) throw new IllegalArgumentException("SKILL version ngoài raw byte");
        return (byte) parsed;
    }
}
