package com.nsocry.operations;

import com.nsocry.assets.SkillAssetBundle;
import com.nsocry.assets.SkillAssetCodec;
import com.nsocry.assets.SkillAssetSeedArtifact;
import com.nsocry.assets.SkillAssetSeedManifest;
import com.nsocry.assets.SkillAssetSeedManifestParser;
import com.nsocry.assets.SkillAssetSeedValidationResult;
import com.nsocry.assets.SkillAssetSeedValidator;
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

/** Xuất và dry-run archive SKILL mà không mở database hoặc publish runtime. */
public final class SkillAssetSeedArchiveService {
    private static final String PAYLOAD_ENTRY = "skill.bin";
    private static final String MANIFEST_ENTRY = "skill.manifest";
    private static final int MAX_PAYLOAD_BYTES = 16 * 1024 * 1024;
    private static final int MAX_MANIFEST_BYTES = 8 * 1024;

    /** Ghi qua file tạm và atomic move; không ghi đè candidate đã tồn tại. */
    public void export(SkillAssetSeedArtifact artifact, Path target) throws IOException {
        Objects.requireNonNull(artifact, "artifact");
        Path absolute = Objects.requireNonNull(target, "target").toAbsolutePath().normalize();
        Path directory = Objects.requireNonNull(absolute.getParent(), "target parent");
        Files.createDirectories(directory);
        if (Files.exists(absolute)) throw new IOException("Không ghi đè SKILL seed archive đã tồn tại");
        Path temporary = Files.createTempFile(directory, ".nsocry-skill-seed-", ".tmp");
        try {
            try (ZipOutputStream output = new ZipOutputStream(Files.newOutputStream(temporary))) {
                write(output, PAYLOAD_ENTRY, artifact.payload());
                write(output, MANIFEST_ENTRY, artifact.manifestText().getBytes(StandardCharsets.UTF_8));
            }
            Files.move(temporary, absolute, StandardCopyOption.ATOMIC_MOVE);
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    /** Đọc đủ hai entry, decode và xác minh lại manifest/checksum/raw-byte. */
    public SkillAssetSeedValidationResult dryRun(Path archive) throws IOException {
        Map<String, byte[]> entries = readEntries(Objects.requireNonNull(archive, "archive"));
        byte[] payload = require(entries, PAYLOAD_ENTRY);
        String manifestText = new String(require(entries, MANIFEST_ENTRY), StandardCharsets.UTF_8);
        SkillAssetSeedManifest manifest = SkillAssetSeedManifestParser.parse(manifestText);
        SkillAssetBundle bundle = SkillAssetCodec.decode(payload);
        return SkillAssetSeedValidator.validate(bundle, manifest);
    }

    private static void write(ZipOutputStream output, String name, byte[] content) throws IOException {
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
                    default -> throw new IOException("SKILL seed archive chứa entry không hợp lệ");
                };
                if (entry.isDirectory() || entries.containsKey(entry.getName())) {
                    throw new IOException("SKILL seed archive chứa entry trùng hoặc directory");
                }
                entries.put(entry.getName(), readBounded(input, limit));
                input.closeEntry();
            }
        }
        if (entries.size() != 2) throw new IOException("SKILL seed archive thiếu entry bắt buộc");
        return entries;
    }

    private static byte[] readBounded(InputStream input, int limit) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int total = 0;
        int read;
        while ((read = input.read(buffer)) != -1) {
            total += read;
            if (total > limit) throw new IOException("SKILL seed archive vượt giới hạn kích thước");
            output.write(buffer, 0, read);
        }
        return output.toByteArray();
    }

    private static byte[] require(Map<String, byte[]> entries, String name) throws IOException {
        byte[] content = entries.get(name);
        if (content == null) throw new IOException("SKILL seed archive thiếu " + name);
        return content;
    }
}
