package com.nsocry.bootstrap;

import com.nsocry.assets.SkillAssetSeedValidationResult;
import com.nsocry.configuration.DatabaseConfiguration;
import com.nsocry.configuration.DatabaseConfigurationLoader;
import com.nsocry.operations.SkillAssetSeedArchiveService;
import com.nsocry.operations.ValidatedSkillAssetSeedArchive;
import com.nsocry.persistence.JdbcSkillAssetSchemaInspector;
import com.nsocry.persistence.JdbcSkillAssetSeedImporter;
import com.nsocry.persistence.MariaDbDataSourceFactory;
import com.nsocry.persistence.SkillAssetSchemaPreflightReport;
import java.io.Console;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Locale;
import java.util.Objects;
import javax.sql.DataSource;

/** Command tương tác import SKILL đã duyệt; không migration hoặc publish runtime. */
public final class SkillAssetSeedImportCommand {
    private SkillAssetSeedImportCommand() {
    }

    /** Archive + schema READY + full SHA-256 là ba gate bắt buộc trước transaction. */
    public static void main(String[] args) throws Exception {
        if (args == null || args.length != 1) {
            throw new IllegalArgumentException("skill-seed-import yêu cầu đúng một archive path");
        }
        Console console = System.console();
        if (console == null) throw new IllegalStateException("interactive console is required");
        ValidatedSkillAssetSeedArchive archive = new SkillAssetSeedArchiveService()
                .readValidated(Path.of(args[0]));
        DatabaseConfiguration configuration = new DatabaseConfigurationLoader()
                .load(Path.of("config", "nsocry.properties"), System.getenv());
        DataSource dataSource = MariaDbDataSourceFactory.create(configuration);
        SkillAssetSchemaPreflightReport schema = new JdbcSkillAssetSchemaInspector(dataSource).inspect();
        if (!schema.ready()) throw new IllegalStateException("SKILL schema preflight NOT_READY");
        printCandidate(console, archive.validation());
        String confirmation = console.readLine("Nhập toàn bộ SHA-256 để xác nhận import: ");
        if (!matchesChecksum(archive.validation().payloadSha256(), confirmation)) {
            throw new IllegalArgumentException("SHA-256 confirmation không khớp");
        }
        SkillAssetSeedValidationResult imported = new JdbcSkillAssetSeedImporter(dataSource)
                .importSeed(archive.payload(), archive.manifestText());
        var structure = imported.structure();
        console.printf("SKILL seed IMPORTED: optionCount=%d, classCount=%d, templateCount=%d, levelCount=%d, levelOptionCount=%d, sha256=%s%n",
                structure.optionTemplateCount(), structure.classCount(), structure.skillTemplateCount(),
                structure.skillLevelCount(), structure.skillLevelOptionCount(), imported.payloadSha256());
        console.printf("runtimeSnapshotPublished=false%n");
    }

    /** So checksum constant-time, bỏ khoảng trắng ngoài và không phân biệt hoa thường. */
    static boolean matchesChecksum(String expected, String confirmation) {
        Objects.requireNonNull(expected, "expected");
        if (confirmation == null) return false;
        byte[] expectedBytes = expected.toLowerCase(Locale.ROOT).getBytes(StandardCharsets.US_ASCII);
        byte[] actualBytes = confirmation.trim().toLowerCase(Locale.ROOT).getBytes(StandardCharsets.US_ASCII);
        return MessageDigest.isEqual(expectedBytes, actualBytes);
    }

    private static void printCandidate(Console console, SkillAssetSeedValidationResult validation) {
        var structure = validation.structure();
        console.printf("SKILL seed candidate: version=%d, options=%d, classes=%d, templates=%d, levels=%d, levelOptions=%d%n",
                Byte.toUnsignedInt(validation.version()), structure.optionTemplateCount(), structure.classCount(),
                structure.skillTemplateCount(), structure.skillLevelCount(), structure.skillLevelOptionCount());
        console.printf("rawByteDifferences=%s%n", validation.rawByteDifferences());
        console.printf("payloadLength=%d, sha256=%s%n", validation.payloadLength(), validation.payloadSha256());
        console.printf("Schema V003 READY và backup phải được xác nhận trước thao tác này.%n");
    }
}
