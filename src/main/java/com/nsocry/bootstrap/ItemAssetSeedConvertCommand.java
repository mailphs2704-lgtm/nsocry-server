package com.nsocry.bootstrap;

import com.nsocry.assets.ItemAssetSeedArtifact;
import com.nsocry.assets.ItemAssetSeedArtifactGenerator;
import com.nsocry.assets.conversion.ItemAssetConversionReport;
import com.nsocry.assets.conversion.ItemAssetConversionResult;
import com.nsocry.assets.conversion.ReferenceItemAssetConverter;
import com.nsocry.assets.conversion.ReferenceItemDumpRows;
import com.nsocry.assets.conversion.ReferenceItemSqlDumpParser;
import com.nsocry.operations.ItemAssetSeedArchiveService;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/** Command chuyển ITEM trong dump tham chiếu thành candidate archive, không dùng JDBC. */
public final class ItemAssetSeedConvertCommand {
    private static final long MAX_DUMP_BYTES = 64L * 1024 * 1024;
    private static final byte REFERENCE_ITEM_VERSION = 26;

    private ItemAssetSeedConvertCommand() {
    }

    /** Chấp nhận đúng một dump path, xuất candidate cạnh file nguồn và in metadata. */
    public static void main(String[] args) throws Exception {
        if (args == null || args.length != 1) {
            throw new IllegalArgumentException("item-seed-convert yêu cầu đúng một dump path");
        }
        convert(Path.of(args[0]), System.out);
    }

    /** Thực hiện pipeline offline và trả đường dẫn archive vừa tạo để unit test đối chiếu. */
    static Path convert(Path dumpPath, PrintStream output) throws Exception {
        Objects.requireNonNull(dumpPath, "dumpPath");
        Objects.requireNonNull(output, "output");
        Path absoluteDump = dumpPath.toAbsolutePath().normalize();
        requireReadableDump(absoluteDump);
        String dump = Files.readString(absoluteDump, StandardCharsets.UTF_8);
        ReferenceItemDumpRows rows = ReferenceItemSqlDumpParser.parse(dump);
        ItemAssetConversionResult conversion = ReferenceItemAssetConverter.convert(
                REFERENCE_ITEM_VERSION, rows.optionRows(), rows.itemRows());
        ItemAssetSeedArtifact artifact = ItemAssetSeedArtifactGenerator.generate(conversion.bundle());
        Path archive = candidatePath(absoluteDump);
        new ItemAssetSeedArchiveService().export(artifact, archive);
        printReport(output, archive, conversion.report(), artifact);
        return archive;
    }

    /** Từ chối file thiếu, không phải regular file hoặc vượt hard limit trước khi đọc. */
    private static void requireReadableDump(Path dump) throws IOException {
        if (!Files.isRegularFile(dump)) {
            throw new IOException("ITEM dump không tồn tại hoặc không phải regular file");
        }
        if (Files.size(dump) > MAX_DUMP_BYTES) {
            throw new IOException("ITEM dump vượt giới hạn 64 MiB");
        }
    }

    /** Sinh tên output xác định mà không ghi đè chính dump đầu vào. */
    private static Path candidatePath(Path dump) {
        String fileName = dump.getFileName().toString();
        int extension = fileName.lastIndexOf('.');
        String baseName = extension > 0 ? fileName.substring(0, extension) : fileName;
        return dump.resolveSibling(baseName + "-item-seed-v26-candidate.zip");
    }

    /** Chỉ in đường dẫn và metadata kiểm chứng, không in nội dung ITEM. */
    private static void printReport(
            PrintStream output,
            Path archive,
            ItemAssetConversionReport report,
            ItemAssetSeedArtifact artifact) {
        output.println("ITEM seed candidate CREATED");
        output.println("archive=" + archive);
        output.println("optionCount=" + report.optionCount());
        output.println("itemCount=" + report.itemCount());
        output.println("upgradableItemCount=" + report.upgradableItemCount());
        output.println("fashionNotTransferred=" + report.fashionValueNotTransferredCount());
        output.println("payloadLength=" + artifact.validation().payloadLength());
        output.println("sha256=" + artifact.validation().payloadSha256());
        output.println("databaseChanged=false");
    }
}
