package com.nsocry.assets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import org.junit.jupiter.api.Test;

class ItemAssetCodecTest {
    @Test
    void encodesVerifiedItemWireOrder() throws Exception {
        ItemAssetBundle bundle = fixtureBundle();

        byte[] payload = ItemAssetCodec.encode(bundle);

        assertArrayEquals(HexFormat.of().parseHex(
                "1a01000754616e67204850020001030100044b69656d00054d6f2074610a000c001401"), payload);
    }

    @Test
    void roundTripsBundleThroughValidatorParser() throws Exception {
        ItemAssetBundle expected = fixtureBundle();

        ItemAssetBundle actual = ItemAssetCodec.decode(ItemAssetCodec.encode(expected));

        assertEquals(expected, actual);
    }

    @Test
    void supportsEmptyCatalog() throws Exception {
        ItemAssetBundle bundle = new ItemAssetBundle((byte) 3, List.of(), List.of());

        assertArrayEquals(new byte[] {3, 0, 0, 0}, ItemAssetCodec.encode(bundle));
    }

    @Test
    void rejectsTrailingBytes() throws Exception {
        byte[] valid = ItemAssetCodec.encode(fixtureBundle());
        byte[] invalid = java.util.Arrays.copyOf(valid, valid.length + 1);

        assertThrows(IOException.class, () -> ItemAssetCodec.decode(invalid));
    }

    @Test
    void rejectsMoreThanUnsignedByteOptions() {
        List<ItemOptionAsset> options = new ArrayList<>();
        for (int index = 0; index < 256; index++) {
            options.add(new ItemOptionAsset("o" + index, (byte) 0));
        }
        ItemAssetBundle bundle = new ItemAssetBundle((byte) 1, options, List.of());

        assertThrows(IllegalArgumentException.class, () -> ItemAssetCodec.encode(bundle));
    }

    private static ItemAssetBundle fixtureBundle() {
        return new ItemAssetBundle(
                (byte) 26,
                List.of(new ItemOptionAsset("Tang HP", (byte) 2)),
                List.of(new ItemTemplateAsset(
                        (byte) 3,
                        (byte) 1,
                        "Kiem",
                        "Mo ta",
                        (byte) 10,
                        (short) 12,
                        (short) 20,
                        true)));
    }
}
