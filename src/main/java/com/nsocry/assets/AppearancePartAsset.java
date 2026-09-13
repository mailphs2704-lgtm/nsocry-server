package com.nsocry.assets;

import java.util.List;
import java.util.Objects;

/** Một part head/body gồm id, ảnh nhỏ và các lớp ảnh ghép. */
public record AppearancePartAsset(
        short id,
        short smallImageId,
        List<AppearanceLayerAsset> layers) {

    /** Sao chép danh sách lớp để giữ part bất biến. */
    public AppearancePartAsset {
        Objects.requireNonNull(layers, "layers");
        layers = List.copyOf(layers);
    }
}
