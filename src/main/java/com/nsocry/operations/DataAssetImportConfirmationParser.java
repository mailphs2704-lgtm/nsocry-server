package com.nsocry.operations;

import com.nsocry.persistence.DataAssetOverwriteMode;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;

/** Parse câu xác nhận DATA import chính xác; không chấp nhận mặc định hoặc câu gần đúng. */
public final class DataAssetImportConfirmationParser {
    private DataAssetImportConfirmationParser() {
    }

    /**
     * Cú pháp: IMPORT DATA V<version> INSERT_ONLY|OVERWRITE <sha256>.
     * Version và checksum phải đúng candidate đã validate.
     */
    public static DataAssetOverwriteMode parse(
            byte expectedVersion,
            String expectedSha256,
            String confirmation) {
        Objects.requireNonNull(expectedSha256, "expectedSha256");
        if (confirmation == null) {
            throw new IllegalArgumentException("Thiếu xác nhận DATA import");
        }
        String sha = expectedSha256.toLowerCase(Locale.ROOT);
        String prefix = "IMPORT DATA V" + Byte.toUnsignedInt(expectedVersion) + " ";
        String insertOnly = prefix + "INSERT_ONLY " + sha;
        String overwrite = prefix + "OVERWRITE " + sha;
        if (constantTimeEquals(insertOnly, confirmation.trim())) {
            return DataAssetOverwriteMode.REJECT_EXISTING;
        }
        if (constantTimeEquals(overwrite, confirmation.trim())) {
            return DataAssetOverwriteMode.REPLACE_SAME_VERSION;
        }
        throw new IllegalArgumentException(
                "Xác nhận DATA import không khớp version/action/SHA-256");
    }

    private static boolean constantTimeEquals(String expected, String actual) {
        return MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.US_ASCII),
                actual.getBytes(StandardCharsets.US_ASCII));
    }
}
