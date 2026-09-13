package com.nsocry.assets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import java.util.List;
import org.junit.jupiter.api.Test;

class AppearanceAssetSeedArtifactGeneratorTest {
    @Test
    void locksPayloadChecksumAndRoundTrip() throws Exception {
        AppearanceAssetBundle bundle = bundle();

        AppearanceAssetSeedArtifact artifact =
                AppearanceAssetSeedArtifactGenerator.generate(bundle);

        assertEquals(45, artifact.payloadLength());
        assertEquals(64, artifact.payloadSha256().length());
        assertEquals(bundle, AppearanceAssetCodec.decode(artifact.payload()));
    }

    @Test
    void returnsDefensivePayloadCopies() {
        AppearanceAssetSeedArtifact artifact =
                AppearanceAssetSeedArtifactGenerator.generate(bundle());

        byte[] first = artifact.payload();
        byte[] second = artifact.payload();

        assertNotSame(first, second);
        first[0] = 99;
        assertArrayEquals(second, artifact.payload());
    }

    private static AppearanceAssetBundle bundle() {
        AppearancePartAsset part = new AppearancePartAsset(
                (short) 1, (short) 2,
                List.of(new AppearanceLayerAsset((short) 3, (short) 4, (short) 5)));
        return new AppearanceAssetBundle(
                List.of(part), List.of(part), List.of(part),
                List.of(new LegAppearanceAsset((short) 6, (short) 7)),
                List.of(part), List.of(part), List.of(part),
                List.of(new MountAppearanceAsset(
                        (short) 8,
                        List.of(List.of((short) 9), List.of(), List.of(),
                                List.of(), List.of(), List.of()))));
    }
}
