package com.nsocry.bootstrap;

import com.nsocry.configuration.DatabaseConfiguration;
import com.nsocry.configuration.DatabaseConfigurationLoader;
import com.nsocry.operations.DataAssetImportAuthorization;
import com.nsocry.operations.DataAssetImportConfirmationParser;
import com.nsocry.operations.DataAssetImportSafetyGate;
import com.nsocry.operations.DataAssetImportWorkflow;
import com.nsocry.operations.DataAssetSeedArchiveService;
import com.nsocry.operations.ValidatedDataAssetSeedArchive;
import com.nsocry.persistence.DataAssetOverwriteMode;
import com.nsocry.persistence.DataAssetSchemaPreflightReport;
import com.nsocry.persistence.DataAssetSeedImportResult;
import com.nsocry.persistence.JdbcDataAssetSchemaInspector;
import com.nsocry.persistence.MariaDbDataSourceFactory;
import java.io.PrintStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;
import javax.sql.DataSource;

/**
 * Import DATA đã được chủ dự án cấp quyền riêng bằng REJECT_EXISTING.
 * Command không hỗ trợ overwrite, runtime publish hoặc startup wiring.
 */
public final class DataAssetSeedImportCommand {
    private DataAssetSeedImportCommand() {
    }

    /** Chạy đủ archive/backup/confirmation/schema/transaction/read-back gates. */
    public static void main(String[] args) throws Exception {
        if (args == null || args.length != 1) {
            throw new IllegalArgumentException(
                    "data-seed-import yêu cầu đúng một plan properties path");
        }
        Path planPath = Path.of(args[0]).toAbsolutePath().normalize();
        Properties properties = load(planPath);
        Path base = Objects.requireNonNull(planPath.getParent(), "plan parent");
        ValidatedDataAssetSeedArchive archive = new DataAssetSeedArchiveService()
                .readValidated(resolve(base, require(properties, "archive.path")));

        DataAssetOverwriteMode mode = DataAssetImportConfirmationParser.parse(
                archive.validation().version(), archive.validation().payloadSha256(),
                require(properties, "confirmation"));
        requireRejectExisting(mode);

        DataAssetImportAuthorization authorization = DataAssetImportSafetyGate.authorize(
                resolve(base, require(properties, "backup.path")),
                require(properties, "backup.sha256"),
                archive.validation().payloadSha256(),
                archive.validation().payloadSha256(),
                mode);

        DatabaseConfiguration configuration = new DatabaseConfigurationLoader()
                .load(Path.of("config", "nsocry.properties"), System.getenv());
        DataSource dataSource = MariaDbDataSourceFactory.create(configuration);
        DataAssetSchemaPreflightReport schema =
                new JdbcDataAssetSchemaInspector(dataSource).inspect();
        DataAssetSeedImportResult result =
                new DataAssetImportWorkflow(dataSource).execute(archive, authorization, schema);
        printReport(result, System.out);
    }

    /** Khóa phạm vi quyền hiện tại: tuyệt đối không chấp nhận OVERWRITE. */
    static void requireRejectExisting(DataAssetOverwriteMode mode) {
        if (mode != DataAssetOverwriteMode.REJECT_EXISTING) {
            throw new IllegalArgumentException(
                    "Quyền hiện tại chỉ cho phép DATA import REJECT_EXISTING");
        }
    }

    /** In kết quả sau transaction và read-back; runtime/startup vẫn không đổi. */
    static void printReport(DataAssetSeedImportResult result, PrintStream output) {
        Objects.requireNonNull(result, "result");
        Objects.requireNonNull(output, "output");
        output.println("DATA seed IMPORTED_AND_VERIFIED");
        output.println("version=" + Byte.toUnsignedInt(result.validation().version()));
        output.println("taskGroupCount=" + result.validation().taskGroupCount());
        output.println("experienceCount=" + result.validation().experienceCount());
        output.println("payloadLength=" + result.validation().payloadLength());
        output.println("sha256=" + result.validation().payloadSha256());
        output.println("overwritten=" + result.overwritten());
        output.println("databaseChanged=true");
        output.println("dataImported=true");
        output.println("runtimeSnapshotPublished=false");
        output.println("serverStartupWired=false");
    }

    private static Properties load(Path path) throws Exception {
        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            properties.load(reader);
        }
        return properties;
    }

    private static String require(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Thiếu DATA import property: " + key);
        }
        return value.trim();
    }

    private static Path resolve(Path base, String value) {
        Path path = Path.of(value);
        return (path.isAbsolute() ? path : base.resolve(path)).normalize();
    }
}
