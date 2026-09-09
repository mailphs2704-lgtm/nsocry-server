package com.nsocry.bootstrap;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nsocry.assets.DataAssetSeedValidationResult;
import com.nsocry.operations.DataAssetImportAuthorization;
import com.nsocry.operations.ValidatedDataAssetSeedArchive;
import com.nsocry.persistence.DataAssetOverwriteMode;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class DataAssetImportPlanCommandTest {
    @Test
    void reportProvesPlanIsOfflineAndHasNoSideEffects() {
        String sha = "a".repeat(64);
        var validation = new DataAssetSeedValidationResult((byte) 7, 43, 131, 3, sha);
        var archive = validatedArchive(new byte[] {7, 1, 2}, "manifest", validation);
        var authorization = new DataAssetImportAuthorization(
                Path.of("backup.sql"), 234839, "b".repeat(64), sha,
                DataAssetOverwriteMode.REJECT_EXISTING);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        DataAssetImportPlanCommand.printReport(
                archive, authorization,
                new PrintStream(bytes, true, StandardCharsets.UTF_8));

        String report = bytes.toString(StandardCharsets.UTF_8);
        assertTrue(report.contains("DATA import plan AUTHORIZED_OFFLINE"));
        assertTrue(report.contains("overwriteMode=REJECT_EXISTING"));
        assertTrue(report.contains("databaseConnectionOpened=false"));
        assertTrue(report.contains("databaseChanged=false"));
        assertTrue(report.contains("dataImported=false"));
    }

    private static ValidatedDataAssetSeedArchive validatedArchive(
            byte[] payload, String manifest, DataAssetSeedValidationResult validation) {
        try {
            var constructor = ValidatedDataAssetSeedArchive.class.getDeclaredConstructor(
                    byte[].class, String.class, DataAssetSeedValidationResult.class);
            constructor.setAccessible(true);
            return constructor.newInstance(payload, manifest, validation);
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError(exception);
        }
    }
}
