package com.nsocry.assets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class ItemAssetSeedArtifactGeneratorTest {
    private static final String SHA256 =
            "0ed18aa1fe78dbe8416484ea65797bdbae5776ad78a965d835369266d900386d";

    @Test
    void generatesDeterministicPayloadAndManifest() throws Exception {
        ItemAssetSeedArtifact first = ItemAssetSeedArtifactGenerator.generate(fixture());
        ItemAssetSeedArtifact second = ItemAssetSeedArtifactGenerator.generate(fixture());

        assertArrayEquals(first.payload(), second.payload());
        assertEquals(first.manifestText(), second.manifestText());
    }

    @Test
    void writesStableManifestFormatAndMetadata() throws Exception {
        ItemAssetSeedArtifact artifact = ItemAssetSeedArtifactGenerator.generate(fixture());

        assertEquals("""
                format=nsocry-item-seed-v1
                version=26
                optionCount=1
                itemCount=1
                payloadLength=35
                sha256=0ed18aa1fe78dbe8416484ea65797bdbae5776ad78a965d835369266d900386d
                """, artifact.manifestText());
        assertEquals(SHA256, artifact.manifest().payloadSha256());
        assertEquals(35, artifact.validation().payloadLength());
    }

    @Test
    void returnsDefensivePayloadCopies() throws Exception {
        ItemAssetSeedArtifact artifact = ItemAssetSeedArtifactGenerator.generate(fixture());
        byte[] changed = artifact.payload();
        changed[0] = 99;

        assertNotEquals(99, artifact.payload()[0]);
    }

    @Test
    void payloadCanBeDecodedWithoutSqlOrDatabase() throws Exception {
        ItemAssetSeedArtifact artifact = ItemAssetSeedArtifactGenerator.generate(fixture());

        assertEquals(fixture(), ItemAssetCodec.decode(artifact.payload()));
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
