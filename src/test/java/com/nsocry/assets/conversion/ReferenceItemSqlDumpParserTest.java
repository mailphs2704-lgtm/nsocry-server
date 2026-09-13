package com.nsocry.assets.conversion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ReferenceItemSqlDumpParserTest {
    @Test
    void parsesOnlyRequiredTablesAndMapsColumns() {
        ReferenceItemDumpRows rows = ReferenceItemSqlDumpParser.parse(dump(
                "(0, 2, 'Tấn công +#')",
                "(0, 'Đá Cry', 26, 2, 'Dùng nâng cấp', 1, 188, -1, -1, 1)"));

        assertEquals(1, rows.optionRows().size());
        assertEquals("Tấn công +#", rows.optionRows().get(0).name());
        assertEquals(1, rows.itemRows().size());
        assertEquals(188, rows.itemRows().get(0).icon());
        assertEquals(1, rows.itemRows().get(0).upgradableValue());
    }

    @Test
    void decodesBackslashAndDoubledQuoteEscapes() {
        ReferenceItemDumpRows rows = ReferenceItemSqlDumpParser.parse(dump(
                "(0, 0, 'Kháng \\'hỏa\\'')",
                "(0, 'Cry''s kiếm', 3, 1, 'Dòng 1\\nDòng 2', 1, 10, -1, -1, 0)"));

        assertEquals("Kháng 'hỏa'", rows.optionRows().get(0).name());
        assertEquals("Cry's kiếm", rows.itemRows().get(0).name());
        assertEquals("Dòng 1\nDòng 2", rows.itemRows().get(0).description());
    }

    @Test
    void ignoresSemicolonInsideQuotedDescription() {
        ReferenceItemDumpRows rows = ReferenceItemSqlDumpParser.parse(dump(
                "(0, 0, 'A')",
                "(0, 'B', 1, 0, 'trước; sau', 1, 2, -1, -1, 0)"));

        assertEquals("trước; sau", rows.itemRows().get(0).description());
    }

    @Test
    void rejectsMissingRequiredStatement() {
        assertThrows(IllegalArgumentException.class,
                () -> ReferenceItemSqlDumpParser.parse("SELECT 1;"));
    }

    @Test
    void rejectsRowWithUnexpectedColumnCount() {
        assertThrows(IllegalArgumentException.class, () -> ReferenceItemSqlDumpParser.parse(dump(
                "(0, 0)",
                "(0, 'B', 1, 0, '', 1, 2, -1, -1, 0)")));
    }

    private static String dump(String options, String items) {
        return """
                INSERT INTO `ignored` (`value`) VALUES ('do not parse');
                INSERT INTO `item_option` (`id`, `type`, `name`) VALUES
                %s;
                INSERT INTO `item` (`id`, `name`, `type`, `gender`, `description`, `level`, `icon`, `part`, `fashion`, `isUpToUp`) VALUES
                %s;
                """.formatted(options, items);
    }
}
