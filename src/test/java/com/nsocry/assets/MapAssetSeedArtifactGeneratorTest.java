package com.nsocry.assets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class MapAssetSeedArtifactGeneratorTest {
    @Test
    void generatesDeterministicArtifactAndManifest() {
        MapAssetSeedArtifact first = MapAssetSeedArtifactGenerator.generate(bundle());
        MapAssetSeedArtifact second = MapAssetSeedArtifactGenerator.generate(bundle());

        assertArrayEquals(first.payload(), second.payload());
        assertEquals(first.manifestText(), second.manifestText());
        assertEquals(7, Byte.toUnsignedInt(first.validation().version()));
        assertEquals(1, first.validation().mapCount());
        assertEquals(1, first.validation().npcCount());
        assertEquals(1, first.validation().mobCount());
        assertTrue(first.manifestText().startsWith("format=nsocry-map-seed-v1\n"));
        assertTrue(first.manifestText().contains(
                "sha256=" + first.validation().payloadSha256()));
    }

    @Test
    void validatorRejectsChecksumMismatch() throws Exception {
        MapAssetBundle bundle = bundle();
        byte[] payload = MapAssetCodec.encode(bundle);
        MapAssetSeedManifest manifest = new MapAssetSeedManifest(
                bundle.version(), 1, 1, 1, payload.length, "0".repeat(64));

        assertThrows(IllegalArgumentException.class,
                () -> MapAssetSeedValidator.validate(bundle, manifest));
    }

    @Test
    void manifestRejectsCountsOutsideWireLimits() {
        String sha = "0".repeat(64);

        assertThrows(IllegalArgumentException.class,
                () -> new MapAssetSeedManifest((byte) 7, 256, 1, 1, 0, sha));
        assertThrows(IllegalArgumentException.class,
                () -> new MapAssetSeedManifest((byte) 7, 1, 128, 1, 0, sha));
        assertThrows(IllegalArgumentException.class,
                () -> new MapAssetSeedManifest((byte) 7, 1, 1, 32_768, 0, sha));
    }

    @Test
    void artifactDefensivelyCopiesPayload() {
        MapAssetSeedArtifact artifact = MapAssetSeedArtifactGenerator.generate(bundle());
        byte[] first = artifact.payload();
        int original = Byte.toUnsignedInt(first[0]);
        first[0] = (byte) (first[0] + 1);

        assertNotEquals(Byte.toUnsignedInt(first[0]), original);
        assertEquals(original, Byte.toUnsignedInt(artifact.payload()[0]));
    }

    @Test
    void generatedPayloadRoundTripsRawByte() throws Exception {
        MapAssetSeedArtifact artifact = MapAssetSeedArtifactGenerator.generate(bundle());
        MapAssetBundle decoded = MapAssetCodec.decode(artifact.payload());

        assertEquals(bundle(), decoded);
        assertEquals(200, Byte.toUnsignedInt(decoded.mobs().get(0).moveRange()));
    }

    private static MapAssetBundle bundle() {
        return new MapAssetBundle(
                (byte) 7,
                List.of("Map Cry"),
                List.of(new NpcTemplateAsset(
                        "NPC Cry", (short) 56, (short) 57, (short) 58,
                        List.of(List.of("Mua", "Bán"), List.of("Nói chuyện")))),
                List.of(new MobTemplateAsset(
                        (byte) 4, "Mob Cry", 500_000, (byte) 200, (byte) 2)));
    }
}
