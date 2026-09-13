package com.nsocry.operations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nsocry.persistence.DataAssetOverwriteMode;
import org.junit.jupiter.api.Test;

class DataAssetImportConfirmationParserTest {
    private static final String SHA =
            "242a3551cc110c4eda9f8e40f06fcd0f0b0b2d32bcab6f1b07669dbd0c9b148b";

    @Test
    void mapsExactInsertOnlyConfirmationToRejectExisting() {
        assertEquals(DataAssetOverwriteMode.REJECT_EXISTING,
                DataAssetImportConfirmationParser.parse(
                        (byte) 7, SHA, "IMPORT DATA V7 INSERT_ONLY " + SHA));
    }

    @Test
    void mapsExactOverwriteConfirmationToReplaceSameVersion() {
        assertEquals(DataAssetOverwriteMode.REPLACE_SAME_VERSION,
                DataAssetImportConfirmationParser.parse(
                        (byte) 7, SHA, "IMPORT DATA V7 OVERWRITE " + SHA));
    }

    @Test
    void rejectsWrongVersionOrChecksum() {
        assertThrows(IllegalArgumentException.class, () ->
                DataAssetImportConfirmationParser.parse(
                        (byte) 7, SHA, "IMPORT DATA V6 INSERT_ONLY " + SHA));
        assertThrows(IllegalArgumentException.class, () ->
                DataAssetImportConfirmationParser.parse(
                        (byte) 7, SHA, "IMPORT DATA V7 INSERT_ONLY " + "0".repeat(64)));
    }

    @Test
    void rejectsMissingOrAmbiguousAction() {
        assertThrows(IllegalArgumentException.class, () ->
                DataAssetImportConfirmationParser.parse((byte) 7, SHA, null));
        assertThrows(IllegalArgumentException.class, () ->
                DataAssetImportConfirmationParser.parse(
                        (byte) 7, SHA, "IMPORT DATA V7 " + SHA));
    }
}
