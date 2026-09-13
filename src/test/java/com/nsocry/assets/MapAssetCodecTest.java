package com.nsocry.assets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;

class MapAssetCodecTest {
    @Test
    void roundTripsMapNpcMenuAndMobCatalog() throws Exception {
        MapAssetBundle expected = fixtureBundle();

        MapAssetBundle actual = MapAssetCodec.decode(MapAssetCodec.encode(expected));

        assertEquals(expected, actual);
    }

    @Test
    void supportsEmptyCatalog() throws Exception {
        MapAssetBundle bundle = new MapAssetBundle((byte) 26, List.of(), List.of(), List.of());

        assertArrayEquals(new byte[] {26, 0, 0, 0, 0}, MapAssetCodec.encode(bundle));
    }

    @Test
    void rejectsNegativeNpcCount() {
        byte[] payload = {26, 0, (byte) 255};

        assertThrows(IOException.class, () -> MapAssetCodec.decode(payload));
    }

    @Test
    void rejectsTrailingBytes() throws Exception {
        byte[] valid = MapAssetCodec.encode(fixtureBundle());
        byte[] invalid = java.util.Arrays.copyOf(valid, valid.length + 1);

        assertThrows(IOException.class, () -> MapAssetCodec.decode(invalid));
    }

    private static MapAssetBundle fixtureBundle() {
        NpcTemplateAsset npc = new NpcTemplateAsset(
                "Truong lang", (short) 1, (short) 2, (short) 3,
                List.of(List.of("Noi chuyen", "Roi di")));
        MobTemplateAsset mob = new MobTemplateAsset(
                (byte) 4, "Bocau", 100, (byte) 5, (byte) 6);
        return new MapAssetBundle((byte) 26, List.of("Lang Cry"), List.of(npc), List.of(mob));
    }
}
