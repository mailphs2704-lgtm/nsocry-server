package com.nsocry.bootstrap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nsocry.assets.ItemAssetValidationResult;
import com.nsocry.operations.ItemAssetSeedArchiveService;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ItemAssetSeedConvertCommandTest {
    @TempDir
    Path directory;

    @Test
    void convertsDumpToVerifiedCandidateBesideSource() throws Exception {
        Path dump = writeFixture("database.sql");
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        Path archive = ItemAssetSeedConvertCommand.convert(dump, new PrintStream(output, true, StandardCharsets.UTF_8));
        ItemAssetValidationResult result = new ItemAssetSeedArchiveService().dryRun(archive);

        assertEquals(directory.resolve("database-item-seed-v26-candidate.zip"), archive);
        assertEquals(1, result.optionCount());
        assertEquals(1, result.itemCount());
        String report = output.toString(StandardCharsets.UTF_8);
        assertTrue(report.contains("ITEM seed candidate CREATED"));
        assertTrue(report.contains("databaseChanged=false"));
    }

    @Test
    void refusesToOverwriteExistingCandidate() throws Exception {
        Path dump = writeFixture("source.sql");
        PrintStream output = new PrintStream(new ByteArrayOutputStream());
        ItemAssetSeedConvertCommand.convert(dump, output);

        assertThrows(IOException.class, () -> ItemAssetSeedConvertCommand.convert(dump, output));
    }

    @Test
    void rejectsMissingDumpBeforeConversion() {
        assertThrows(IOException.class, () -> ItemAssetSeedConvertCommand.convert(
                directory.resolve("missing.sql"), new PrintStream(new ByteArrayOutputStream())));
    }

    @Test
    void rejectsDumpLargerThanHardLimit() throws Exception {
        Path dump = directory.resolve("large.sql");
        try (FileChannel channel = FileChannel.open(dump, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
            channel.position(64L * 1024 * 1024);
            channel.write(ByteBuffer.wrap(new byte[] {0}));
        }

        assertThrows(IOException.class, () -> ItemAssetSeedConvertCommand.convert(
                dump, new PrintStream(new ByteArrayOutputStream())));
    }

    private Path writeFixture(String name) throws IOException {
        Path path = directory.resolve(name);
        Files.writeString(path, """
                INSERT INTO `item_option` (`id`, `type`, `name`) VALUES
                (0, 2, 'Tấn công +#');
                INSERT INTO `item` (`id`, `name`, `type`, `gender`, `description`, `level`, `icon`, `part`, `fashion`, `isUpToUp`) VALUES
                (0, 'Đá Cry', 26, 2, 'Dùng nâng cấp', 1, 188, -1, -1, 1);
                """, StandardCharsets.UTF_8);
        return path;
    }
}
