package com.nsocry.bootstrap;

import com.nsocry.assets.*;
import com.nsocry.configuration.*;
import com.nsocry.operations.*;
import com.nsocry.persistence.*;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Objects;
import javax.sql.DataSource;

/** Publish thử MAP snapshot từ JDBC sau toàn bộ gate; không ghi database. */
public final class MapAssetRuntimePublishCommand {
    private MapAssetRuntimePublishCommand() { }

    /** Đọc archive, preflight schema và publish vào atomic store cục bộ của command. */
    public static void main(String[] args) throws Exception {
        if (args == null || args.length != 1) {
            throw new IllegalArgumentException("map-runtime-publish yêu cầu đúng một archive path");
        }
        ValidatedMapAssetSeedArchive archive = new MapAssetSeedArchiveService()
                .readValidated(Path.of(args[0]));
        DatabaseConfiguration config = new DatabaseConfigurationLoader()
                .load(Path.of("config", "nsocry.properties"), System.getenv());
        DataSource dataSource = MariaDbDataSourceFactory.create(config);
        if (!new JdbcMapAssetSchemaInspector(dataSource).inspect().ready()) {
            throw new IllegalStateException("MAP schema preflight NOT_READY");
        }
        AtomicMapAssetRuntimeSnapshotStore store = new AtomicMapAssetRuntimeSnapshotStore();
        MapAssetRuntimeSnapshot snapshot = publish(
                new JdbcMapAssetSource(dataSource, archive.validation().version()),
                archive.manifestText(), store);
        printReport(snapshot, store, System.out);
    }

    /** Parse manifest rồi điều phối source → validate → atomic publish. */
    static MapAssetRuntimeSnapshot publish(
            MapAssetSource source,
            String manifestText,
            AtomicMapAssetRuntimeSnapshotStore store) throws Exception {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(store, "store");
        MapAssetSeedManifest manifest = MapAssetSeedManifestParser.parse(manifestText);
        return new MapAssetRuntimePublishService(source, manifest, store).rebuildAndPublish();
    }

    /** In metadata của đúng snapshot hiện hành cùng trạng thái mutation/startup. */
    static void printReport(
            MapAssetRuntimeSnapshot snapshot,
            AtomicMapAssetRuntimeSnapshotStore store,
            PrintStream output) {
        Objects.requireNonNull(snapshot, "snapshot");
        Objects.requireNonNull(store, "store");
        Objects.requireNonNull(output, "output");
        MapAssetRuntimeSnapshot current = store.currentSnapshot()
                .orElseThrow(() -> new IllegalStateException("MAP runtime snapshot chưa publish"));
        if (current != snapshot) throw new IllegalStateException("MAP snapshot không phải bản vừa publish");
        output.println("MAP runtime snapshot PUBLISHED");
        output.println("version=" + Byte.toUnsignedInt(snapshot.version()));
        output.println("mapCount=" + snapshot.mapCount());
        output.println("npcCount=" + snapshot.npcCount());
        output.println("mobCount=" + snapshot.mobCount());
        output.println("payloadLength=" + snapshot.payloadLength());
        output.println("sha256=" + snapshot.payloadSha256());
        output.println("databaseChanged=false");
        output.println("runtimeSnapshotPublished=true");
        output.println("serverStartupWired=false");
    }
}
