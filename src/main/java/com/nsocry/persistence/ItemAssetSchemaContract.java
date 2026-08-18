package com.nsocry.persistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/** Đối chiếu metadata database với contract V002 mà không thực hiện DDL. */
public final class ItemAssetSchemaContract {
    private static final Map<String, ExpectedColumn> EXPECTED = expectedColumns();

    private ItemAssetSchemaContract() {
    }

    /** Trả report đầy đủ về cột thiếu, thừa hoặc sai type/nullability. */
    public static ItemAssetSchemaPreflightReport evaluate(List<ItemAssetSchemaColumn> actualColumns) {
        Objects.requireNonNull(actualColumns, "actualColumns");
        Map<String, ItemAssetSchemaColumn> actual = new LinkedHashMap<>();
        List<String> differences = new ArrayList<>();
        for (ItemAssetSchemaColumn column : actualColumns) {
            Objects.requireNonNull(column, "column");
            String key = key(column.tableName(), column.columnName());
            if (actual.putIfAbsent(key, column) != null) {
                differences.add("Cột metadata bị trùng: " + key);
            }
        }
        for (Map.Entry<String, ExpectedColumn> entry : EXPECTED.entrySet()) {
            ItemAssetSchemaColumn column = actual.remove(entry.getKey());
            if (column == null) {
                differences.add("Thiếu cột: " + entry.getKey());
            } else if (!entry.getValue().matches(column)) {
                differences.add("Sai contract cột: " + entry.getKey());
            }
        }
        for (String unexpected : actual.keySet()) {
            differences.add("Cột không thuộc V002: " + unexpected);
        }
        return new ItemAssetSchemaPreflightReport(differences.isEmpty(), differences);
    }

    /** Khai báo đúng 12 cột do migration V002 tạo. */
    private static Map<String, ExpectedColumn> expectedColumns() {
        Map<String, ExpectedColumn> columns = new LinkedHashMap<>();
        add(columns, "client_item_options", "id", "smallint", true);
        add(columns, "client_item_options", "name", "varchar", false);
        add(columns, "client_item_options", "type", "tinyint", false);
        add(columns, "client_item_templates", "id", "smallint", true);
        add(columns, "client_item_templates", "type", "tinyint", false);
        add(columns, "client_item_templates", "gender", "tinyint", false);
        add(columns, "client_item_templates", "name", "varchar", false);
        add(columns, "client_item_templates", "description", "varchar", false);
        add(columns, "client_item_templates", "required_level", "tinyint", false);
        add(columns, "client_item_templates", "icon_id", "smallint", false);
        add(columns, "client_item_templates", "part_id", "smallint", false);
        add(columns, "client_item_templates", "upgradable", "tinyint", false);
        return Collections.unmodifiableMap(columns);
    }

    private static void add(
            Map<String, ExpectedColumn> columns,
            String table,
            String column,
            String dataType,
            boolean unsigned) {
        columns.put(key(table, column), new ExpectedColumn(dataType, unsigned));
    }

    private static String key(String table, String column) {
        return table.toLowerCase(Locale.ROOT) + "." + column.toLowerCase(Locale.ROOT);
    }

    /** Contract type, unsigned và NOT NULL của một cột. */
    private record ExpectedColumn(String dataType, boolean unsigned) {
        boolean matches(ItemAssetSchemaColumn actual) {
            String actualDataType = actual.dataType().toLowerCase(Locale.ROOT);
            String actualColumnType = actual.columnType().toLowerCase(Locale.ROOT);
            return dataType.equals(actualDataType)
                    && unsigned == actualColumnType.contains("unsigned")
                    && !actual.nullable();
        }
    }
}
