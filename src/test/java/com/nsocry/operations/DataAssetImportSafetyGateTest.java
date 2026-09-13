package com.nsocry.operations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nsocry.persistence.DataAssetOverwriteMode;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HexFormat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DataAssetImportSafetyGateTest {
    private static final String CANDIDATE_SHA =
            "242a3551cc110c4eda9f8e40f06fcd0f0b0b2d32bcab6f1b07669dbd0c9b148b";

    @TempDir
    Path directory;

    @Test
    void authorizesNonEmptyBackupAndExactChecksums() throws Exception {
        Path backup = directory.resolve("before-v005.sql");
        Files.writeString(backup, "CREATE TABLE checkpoint;");
        String backupSha = sha256(Files.readAllBytes(backup));

        DataAssetImportAuthorization result = DataAssetImportSafetyGate.authorize(
                backup, backupSha, CANDIDATE_SHA, CANDIDATE_SHA,
                DataAssetOverwriteMode.REJECT_EXISTING);

        assertEquals(Files.size(backup), result.backupSize());
        assertEquals(backupSha, result.backupSha256());
        assertEquals(DataAssetOverwriteMode.REJECT_EXISTING, result.overwriteMode());
    }

    @Test
    void rejectsEmptyBackup() throws Exception {
        Path backup = Files.createFile(directory.resolve("empty.sql"));
        assertThrows(IOException.class, () -> DataAssetImportSafetyGate.authorize(
                backup, "0".repeat(64), CANDIDATE_SHA, CANDIDATE_SHA,
                DataAssetOverwriteMode.REJECT_EXISTING));
    }

    @Test
    void rejectsBackupChecksumMismatch() throws Exception {
        Path backup = directory.resolve("backup.sql");
        Files.writeString(backup, "valid");
        assertThrows(IOException.class, () -> DataAssetImportSafetyGate.authorize(
                backup, "0".repeat(64), CANDIDATE_SHA, CANDIDATE_SHA,
                DataAssetOverwriteMode.REJECT_EXISTING));
    }

    @Test
    void rejectsCandidateConfirmationMismatch() throws Exception {
        Path backup = directory.resolve("backup-confirm.sql");
        Files.writeString(backup, "valid");
        String backupSha = sha256(Files.readAllBytes(backup));
        assertThrows(IllegalArgumentException.class, () -> DataAssetImportSafetyGate.authorize(
                backup, backupSha, CANDIDATE_SHA, "0".repeat(64),
                DataAssetOverwriteMode.REPLACE_SAME_VERSION));
    }

    private static String sha256(byte[] bytes) throws Exception {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
    }
}
