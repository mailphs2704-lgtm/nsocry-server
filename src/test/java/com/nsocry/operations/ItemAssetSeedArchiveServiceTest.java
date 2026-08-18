package com.nsocry.operations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nsocry.assets.ItemAssetBundle;
import com.nsocry.assets.ItemAssetSeedArtifact;
import com.nsocry.assets.ItemAssetSeedArtifactGenerator;
import com.nsocry.assets.ItemAssetSeedValidationException;
import com.nsocry.assets.ItemAssetValidationResult;
import com.nsocry.assets.ItemOptionAsset;
import com.nsocry.assets.ItemTemplateAsset;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ItemAssetSeedArchiveServiceTest {
    @TempDir
    Path directory;

    @Test
    void exportsThenDryRunsWithoutDatabase() throws Exception {
        ItemAssetSeedArtifact artifact = ItemAssetSeedArtifactGenerator.generate(fixture());
        Path archive = directory.resolve("item-seed.zip");
        ItemAssetSeedArchiveService service = new ItemAssetSeedArchiveService();

        service.export(artifact, archive);
        ItemAssetValidationResult result = service.dryRun(archive);

        assertEquals(26, Byte.toUnsignedInt(result.version()));
        assertEquals(1, result.optionCount());
        assertEquals(1, result.itemCount());
    }

    @Test
    void refusesToOverwriteExistingArchive() throws Exception {
        ItemAssetSeedArtifact artifact = ItemAssetSeedArtifactGenerator.generate(fixture());
        Path archive = directory.resolve("item-seed.zip");
        ItemAssetSeedArchiveService service = new ItemAssetSeedArchiveService();
        service.export(artifact, archive);

        assertThrows(IOException.class, () -> service.export(artifact, archive));
    }

    @Test
    void rejectsArchiveWithUnexpectedEntry() throws Exception {
        Path archive = directory.resolve("unexpected.zip");
        try (ZipOutputStream output = new ZipOutputStream(Files.newOutputStream(archive))) {
            output.putNextEntry(new ZipEntry("other.txt"));
            output.write("no".getBytes(StandardCharsets.UTF_8));
            output.closeEntry();
        }

        assertThrows(IOException.class, () -> new ItemAssetSeedArchiveService().dryRun(archive));
    }

    @Test
    void rejectsPayloadChangedAfterManifestWasCreated() throws Exception {
        ItemAssetSeedArtifact artifact = ItemAssetSeedArtifactGenerator.generate(fixture());
        byte[] changed = artifact.payload();
        changed[changed.length - 1] ^= 1;
        Path archive = directory.resolve("changed.zip");
        try (ZipOutputStream output = new ZipOutputStream(Files.newOutputStream(archive))) {
            write(output, "item.bin", changed);
            write(output, "item.manifest", artifact.manifestText().getBytes(StandardCharsets.UTF_8));
        }

        assertThrows(ItemAssetSeedValidationException.class,
                () -> new ItemAssetSeedArchiveService().dryRun(archive));
    }

    private static void write(ZipOutputStream output, String name, byte[] content) throws IOException {
        output.putNextEntry(new ZipEntry(name));
        output.write(content);
        output.closeEntry();
    }

    private static ItemAssetBundle fixture() {
        return new ItemAssetBundle(
                (byte) 26,
                List.of(new ItemOptionAsset("Tang HP", (byte) 2)),
                List.of(new ItemTemplateAsset(
                        (byte) 3, (byte) 1, "Kiem", "Mo ta",
                        (byte) 10, (short) 12, (short) 20, true)));
    }
}
