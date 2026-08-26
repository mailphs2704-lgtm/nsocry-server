package com.nsocry.assets.conversion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ReferenceDataDumpInventoryParserTest {
    @Test
    void inventoriesAllAuthoritativeSources() {
        DataDumpInventoryReport report = ReferenceDataDumpInventoryParser.parse(validDump());

        assertEquals(1, report.arrowCount());
        assertEquals(1, report.effectPaintCount());
        assertEquals(1, report.imageCount());
        assertEquals(1, report.partCount());
        assertEquals(1, report.skillPaintCount());
        assertEquals(1, report.taskGroupCount());
        assertEquals(2, report.experienceCount());
        assertEquals(1, report.effectTemplateCount());
        assertEquals(2, report.rawByteDifferences());
    }

    @Test
    void acceptsReferenceEffectImageOutsideSignedShort() {
        String dump = validDump().replace("\"imgId\":4", "\"imgId\":260910");

        DataDumpInventoryReport report = ReferenceDataDumpInventoryParser.parse(dump);
        assertEquals(1, report.effectPaintCount());
    }

    @Test
    void acceptsJsonSimpleMissingObjectCommaCompatibility() {
        String dump = validDump().replace(
                "{\"id\":5,\"dx\":0,\"dy\":0}",
                "{\"id\":5,\"dx\":0\"dy\":0}");

        DataDumpInventoryReport report = ReferenceDataDumpInventoryParser.parse(dump);
        assertEquals(1, report.partCount());
    }

    @Test
    void stillRejectsMissingArrayComma() {
        String dump = validDump().replace("[100,200]", "[100 200]");

        assertThrows(IllegalArgumentException.class,
                () -> ReferenceDataDumpInventoryParser.parse(dump));
    }

    @Test
    void rejectsDuplicateMarker() {
        assertThrows(IllegalArgumentException.class,
                () -> ReferenceDataDumpInventoryParser.parse(validDump()
                        + "\n" + statement(ReferenceDataDumpInventoryParser.ARROW_MARKER,
                        "(2, '[4,5,6]')")));
    }

    @Test
    void rejectsWrongArity() {
        String dump = validDump().replace("(1, '[1,2,3]')", "(1, '[1,2,3]', 4)");
        assertThrows(IllegalArgumentException.class,
                () -> ReferenceDataDumpInventoryParser.parse(dump));
    }

    @Test
    void rejectsTaskArraysWithDifferentLengths() {
        String dump = validDump().replace("(0, '[200]', '[2]')", "(0, '[200,1]', '[2]')");
        assertThrows(IllegalArgumentException.class,
                () -> ReferenceDataDumpInventoryParser.parse(dump));
    }

    @Test
    void rejectsJsonTrailingBytes() {
        String dump = validDump().replace("'[1,2,3]'", "'[1,2,3]x'");
        assertThrows(IllegalArgumentException.class,
                () -> ReferenceDataDumpInventoryParser.parse(dump));
    }

    @Test
    void rejectsSignedByteCountAboveWireLimit() {
        StringBuilder experience = new StringBuilder("[");
        for (int index = 0; index < 128; index++) {
            if (index > 0) experience.append(',');
            experience.append(index);
        }
        experience.append(']');
        String dump = validDump().replace("[100,200]", experience);
        assertThrows(IllegalArgumentException.class,
                () -> ReferenceDataDumpInventoryParser.parse(dump));
    }

    @Test
    void rejectsNonAscendingIdsInsteadOfSilentlySorting() {
        String dump = validDump().replace("(1, '[1,2,3]')", "(2, '[1,2,3]'),(1, '[4,5,6]')");
        assertThrows(IllegalArgumentException.class,
                () -> ReferenceDataDumpInventoryParser.parse(dump));
    }

    private static String validDump() {
        return String.join("\n",
                statement(ReferenceDataDumpInventoryParser.ARROW_MARKER, "(1, '[1,2,3]')"),
                statement(ReferenceDataDumpInventoryParser.EFFECT_PAINT_MARKER,
                        "(1, '[{\"imgId\":4,\"dx\":128,\"dy\":-1}]')"),
                statement(ReferenceDataDumpInventoryParser.IMAGE_MARKER, "(1, '[0,1,2,3,4]')"),
                statement(ReferenceDataDumpInventoryParser.PART_MARKER,
                        "(0, 2, '[{\"id\":5,\"dx\":0,\"dy\":0}]')"),
                statement(ReferenceDataDumpInventoryParser.SKILL_PAINT_MARKER,
                        "(1, 0, 1, 1, '[]', '[]')"),
                statement(ReferenceDataDumpInventoryParser.TASK_MARKER, "(0, '[200]', '[2]')"),
                statement(ReferenceDataDumpInventoryParser.OTHERS_MARKER,
                        "(1, 'exp', '[100,200]')"),
                statement(ReferenceDataDumpInventoryParser.EFFECT_TEMPLATE_MARKER,
                        "(0, 'Food', 0, 10)"));
    }

    private static String statement(String marker, String rows) {
        return marker + "\n" + rows + ";";
    }
}
