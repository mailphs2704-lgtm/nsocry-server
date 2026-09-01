package com.nsocry.bootstrap;

import com.nsocry.assets.DataAssetBundle;
import com.nsocry.assets.DataAssetSeedArtifact;
import com.nsocry.assets.DataAssetSeedArtifactGenerator;
import com.nsocry.assets.conversion.ReferenceDataAssetConverter;
import com.nsocry.assets.conversion.ReferenceGameDataProgressionParser;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;

/** Dry-run DATA candidate từ dump và GameData authoritative; không ghi database/runtime/file. */
public final class DataAssetSeedDryRunCommand {
    private static final long MAX_CONFIG_BYTES = 1024L * 1024;
    private static final long MAX_DUMP_BYTES = 64L * 1024 * 1024;
    private static final long MAX_GAME_DATA_BYTES = 4L * 1024 * 1024;

    private DataAssetSeedDryRunCommand() {
    }

    /** Entry CLI yêu cầu đúng một đường dẫn properties chứa toàn bộ input explicit. */
    public static void main(String[] args) throws Exception {
        if (args == null || args.length != 1) {
            throw new IllegalArgumentException("data-seed-dry-run yêu cầu đúng một config path");
        }
        dryRun(Path.of(args[0]), System.out);
    }

    /** Đọc nguồn bounded, convert trong bộ nhớ và in metadata candidate đã self-validate. */
    static DataAssetSeedArtifact dryRun(Path configurationPath, PrintStream output) throws Exception {
        Objects.requireNonNull(configurationPath, "configurationPath");
        Objects.requireNonNull(output, "output");
        Path configuration = requireFile(configurationPath, MAX_CONFIG_BYTES, "DATA config");
        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(configuration, StandardCharsets.UTF_8)) {
            properties.load(reader);
        }

        Path base = configuration.getParent();
        Path dump = resolve(base, required(properties, "dump.path"));
        Path gameData = resolve(base, required(properties, "game-data.path"));
        requireFile(dump, MAX_DUMP_BYTES, "DATA dump");
        requireFile(gameData, MAX_GAME_DATA_BYTES, "GameData source");
        int version = integer(properties, "data.version");
        double maxPercentAdd = decimal(properties, "max-percent-add");

        String dumpText = Files.readString(dump, StandardCharsets.UTF_8);
        String gameDataText = Files.readString(gameData, StandardCharsets.UTF_8);
        DataAssetBundle bundle = ReferenceDataAssetConverter.convert(
                dumpText,
                version,
                maxPercentAdd,
                ReferenceGameDataProgressionParser.parse(gameDataText));
        DataAssetSeedArtifact artifact = DataAssetSeedArtifactGenerator.generate(bundle);
        print(output, artifact);
        return artifact;
    }

    private static Path requireFile(Path path, long maximumBytes, String label) throws IOException {
        Path absolute = path.toAbsolutePath().normalize();
        if (!Files.isRegularFile(absolute)) {
            throw new IOException(label + " không tồn tại hoặc không phải regular file");
        }
        if (Files.size(absolute) > maximumBytes) {
            throw new IOException(label + " vượt giới hạn byte");
        }
        return absolute;
    }

    private static Path resolve(Path base, String configured) {
        Path path = Path.of(configured);
        return path.isAbsolute() ? path.normalize() : base.resolve(path).normalize();
    }

    private static String required(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("DATA config thiếu " + key);
        }
        return value.trim();
    }

    private static int integer(Properties properties, String key) {
        try {
            return Integer.parseInt(required(properties, key));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("DATA config " + key + " không phải integer", exception);
        }
    }

    private static double decimal(Properties properties, String key) {
        try {
            return Double.parseDouble(required(properties, key));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("DATA config " + key + " không phải decimal", exception);
        }
    }

    private static void print(PrintStream output, DataAssetSeedArtifact artifact) {
        var manifest = artifact.manifest();
        output.println("DATA seed candidate VERIFIED");
        output.println("version=" + Byte.toUnsignedInt(manifest.version()));
        output.println("taskGroupCount=" + manifest.taskGroupCount());
        output.println("experienceCount=" + manifest.experienceCount());
        output.println("payloadLength=" + manifest.payloadLength());
        output.println("sha256=" + manifest.payloadSha256());
        output.println("databaseChanged=false");
        output.println("runtimeSnapshotPublished=false");
        output.println("serverStartupWired=false");
    }
}
