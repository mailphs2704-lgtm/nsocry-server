package com.nsocry.bootstrap;

import com.nsocry.assets.DataAssetSeedArtifact;
import com.nsocry.operations.DataAssetSeedArchiveService;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Objects;

/** Tạo DATA archive từ cùng input authoritative của candidate dry-run đã khóa. */
public final class DataAssetSeedConvertCommand {
    private DataAssetSeedConvertCommand() {
    }

    /** Entry CLI yêu cầu đúng một DATA properties path. */
    public static void main(String[] args) throws Exception {
        if (args == null || args.length != 1) {
            throw new IllegalArgumentException("data-seed-convert yêu cầu đúng một config path");
        }
        convert(Path.of(args[0]), System.out);
    }

    /** Convert trong bộ nhớ, lưu archive cạnh config rồi dry-run đọc lại archive. */
    static Path convert(Path configurationPath, PrintStream output) throws Exception {
        Objects.requireNonNull(configurationPath, "configurationPath");
        Objects.requireNonNull(output, "output");
        DataAssetSeedArtifact artifact = DataAssetSeedDryRunCommand.dryRun(configurationPath, output);
        Path archive = candidatePath(configurationPath.toAbsolutePath().normalize(), artifact);
        DataAssetSeedArchiveService service = new DataAssetSeedArchiveService();
        service.export(artifact, archive);
        service.dryRun(archive);
        output.println("archive=" + archive);
        output.println("archiveRoundTripVerified=true");
        return archive;
    }

    private static Path candidatePath(Path configuration, DataAssetSeedArtifact artifact) {
        String name = configuration.getFileName().toString();
        int extension = name.lastIndexOf('.');
        String base = extension > 0 ? name.substring(0, extension) : name;
        int version = Byte.toUnsignedInt(artifact.manifest().version());
        return configuration.resolveSibling(base + "-data-seed-v" + version + "-candidate.zip");
    }
}
