package com.nsocry.bootstrap;

import com.nsocry.configuration.DatabaseConfiguration;
import com.nsocry.configuration.DatabaseConfigurationLoader;
import com.nsocry.persistence.DataAssetSchemaPreflightReport;
import com.nsocry.persistence.JdbcDataAssetSchemaInspector;
import com.nsocry.persistence.MariaDbDataSourceFactory;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Objects;
import javax.sql.DataSource;

/** Command chỉ đọc kiểm tra database có khớp schema DATA V005 hay không. */
public final class DataAssetSchemaPreflightCommand {
    private DataAssetSchemaPreflightCommand() {
    }

    /** Nạp config, mở DataSource read-only và trả lỗi khi schema chưa sẵn sàng. */
    public static void main(String[] args) throws Exception {
        if (args == null || args.length > 1) {
            throw new IllegalArgumentException("data-schema-preflight nhận tối đa một config path");
        }
        Path path = args.length == 0 ? Path.of("config", "nsocry.properties") : Path.of(args[0]);
        DatabaseConfiguration configuration = new DatabaseConfigurationLoader().load(path, System.getenv());
        DataSource dataSource = MariaDbDataSourceFactory.create(configuration);
        DataAssetSchemaPreflightReport report = new JdbcDataAssetSchemaInspector(dataSource).inspect();
        if (!printReport(report, System.out)) {
            throw new IllegalStateException("DATA schema preflight NOT_READY");
        }
    }

    /** In difference không lộ credential; trả true khi đúng V005. */
    static boolean printReport(DataAssetSchemaPreflightReport report, PrintStream output) {
        Objects.requireNonNull(report, "report");
        Objects.requireNonNull(output, "output");
        output.println("DATA schema preflight " + (report.ready() ? "READY" : "NOT_READY"));
        for (String difference : report.differences()) {
            output.println("difference=" + difference);
        }
        output.println("databaseChanged=false");
        return report.ready();
    }
}
