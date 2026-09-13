package com.nsocry.assets.conversion;

import java.util.Objects;

/** Một dòng item template tham chiếu; giữ cả fashion để báo cáo phần không có trên wire ITEM. */
public record ReferenceItemTemplateRow(
        int id,
        String name,
        int type,
        int gender,
        String description,
        int level,
        int icon,
        int part,
        int fashion,
        int upgradableValue) {

    /** Từ chối chuỗi null trước khi kiểm tra kiểu số. */
    public ReferenceItemTemplateRow {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(description, "description");
    }
}
