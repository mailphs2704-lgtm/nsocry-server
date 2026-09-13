package com.nsocry.bootstrap;

import com.nsocry.assets.AppearanceAssetSeedArtifact;
import com.nsocry.assets.AppearanceAssetSeedArtifactGenerator;
import com.nsocry.assets.conversion.ReferenceAppearanceAssetConverter;
import com.nsocry.operations.AppearanceAssetSeedArchiveService;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;

/** Chuyển appearance authoritative từ DATA dump thành archive đã self-validate, hoàn toàn offline. */
public final class AppearanceAssetSeedConvertCommand {
    private static final long MAX_CONFIG_BYTES = 1024L * 1024;
    private static final long MAX_DUMP_BYTES = 64L * 1024 * 1024;

    private AppearanceAssetSeedConvertCommand() {
    }

    /** Entry CLI yêu cầu đúng một DATA properties path có dump.path. */
    public static void main(String[] args) throws Exception {
        if (args == null || args.length != 1) {
            throw new IllegalArgumentException(
                    "appearance-seed-convert yêu cầu đúng một config path");
        }
        convert(Path.of(args[0]), System.out);
    }

    /** Convert, xuất archive cạnh config và dry-run đọc lại trước khi báo VERIFIED. */
    static Path convert(Path configurationPath, PrintStream output) throws Exception {
        Objects.requireNonNull(output, "output");
        Path configuration = requireFile(
                Objects.requireNonNull(configurationPath, "configurationPath"),
                MAX_CONFIG_BYTES, "appearance config");
        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(configuration, StandardCharsets.UTF_8)) {
            properties.load(reader);
        }
        String configuredDump = properties.getProperty("dump.path");
        if (configuredDump == null || configuredDump.isBlank()) {
            throw new IllegalArgumentException("appearance config thiếu dump.path");
        }
        Path dump = Path.of(configuredDump.trim());
        if (!dump.isAbsolute()) dump = configuration.getParent().resolve(dump).normalize();
        dump = requireFile(dump, MAX_DUMP_BYTES, "appearance dump");

        AppearanceAssetSeedArtifact artifact = AppearanceAssetSeedArtifactGenerator.generate(
                ReferenceAppearanceAssetConverter.convert(
                        Files.readString(dump, StandardCharsets.UTF_8)));
        String name = configuration.getFileName().toString();
        int extension = name.lastIndexOf('.');
        String base = extension > 0 ? name.substring(0, extension) : name;
        Path archive = configuration.resolveSibling(
                base + "-appearance-seed-candidate.zip");
        AppearanceAssetSeedArchiveService service = new AppearanceAssetSeedArchiveService();
        service.export(artifact, archive);
        var validation = service.dryRun(archive);

        output.println("APPEARANCE seed candidate VERIFIED");
        output.println("payloadLength=" + validation.payloadLength());
        output.println("sha256=" + validation.payloadSha256());
        output.println("archive=" + archive);
        output.println("archiveRoundTripVerified=" + validation.roundTripVerified());
        output.println("databaseChanged=false");
        output.println("runtimeSnapshotPublished=false");
        output.println("serverStartupWired=false");
        return archive;
    }

    private static Path requireFile(Path path, long maximumBytes, String label)
            throws IOException {
        Path absolute = path.toAbsolutePath().normalize();
        if (!Files.isRegularFile(absolute)) {
            throw new IOException(label + " không tồn tại hoặc không phải regular file");
        }
        if (Files.size(absolute) > maximumBytes) {
            throw new IOException(label + " vượt giới hạn byte");
        }
        return absolute;
    }
}
