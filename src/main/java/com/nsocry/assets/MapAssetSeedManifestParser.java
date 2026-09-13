package com.nsocry.assets;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/** Parse manifest MAP key=value với schema đóng, không chấp nhận field lạ hoặc trùng. */
public final class MapAssetSeedManifestParser {
    private static final String FORMAT = "nsocry-map-seed-v1";

    private MapAssetSeedManifestParser() {
    }

    /** Parse toàn bộ manifest và fail closed nếu schema/field/value không hợp lệ. */
    public static MapAssetSeedManifest parse(String text) {
        Objects.requireNonNull(text, "text");
        Map<String, String> values = new HashMap<>();
        for (String line : text.split("\\n")) {
            if (line.isEmpty()) continue;
            int separator = line.indexOf('=');
            if (separator <= 0
                    || values.put(line.substring(0, separator), line.substring(separator + 1)) != null) {
                throw new IllegalArgumentException("MAP manifest sai cấu trúc hoặc trùng field");
            }
        }
        if (values.size() != 7 || !FORMAT.equals(values.remove("format"))) {
            throw new IllegalArgumentException("MAP manifest sai format/schema");
        }
        MapAssetSeedManifest manifest = new MapAssetSeedManifest(
                version(values.remove("version")),
                integer(values.remove("mapCount"), "mapCount"),
                integer(values.remove("npcCount"), "npcCount"),
                integer(values.remove("mobCount"), "mobCount"),
                integer(values.remove("payloadLength"), "payloadLength"),
                Objects.requireNonNull(values.remove("sha256"), "sha256"));
        if (!values.isEmpty()) throw new IllegalArgumentException("MAP manifest chứa field lạ");
        return manifest;
    }

    private static int integer(String value, String name) {
        try {
            return Integer.parseInt(Objects.requireNonNull(value, name));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "MAP manifest field " + name + " không phải integer", exception);
        }
    }

    private static byte version(String value) {
        int parsed = integer(value, "version");
        if (parsed < 0 || parsed > 255) {
            throw new IllegalArgumentException("MAP version ngoài raw byte");
        }
        return (byte) parsed;
    }
}
