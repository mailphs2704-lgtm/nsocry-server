package com.nsocry.assets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class ItemAssetSeedValidatorTest {
    private static final String FIXTURE_SHA256 =
            "0ed18aa1fe78dbe8416484ea65797bdbae5776ad78a965d835369266d900386d";

    @Test
    void acceptsExactManifestAndReturnsOperationalMetadata() throws Exception {
        ItemAssetValidationResult result = ItemAssetSeedValidator.validate(
                fixture(), new ItemAssetSeedManifest((byte) 26, 1, 1, 35, FIXTURE_SHA256));

        assertEquals(26, result.version());
        assertEquals(1, result.optionCount());
        assertEquals(1, result.itemCount());
        assertEquals(35, result.payloadLength());
        assertEquals(FIXTURE_SHA256, result.payloadSha256());
    }

    @Test
    void rejectsUnexpectedCountBeforeApproval() {
        ItemAssetSeedManifest manifest = new ItemAssetSeedManifest((byte) 26, 2, 1, 35, FIXTURE_SHA256);

        assertThrows(ItemAssetSeedValidationException.class,
                () -> ItemAssetSeedValidator.validate(fixture(), manifest));
    }

    @Test
    void rejectsChangedPayloadWithOldChecksum() {
        ItemAssetBundle changed = new ItemAssetBundle(
                (byte) 26,
                List.of(new ItemOptionAsset("Tên đã đổi", (byte) 2)),
                fixture().items());
        ItemAssetSeedManifest manifest = new ItemAssetSeedManifest((byte) 26, 1, 1, 35, FIXTURE_SHA256);

        assertThrows(ItemAssetSeedValidationException.class,
                () -> ItemAssetSeedValidator.validate(changed, manifest));
    }

    @Test
    void rejectsMalformedChecksumManifest() {
        assertThrows(IllegalArgumentException.class,
                () -> new ItemAssetSeedManifest((byte) 26, 1, 1, 35, "not-a-sha256"));
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
