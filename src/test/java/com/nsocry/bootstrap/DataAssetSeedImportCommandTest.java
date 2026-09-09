package com.nsocry.bootstrap;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nsocry.assets.DataAssetSeedValidationResult;
import com.nsocry.persistence.DataAssetOverwriteMode;
import com.nsocry.persistence.DataAssetSeedImportResult;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class DataAssetSeedImportCommandTest {
    @Test
    void currentAuthorizationRejectsOverwriteMode() {
        assertThrows(IllegalArgumentException.class, () ->
                DataAssetSeedImportCommand.requireRejectExisting(
                        DataAssetOverwriteMode.REPLACE_SAME_VERSION));
        DataAssetSeedImportCommand.requireRejectExisting(
                DataAssetOverwriteMode.REJECT_EXISTING);
    }

    @Test
    void successReportRequiresImportAndReadBackResult() {
        var validation = new DataAssetSeedValidationResult(
                (byte) 7, 43, 131, 85154,
                "242a3551cc110c4eda9f8e40f06fcd0f0b0b2d32bcab6f1b07669dbd0c9b148b");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        DataAssetSeedImportCommand.printReport(
                new DataAssetSeedImportResult(validation, false),
                new PrintStream(bytes, true, StandardCharsets.UTF_8));

        String report = bytes.toString(StandardCharsets.UTF_8);
        assertTrue(report.contains("DATA seed IMPORTED_AND_VERIFIED"));
        assertTrue(report.contains("overwritten=false"));
        assertTrue(report.contains("databaseChanged=true"));
        assertTrue(report.contains("dataImported=true"));
        assertTrue(report.contains("runtimeSnapshotPublished=false"));
        assertTrue(report.contains("serverStartupWired=false"));
    }
}
