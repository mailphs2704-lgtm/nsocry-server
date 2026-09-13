package com.nsocry.bootstrap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nsocry.assets.DataAssetSeedArtifact;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DataAssetSeedDryRunCommandTest {
    private static final String ARROW = "INSERT INTO `nj_arrow` (`id`, `imgId`) VALUES";
    private static final String EFFECT = "INSERT INTO `nj_effect` (`id`, `info`) VALUES";
    private static final String IMAGE = "INSERT INTO `nj_image` (`id`, `smallImage`) VALUES";
    private static final String PART = "INSERT INTO `nj_part` (`id`, `type`, `part`) VALUES";
    private static final String SKILL = "INSERT INTO `nj_skill` (`id`, `skillId`, `effId`, `numEff`, `skillStand`, `skillFly`) VALUES";
    private static final String TASK = "INSERT INTO `task` (`id`, `npcs`, `maps`) VALUES";
    private static final String OTHERS = "INSERT INTO `others` (`id`, `name`, `value`) VALUES";
    private static final String EFFECT_TEMPLATE = "INSERT INTO `effect` (`id`, `name`, `type`, `icon`) VALUES";

    @TempDir
    Path directory;

    @Test
    void createsVerifiedCandidateWithoutWritingArchive() throws Exception {
        Path config = writeInputs();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        DataAssetSeedArtifact artifact = DataAssetSeedDryRunCommand.dryRun(
                config, new PrintStream(bytes, true, StandardCharsets.UTF_8));

        assertEquals(26, Byte.toUnsignedInt(artifact.manifest().version()));
        assertEquals(1, artifact.manifest().taskGroupCount());
        assertEquals(2, artifact.manifest().experienceCount());
        String report = bytes.toString(StandardCharsets.UTF_8);
        assertTrue(report.contains("DATA seed candidate VERIFIED"));
        assertTrue(report.contains("sha256=" + artifact.manifest().payloadSha256()));
        assertTrue(report.contains("databaseChanged=false"));
        assertEquals(3, Files.list(directory).count());
    }

    @Test
    void rejectsMissingRequiredConfiguration() throws Exception {
        Path config = directory.resolve("missing.properties");
        Files.writeString(config, "dump.path=data.sql\n", StandardCharsets.UTF_8);

        assertThrows(IllegalArgumentException.class, () -> DataAssetSeedDryRunCommand.dryRun(
                config, new PrintStream(new ByteArrayOutputStream())));
    }

    @Test
    void rejectsMissingAuthoritativeInputFile() throws Exception {
        Path config = directory.resolve("missing-file.properties");
        Files.writeString(config, """
                dump.path=missing.sql
                game-data.path=GameData.java
                data.version=26
                max-percent-add=0
                """, StandardCharsets.UTF_8);

        assertThrows(IOException.class, () -> DataAssetSeedDryRunCommand.dryRun(
                config, new PrintStream(new ByteArrayOutputStream())));
    }

    @Test
    void rejectsInvalidExplicitVersion() throws Exception {
        Path config = writeInputs();
        Files.writeString(config, Files.readString(config).replace(
                "data.version=26", "data.version=invalid"), StandardCharsets.UTF_8);

        assertThrows(IllegalArgumentException.class, () -> DataAssetSeedDryRunCommand.dryRun(
                config, new PrintStream(new ByteArrayOutputStream())));
    }

    private Path writeInputs() throws IOException {
        Path dump = directory.resolve("database.sql");
        Path gameData = directory.resolve("GameData.java");
        Path config = directory.resolve("data.properties");
        Files.writeString(dump, validDump(), StandardCharsets.UTF_8);
        Files.writeString(gameData, gameData(), StandardCharsets.UTF_8);
        Files.writeString(config, """
                dump.path=database.sql
                game-data.path=GameData.java
                data.version=26
                max-percent-add=0
                """, StandardCharsets.UTF_8);
        return config;
    }

    private static String validDump() {
        String frame = "{\"status\":23,\"effS0Id\":24,\"e0dx\":25,\"e0dy\":26,"
                + "\"effS1Id\":27,\"e1dx\":28,\"e1dy\":29,\"effS2Id\":30,"
                + "\"e2dx\":31,\"e2dy\":32,\"arrowId\":33,\"adx\":34,\"ady\":35}";
        return String.join("\n",
                statement(ARROW, "(1, '[2,3,4]')"),
                statement(EFFECT,
                        "(1, '[{\"imgId\":5,\"dx\":1,\"dy\":-1}]')"),
                statement(IMAGE, "(1, '[6,7,8,9,10]')"),
                statement(PART,
                        "(0, 2, '[{\"id\":11,\"dx\":12,\"dy\":-3}]')"),
                statement(SKILL,
                        "(1, 20, 21, 22, '[" + frame + "]', '[]')"),
                statement(TASK, "(0, '[1,2]', '[3,4]')"),
                statement(OTHERS, "(1, 'exp', '[100,200]')"),
                statement(EFFECT_TEMPLATE,
                        "(1, 'Food', 2, 10)"));
    }

    private static String gameData() {
        return String.join("\n",
                declaration("UP_CRYSTAL", "1"),
                declaration("UP_CLOTHE", "2"),
                declaration("UP_ADORN", "3"),
                declaration("UP_WEAPON", "4"),
                declaration("COIN_UP_CRYSTAL", "5"),
                declaration("COIN_UP_CLOTHE", "6"),
                declaration("COIN_UP_ADORN", "7"),
                declaration("COIN_UP_WEAPON", "8"),
                declaration("GOLD_UP", "9"),
                declaration("MAX_PERCENT", "80"));
    }

    private static String declaration(String name, String values) {
        return "public static final int[] " + name + " = {" + values + "};";
    }

    private static String statement(String marker, String rows) {
        return marker + "\n" + rows + ";";
    }
}
