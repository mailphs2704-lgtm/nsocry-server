package com.nsocry.bootstrap;

import com.nsocry.assets.AtomicDataAssetRuntimeSnapshotStore;
import com.nsocry.assets.DataAssetRuntimePublishService;
import com.nsocry.assets.DataAssetRuntimeSnapshot;
import com.nsocry.assets.DataAssetSeedManifest;
import com.nsocry.assets.DataAssetSeedManifestParser;
import com.nsocry.assets.DataAssetSource;
import com.nsocry.configuration.DatabaseConfiguration;
import com.nsocry.configuration.DatabaseConfigurationLoader;
import com.nsocry.operations.DataAssetSeedArchiveService;
import com.nsocry.operations.ValidatedDataAssetSeedArchive;
import com.nsocry.persistence.JdbcDataAssetSchemaInspector;
import com.nsocry.persistence.JdbcDataAssetSource;
import com.nsocry.persistence.MariaDbDataSourceFactory;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Objects;
import javax.sql.DataSource;

/** Publish thử DATA snapshot trong tiến trình command riêng; không nối server startup. */
public final class DataAssetRuntimePublishCommand {
    private DataAssetRuntimePublishCommand() {
    }

    /** Validate archive/schema, đọc JDBC v7 rồi atomic publish vào store cục bộ. */
    public static void main(String[] args) throws Exception {
        if (args == null || args.length != 1) {
            throw new IllegalArgumentException(
                    "data-runtime-publish yêu cầu đúng một archive path");
        }
        ValidatedDataAssetSeedArchive archive = new DataAssetSeedArchiveService()
                .readValidated(Path.of(args[0]));
        DatabaseConfiguration configuration = new DatabaseConfigurationLoader()
                .load(Path.of("config", "nsocry.properties"), System.getenv());
        DataSource dataSource = MariaDbDataSourceFactory.create(configuration);
        if (!new JdbcDataAssetSchemaInspector(dataSource).inspect().ready()) {
            throw new IllegalStateException("DATA schema preflight NOT_READY");
        }

        AtomicDataAssetRuntimeSnapshotStore store = new AtomicDataAssetRuntimeSnapshotStore();
        DataAssetRuntimeSnapshot snapshot = publish(
                new JdbcDataAssetSource(dataSource, archive.validation().version()),
                archive.manifestText(), store);
        printReport(snapshot, store, System.out);
    }

    /** Parse manifest rồi điều phối source → validate → atomic publish. */
    static DataAssetRuntimeSnapshot publish(
            DataAssetSource source,
            String manifestText,
            AtomicDataAssetRuntimeSnapshotStore store) throws Exception {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(store, "store");
        DataAssetSeedManifest manifest = DataAssetSeedManifestParser.parse(manifestText);
        return new DataAssetRuntimePublishService(source, manifest, store).rebuildAndPublish();
    }

    /** In metadata snapshot hiện hành và phạm vi side effect của command cô lập. */
    static void printReport(
            DataAssetRuntimeSnapshot snapshot,
            AtomicDataAssetRuntimeSnapshotStore store,
            PrintStream output) {
        Objects.requireNonNull(snapshot, "snapshot");
        Objects.requireNonNull(store, "store");
        Objects.requireNonNull(output, "output");
        DataAssetRuntimeSnapshot current = store.currentSnapshot()
                .orElseThrow(() -> new IllegalStateException("DATA runtime snapshot chưa publish"));
        if (current != snapshot) {
            throw new IllegalStateException("DATA snapshot không phải bản vừa publish");
        }
        output.println("DATA runtime snapshot PUBLISHED_ISOLATED");
        output.println("version=" + Byte.toUnsignedInt(snapshot.version()));
        output.println("taskGroupCount=" + snapshot.taskGroupCount());
        output.println("experienceCount=" + snapshot.experienceCount());
        output.println("payloadLength=" + snapshot.payloadLength());
        output.println("sha256=" + snapshot.payloadSha256());
        output.println("databaseChanged=false");
        output.println("runtimeSnapshotPublished=true");
        output.println("serverStartupWired=false");
    }
}
