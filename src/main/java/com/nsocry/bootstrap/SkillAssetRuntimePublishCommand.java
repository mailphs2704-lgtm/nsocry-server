package com.nsocry.bootstrap;

import com.nsocry.assets.AtomicSkillAssetRuntimeSnapshotStore;
import com.nsocry.assets.SkillAssetRuntimePublishService;
import com.nsocry.assets.SkillAssetRuntimeSnapshot;
import com.nsocry.assets.SkillAssetSeedManifest;
import com.nsocry.assets.SkillAssetSeedManifestParser;
import com.nsocry.assets.SkillAssetSource;
import com.nsocry.configuration.DatabaseConfiguration;
import com.nsocry.configuration.DatabaseConfigurationLoader;
import com.nsocry.operations.SkillAssetSeedArchiveService;
import com.nsocry.operations.ValidatedSkillAssetSeedArchive;
import com.nsocry.persistence.JdbcSkillAssetSchemaInspector;
import com.nsocry.persistence.JdbcSkillAssetSource;
import com.nsocry.persistence.MariaDbDataSourceFactory;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Objects;
import javax.sql.DataSource;

/** Publish thử SKILL snapshot từ JDBC sau toàn bộ gate candidate; không ghi database. */
public final class SkillAssetRuntimePublishCommand {
    private SkillAssetRuntimePublishCommand() {
    }

    /** Đọc archive, preflight schema và publish vào atomic store cục bộ của command. */
    public static void main(String[] args) throws Exception {
        if (args == null || args.length != 1) {
            throw new IllegalArgumentException("skill-runtime-publish yêu cầu đúng một archive path");
        }
        ValidatedSkillAssetSeedArchive archive = new SkillAssetSeedArchiveService()
                .readValidated(Path.of(args[0]));
        DatabaseConfiguration configuration = new DatabaseConfigurationLoader()
                .load(Path.of("config", "nsocry.properties"), System.getenv());
        DataSource dataSource = MariaDbDataSourceFactory.create(configuration);
        if (!new JdbcSkillAssetSchemaInspector(dataSource).inspect().ready()) {
            throw new IllegalStateException("SKILL schema preflight NOT_READY");
        }
        AtomicSkillAssetRuntimeSnapshotStore store = new AtomicSkillAssetRuntimeSnapshotStore();
        SkillAssetRuntimeSnapshot snapshot = publish(
                new JdbcSkillAssetSource(dataSource, archive.validation().version()),
                archive.manifestText(), store);
        printReport(snapshot, store, System.out);
    }

    /** Parse manifest rồi điều phối source → validate → atomic publish. */
    static SkillAssetRuntimeSnapshot publish(
            SkillAssetSource source,
            String manifestText,
            AtomicSkillAssetRuntimeSnapshotStore store) throws Exception {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(store, "store");
        SkillAssetSeedManifest manifest = SkillAssetSeedManifestParser.parse(manifestText);
        return new SkillAssetRuntimePublishService(source, manifest, store).rebuildAndPublish();
    }

    /** In metadata của đúng snapshot hiện hành và trạng thái mutation. */
    static void printReport(
            SkillAssetRuntimeSnapshot snapshot,
            AtomicSkillAssetRuntimeSnapshotStore store,
            PrintStream output) {
        Objects.requireNonNull(snapshot, "snapshot");
        Objects.requireNonNull(store, "store");
        Objects.requireNonNull(output, "output");
        SkillAssetRuntimeSnapshot current = store.currentSnapshot()
                .orElseThrow(() -> new IllegalStateException("SKILL runtime snapshot chưa được publish"));
        if (current != snapshot) {
            throw new IllegalStateException("SKILL runtime snapshot không phải bản vừa publish");
        }
        var structure = snapshot.structure();
        output.println("SKILL runtime snapshot PUBLISHED");
        output.println("version=" + Byte.toUnsignedInt(snapshot.version()));
        output.println("optionTemplateCount=" + structure.optionTemplateCount());
        output.println("classCount=" + structure.classCount());
        output.println("skillTemplateCount=" + structure.skillTemplateCount());
        output.println("skillLevelCount=" + structure.skillLevelCount());
        output.println("skillLevelOptionCount=" + structure.skillLevelOptionCount());
        output.println("payloadLength=" + snapshot.payloadLength());
        output.println("sha256=" + snapshot.payloadSha256());
        output.println("databaseChanged=false");
        output.println("runtimeSnapshotPublished=true");
        output.println("serverStartupWired=false");
    }
}
