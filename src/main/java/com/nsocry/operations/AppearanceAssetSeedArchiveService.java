package com.nsocry.operations;

import com.nsocry.assets.AppearanceAssetBundle;
import com.nsocry.assets.AppearanceAssetCodec;
import com.nsocry.assets.AppearanceAssetSeedArtifact;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Map;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/** Xuất và đọc lại appearance archive offline với entry và kích thước fail-closed. */
public final class AppearanceAssetSeedArchiveService {
    private static final String PAYLOAD_ENTRY = "appearance.bin";
    private static final String MANIFEST_ENTRY = "appearance.manifest";
    private static final int MAX_PAYLOAD_BYTES = 16 * 1024 * 1024;
    private static final int MAX_MANIFEST_BYTES = 4096;

    /** Ghi archive xác định qua file tạm; không ghi đè file tồn tại. */
    public void export(AppearanceAssetSeedArtifact artifact, Path target) throws IOException {
        Objects.requireNonNull(artifact, "artifact");
        Path absolute = Objects.requireNonNull(target, "target").toAbsolutePath().normalize();
        Path directory = Objects.requireNonNull(absolute.getParent(), "target parent");
        Files.createDirectories(directory);
        if (Files.exists(absolute)) throw new IOException("Không ghi đè appearance archive");
        Path temporary = Files.createTempFile(directory, ".nsocry-appearance-", ".tmp");
        try {
            try (ZipOutputStream output = new ZipOutputStream(Files.newOutputStream(temporary))) {
                write(output, PAYLOAD_ENTRY, artifact.payload());
                String manifest = "format=nsocry-appearance-seed-v1\n"
                        + "payloadLength=" + artifact.payloadLength() + "\n"
                        + "sha256=" + artifact.payloadSha256() + "\n";
                write(output, MANIFEST_ENTRY, manifest.getBytes(StandardCharsets.UTF_8));
            }
            Files.move(temporary, absolute, StandardCopyOption.ATOMIC_MOVE);
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    /** Đọc đủ hai entry, đối chiếu manifest/checksum rồi bắt buộc codec round-trip. */
    public AppearanceAssetArchiveValidationResult dryRun(Path archive) throws IOException {
        Map<String, byte[]> entries = readEntries(Objects.requireNonNull(archive, "archive"));
        byte[] payload = require(entries, PAYLOAD_ENTRY);
        Map<String, String> manifest = parseManifest(
                new String(require(entries, MANIFEST_ENTRY), StandardCharsets.UTF_8));
        if (!"nsocry-appearance-seed-v1".equals(manifest.get("format"))) {
            throw new IOException("Sai format appearance manifest");
        }
        int expectedLength;
        try {
            expectedLength = Integer.parseInt(requireValue(manifest, "payloadLength"));
        } catch (NumberFormatException failure) {
            throw new IOException("payloadLength appearance không hợp lệ", failure);
        }
        String expectedSha = requireValue(manifest, "sha256");
        String actualSha = sha256(payload);
        if (payload.length != expectedLength || !actualSha.equals(expectedSha)) {
            throw new IOException("Appearance archive không khớp length/checksum");
        }
        AppearanceAssetBundle decoded = AppearanceAssetCodec.decode(payload);
        byte[] roundTrip = AppearanceAssetCodec.encode(decoded);
        if (!Arrays.equals(payload, roundTrip)) {
            throw new IOException("Appearance archive không round-trip");
        }
        return new AppearanceAssetArchiveValidationResult(payload.length, actualSha, true);
    }

    private static Map<String, byte[]> readEntries(Path archive) throws IOException {
        Map<String, byte[]> entries = new HashMap<>();
        try (ZipInputStream input = new ZipInputStream(Files.newInputStream(archive))) {
            ZipEntry entry;
            while ((entry = input.getNextEntry()) != null) {
                int limit = switch (entry.getName()) {
                    case PAYLOAD_ENTRY -> MAX_PAYLOAD_BYTES;
                    case MANIFEST_ENTRY -> MAX_MANIFEST_BYTES;
                    default -> throw new IOException("Appearance archive có entry không hợp lệ");
                };
                if (entry.isDirectory() || entries.containsKey(entry.getName())) {
                    throw new IOException("Appearance archive có directory/entry trùng");
                }
                entries.put(entry.getName(), readBounded(input, limit));
                input.closeEntry();
            }
        }
        if (entries.size() != 2) throw new IOException("Appearance archive thiếu entry");
        return entries;
    }

    private static Map<String, String> parseManifest(String text) throws IOException {
        Map<String, String> values = new HashMap<>();
        for (String line : text.split("\\R")) {
            if (line.isBlank()) continue;
            int separator = line.indexOf('=');
            if (separator <= 0 || values.put(line.substring(0, separator),
                    line.substring(separator + 1)) != null) {
                throw new IOException("Appearance manifest không hợp lệ");
            }
        }
        if (values.size() != 3) throw new IOException("Appearance manifest thừa/thiếu field");
        return values;
    }

    private static void write(ZipOutputStream output, String name, byte[] content) throws IOException {
        ZipEntry entry = new ZipEntry(name);
        entry.setTime(0L);
        output.putNextEntry(entry);
        output.write(content);
        output.closeEntry();
    }

    private static byte[] readBounded(InputStream input, int limit) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int total = 0;
        int read;
        while ((read = input.read(buffer)) != -1) {
            total += read;
            if (total > limit) throw new IOException("Appearance archive vượt giới hạn");
            output.write(buffer, 0, read);
        }
        return output.toByteArray();
    }

    private static byte[] require(Map<String, byte[]> entries, String key) throws IOException {
        byte[] value = entries.get(key);
        if (value == null) throw new IOException("Appearance archive thiếu " + key);
        return value;
    }

    private static String requireValue(Map<String, String> values, String key) throws IOException {
        String value = values.get(key);
        if (value == null) throw new IOException("Appearance manifest thiếu " + key);
        return value;
    }

    private static String sha256(byte[] payload) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(payload));
        } catch (NoSuchAlgorithmException impossible) {
            throw new IllegalStateException("JVM không hỗ trợ SHA-256", impossible);
        }
    }
}
