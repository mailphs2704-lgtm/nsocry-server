package com.nsocry.bootstrap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nsocry.assets.SkillAssetSeedArtifactGenerator;
import com.nsocry.assets.conversion.ReferenceSkillAssetConverter;
import com.nsocry.operations.SkillAssetSeedArchiveService;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SkillAssetSeedCommandsTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void generatorIsDeterministicAndPreservesRawByteDifference() {
        var bundle = ReferenceSkillAssetConverter.convert((byte) 26,
                validDump().replace(", 1, 30, 18, '[", ", 150, 30, 18, '[")).bundle();
        var first = SkillAssetSeedArtifactGenerator.generate(bundle);
        var second = SkillAssetSeedArtifactGenerator.generate(bundle);

        assertEquals(first.manifestText(), second.manifestText());
        assertEquals(150, first.validation().rawByteDifferences().get(0).value());
    }

    @Test
    void archiveRoundTripValidatesCodecCountsAndChecksum() throws Exception {
        var artifact = SkillAssetSeedArtifactGenerator.generate(
                ReferenceSkillAssetConverter.convert((byte) 26, validDump()).bundle());
        Path archive = temporaryDirectory.resolve("skill.zip");
        var service = new SkillAssetSeedArchiveService();
        service.export(artifact, archive);

        var result = service.dryRun(archive);
        assertEquals(1, result.structure().skillLevelCount());
        assertEquals(artifact.validation().payloadSha256(), result.payloadSha256());
    }

    @Test
    void archiveRejectsUnexpectedEntry() throws Exception {
        Path archive = temporaryDirectory.resolve("invalid.zip");
        try (ZipOutputStream output = new ZipOutputStream(Files.newOutputStream(archive))) {
            output.putNextEntry(new ZipEntry("unknown.bin"));
            output.write(1);
            output.closeEntry();
        }

        assertThrows(IOException.class, () -> new SkillAssetSeedArchiveService().dryRun(archive));
    }

    @Test
    void convertCommandCreatesCandidateWithoutDatabaseMutation() throws Exception {
        Path dump = temporaryDirectory.resolve("database.sql");
        Files.writeString(dump, validDump(), StandardCharsets.UTF_8);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        Path archive = SkillAssetSeedConvertCommand.convert(dump, new PrintStream(bytes, true, StandardCharsets.UTF_8));
        String report = bytes.toString(StandardCharsets.UTF_8);
        assertTrue(Files.isRegularFile(archive));
        assertTrue(report.contains("SKILL seed candidate CREATED"));
        assertTrue(report.contains("databaseChanged=false"));
    }

    @Test
    void launcherParsesBothSkillSeedCommands() {
        assertEquals(NsocryLauncher.LaunchCommand.SKILL_SEED_CONVERT,
                NsocryLauncher.parse(new String[] {"skill-seed-convert", "database.sql"}).command());
        assertEquals(NsocryLauncher.LaunchCommand.SKILL_SEED_DRY_RUN,
                NsocryLauncher.parse(new String[] {"skill-seed-dry-run", "skill.zip"}).command());
    }

    private static String validDump() {
        return """
                INSERT INTO `clazz` (`id`, `name`) VALUES
                (0, 'Ninja Cry');
                INSERT INTO `skill_option` (`id`, `name`) VALUES
                (0, 'Tấn công');
                INSERT INTO `skill_template` (`id`, `class`, `name`, `max_point`, `type`, `icon`, `description`, `skillTemplates`) VALUES
                (0, 0, 'Chiêu Cry', 12, 1, 318, 'Mô tả', '[]');
                INSERT INTO `skill` (`id`, `template_id`, `max_fight`, `level`, `mana_use`, `cooldown`, `point`, `dx`, `dy`, `options`) VALUES
                (0, 0, 1, 10, 20, 500, 1, 30, 18, '[{\"param\":10,\"id\":0}]');
                """;
    }
}
