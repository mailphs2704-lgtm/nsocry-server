package com.nsocry.assets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class DataAssetSeedManifestParserTest {
    @Test
    void parsesCanonicalManifestIncludingUnsignedExperienceCount() {
        DataAssetSeedManifest manifest = DataAssetSeedManifestParser.parse(manifest("131"));

        assertEquals(7, Byte.toUnsignedInt(manifest.version()));
        assertEquals(43, manifest.taskGroupCount());
        assertEquals(131, manifest.experienceCount());
        assertEquals(85154, manifest.payloadLength());
    }

    @Test
    void rejectsChangedOrderMissingNewlineAndOutOfRangeCount() {
        String canonical = manifest("131");
        assertThrows(IllegalArgumentException.class,
                () -> DataAssetSeedManifestParser.parse(canonical.replace(
                        "version=7\ntaskGroupCount=43", "taskGroupCount=43\nversion=7")));
        assertThrows(IllegalArgumentException.class,
                () -> DataAssetSeedManifestParser.parse(canonical.substring(0, canonical.length() - 1)));
        assertThrows(IllegalArgumentException.class,
                () -> DataAssetSeedManifestParser.parse(manifest("256")));
    }

    private static String manifest(String experienceCount) {
        return "format=nsocry-data-seed-v1\n"
                + "version=7\n"
                + "taskGroupCount=43\n"
                + "experienceCount=" + experienceCount + "\n"
                + "payloadLength=85154\n"
                + "sha256=242a3551cc110c4eda9f8e40f06fcd0f0b0b2d32bcab6f1b07669dbd0c9b148b\n";
    }
}
