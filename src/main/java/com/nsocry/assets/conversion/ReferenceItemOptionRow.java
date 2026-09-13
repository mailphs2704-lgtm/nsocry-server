package com.nsocry.assets.conversion;

import java.util.Objects;

/** Một dòng item option từ nguồn tham chiếu dùng riêng cho công cụ chuyển đổi offline. */
public record ReferenceItemOptionRow(int id, int type, String name) {
    /** Từ chối chuỗi null trước khi chuyển sang read model NSOCry. */
    public ReferenceItemOptionRow {
        Objects.requireNonNull(name, "name");
    }
}
