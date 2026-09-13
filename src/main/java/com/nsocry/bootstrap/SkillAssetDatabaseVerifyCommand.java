package com.nsocry.bootstrap;

import com.nsocry.assets.SkillAssetBundle;
import com.nsocry.assets.SkillAssetSeedManifest;
import com.nsocry.assets.SkillAssetSeedManifestParser;
import com.nsocry.assets.SkillAssetSeedValidationResult;
import com.nsocry.assets.SkillAssetSeedValidator;
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

/** Xác minh dữ liệu SKILL trong database tái tạo đúng payload candidate. */
public final class SkillAssetDatabaseVerifyCommand {
    private SkillAssetDatabaseVerifyCommand() {
    }

    /** Đọc archive/schema/JDBC source và so payload end-to-end, không ghi database. */
    public static void main(String[] args) throws Exception {
        if (args == null || args.length != 1) {
            throw new IllegalArgumentException("skill-seed-db-verify yêu cầu đúng một archive path");
        }
        ValidatedSkillAssetSeedArchive archive = new SkillAssetSeedArchiveService()
                .readValidated(Path.of(args[0]));
        DatabaseConfiguration configuration = new DatabaseConfigurationLoader()
                .load(Path.of("config", "nsocry.properties"), System.getenv());
        DataSource dataSource = MariaDbDataSourceFactory.create(configuration);
        if (!new JdbcSkillAssetSchemaInspector(dataSource).inspect().ready()) {
            throw new IllegalStateException("SKILL schema preflight NOT_READY");
        }
        SkillAssetSeedValidationResult result = verify(
                new JdbcSkillAssetSource(dataSource, archive.validation().version()), archive.manifestText());
        printReport(result, System.out);
    }

    /** Load qua source port rồi validate toàn bộ bundle với manifest candidate. */
    static SkillAssetSeedValidationResult verify(SkillAssetSource source, String manifestText) throws Exception {
        Objects.requireNonNull(source, "source");
        SkillAssetSeedManifest manifest = SkillAssetSeedManifestParser.parse(manifestText);
        SkillAssetBundle bundle = Objects.requireNonNull(source.load(), "skill source result");
        return SkillAssetSeedValidator.validate(bundle, manifest);
    }

    /** In count/checksum/raw-byte và khẳng định không đổi database/runtime. */
    static void printReport(SkillAssetSeedValidationResult result, PrintStream output) {
        Objects.requireNonNull(result, "result");
        Objects.requireNonNull(output, "output");
        var structure = result.structure();
        output.println("SKILL database payload VERIFIED");
        output.println("version=" + Byte.toUnsignedInt(result.version()));
        output.println("optionTemplateCount=" + structure.optionTemplateCount());
        output.println("classCount=" + structure.classCount());
        output.println("skillTemplateCount=" + structure.skillTemplateCount());
        output.println("skillLevelCount=" + structure.skillLevelCount());
        output.println("skillLevelOptionCount=" + structure.skillLevelOptionCount());
        output.println("rawByteDifferences=" + result.rawByteDifferences());
        output.println("payloadLength=" + result.payloadLength());
        output.println("sha256=" + result.payloadSha256());
        output.println("databaseChanged=false");
        output.println("runtimeSnapshotPublished=false");
    }
}
