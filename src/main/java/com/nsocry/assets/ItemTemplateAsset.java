package com.nsocry.assets;

import java.util.Objects;

/** Metadata hiển thị bất biến của một mẫu vật phẩm phía client. */
public record ItemTemplateAsset(
        byte type,
        byte gender,
        String name,
        String description,
        byte level,
        short icon,
        short part,
        boolean upgradable) {

    /** Bảo đảm các trường chuỗi luôn sẵn sàng cho wire encoder. */
    public ItemTemplateAsset {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(description, "description");
    }
}
