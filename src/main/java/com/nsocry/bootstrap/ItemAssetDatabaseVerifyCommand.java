package com.nsocry.bootstrap;

import com.nsocry.assets.ItemAssetBundle;
import com.nsocry.assets.ItemAssetSeedManifest;
import com.nsocry.assets.ItemAssetSeedManifestParser;
import com.nsocry.assets.ItemAssetSeedValidator;
import com.nsocry.assets.ItemAssetSource;
import com.nsocry.assets.ItemAssetValidationResult;
import com.nsocry.configuration.DatabaseConfiguration;
import com.nsocry.configuration.DatabaseConfigurationLoader;
import com.nsocry.operations.ItemAssetSeedArchiveService;
import com.nsocry.operations.ValidatedItemAssetSeedArchive;
import com.nsocry.persistence.ItemAssetSchemaPreflightReport;
import com.nsocry.persistence.JdbcItemAssetSchemaInspector;
import com.nsocry.persistence.JdbcItemAssetSource;
import com.nsocry.persistence.MariaDbDataSourceFactory;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Objects;
import javax.sql.DataSource;

/** Xác minh dữ liệu ITEM trong database tái tạo đúng payload candidate mà không ghi dữ liệu. */
public final class ItemAssetDatabaseVerifyCommand {
    private ItemAssetDatabaseVerifyCommand() {
    }

    /** Đọc archive, schema và JDBC source rồi so count/length/SHA-256 end-to-end. */
    public static void main(String[] args) throws Exception {
        if (args == null || args.length != 1) {
            throw new IllegalArgumentException("item-seed-db-verify yêu cầu đúng một archive path");
        }
        ValidatedItemAssetSeedArchive archive = new ItemAssetSeedArchiveService()
                .readValidated(Path.of(args[0]));
        DatabaseConfiguration configuration = new DatabaseConfigurationLoader()
                .load(Path.of("config", "nsocry.properties"), System.getenv());
        DataSource dataSource = MariaDbDataSourceFactory.create(configuration);
        requireReady(new JdbcItemAssetSchemaInspector(dataSource).inspect());
        ItemAssetValidationResult result = verify(
                new JdbcItemAssetSource(dataSource, archive.validation().version()),
                archive.manifestText());
        printReport(result, System.out);
    }

    /** Load bundle qua source port và validate với manifest candidate. */
    static ItemAssetValidationResult verify(ItemAssetSource source, String manifestText) throws Exception {
        Objects.requireNonNull(source, "source");
        ItemAssetSeedManifest manifest = ItemAssetSeedManifestParser.parse(manifestText);
        ItemAssetBundle bundle = Objects.requireNonNull(source.load(), "item source result");
        return ItemAssetSeedValidator.validate(bundle, manifest);
    }

    /** Chặn đọc asset nếu schema không còn đúng V002. */
    static void requireReady(ItemAssetSchemaPreflightReport report) {
        Objects.requireNonNull(report, "report");
        if (!report.ready()) {
            throw new IllegalStateException("ITEM schema preflight NOT_READY");
        }
    }

    /** In metadata end-to-end và khẳng định command không thay đổi DB/runtime. */
    static void printReport(ItemAssetValidationResult result, PrintStream output) {
        Objects.requireNonNull(result, "result");
        Objects.requireNonNull(output, "output");
        output.println("ITEM database payload VERIFIED");
        output.println("version=" + Byte.toUnsignedInt(result.version()));
        output.println("optionCount=" + result.optionCount());
        output.println("itemCount=" + result.itemCount());
        output.println("payloadLength=" + result.payloadLength());
        output.println("sha256=" + result.payloadSha256());
        output.println("databaseChanged=false");
        output.println("runtimeSnapshotPublished=false");
    }
}
