package com.nsocry.persistence;

import java.util.Objects;

/** Metadata tối thiểu của một cột SKILL đọc từ information_schema. */
public record SkillAssetSchemaColumn(
        String tableName,
        String columnName,
        String dataType,
        String columnType,
        boolean nullable) {

    /** Từ chối metadata null trước khi đối chiếu contract V003. */
    public SkillAssetSchemaColumn {
        Objects.requireNonNull(tableName, "tableName");
        Objects.requireNonNull(columnName, "columnName");
        Objects.requireNonNull(dataType, "dataType");
        Objects.requireNonNull(columnType, "columnType");
    }
}
