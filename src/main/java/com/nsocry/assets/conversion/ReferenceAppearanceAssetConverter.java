package com.nsocry.assets.conversion;

import com.nsocry.assets.AppearanceAssetBundle;
import com.nsocry.assets.AppearanceLayerAsset;
import com.nsocry.assets.AppearancePartAsset;
import com.nsocry.assets.LegAppearanceAsset;
import com.nsocry.assets.MountAppearanceAsset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Tái tạo appearance UPDATE_VERSION từ các row others và mount cố định của source tham chiếu. */
public final class ReferenceAppearanceAssetConverter {
    private ReferenceAppearanceAssetConverter() {
    }

    /** Parse bảy nhóm appearance authoritative và ghép hai mount đúng thứ tự source. */
    public static AppearanceAssetBundle convert(String dump) {
        Objects.requireNonNull(dump, "dump");
        Map<String, String> others = ReferenceDataDumpInventoryParser.rows(
                dump, ReferenceDataDumpInventoryParser.OTHERS_MARKER, 3, "others").stream()
                .collect(java.util.stream.Collectors.toMap(
                        row -> row.get(1), row -> row.get(2), (left, right) -> {
                            throw new IllegalArgumentException("others trùng name appearance");
                        }));
        return new AppearanceAssetBundle(
                parts(required(others, "head_jump"), "head_jump"),
                parts(required(others, "head_normal"), "head_normal"),
                parts(required(others, "head_boc_dau"), "head_boc_dau"),
                legs(required(others, "leg")),
                parts(required(others, "body_jump"), "body_jump"),
                parts(required(others, "body_normal"), "body_normal"),
                parts(required(others, "body_boc_dau"), "body_boc_dau"),
                mounts());
    }

    private static List<AppearancePartAsset> parts(String json, String field) {
        List<AppearancePartAsset> result = new ArrayList<>();
        for (Object value : ReferenceDataDumpInventoryParser.array(json, field)) {
            Map<?, ?> object = ReferenceDataDumpInventoryParser.object(value, field);
            List<AppearanceLayerAsset> layers = new ArrayList<>();
            for (Object item : list(
                    ReferenceDataDumpInventoryParser.required(object, "item"), field + ".item")) {
                Map<?, ?> layer = ReferenceDataDumpInventoryParser.object(item, field + ".item");
                layers.add(new AppearanceLayerAsset(
                        shortValue(layer, "id", field),
                        shortValue(layer, "dx", field),
                        shortValue(layer, "dy", field)));
            }
            result.add(new AppearancePartAsset(
                    shortValue(object, "id", field),
                    shortValue(object, "small", field),
                    layers));
        }
        return List.copyOf(result);
    }

    private static List<LegAppearanceAsset> legs(String json) {
        List<LegAppearanceAsset> result = new ArrayList<>();
        for (Object value : ReferenceDataDumpInventoryParser.array(json, "leg")) {
            Map<?, ?> object = ReferenceDataDumpInventoryParser.object(value, "leg");
            result.add(new LegAppearanceAsset(
                    shortValue(object, "id", "leg"),
                    shortValue(object, "small", "leg")));
        }
        return List.copyOf(result);
    }

    private static List<MountAppearanceAsset> mounts() {
        return List.of(
                new MountAppearanceAsset((short) 776, List.of(
                        shorts(3049, 3050),
                        shorts(3051, 3051, 3052, 3052, 3053, 3053),
                        shorts(3054), shorts(3055), shorts(3056),
                        shorts(3049, 3049, 3049, 3050, 3050, 3050))),
                new MountAppearanceAsset((short) 777, List.of(
                        shorts(3057, 3058),
                        shorts(3059, 3059, 3060, 3060, 3061, 3061, 3062, 3062),
                        shorts(3063), shorts(3064), shorts(3065),
                        shorts(3057, 3057, 3057, 3078, 3058, 3058))));
    }

    private static List<Short> shorts(int... values) {
        List<Short> result = new ArrayList<>(values.length);
        for (int value : values) result.add((short) value);
        return List.copyOf(result);
    }

    private static short shortValue(Map<?, ?> object, String key, String field) {
        long value = ReferenceDataDumpInventoryParser.number(
                ReferenceDataDumpInventoryParser.required(object, key), field + "." + key);
        if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
            throw new IllegalArgumentException(field + "." + key + " vượt short: " + value);
        }
        return (short) value;
    }

    private static List<?> list(Object value, String field) {
        if (!(value instanceof List<?> values)) {
            throw new IllegalArgumentException(field + " phải là JSON array");
        }
        return values;
    }

    private static String required(Map<String, String> rows, String name) {
        String value = rows.get(name);
        if (value == null) throw new IllegalArgumentException("others thiếu row " + name);
        return value;
    }
}
