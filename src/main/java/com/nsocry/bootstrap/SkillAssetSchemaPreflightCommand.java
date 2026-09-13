package com.nsocry.bootstrap;

import com.nsocry.configuration.DatabaseConfiguration;
import com.nsocry.configuration.DatabaseConfigurationLoader;
import com.nsocry.persistence.JdbcSkillAssetSchemaInspector;
import com.nsocry.persistence.MariaDbDataSourceFactory;
import com.nsocry.persistence.SkillAssetSchemaPreflightReport;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Objects;
import javax.sql.DataSource;

/** Command chỉ đọc kiểm tra database hiện tại có khớp schema SKILL V003 hay không. */
public final class SkillAssetSchemaPreflightCommand {
    private SkillAssetSchemaPreflightCommand() {
    }

    /** Nạp config, mở DataSource read-only và trả lỗi khi schema chưa sẵn sàng. */
    public static void main(String[] args) throws Exception {
        if (args == null || args.length > 1) {
            throw new IllegalArgumentException("skill-schema-preflight nhận tối đa một config path");
        }
        Path path = args.length == 0 ? Path.of("config", "nsocry.properties") : Path.of(args[0]);
        DatabaseConfiguration configuration = new DatabaseConfigurationLoader().load(path, System.getenv());
        DataSource dataSource = MariaDbDataSourceFactory.create(configuration);
        SkillAssetSchemaPreflightReport report = new JdbcSkillAssetSchemaInspector(dataSource).inspect();
        if (!printReport(report, System.out)) {
            throw new IllegalStateException("SKILL schema preflight NOT_READY");
        }
    }

    /** In difference mà không lộ URL/user/password; trả true khi đúng V003. */
    static boolean printReport(SkillAssetSchemaPreflightReport report, PrintStream output) {
        Objects.requireNonNull(report, "report");
        Objects.requireNonNull(output, "output");
        output.println("SKILL schema preflight " + (report.ready() ? "READY" : "NOT_READY"));
        for (String difference : report.differences()) output.println("difference=" + difference);
        output.println("databaseChanged=false");
        return report.ready();
    }
}
