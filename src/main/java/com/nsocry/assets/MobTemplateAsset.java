package com.nsocry.assets;

import java.util.Objects;

/** Metadata tĩnh của một loại quái mà client cần để khởi tạo template. */
public record MobTemplateAsset(
        byte type,
        String name,
        int health,
        byte moveRange,
        byte speed) {

    /** Từ chối tên null trước khi ghi modified UTF. */
    public MobTemplateAsset {
        Objects.requireNonNull(name, "name");
    }
}
