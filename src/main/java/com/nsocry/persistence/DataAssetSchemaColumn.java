package com.nsocry.persistence;

import java.util.Objects;

/** Metadata một cột schema DATA V005 đọc từ information_schema. */
public record DataAssetSchemaColumn(
        String tableName,
        String columnName,
        String dataType,
        String columnType,
        boolean nullable) {

    /** Từ chối metadata thiếu tên/type trước khi đánh giá contract. */
    public DataAssetSchemaColumn {
        Objects.requireNonNull(tableName, "tableName");
        Objects.requireNonNull(columnName, "columnName");
        Objects.requireNonNull(dataType, "dataType");
        Objects.requireNonNull(columnType, "columnType");
    }
}
