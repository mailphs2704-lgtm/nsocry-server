package com.nsocry.bootstrap;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SkillAssetSeedImportCommandTest {
    private static final String SHA256 =
            "4f13faa5d95653ff9d04945d0fe8a5146030526383944d22de1786c497155cf5";

    @Test
    void acceptsExactChecksumIgnoringCaseAndOuterWhitespace() {
        assertTrue(SkillAssetSeedImportCommand.matchesChecksum(SHA256, "  " + SHA256.toUpperCase() + "  "));
    }

    @Test
    void rejectsOneCharacterDifference() {
        assertFalse(SkillAssetSeedImportCommand.matchesChecksum(
                SHA256, "5f13faa5d95653ff9d04945d0fe8a5146030526383944d22de1786c497155cf5"));
    }

    @Test
    void rejectsCancelledConfirmation() {
        assertFalse(SkillAssetSeedImportCommand.matchesChecksum(SHA256, null));
    }
}
