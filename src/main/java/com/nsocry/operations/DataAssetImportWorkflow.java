package com.nsocry.operations;

import com.nsocry.assets.DataAssetSeedValidationResult;
import com.nsocry.persistence.DataAssetSchemaPreflightReport;
import com.nsocry.persistence.DataAssetSeedImportException;
import com.nsocry.persistence.DataAssetSeedImportResult;
import com.nsocry.persistence.JdbcDataAssetSeedImporter;
import com.nsocry.persistence.JdbcDataAssetSeedVerifier;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Objects;
import javax.sql.DataSource;

/**
 * Ghép authorization, schema gate, transaction import và read-back verify.
 * Lớp này không được route ra launcher trước khi chủ dự án cấp quyền import riêng.
 */
public final class DataAssetImportWorkflow {
    private final ImportStep importer;
    private final VerifyStep verifier;

    /** Composition production dùng chung DataSource, nhưng chưa mở connection tại constructor. */
    public DataAssetImportWorkflow(DataSource dataSource) {
        Objects.requireNonNull(dataSource, "dataSource");
        JdbcDataAssetSeedImporter jdbcImporter = new JdbcDataAssetSeedImporter(dataSource);
        JdbcDataAssetSeedVerifier jdbcVerifier = new JdbcDataAssetSeedVerifier(dataSource);
        this.importer = jdbcImporter::importSeed;
        this.verifier = jdbcVerifier::verify;
    }

    /** Constructor test khóa thứ tự mà không mở database thật. */
    DataAssetImportWorkflow(ImportStep importer, VerifyStep verifier) {
        this.importer = Objects.requireNonNull(importer, "importer");
        this.verifier = Objects.requireNonNull(verifier, "verifier");
    }

    /**
     * Fail trước DML nếu schema/authorization sai; sau commit bắt buộc read-back checksum.
     */
    public DataAssetSeedImportResult execute(
            ValidatedDataAssetSeedArchive archive,
            DataAssetImportAuthorization authorization,
            DataAssetSchemaPreflightReport schema) throws DataAssetSeedImportException {
        Objects.requireNonNull(archive, "archive");
        Objects.requireNonNull(authorization, "authorization");
        Objects.requireNonNull(schema, "schema");
        if (!schema.ready()) {
            throw new DataAssetSeedImportException("DATA schema preflight NOT_READY");
        }
        if (!constantTimeEquals(
                archive.validation().payloadSha256(), authorization.candidateSha256())) {
            throw new DataAssetSeedImportException(
                    "DATA authorization không thuộc candidate archive");
        }

        DataAssetSeedImportResult imported =
                importer.importSeed(archive, authorization.overwriteMode());
        DataAssetSeedValidationResult verified = verifier.verify(archive);
        if (!imported.validation().equals(verified)) {
            throw new DataAssetSeedImportException(
                    "DATA read-back metadata không khớp kết quả transaction");
        }
        return imported;
    }

    private static boolean constantTimeEquals(String expected, String actual) {
        return MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.US_ASCII),
                actual.getBytes(StandardCharsets.US_ASCII));
    }

    @FunctionalInterface
    interface ImportStep {
        DataAssetSeedImportResult importSeed(
                ValidatedDataAssetSeedArchive archive,
                com.nsocry.persistence.DataAssetOverwriteMode mode)
                throws DataAssetSeedImportException;
    }

    @FunctionalInterface
    interface VerifyStep {
        DataAssetSeedValidationResult verify(ValidatedDataAssetSeedArchive archive)
                throws DataAssetSeedImportException;
    }
}
