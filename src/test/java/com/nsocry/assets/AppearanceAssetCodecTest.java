package com.nsocry.assets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;

class AppearanceAssetCodecTest {
    @Test
    void roundTripsHeadBodyLegAndMount() throws Exception {
        AppearanceAssetBundle expected = fixtureBundle();

        AppearanceAssetBundle actual = AppearanceAssetCodec.decode(
                AppearanceAssetCodec.encode(expected));

        assertEquals(expected, actual);
    }

    @Test
    void requiresEqualVariantCounts() {
        assertThrows(IllegalArgumentException.class, () -> new AppearanceAssetBundle(
                List.of(part()), List.of(), List.of(part()), List.of(),
                List.of(), List.of(), List.of(), List.of()));
    }

    @Test
    void requiresExactlySixMountFrameGroups() {
        assertThrows(IllegalArgumentException.class,
                () -> new MountAppearanceAsset((short) 1, List.of(List.of())));
    }

    @Test
    void rejectsInvalidPartDescriptor() {
        byte[] payload = {1, 3};

        assertThrows(IOException.class, () -> AppearanceAssetCodec.decode(payload));
    }

    private static AppearanceAssetBundle fixtureBundle() {
        List<AppearancePartAsset> parts = List.of(part());
        MountAppearanceAsset mount = new MountAppearanceAsset(
                (short) 50,
                List.of(List.of((short) 1), List.of(), List.of((short) 2),
                        List.of(), List.of((short) 3), List.of()));
        return new AppearanceAssetBundle(
                parts, parts, parts,
                List.of(new LegAppearanceAsset((short) 20, (short) 21)),
                parts, parts, parts,
                List.of(mount));
    }

    private static AppearancePartAsset part() {
        return new AppearancePartAsset(
                (short) 10,
                (short) 11,
                List.of(new AppearanceLayerAsset((short) 12, (short) 1, (short) -1)));
    }
}
