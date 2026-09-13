package com.nsocry.bootstrap;

import com.nsocry.assets.ItemAssetValidationResult;
import com.nsocry.configuration.DatabaseConfiguration;
import com.nsocry.configuration.DatabaseConfigurationLoader;
import com.nsocry.operations.ItemAssetSeedArchiveService;
import com.nsocry.operations.ValidatedItemAssetSeedArchive;
import com.nsocry.persistence.ItemAssetSchemaPreflightReport;
import com.nsocry.persistence.JdbcItemAssetSchemaInspector;
import com.nsocry.persistence.JdbcItemAssetSeedImporter;
import com.nsocry.persistence.MariaDbDataSourceFactory;
import java.io.Console;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Locale;
import java.util.Objects;
import javax.sql.DataSource;

/** Command tương tác import ITEM seed đã duyệt; không chạy migration hoặc publish runtime. */
public final class ItemAssetSeedImportCommand {
    private ItemAssetSeedImportCommand() {
    }

    /** Kiểm định archive/schema, yêu cầu nhập đúng SHA-256 rồi mới mở transaction import. */
    public static void main(String[] args) throws Exception {
        if (args == null || args.length != 1) {
            throw new IllegalArgumentException("item-seed-import yêu cầu đúng một archive path");
        }
        Console console = System.console();
        if (console == null) {
            throw new IllegalStateException("interactive console is required");
        }
        ValidatedItemAssetSeedArchive archive = new ItemAssetSeedArchiveService()
                .readValidated(Path.of(args[0]));

        DatabaseConfiguration configuration = new DatabaseConfigurationLoader()
                .load(Path.of("config", "nsocry.properties"), System.getenv());
        DataSource dataSource = MariaDbDataSourceFactory.create(configuration);
        ItemAssetSchemaPreflightReport schema = new JdbcItemAssetSchemaInspector(dataSource).inspect();
        if (!schema.ready()) {
            throw new IllegalStateException("ITEM schema preflight NOT_READY");
        }
        printCandidate(console, archive.validation());
        String confirmation = console.readLine("Nhập toàn bộ SHA-256 để xác nhận import: ");
        if (!matchesChecksum(archive.validation().payloadSha256(), confirmation)) {
            throw new IllegalArgumentException("SHA-256 confirmation không khớp");
        }
        ItemAssetValidationResult imported = new JdbcItemAssetSeedImporter(dataSource)
                .importSeed(archive.payload(), archive.manifestText());
        console.printf("ITEM seed IMPORTED: optionCount=%d, itemCount=%d, sha256=%s%n",
                imported.optionCount(), imported.itemCount(), imported.payloadSha256());
        console.printf("runtimeSnapshotPublished=false%n");
    }

    /** So checksum constant-time sau khi chuẩn hóa chữ thường và trim. */
    static boolean matchesChecksum(String expected, String confirmation) {
        Objects.requireNonNull(expected, "expected");
        if (confirmation == null) {
            return false;
        }
        byte[] expectedBytes = expected.toLowerCase(Locale.ROOT).getBytes(StandardCharsets.US_ASCII);
        byte[] actualBytes = confirmation.trim().toLowerCase(Locale.ROOT).getBytes(StandardCharsets.US_ASCII);
        return MessageDigest.isEqual(expectedBytes, actualBytes);
    }

    /** In metadata cần đối chiếu mà không in payload hoặc database credential. */
    private static void printCandidate(Console console, ItemAssetValidationResult validation) {
        console.printf("ITEM seed candidate: version=%d, optionCount=%d, itemCount=%d%n",
                Byte.toUnsignedInt(validation.version()), validation.optionCount(), validation.itemCount());
        console.printf("payloadLength=%d, sha256=%s%n",
                validation.payloadLength(), validation.payloadSha256());
        console.printf("Schema V002 và backup phải được xác nhận trước thao tác này.%n");
    }
}
