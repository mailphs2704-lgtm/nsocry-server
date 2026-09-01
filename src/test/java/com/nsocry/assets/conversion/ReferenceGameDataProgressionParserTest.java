package com.nsocry.assets.conversion;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nsocry.assets.ProgressionTable;
import java.util.EnumMap;
import org.junit.jupiter.api.Test;

class ReferenceGameDataProgressionParserTest {
    @Test
    void parsesAllTenTablesInNsocryWireMapping() {
        EnumMap<ProgressionTable, int[]> tables =
                ReferenceGameDataProgressionParser.parse(validSource());

        assertEquals(10, tables.size());
        assertArrayEquals(new int[] {1, 2}, tables.get(ProgressionTable.CRYSTAL_REQUIREMENT));
        assertArrayEquals(new int[] {80, 75}, tables.get(ProgressionTable.MAX_PERCENT));
    }

    @Test
    void acceptsWhitespaceAndNegativeIntegerLiteral() {
        String source = validSource().replace(
                "public static final int[] GOLD_UP = {9, 10};",
                "public static final int[] GOLD_UP = { -9,\n 10 };");

        assertArrayEquals(new int[] {-9, 10},
                ReferenceGameDataProgressionParser.parse(source).get(ProgressionTable.GOLD_COST));
    }

    @Test
    void rejectsMissingAuthoritativeTable() {
        String source = validSource().replace(
                "public static final int[] MAX_PERCENT = {80, 75};", "");

        assertThrows(IllegalArgumentException.class,
                () -> ReferenceGameDataProgressionParser.parse(source));
    }

    @Test
    void rejectsDuplicateDeclaration() {
        String duplicate = validSource()
                + "\npublic static final int[] UP_CRYSTAL = {3};";

        assertThrows(IllegalArgumentException.class,
                () -> ReferenceGameDataProgressionParser.parse(duplicate));
    }

    @Test
    void rejectsNonIntegerExpression() {
        String source = validSource().replace(
                "public static final int[] UP_WEAPON = {7, 8};",
                "public static final int[] UP_WEAPON = {7, 4 * 2};");

        assertThrows(IllegalArgumentException.class,
                () -> ReferenceGameDataProgressionParser.parse(source));
    }

    private static String validSource() {
        return String.join("\n",
                "public static final int[] UP_CRYSTAL = {1, 2};",
                "public static final int[] UP_CLOTHE = {3, 4};",
                "public static final int[] UP_ADORN = {5, 6};",
                "public static final int[] UP_WEAPON = {7, 8};",
                "public static final int[] COIN_UP_CRYSTAL = {1, 2};",
                "public static final int[] COIN_UP_CLOTHE = {3, 4};",
                "public static final int[] COIN_UP_ADORN = {5, 6};",
                "public static final int[] COIN_UP_WEAPON = {7, 8};",
                "public static final int[] GOLD_UP = {9, 10};",
                "public static final int[] MAX_PERCENT = {80, 75};");
    }
}
