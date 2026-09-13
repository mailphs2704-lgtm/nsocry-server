package com.nsocry.assets;

import java.util.Objects;

/** Metadata một loại tùy chọn vật phẩm mà client cần để hiển thị. */
public record ItemOptionAsset(String name, byte type) {
    /** Từ chối tên null trước khi mã hóa modified UTF. */
    public ItemOptionAsset {
        Objects.requireNonNull(name, "name");
    }
}
