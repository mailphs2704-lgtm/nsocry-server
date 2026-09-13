package com.nsocry.persistence;

import java.util.Objects;

/** Mô tả tối thiểu một cột ITEM asset lấy từ information_schema. */
public record ItemAssetSchemaColumn(
        String tableName,
        String columnName,
        String dataType,
        String columnType,
        boolean nullable) {

    /** Chuẩn hóa và từ chối metadata null. */
    public ItemAssetSchemaColumn {
        Objects.requireNonNull(tableName, "tableName");
        Objects.requireNonNull(columnName, "columnName");
        Objects.requireNonNull(dataType, "dataType");
        Objects.requireNonNull(columnType, "columnType");
    }
}
