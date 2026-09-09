package com.nsocry.operations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nsocry.assets.DataAssetSeedValidationResult;
import com.nsocry.persistence.DataAssetOverwriteMode;
import com.nsocry.persistence.DataAssetSchemaPreflightReport;
import com.nsocry.persistence.DataAssetSeedImportException;
import com.nsocry.persistence.DataAssetSeedImportResult;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class DataAssetImportWorkflowTest {
    private static final String SHA = "a".repeat(64);
    private static final DataAssetSeedValidationResult VALIDATION =
            new DataAssetSeedValidationResult((byte) 7, 43, 131, 3, SHA);
    private static final ValidatedDataAssetSeedArchive ARCHIVE =
            new ValidatedDataAssetSeedArchive(new byte[] {7, 1, 2}, "manifest", VALIDATION);

    @Test
    void importsThenVerifiesInStrictOrder() throws Exception {
        List<String> order = new ArrayList<>();
        DataAssetImportWorkflow workflow = new DataAssetImportWorkflow(
                (archive, mode) -> {
                    order.add("import");
                    return new DataAssetSeedImportResult(VALIDATION, false);
                },
                archive -> {
                    order.add("verify");
                    return VALIDATION;
                });

        DataAssetSeedImportResult result =
                workflow.execute(ARCHIVE, authorization(SHA),
                        new DataAssetSchemaPreflightReport(true, List.of()));

        assertEquals(List.of("import", "verify"), order);
        assertEquals(VALIDATION, result.validation());
    }

    @Test
    void rejectsNotReadySchemaBeforeImporter() {
        List<String> order = new ArrayList<>();
        DataAssetImportWorkflow workflow = new DataAssetImportWorkflow(
                (archive, mode) -> {
                    order.add("import");
                    return new DataAssetSeedImportResult(VALIDATION, false);
                },
                archive -> VALIDATION);

        assertThrows(DataAssetSeedImportException.class, () ->
                workflow.execute(ARCHIVE, authorization(SHA),
                        new DataAssetSchemaPreflightReport(false, List.of("missing"))));
        assertEquals(List.of(), order);
    }

    @Test
    void rejectsAuthorizationForDifferentCandidateBeforeImporter() {
        List<String> order = new ArrayList<>();
        DataAssetImportWorkflow workflow = new DataAssetImportWorkflow(
                (archive, mode) -> {
                    order.add("import");
                    return new DataAssetSeedImportResult(VALIDATION, false);
                },
                archive -> VALIDATION);

        assertThrows(DataAssetSeedImportException.class, () ->
                workflow.execute(ARCHIVE, authorization("b".repeat(64)),
                        new DataAssetSchemaPreflightReport(true, List.of())));
        assertEquals(List.of(), order);
    }

    @Test
    void propagatesReadBackFailureAfterImport() {
        DataAssetImportWorkflow workflow = new DataAssetImportWorkflow(
                (archive, mode) -> new DataAssetSeedImportResult(VALIDATION, false),
                archive -> { throw new DataAssetSeedImportException("read-back failed"); });

        assertThrows(DataAssetSeedImportException.class, () ->
                workflow.execute(ARCHIVE, authorization(SHA),
                        new DataAssetSchemaPreflightReport(true, List.of())));
    }

    private static DataAssetImportAuthorization authorization(String candidateSha) {
        return new DataAssetImportAuthorization(
                Path.of("backup.sql"), 1, "c".repeat(64), candidateSha,
                DataAssetOverwriteMode.REJECT_EXISTING);
    }
}
