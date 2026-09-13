package com.nsocry.bootstrap;

import com.nsocry.assets.SkillAssetSeedArtifact;
import com.nsocry.assets.SkillAssetSeedArtifactGenerator;
import com.nsocry.assets.conversion.ReferenceSkillAssetConverter;
import com.nsocry.assets.conversion.SkillAssetConversionResult;
import com.nsocry.operations.SkillAssetSeedArchiveService;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/** Command tạo SKILL candidate offline từ dump, không dùng JDBC. */
public final class SkillAssetSeedConvertCommand {
    private static final long MAX_DUMP_BYTES = 64L * 1024 * 1024;
    private static final byte REFERENCE_SKILL_VERSION = 26;

    private SkillAssetSeedConvertCommand() {
    }

    /** Entry CLI yêu cầu đúng một dump path rồi tạo SKILL candidate offline. */
    public static void main(String[] args) throws Exception {
        if (args == null || args.length != 1) {
            throw new IllegalArgumentException("skill-seed-convert yêu cầu đúng một dump path");
        }
        convert(Path.of(args[0]), System.out);
    }

    /** Chạy converter/validator rồi tạo archive xác định cạnh dump nguồn. */
    static Path convert(Path dumpPath, PrintStream output) throws Exception {
        Objects.requireNonNull(dumpPath, "dumpPath");
        Objects.requireNonNull(output, "output");
        Path dump = dumpPath.toAbsolutePath().normalize();
        if (!Files.isRegularFile(dump)) throw new IOException("SKILL dump không tồn tại hoặc không phải regular file");
        if (Files.size(dump) > MAX_DUMP_BYTES) throw new IOException("SKILL dump vượt giới hạn 64 MiB");
        SkillAssetConversionResult conversion = ReferenceSkillAssetConverter.convert(
                REFERENCE_SKILL_VERSION, Files.readString(dump, StandardCharsets.UTF_8));
        SkillAssetSeedArtifact artifact = SkillAssetSeedArtifactGenerator.generate(conversion.bundle());
        Path archive = candidatePath(dump);
        new SkillAssetSeedArchiveService().export(artifact, archive);
        var structure = artifact.validation().structure();
        output.println("SKILL seed candidate CREATED");
        output.println("archive=" + archive);
        output.println("optionTemplateCount=" + structure.optionTemplateCount());
        output.println("classCount=" + structure.classCount());
        output.println("skillTemplateCount=" + structure.skillTemplateCount());
        output.println("skillLevelCount=" + structure.skillLevelCount());
        output.println("skillLevelOptionCount=" + structure.skillLevelOptionCount());
        output.println("rawByteDifferences=" + artifact.validation().rawByteDifferences());
        output.println("payloadLength=" + artifact.validation().payloadLength());
        output.println("sha256=" + artifact.validation().payloadSha256());
        output.println("databaseChanged=false");
        return archive;
    }

    private static Path candidatePath(Path dump) {
        String name = dump.getFileName().toString();
        int extension = name.lastIndexOf('.');
        String base = extension > 0 ? name.substring(0, extension) : name;
        return dump.resolveSibling(base + "-skill-seed-v26-candidate.zip");
    }
}
