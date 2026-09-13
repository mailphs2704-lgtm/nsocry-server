package com.nsocry.bootstrap;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nsocry.assets.DataAssetSeedValidationResult;
import com.nsocry.persistence.DataAssetSchemaPreflightReport;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;

class DataAssetDatabaseVerifyCommandTest {
    @Test
    void rejectsSchemaThatIsNotReady() {
        assertThrows(IllegalStateException.class, () ->
                DataAssetDatabaseVerifyCommand.requireReady(
                        new DataAssetSchemaPreflightReport(false, List.of("Thiếu cột"))));
    }

    @Test
    void reportLocksReadOnlySideEffects() {
        var result = new DataAssetSeedValidationResult(
                (byte) 7, 43, 131, 85154,
                "242a3551cc110c4eda9f8e40f06fcd0f0b0b2d32bcab6f1b07669dbd0c9b148b");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        DataAssetDatabaseVerifyCommand.printReport(
                result, new PrintStream(bytes, true, StandardCharsets.UTF_8));

        String report = bytes.toString(StandardCharsets.UTF_8);
        assertTrue(report.contains("DATA database payload VERIFIED"));
        assertTrue(report.contains("version=7"));
        assertTrue(report.contains("databaseChanged=false"));
        assertTrue(report.contains("dataImported=false"));
        assertTrue(report.contains("runtimeSnapshotPublished=false"));
        assertTrue(report.contains("serverStartupWired=false"));
    }
}
