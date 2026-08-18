package com.nsocry.assets;

import java.util.List;
import java.util.Objects;

/** Read model bất biến dùng để build payload ITEM, tách khỏi entity gameplay và persistence. */
public record ItemAssetBundle(
        byte version,
        List<ItemOptionAsset> options,
        List<ItemTemplateAsset> items) {

    /** Sao chép hai danh sách để bundle không đổi trong lúc build. */
    public ItemAssetBundle {
        Objects.requireNonNull(options, "options");
        Objects.requireNonNull(items, "items");
        options = List.copyOf(options);
        items = List.copyOf(items);
    }
}
