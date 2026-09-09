package com.nsocry.bootstrap;

import com.nsocry.assets.DataAssetSeedValidationResult;
import com.nsocry.configuration.DatabaseConfiguration;
import com.nsocry.configuration.DatabaseConfigurationLoader;
import com.nsocry.operations.DataAssetSeedArchiveService;
import com.nsocry.operations.ValidatedDataAssetSeedArchive;
import com.nsocry.persistence.DataAssetSchemaPreflightReport;
import com.nsocry.persistence.JdbcDataAssetSchemaInspector;
import com.nsocry.persistence.JdbcDataAssetSeedVerifier;
import com.nsocry.persistence.MariaDbDataSourceFactory;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Objects;
import javax.sql.DataSource;

/** Xác minh row DATA trong database khớp archive authoritative mà không ghi dữ liệu. */
public final class DataAssetDatabaseVerifyCommand {
    private DataAssetDatabaseVerifyCommand() {
    }

    /** Đọc archive, preflight schema và read-back đủ bảy cột trên connection read-only. */
    public static void main(String[] args) throws Exception {
        if (args == null || args.length != 1) {
            throw new IllegalArgumentException("data-seed-db-verify yêu cầu đúng một archive path");
        }
        ValidatedDataAssetSeedArchive archive = new DataAssetSeedArchiveService()
                .readValidated(Path.of(args[0]));
        DatabaseConfiguration configuration = new DatabaseConfigurationLoader()
                .load(Path.of("config", "nsocry.properties"), System.getenv());
        DataSource dataSource = MariaDbDataSourceFactory.create(configuration);
        requireReady(new JdbcDataAssetSchemaInspector(dataSource).inspect());
        DataAssetSeedValidationResult result =
                new JdbcDataAssetSeedVerifier(dataSource).verify(archive);
        printReport(result, System.out);
    }

    /** Chặn read-back khi schema không còn đúng V005. */
    static void requireReady(DataAssetSchemaPreflightReport report) {
        Objects.requireNonNull(report, "report");
        if (!report.ready()) {
            throw new IllegalStateException("DATA schema preflight NOT_READY");
        }
    }

    /** In bằng chứng checksum và các cờ bất biến của command chỉ đọc. */
    static void printReport(DataAssetSeedValidationResult result, PrintStream output) {
        Objects.requireNonNull(result, "result");
        Objects.requireNonNull(output, "output");
        output.println("DATA database payload VERIFIED");
        output.println("version=" + Byte.toUnsignedInt(result.version()));
        output.println("taskGroupCount=" + result.taskGroupCount());
        output.println("experienceCount=" + result.experienceCount());
        output.println("payloadLength=" + result.payloadLength());
        output.println("sha256=" + result.payloadSha256());
        output.println("databaseChanged=false");
        output.println("dataImported=false");
        output.println("runtimeSnapshotPublished=false");
        output.println("serverStartupWired=false");
    }
}
