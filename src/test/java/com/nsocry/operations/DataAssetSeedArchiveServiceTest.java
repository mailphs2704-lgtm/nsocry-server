package com.nsocry.operations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nsocry.assets.ClientGraphicBlock;
import com.nsocry.assets.DataAssetBundle;
import com.nsocry.assets.DataAssetSeedArtifact;
import com.nsocry.assets.DataAssetSeedArtifactGenerator;
import com.nsocry.assets.ProgressionTable;
import com.nsocry.assets.TaskRouteAsset;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DataAssetSeedArchiveServiceTest {
    @TempDir
    Path directory;

    @Test
    void exportsAndReadsBackCandidateWithoutMutation() throws Exception {
        DataAssetSeedArtifact artifact = DataAssetSeedArtifactGenerator.generate(bundle());
        Path archive = directory.resolve("data.zip");
        DataAssetSeedArchiveService service = new DataAssetSeedArchiveService();

        service.export(artifact, archive);
        var result = service.dryRun(archive);

        assertEquals(7, Byte.toUnsignedInt(result.version()));
        assertEquals(1, result.taskGroupCount());
        assertEquals(131, result.experienceCount());
        assertEquals(artifact.payload().length, result.payloadLength());
    }

    @Test
    void refusesOverwriteAndUnexpectedEntry() throws Exception {
        DataAssetSeedArtifact artifact = DataAssetSeedArtifactGenerator.generate(bundle());
        Path archive = directory.resolve("data.zip");
        DataAssetSeedArchiveService service = new DataAssetSeedArchiveService();
        service.export(artifact, archive);
        assertThrows(IOException.class, () -> service.export(artifact, archive));

        Path unexpected = directory.resolve("unexpected.zip");
        try (ZipOutputStream output = new ZipOutputStream(Files.newOutputStream(unexpected))) {
            write(output, "other.txt", "no".getBytes(StandardCharsets.UTF_8));
        }
        assertThrows(IOException.class, () -> service.dryRun(unexpected));
    }

    @Test
    void rejectsChangedPayloadAndReturnsDefensiveCopy() throws Exception {
        DataAssetSeedArtifact artifact = DataAssetSeedArtifactGenerator.generate(bundle());
        byte[] changed = artifact.payload();
        changed[changed.length - 1] ^= 1;
        Path corrupt = directory.resolve("corrupt.zip");
        try (ZipOutputStream output = new ZipOutputStream(Files.newOutputStream(corrupt))) {
            write(output, "data.bin", changed);
            write(output, "data.manifest", artifact.manifestText().getBytes(StandardCharsets.UTF_8));
        }
        assertThrows(IllegalArgumentException.class,
                () -> new DataAssetSeedArchiveService().dryRun(corrupt));

        Path valid = directory.resolve("valid.zip");
        DataAssetSeedArchiveService service = new DataAssetSeedArchiveService();
        service.export(artifact, valid);
        ValidatedDataAssetSeedArchive validated = service.readValidated(valid);
        byte[] exposed = validated.payload();
        exposed[0] = 99;
        assertNotEquals(99, validated.payload()[0]);
    }

    private static void write(ZipOutputStream output, String name, byte[] content) throws IOException {
        output.putNextEntry(new ZipEntry(name));
        output.write(content);
        output.closeEntry();
    }

    private static DataAssetBundle bundle() {
        EnumMap<ClientGraphicBlock, byte[]> graphics = new EnumMap<>(ClientGraphicBlock.class);
        for (ClientGraphicBlock block : ClientGraphicBlock.values()) {
            graphics.put(block, new byte[] {(byte) block.ordinal()});
        }
        EnumMap<ProgressionTable, int[]> progression = new EnumMap<>(ProgressionTable.class);
        for (ProgressionTable table : ProgressionTable.values()) {
            progression.put(table, new int[] {table.ordinal() + 1});
        }
        long[] experience = new long[131];
        Arrays.setAll(experience, index -> index * 100L);
        return new DataAssetBundle(
                (byte) 7,
                graphics,
                List.of(new TaskRouteAsset(new byte[] {1}, new byte[] {2})),
                experience,
                progression,
                new byte[] {0});
    }
}
