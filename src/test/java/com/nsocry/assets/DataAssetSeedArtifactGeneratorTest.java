package com.nsocry.assets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumMap;
import java.util.List;
import org.junit.jupiter.api.Test;

class DataAssetSeedArtifactGeneratorTest {
    @Test
    void generatesDeterministicPayloadAndManifest() {
        DataAssetSeedArtifact first = DataAssetSeedArtifactGenerator.generate(bundle());
        DataAssetSeedArtifact second = DataAssetSeedArtifactGenerator.generate(bundle());

        assertArrayEquals(first.payload(), second.payload());
        assertEquals(first.manifest(), second.manifest());
        assertEquals(first.manifestText(), second.manifestText());
        assertTrue(first.manifestText().startsWith("format=nsocry-data-seed-v1\n"));
        assertTrue(first.manifestText().contains("sha256=" + first.manifest().payloadSha256()));
    }

    @Test
    void manifestMatchesEncodedCandidate() throws Exception {
        DataAssetSeedArtifact artifact = DataAssetSeedArtifactGenerator.generate(bundle());

        assertEquals(26, Byte.toUnsignedInt(artifact.manifest().version()));
        assertEquals(1, artifact.manifest().taskGroupCount());
        assertEquals(2, artifact.manifest().experienceCount());
        assertEquals(DataAssetCodec.encode(bundle()).length, artifact.manifest().payloadLength());
        assertEquals(64, artifact.manifest().payloadSha256().length());
    }

    @Test
    void validatorRejectsChecksumMismatch() {
        DataAssetSeedArtifact artifact = DataAssetSeedArtifactGenerator.generate(bundle());
        DataAssetSeedManifest invalid = new DataAssetSeedManifest(
                artifact.manifest().version(),
                artifact.manifest().taskGroupCount(),
                artifact.manifest().experienceCount(),
                artifact.manifest().payloadLength(),
                "0".repeat(64));

        assertThrows(IllegalArgumentException.class,
                () -> DataAssetSeedValidator.validate(bundle(), invalid));
    }

    @Test
    void artifactDefensivelyCopiesPayload() {
        DataAssetSeedArtifact artifact = DataAssetSeedArtifactGenerator.generate(bundle());
        byte[] changed = artifact.payload();
        int original = Byte.toUnsignedInt(changed[0]);
        changed[0]++;

        assertNotEquals(original, Byte.toUnsignedInt(changed[0]));
        assertEquals(original, Byte.toUnsignedInt(artifact.payload()[0]));
    }

    @Test
    void generatedPayloadRoundTripsRawTaskByte() throws Exception {
        DataAssetSeedArtifact artifact = DataAssetSeedArtifactGenerator.generate(bundle());
        DataAssetBundle decoded = DataAssetCodec.decode(artifact.payload());

        assertEquals(200, Byte.toUnsignedInt(decoded.taskRoutes().get(0).get(0).npcId()));
        assertArrayEquals(new long[] {100L, 200L}, decoded.experienceThresholds());
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
        return new DataAssetBundle(
                (byte) 26,
                graphics,
                List.of(List.of(new TaskRouteAsset((byte) 200, (byte) 2))),
                new long[] {100L, 200L},
                progression,
                new byte[] {1, 2, 3});
    }
}
