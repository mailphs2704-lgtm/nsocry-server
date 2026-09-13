package com.nsocry.bootstrap;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ItemAssetSeedImportCommandTest {
    private static final String SHA256 =
            "abb320fb8a940fc28c49c6d0c5b84e09e83d28248130884881845b9dd5bea6f8";

    @Test
    void acceptsExactChecksumIgnoringCaseAndOuterWhitespace() {
        assertTrue(ItemAssetSeedImportCommand.matchesChecksum(
                SHA256, "  ABB320FB8A940FC28C49C6D0C5B84E09E83D28248130884881845B9DD5BEA6F8  "));
    }

    @Test
    void rejectsOneCharacterChecksumDifference() {
        assertFalse(ItemAssetSeedImportCommand.matchesChecksum(
                SHA256, "bbb320fb8a940fc28c49c6d0c5b84e09e83d28248130884881845b9dd5bea6f8"));
    }

    @Test
    void rejectsCancelledConfirmation() {
        assertFalse(ItemAssetSeedImportCommand.matchesChecksum(SHA256, null));
    }
}
