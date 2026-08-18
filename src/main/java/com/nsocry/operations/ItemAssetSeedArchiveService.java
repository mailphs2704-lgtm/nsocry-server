package com.nsocry.operations;

import com.nsocry.assets.ItemAssetBundle;
import com.nsocry.assets.ItemAssetCodec;
import com.nsocry.assets.ItemAssetSeedArtifact;
import com.nsocry.assets.ItemAssetSeedManifest;
import com.nsocry.assets.ItemAssetSeedManifestParser;
import com.nsocry.assets.ItemAssetSeedValidationException;
import com.nsocry.assets.ItemAssetSeedValidator;
import com.nsocry.assets.ItemAssetValidationResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/** Xuất và dry-run archive ITEM seed mà không truy cập database. */
public final class ItemAssetSeedArchiveService {
    private static final String PAYLOAD_ENTRY = "item.bin";
    private static final String MANIFEST_ENTRY = "item.manifest";
    private static final int MAX_PAYLOAD_BYTES = 16 * 1024 * 1024;
    private static final int MAX_MANIFEST_BYTES = 4 * 1024;

    /** Ghi archive qua file tạm rồi atomic move; không ghi đè artifact đã tồn tại. */
    public void export(ItemAssetSeedArtifact artifact, Path target) throws IOException {
        Objects.requireNonNull(artifact, "artifact");
        Path absoluteTarget = Objects.requireNonNull(target, "target").toAbsolutePath().normalize();
        Path directory = Objects.requireNonNull(absoluteTarget.getParent(), "target parent");
        Files.createDirectories(directory);
        if (Files.exists(absoluteTarget)) {
            throw new IOException("Không ghi đè ITEM seed archive đã tồn tại");
        }
        Path temporary = Files.createTempFile(directory, ".nsocry-item-seed-", ".tmp");
        try {
            try (ZipOutputStream output = new ZipOutputStream(Files.newOutputStream(temporary))) {
                writeEntry(output, PAYLOAD_ENTRY, artifact.payload());
                writeEntry(output, MANIFEST_ENTRY, artifact.manifestText().getBytes(StandardCharsets.UTF_8));
            }
            Files.move(temporary, absoluteTarget, StandardCopyOption.ATOMIC_MOVE);
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    /** Đọc, kiểm định archive và trả metadata; không mở JDBC connection. */
    public ItemAssetValidationResult dryRun(Path archive)
            throws IOException, ItemAssetSeedValidationException {
        Objects.requireNonNull(archive, "archive");
        Map<String, byte[]> entries = readEntries(archive);
        byte[] payload = requireEntry(entries, PAYLOAD_ENTRY);
        String manifestText = new String(requireEntry(entries, MANIFEST_ENTRY), StandardCharsets.UTF_8);
        ItemAssetSeedManifest manifest = ItemAssetSeedManifestParser.parse(manifestText);
        ItemAssetBundle bundle = ItemAssetCodec.decode(payload);
        return ItemAssetSeedValidator.validate(bundle, manifest);
    }

    /** Ghi một zip entry với timestamp cố định để output không phụ thuộc đồng hồ máy. */
    private static void writeEntry(ZipOutputStream output, String name, byte[] content) throws IOException {
        ZipEntry entry = new ZipEntry(name);
        entry.setTime(0L);
        output.putNextEntry(entry);
        output.write(content);
        output.closeEntry();
    }

    /** Chỉ chấp nhận đúng hai entry đã định nghĩa và giới hạn kích thước giải nén. */
    private static Map<String, byte[]> readEntries(Path archive) throws IOException {
        Map<String, byte[]> entries = new HashMap<>();
        try (ZipInputStream input = new ZipInputStream(Files.newInputStream(archive))) {
            ZipEntry entry;
            while ((entry = input.getNextEntry()) != null) {
                int limit = switch (entry.getName()) {
                    case PAYLOAD_ENTRY -> MAX_PAYLOAD_BYTES;
                    case MANIFEST_ENTRY -> MAX_MANIFEST_BYTES;
                    default -> throw new IOException("ITEM seed archive chứa entry không hợp lệ");
                };
                if (entry.isDirectory() || entries.containsKey(entry.getName())) {
                    throw new IOException("ITEM seed archive chứa entry trùng hoặc directory");
                }
                entries.put(entry.getName(), readBounded(input, limit));
                input.closeEntry();
            }
        }
        if (entries.size() != 2) {
            throw new IOException("ITEM seed archive thiếu entry bắt buộc");
        }
        return entries;
    }

    /** Đọc entry với hard limit để chống archive giải nén quá lớn. */
    private static byte[] readBounded(InputStream input, int limit) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int total = 0;
        int read;
        while ((read = input.read(buffer)) != -1) {
            total += read;
            if (total > limit) {
                throw new IOException("ITEM seed archive vượt giới hạn kích thước");
            }
            output.write(buffer, 0, read);
        }
        return output.toByteArray();
    }

    /** Yêu cầu entry tồn tại trước khi parse. */
    private static byte[] requireEntry(Map<String, byte[]> entries, String name) throws IOException {
        byte[] content = entries.get(name);
        if (content == null) {
            throw new IOException("ITEM seed archive thiếu " + name);
        }
        return content;
    }
}
