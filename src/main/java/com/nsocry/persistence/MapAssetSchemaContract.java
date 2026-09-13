package com.nsocry.persistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/** Đối chiếu metadata database với đúng 18 cột MAP V004 mà không thực hiện DDL. */
public final class MapAssetSchemaContract {
    private static final Map<String, ExpectedColumn> EXPECTED = expectedColumns();

    private MapAssetSchemaContract() {
    }

    /** Báo đầy đủ cột thiếu, thừa, trùng hoặc sai type/unsigned/nullability. */
    public static MapAssetSchemaPreflightReport evaluate(List<MapAssetSchemaColumn> actualColumns) {
        Objects.requireNonNull(actualColumns, "actualColumns");
        Map<String, MapAssetSchemaColumn> actual = new LinkedHashMap<>();
        List<String> differences = new ArrayList<>();
        for (MapAssetSchemaColumn column : actualColumns) {
            Objects.requireNonNull(column, "column");
            String key = key(column.tableName(), column.columnName());
            if (actual.putIfAbsent(key, column) != null) {
                differences.add("Cột metadata bị trùng: " + key);
            }
        }
        for (Map.Entry<String, ExpectedColumn> expected : EXPECTED.entrySet()) {
            MapAssetSchemaColumn column = actual.remove(expected.getKey());
            if (column == null) {
                differences.add("Thiếu cột: " + expected.getKey());
            } else if (!expected.getValue().matches(column)) {
                differences.add("Sai contract cột: " + expected.getKey());
            }
        }
        for (String unexpected : actual.keySet()) {
            differences.add("Cột không thuộc V004: " + unexpected);
        }
        return new MapAssetSchemaPreflightReport(differences.isEmpty(), differences);
    }

    private static Map<String, ExpectedColumn> expectedColumns() {
        Map<String, ExpectedColumn> columns = new LinkedHashMap<>();
        add(columns, "client_map_names", "id", "tinyint", true);
        add(columns, "client_map_names", "name", "varchar", false);
        add(columns, "client_npc_templates", "id", "tinyint", true);
        add(columns, "client_npc_templates", "name", "varchar", false);
        add(columns, "client_npc_templates", "head", "smallint", false);
        add(columns, "client_npc_templates", "body", "smallint", false);
        add(columns, "client_npc_templates", "leg", "smallint", false);
        add(columns, "client_npc_templates", "menu_row_count", "tinyint", true);
        add(columns, "client_npc_menu_entries", "npc_id", "tinyint", true);
        add(columns, "client_npc_menu_entries", "row_order", "tinyint", true);
        add(columns, "client_npc_menu_entries", "choice_order", "tinyint", true);
        add(columns, "client_npc_menu_entries", "text", "varchar", false);
        add(columns, "client_mob_templates", "id", "smallint", true);
        add(columns, "client_mob_templates", "type", "tinyint", false);
        add(columns, "client_mob_templates", "name", "varchar", false);
        add(columns, "client_mob_templates", "health", "int", false);
        add(columns, "client_mob_templates", "move_range", "tinyint", false);
        add(columns, "client_mob_templates", "speed", "tinyint", false);
        return Collections.unmodifiableMap(columns);
    }

    private static void add(Map<String, ExpectedColumn> target, String table, String column,
            String dataType, boolean unsigned) {
        target.put(key(table, column), new ExpectedColumn(dataType, unsigned));
    }

    private static String key(String table, String column) {
        return table.toLowerCase(Locale.ROOT) + "." + column.toLowerCase(Locale.ROOT);
    }

    private record ExpectedColumn(String dataType, boolean unsigned) {
        boolean matches(MapAssetSchemaColumn actual) {
            return dataType.equals(actual.dataType().toLowerCase(Locale.ROOT))
                    && unsigned == actual.columnType().toLowerCase(Locale.ROOT).contains("unsigned")
                    && !actual.nullable();
        }
    }
}
