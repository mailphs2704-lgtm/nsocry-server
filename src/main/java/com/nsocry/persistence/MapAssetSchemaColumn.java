package com.nsocry.persistence;

import java.util.Objects;

/** Metadata một cột MAP đọc từ information_schema, không chứa dữ liệu game. */
public record MapAssetSchemaColumn(
        String tableName,
        String columnName,
        String dataType,
        String columnType,
        boolean nullable) {

    /** Chuẩn hóa contract đầu vào và từ chối metadata null. */
    public MapAssetSchemaColumn {
        Objects.requireNonNull(tableName, "tableName");
        Objects.requireNonNull(columnName, "columnName");
        Objects.requireNonNull(dataType, "dataType");
        Objects.requireNonNull(columnType, "columnType");
    }
}
