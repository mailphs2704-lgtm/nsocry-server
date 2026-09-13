package com.nsocry.persistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/** Đối chiếu metadata với đúng bảy cột DATA V005 mà không thực hiện DDL. */
public final class DataAssetSchemaContract {
    private static final Map<String, ExpectedColumn> EXPECTED = expectedColumns();

    private DataAssetSchemaContract() {
    }

    /** Báo cột thiếu, thừa, trùng hoặc sai type/unsigned/nullability. */
    public static DataAssetSchemaPreflightReport evaluate(List<DataAssetSchemaColumn> actualColumns) {
        Objects.requireNonNull(actualColumns, "actualColumns");
        Map<String, DataAssetSchemaColumn> actual = new LinkedHashMap<>();
        List<String> differences = new ArrayList<>();
        for (DataAssetSchemaColumn column : actualColumns) {
            Objects.requireNonNull(column, "column");
            String key = key(column.tableName(), column.columnName());
            if (actual.putIfAbsent(key, column) != null) {
                differences.add("Cột metadata bị trùng: " + key);
            }
        }
        for (Map.Entry<String, ExpectedColumn> expected : EXPECTED.entrySet()) {
            DataAssetSchemaColumn column = actual.remove(expected.getKey());
            if (column == null) {
                differences.add("Thiếu cột: " + expected.getKey());
            } else if (!expected.getValue().matches(column)) {
                differences.add("Sai contract cột: " + expected.getKey());
            }
        }
        for (String unexpected : actual.keySet()) {
            differences.add("Cột không thuộc V005: " + unexpected);
        }
        return new DataAssetSchemaPreflightReport(differences.isEmpty(), differences);
    }

    private static Map<String, ExpectedColumn> expectedColumns() {
        Map<String, ExpectedColumn> columns = new LinkedHashMap<>();
        add(columns, "version", "tinyint", true);
        add(columns, "task_group_count", "tinyint", true);
        add(columns, "experience_count", "tinyint", true);
        add(columns, "payload_length", "int", true);
        add(columns, "payload_sha256", "char", false);
        add(columns, "payload", "longblob", false);
        add(columns, "manifest_text", "text", false);
        return Collections.unmodifiableMap(columns);
    }

    private static void add(Map<String, ExpectedColumn> target, String column,
            String dataType, boolean unsigned) {
        target.put(key("client_data_assets", column), new ExpectedColumn(dataType, unsigned));
    }

    private static String key(String table, String column) {
        return table.toLowerCase(Locale.ROOT) + "." + column.toLowerCase(Locale.ROOT);
    }

    private record ExpectedColumn(String dataType, boolean unsigned) {
        boolean matches(DataAssetSchemaColumn actual) {
            return dataType.equals(actual.dataType().toLowerCase(Locale.ROOT))
                    && unsigned == actual.columnType().toLowerCase(Locale.ROOT).contains("unsigned")
                    && !actual.nullable();
        }
    }
}
