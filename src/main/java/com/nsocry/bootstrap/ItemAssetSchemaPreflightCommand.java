package com.nsocry.bootstrap;

import com.nsocry.configuration.DatabaseConfiguration;
import com.nsocry.configuration.DatabaseConfigurationLoader;
import com.nsocry.persistence.ItemAssetSchemaPreflightReport;
import com.nsocry.persistence.JdbcItemAssetSchemaInspector;
import com.nsocry.persistence.MariaDbDataSourceFactory;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Objects;
import javax.sql.DataSource;

/** Command chỉ đọc kiểm tra database hiện tại có khớp schema ITEM V002 hay không. */
public final class ItemAssetSchemaPreflightCommand {
    private ItemAssetSchemaPreflightCommand() {
    }

    /** Nạp config, mở DataSource chỉ đọc và dừng với lỗi nếu schema chưa sẵn sàng. */
    public static void main(String[] args) throws Exception {
        if (args == null || args.length > 1) {
            throw new IllegalArgumentException("item-schema-preflight nhận tối đa một config path");
        }
        Path path = args.length == 0 ? Path.of("config", "nsocry.properties") : Path.of(args[0]);
        DatabaseConfiguration configuration = new DatabaseConfigurationLoader().load(path, System.getenv());
        DataSource dataSource = MariaDbDataSourceFactory.create(configuration);
        ItemAssetSchemaPreflightReport result = new JdbcItemAssetSchemaInspector(dataSource).inspect();
        if (!printReport(result, System.out)) {
            throw new IllegalStateException("ITEM schema preflight NOT_READY");
        }
    }

    /** In report không chứa URL/user/password và trả true khi schema đúng V002. */
    static boolean printReport(ItemAssetSchemaPreflightReport report, PrintStream output) {
        Objects.requireNonNull(report, "report");
        Objects.requireNonNull(output, "output");
        output.println("ITEM schema preflight " + (report.ready() ? "READY" : "NOT_READY"));
        for (String difference : report.differences()) {
            output.println("difference=" + difference);
        }
        output.println("databaseChanged=false");
        return report.ready();
    }
}
