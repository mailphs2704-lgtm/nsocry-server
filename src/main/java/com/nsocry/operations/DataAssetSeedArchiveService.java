package com.nsocry.operations;

import com.nsocry.assets.DataAssetBundle;
import com.nsocry.assets.DataAssetCodec;
import com.nsocry.assets.DataAssetSeedArtifact;
import com.nsocry.assets.DataAssetSeedManifest;
import com.nsocry.assets.DataAssetSeedManifestParser;
import com.nsocry.assets.DataAssetSeedValidationResult;
import com.nsocry.assets.DataAssetSeedValidator;
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

/** Xuất và đọc lại archive DATA seed offline; không truy cập database/runtime. */
public final class DataAssetSeedArchiveService {
    private static final String PAYLOAD_ENTRY = "data.bin";
    private static final String MANIFEST_ENTRY = "data.manifest";
    private static final int MAX_PAYLOAD_BYTES = 16 * 1024 * 1024;
    private static final int MAX_MANIFEST_BYTES = 4 * 1024;

    /** Ghi candidate qua file tạm và atomic move; không ghi đè archive tồn tại. */
    public void export(DataAssetSeedArtifact artifact, Path target) throws IOException {
        Objects.requireNonNull(artifact, "artifact");
        Path absoluteTarget = Objects.requireNonNull(target, "target").toAbsolutePath().normalize();
        Path directory = Objects.requireNonNull(absoluteTarget.getParent(), "target parent");
        Files.createDirectories(directory);
        if (Files.exists(absoluteTarget)) {
            throw new IOException("Không ghi đè DATA seed archive đã tồn tại");
        }
        Path temporary = Files.createTempFile(directory, ".nsocry-data-seed-", ".tmp");
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

    /** Dry-run archive và trả metadata sau decode/encode/checksum validation. */
    public DataAssetSeedValidationResult dryRun(Path archive) throws IOException {
        return readValidated(archive).validation();
    }

    /** Đọc archive fail-closed, không mở JDBC và không publish runtime snapshot. */
    public ValidatedDataAssetSeedArchive readValidated(Path archive) throws IOException {
        Objects.requireNonNull(archive, "archive");
        Map<String, byte[]> entries = readEntries(archive);
        byte[] payload = requireEntry(entries, PAYLOAD_ENTRY);
        String manifestText = new String(requireEntry(entries, MANIFEST_ENTRY), StandardCharsets.UTF_8);
        DataAssetSeedManifest manifest = DataAssetSeedManifestParser.parse(manifestText);
        DataAssetBundle bundle = DataAssetCodec.decode(payload);
        DataAssetSeedValidationResult validation = DataAssetSeedValidator.validate(bundle, manifest);
        return new ValidatedDataAssetSeedArchive(payload, manifestText, validation);
    }

    private static void writeEntry(ZipOutputStream output, String name, byte[] content) throws IOException {
        ZipEntry entry = new ZipEntry(name);
        entry.setTime(0L);
        output.putNextEntry(entry);
        output.write(content);
        output.closeEntry();
    }

    private static Map<String, byte[]> readEntries(Path archive) throws IOException {
        Map<String, byte[]> entries = new HashMap<>();
        try (ZipInputStream input = new ZipInputStream(Files.newInputStream(archive))) {
            ZipEntry entry;
            while ((entry = input.getNextEntry()) != null) {
                int limit = switch (entry.getName()) {
                    case PAYLOAD_ENTRY -> MAX_PAYLOAD_BYTES;
                    case MANIFEST_ENTRY -> MAX_MANIFEST_BYTES;
                    default -> throw new IOException("DATA seed archive chứa entry không hợp lệ");
                };
                if (entry.isDirectory() || entries.containsKey(entry.getName())) {
                    throw new IOException("DATA seed archive chứa entry trùng hoặc directory");
                }
                entries.put(entry.getName(), readBounded(input, limit));
                input.closeEntry();
            }
        }
        if (entries.size() != 2) {
            throw new IOException("DATA seed archive thiếu entry bắt buộc");
        }
        return entries;
    }

    private static byte[] readBounded(InputStream input, int limit) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int total = 0;
        int read;
        while ((read = input.read(buffer)) != -1) {
            total += read;
            if (total > limit) {
                throw new IOException("DATA seed archive vượt giới hạn kích thước");
            }
            output.write(buffer, 0, read);
        }
        return output.toByteArray();
    }

    private static byte[] requireEntry(Map<String, byte[]> entries, String name) throws IOException {
        byte[] content = entries.get(name);
        if (content == null) {
            throw new IOException("DATA seed archive thiếu " + name);
        }
        return content;
    }
}
