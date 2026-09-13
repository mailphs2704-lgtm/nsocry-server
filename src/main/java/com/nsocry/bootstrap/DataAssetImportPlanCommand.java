package com.nsocry.bootstrap;

import com.nsocry.operations.DataAssetImportAuthorization;
import com.nsocry.operations.DataAssetImportConfirmationParser;
import com.nsocry.operations.DataAssetImportSafetyGate;
import com.nsocry.operations.DataAssetSeedArchiveService;
import com.nsocry.operations.ValidatedDataAssetSeedArchive;
import com.nsocry.persistence.DataAssetOverwriteMode;
import java.io.PrintStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;

/** Diễn tập authorization DATA import hoàn toàn offline; không tạo DataSource hoặc chạy DML. */
public final class DataAssetImportPlanCommand {
    private DataAssetImportPlanCommand() {
    }

    /** Đọc plan properties, validate archive/backup/confirmation rồi in bằng chứng side-effect false. */
    public static void main(String[] args) throws Exception {
        if (args == null || args.length != 1) {
            throw new IllegalArgumentException(
                    "data-seed-import-plan yêu cầu đúng một properties path");
        }
        Path planPath = Path.of(args[0]).toAbsolutePath().normalize();
        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(planPath, StandardCharsets.UTF_8)) {
            properties.load(reader);
        }
        Path base = Objects.requireNonNull(planPath.getParent(), "plan parent");
        Path archivePath = resolve(base, require(properties, "archive.path"));
        Path backupPath = resolve(base, require(properties, "backup.path"));
        String backupSha256 = require(properties, "backup.sha256");
        String confirmation = require(properties, "confirmation");

        ValidatedDataAssetSeedArchive archive =
                new DataAssetSeedArchiveService().readValidated(archivePath);
        DataAssetOverwriteMode mode = DataAssetImportConfirmationParser.parse(
                archive.validation().version(),
                archive.validation().payloadSha256(),
                confirmation);
        DataAssetImportAuthorization authorization = DataAssetImportSafetyGate.authorize(
                backupPath,
                backupSha256,
                archive.validation().payloadSha256(),
                archive.validation().payloadSha256(),
                mode);
        printReport(archive, authorization, System.out);
    }

    /** In plan đã authorize và khẳng định không mở database/import/runtime. */
    static void printReport(
            ValidatedDataAssetSeedArchive archive,
            DataAssetImportAuthorization authorization,
            PrintStream output) {
        Objects.requireNonNull(archive, "archive");
        Objects.requireNonNull(authorization, "authorization");
        Objects.requireNonNull(output, "output");
        output.println("DATA import plan AUTHORIZED_OFFLINE");
        output.println("version=" + Byte.toUnsignedInt(archive.validation().version()));
        output.println("payloadSha256=" + archive.validation().payloadSha256());
        output.println("backupPath=" + authorization.backupPath());
        output.println("backupSize=" + authorization.backupSize());
        output.println("backupSha256=" + authorization.backupSha256());
        output.println("overwriteMode=" + authorization.overwriteMode());
        output.println("databaseConnectionOpened=false");
        output.println("databaseChanged=false");
        output.println("dataImported=false");
        output.println("runtimeSnapshotPublished=false");
        output.println("serverStartupWired=false");
    }

    private static String require(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Thiếu DATA import plan property: " + key);
        }
        return value.trim();
    }

    private static Path resolve(Path base, String value) {
        Path path = Path.of(value);
        return (path.isAbsolute() ? path : base.resolve(path)).normalize();
    }
}
