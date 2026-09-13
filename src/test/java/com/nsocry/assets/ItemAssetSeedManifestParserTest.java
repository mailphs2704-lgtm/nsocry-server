package com.nsocry.assets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ItemAssetSeedManifestParserTest {
    @Test
    void parsesCanonicalManifest() throws Exception {
        ItemAssetSeedManifest manifest = ItemAssetSeedManifestParser.parse(validManifest());

        assertEquals(26, Byte.toUnsignedInt(manifest.version()));
        assertEquals(1, manifest.optionCount());
        assertEquals(1, manifest.itemCount());
        assertEquals(35, manifest.payloadLength());
    }

    @Test
    void rejectsUnknownLineOrNonCanonicalOrder() {
        String changed = validManifest().replace("optionCount=1\nitemCount=1", "itemCount=1\noptionCount=1");

        assertThrows(ItemAssetSeedValidationException.class,
                () -> ItemAssetSeedManifestParser.parse(changed));
    }

    private static String validManifest() {
        return """
                format=nsocry-item-seed-v1
                version=26
                optionCount=1
                itemCount=1
                payloadLength=35
                sha256=0ed18aa1fe78dbe8416484ea65797bdbae5776ad78a965d835369266d900386d
                """;
    }
}
