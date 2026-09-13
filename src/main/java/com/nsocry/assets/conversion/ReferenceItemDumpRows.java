package com.nsocry.assets.conversion;

import java.util.List;
import java.util.Objects;

/** Hai tập row ITEM được parse từ dump tham chiếu trước bước chuyển sang NSOCry. */
public record ReferenceItemDumpRows(
        List<ReferenceItemOptionRow> optionRows,
        List<ReferenceItemTemplateRow> itemRows) {

    /** Sao chép danh sách để kết quả parse không bị sửa ngoài ý muốn. */
    public ReferenceItemDumpRows {
        Objects.requireNonNull(optionRows, "optionRows");
        Objects.requireNonNull(itemRows, "itemRows");
        optionRows = List.copyOf(optionRows);
        itemRows = List.copyOf(itemRows);
    }
}
