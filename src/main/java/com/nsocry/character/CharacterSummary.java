package com.nsocry.character;

import java.util.Objects;

/** Dữ liệu hiển thị tối thiểu của một nhân vật trong màn hình chọn nhân vật. */
public record CharacterSummary(
        byte gender,
        String name,
        String school,
        byte level,
        short head,
        short weapon,
        short body,
        short leg) {

    /** Từ chối giá trị chuỗi null trước khi dữ liệu đi vào bộ mã hóa giao thức. */
    public CharacterSummary {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(school, "school");
    }
}
