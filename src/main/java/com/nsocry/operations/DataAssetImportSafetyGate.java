package com.nsocry.operations;

import com.nsocry.persistence.DataAssetOverwriteMode;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Objects;

/** Gate offline fail-closed cho backup và xác nhận candidate trước mọi thao tác DATA JDBC. */
public final class DataAssetImportSafetyGate {
    private DataAssetImportSafetyGate() {
    }

    /**
     * Xác minh file backup thật, checksum backup và xác nhận candidate; không mở database.
     * expectedBackupSha256 phải đến từ checkpoint backup đã được chủ dự án duyệt.
     */
    public static DataAssetImportAuthorization authorize(
            Path backupPath,
            String expectedBackupSha256,
            String expectedCandidateSha256,
            String candidateConfirmation,
            DataAssetOverwriteMode overwriteMode) throws IOException {
        Path backup = Objects.requireNonNull(backupPath, "backupPath").toAbsolutePath().normalize();
        if (!Files.isRegularFile(backup)) {
            throw new IOException("Không tìm thấy file backup DATA hợp lệ");
        }
        long size = Files.size(backup);
        if (size <= 0) {
            throw new IOException("File backup DATA rỗng");
        }
        String expectedBackup = normalizeSha256(expectedBackupSha256, "expectedBackupSha256");
        String expectedCandidate = normalizeSha256(expectedCandidateSha256, "expectedCandidateSha256");
        String confirmation = normalizeSha256(candidateConfirmation, "candidateConfirmation");
        String actualBackup = sha256(backup);
        if (!constantTimeEquals(expectedBackup, actualBackup)) {
            throw new IOException("SHA-256 backup DATA không khớp checkpoint");
        }
        if (!constantTimeEquals(expectedCandidate, confirmation)) {
            throw new IllegalArgumentException("SHA-256 xác nhận DATA candidate không khớp");
        }
        return new DataAssetImportAuthorization(
                backup, size, actualBackup, expectedCandidate,
                Objects.requireNonNull(overwriteMode, "overwriteMode"));
    }

    private static String normalizeSha256(String value, String field) {
        Objects.requireNonNull(value, field);
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        if (normalized.length() != 64) {
            throw new IllegalArgumentException(field + " phải có 64 ký tự hex");
        }
        HexFormat.of().parseHex(normalized);
        return normalized;
    }

    private static String sha256(Path path) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try (InputStream input = Files.newInputStream(path)) {
                byte[] buffer = new byte[8192];
                int read;
                while ((read = input.read(buffer)) != -1) {
                    digest.update(buffer, 0, read);
                }
            }
            return HexFormat.of().formatHex(digest.digest());
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("JVM không hỗ trợ SHA-256", exception);
        }
    }

    private static boolean constantTimeEquals(String expected, String actual) {
        return MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.US_ASCII),
                actual.getBytes(StandardCharsets.US_ASCII));
    }
}
