package com.nsocry.bootstrap;

import com.nsocry.assets.MapAssetSeedArtifact;
import com.nsocry.assets.MapAssetSeedArtifactGenerator;
import com.nsocry.assets.conversion.MapAssetConversionResult;
import com.nsocry.assets.conversion.ReferenceMapAssetConverter;
import com.nsocry.operations.MapAssetSeedArchiveService;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/** Tạo MAP seed candidate offline từ dump; không mở database hoặc publish runtime. */
public final class MapAssetSeedConvertCommand {
    private static final long MAX_DUMP_BYTES = 64L * 1024 * 1024;
    private static final byte REFERENCE_MAP_VERSION = 7;

    private MapAssetSeedConvertCommand() {
    }

    public static void main(String[] args) throws Exception {
        if (args == null || args.length != 1) {
            throw new IllegalArgumentException("map-seed-convert yêu cầu đúng một dump path");
        }
        convert(Path.of(args[0]), System.out);
    }

    /** Convert, validate và xuất archive xác định cạnh dump nguồn. */
    static Path convert(Path dumpPath, PrintStream output) throws Exception {
        Objects.requireNonNull(dumpPath, "dumpPath");
        Objects.requireNonNull(output, "output");
        Path dump = dumpPath.toAbsolutePath().normalize();
        if (!Files.isRegularFile(dump)) {
            throw new IOException("MAP dump không tồn tại hoặc không phải regular file");
        }
        if (Files.size(dump) > MAX_DUMP_BYTES) {
            throw new IOException("MAP dump vượt giới hạn 64 MiB");
        }
        MapAssetConversionResult conversion = ReferenceMapAssetConverter.convert(
                REFERENCE_MAP_VERSION, Files.readString(dump, StandardCharsets.UTF_8));
        MapAssetSeedArtifact artifact = MapAssetSeedArtifactGenerator.generate(conversion.bundle());
        Path archive = candidatePath(dump);
        new MapAssetSeedArchiveService().export(artifact, archive);
        var validation = artifact.validation();
        output.println("MAP seed candidate CREATED");
        output.println("archive=" + archive);
        output.println("mapCount=" + validation.mapCount());
        output.println("npcCount=" + validation.npcCount());
        output.println("mobCount=" + validation.mobCount());
        output.println("rawByteDifferences=" + conversion.report().rawByteDifferences());
        output.println("payloadLength=" + validation.payloadLength());
        output.println("sha256=" + validation.payloadSha256());
        output.println("databaseChanged=false");
        output.println("runtimeSnapshotPublished=false");
        return archive;
    }

    private static Path candidatePath(Path dump) {
        String name = dump.getFileName().toString();
        int extension = name.lastIndexOf('.');
        String base = extension > 0 ? name.substring(0, extension) : name;
        return dump.resolveSibling(base + "-map-seed-v7-candidate.zip");
    }
}
