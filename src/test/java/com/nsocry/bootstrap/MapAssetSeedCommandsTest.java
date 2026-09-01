package com.nsocry.bootstrap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nsocry.assets.MapAssetSeedManifestParser;
import com.nsocry.operations.MapAssetSeedArchiveService;
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

class MapAssetSeedCommandsTest {
    @TempDir
    Path directory;

    @Test
    void convertThenDryRunPreservesCountsAndChecksum() throws Exception {
        Path dump = directory.resolve("database.sql");
        Files.writeString(dump, validDump(), StandardCharsets.UTF_8);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        Path archive = MapAssetSeedConvertCommand.convert(
                dump, new PrintStream(bytes, true, StandardCharsets.UTF_8));
        var validation = new MapAssetSeedArchiveService().dryRun(archive);

        assertEquals(directory.resolve("database-map-seed-v7-candidate.zip"), archive);
        assertEquals(1, validation.mapCount());
        assertEquals(1, validation.npcCount());
        assertEquals(1, validation.mobCount());
        String report = bytes.toString(StandardCharsets.UTF_8);
        assertTrue(report.contains("MAP seed candidate CREATED"));
        assertTrue(report.contains("databaseChanged=false"));
        assertTrue(report.contains("runtimeSnapshotPublished=false"));
    }

    @Test
    void archiveRejectsPayloadChangedAfterManifest() throws Exception {
        Path archive = candidate();
        byte[][] content = readCandidateEntries(archive);
        content[0][content[0].length - 1] ^= 1;
        Path changed = directory.resolve("changed.zip");
        try (ZipOutputStream output = new ZipOutputStream(Files.newOutputStream(changed))) {
            write(output, "map.bin", content[0]);
            write(output, "map.manifest", content[1]);
        }

        assertThrows(IllegalArgumentException.class,
                () -> new MapAssetSeedArchiveService().dryRun(changed));
    }

    @Test
    void archiveRejectsUnexpectedEntry() throws Exception {
        Path archive = directory.resolve("invalid.zip");
        try (ZipOutputStream output = new ZipOutputStream(Files.newOutputStream(archive))) {
            write(output, "unknown.bin", new byte[] {1});
        }

        assertThrows(IOException.class,
                () -> new MapAssetSeedArchiveService().dryRun(archive));
    }

    @Test
    void validatedArchiveReturnsDefensivePayloadCopy() throws Exception {
        var validated = new MapAssetSeedArchiveService().readValidated(candidate());
        byte[] first = validated.payload();
        int original = Byte.toUnsignedInt(first[0]);
        first[0] ^= 1;

        assertNotEquals(Byte.toUnsignedInt(first[0]), original);
        assertEquals(original, Byte.toUnsignedInt(validated.payload()[0]));
    }

    @Test
    void manifestParserRejectsUnknownOrDuplicateField() {
        String valid = """
                format=nsocry-map-seed-v1
                version=7
                mapCount=1
                npcCount=1
                mobCount=1
                payloadLength=10
                sha256=%s
                """.formatted("0".repeat(64));

        assertEquals(7, Byte.toUnsignedInt(MapAssetSeedManifestParser.parse(valid).version()));
        assertThrows(IllegalArgumentException.class,
                () -> MapAssetSeedManifestParser.parse(valid + "unknown=1\n"));
        assertThrows(IllegalArgumentException.class,
                () -> MapAssetSeedManifestParser.parse(valid + "mapCount=1\n"));
    }

    @Test
    void launcherParsesMapConvertAndDryRunCommands() {
        assertEquals(NsocryLauncher.LaunchCommand.MAP_SEED_CONVERT,
                NsocryLauncher.parse(new String[] {"map-seed-convert", "database.sql"}).command());
        assertEquals(NsocryLauncher.LaunchCommand.MAP_SEED_DRY_RUN,
                NsocryLauncher.parse(new String[] {"map-seed-dry-run", "map.zip"}).command());
    }

    private Path candidate() throws Exception {
        Path dump = directory.resolve("source.sql");
        Files.writeString(dump, validDump(), StandardCharsets.UTF_8);
        return MapAssetSeedConvertCommand.convert(dump, new PrintStream(new ByteArrayOutputStream()));
    }

    private static byte[][] readCandidateEntries(Path archive) throws IOException {
        byte[][] result = new byte[2][];
        try (var input = new java.util.zip.ZipInputStream(Files.newInputStream(archive))) {
            ZipEntry entry;
            while ((entry = input.getNextEntry()) != null) {
                if (entry.getName().equals("map.bin")) result[0] = input.readAllBytes();
                if (entry.getName().equals("map.manifest")) result[1] = input.readAllBytes();
            }
        }
        return result;
    }

    private static void write(ZipOutputStream output, String name, byte[] content) throws IOException {
        output.putNextEntry(new ZipEntry(name));
        output.write(content);
        output.closeEntry();
    }

    private static String validDump() {
        return """
                INSERT INTO `map` (`id`, `name`, `npc`, `waypoint`, `monster`, `zone_number`, `locationStand`, `tileId`, `bgId`, `type`, `item`, `behind`, `betwen`, `front`) VALUES
                (0, 'Map Cry', '[]', '[]', '[]', 30, '[]', 1, 0, 0, '[]', '[]', '[]', '[]');
                INSERT INTO `npc` (`id`, `name`, `head`, `body`, `leg`, `menu`) VALUES
                (0, 'NPC Cry', 56, 57, 58, '[["Mua","Bán"],["Nói chuyện"]]');
                INSERT INTO `monster` (`id`, `name`, `level`, `boss`, `type`, `hp`, `range_move`, `speed`, `type_fly`, `n_img`, `move`, `attack`, `sprites`, `frames`, `sequence`, `frame_char`, `index_splash`) VALUES
                (0, 'Mob Cry', 1, 0, 4, 500000, 200, 2, 0, 3, '[]', '[]', '[]', '[]', '[]', '[]', '[]');
                """;
    }
}
