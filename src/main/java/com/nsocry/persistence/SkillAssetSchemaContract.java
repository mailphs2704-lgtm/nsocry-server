package com.nsocry.persistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/** Đối chiếu metadata database với đúng 26 cột V003 mà không thực hiện DDL. */
public final class SkillAssetSchemaContract {
    private static final Map<String, ExpectedColumn> EXPECTED = expectedColumns();

    private SkillAssetSchemaContract() {
    }

    /** Báo đầy đủ cột thiếu, thừa, trùng hoặc sai type/unsigned/nullability. */
    public static SkillAssetSchemaPreflightReport evaluate(List<SkillAssetSchemaColumn> actualColumns) {
        Objects.requireNonNull(actualColumns, "actualColumns");
        Map<String, SkillAssetSchemaColumn> actual = new LinkedHashMap<>();
        List<String> differences = new ArrayList<>();
        for (SkillAssetSchemaColumn column : actualColumns) {
            Objects.requireNonNull(column, "column");
            String key = key(column.tableName(), column.columnName());
            if (actual.putIfAbsent(key, column) != null) differences.add("Cột metadata bị trùng: " + key);
        }
        for (Map.Entry<String, ExpectedColumn> expected : EXPECTED.entrySet()) {
            SkillAssetSchemaColumn column = actual.remove(expected.getKey());
            if (column == null) differences.add("Thiếu cột: " + expected.getKey());
            else if (!expected.getValue().matches(column)) differences.add("Sai contract cột: " + expected.getKey());
        }
        for (String unexpected : actual.keySet()) differences.add("Cột không thuộc V003: " + unexpected);
        return new SkillAssetSchemaPreflightReport(differences.isEmpty(), differences);
    }

    private static Map<String, ExpectedColumn> expectedColumns() {
        Map<String, ExpectedColumn> columns = new LinkedHashMap<>();
        add(columns, "client_skill_options", "id", "tinyint", true);
        add(columns, "client_skill_options", "name", "varchar", false);
        add(columns, "client_skill_classes", "id", "tinyint", true);
        add(columns, "client_skill_classes", "name", "varchar", false);
        add(columns, "client_skill_templates", "id", "tinyint", true);
        add(columns, "client_skill_templates", "class_id", "tinyint", true);
        add(columns, "client_skill_templates", "sort_order", "tinyint", true);
        add(columns, "client_skill_templates", "name", "varchar", false);
        add(columns, "client_skill_templates", "max_point", "tinyint", false);
        add(columns, "client_skill_templates", "type", "tinyint", false);
        add(columns, "client_skill_templates", "icon_id", "smallint", false);
        add(columns, "client_skill_templates", "description", "varchar", false);
        add(columns, "client_skill_levels", "id", "smallint", true);
        add(columns, "client_skill_levels", "template_id", "tinyint", true);
        add(columns, "client_skill_levels", "sort_order", "tinyint", true);
        add(columns, "client_skill_levels", "point", "smallint", true);
        add(columns, "client_skill_levels", "required_level", "tinyint", false);
        add(columns, "client_skill_levels", "mana_use", "smallint", false);
        add(columns, "client_skill_levels", "cooldown", "int", false);
        add(columns, "client_skill_levels", "dx", "smallint", false);
        add(columns, "client_skill_levels", "dy", "smallint", false);
        add(columns, "client_skill_levels", "max_fight", "tinyint", false);
        add(columns, "client_skill_level_options", "skill_level_id", "smallint", true);
        add(columns, "client_skill_level_options", "sort_order", "tinyint", true);
        add(columns, "client_skill_level_options", "parameter_value", "smallint", false);
        add(columns, "client_skill_level_options", "option_template_id", "tinyint", true);
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
        boolean matches(SkillAssetSchemaColumn actual) {
            return dataType.equals(actual.dataType().toLowerCase(Locale.ROOT))
                    && unsigned == actual.columnType().toLowerCase(Locale.ROOT).contains("unsigned")
                    && !actual.nullable();
        }
    }
}
