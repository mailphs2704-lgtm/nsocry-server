package com.nsocry.assets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class SkillAssetCodecTest {
    @Test
    void roundTripsCompleteNestedSkillStructure() throws Exception {
        SkillAssetBundle expected = fixtureBundle();

        SkillAssetBundle actual = SkillAssetCodec.decode(SkillAssetCodec.encode(expected));

        assertEquals(expected, actual);
    }

    @Test
    void supportsEmptyCatalog() throws Exception {
        SkillAssetBundle bundle = new SkillAssetBundle((byte) 26, List.of(), List.of());

        assertArrayEquals(new byte[] {26, 0, 0}, SkillAssetCodec.encode(bundle));
    }

    @Test
    void rejectsNegativeSignedCountWhenDecoding() {
        byte[] payload = {(byte) 26, (byte) 255};

        assertThrows(IOException.class, () -> SkillAssetCodec.decode(payload));
    }

    @Test
    void rejectsTooManyTemplatesBeforeEncoding() {
        List<SkillTemplateAsset> templates = new ArrayList<>();
        for (int index = 0; index < 128; index++) {
            templates.add(new SkillTemplateAsset((byte) index, "s", (byte) 1,
                    (byte) 0, (short) 0, "d", List.of()));
        }
        SkillAssetBundle bundle = new SkillAssetBundle((byte) 1, List.of(),
                List.of(new SkillClassAsset("Cry", templates)));

        assertThrows(IllegalArgumentException.class, () -> SkillAssetCodec.encode(bundle));
    }

    private static SkillAssetBundle fixtureBundle() {
        SkillLevelAsset level = new SkillLevelAsset(
                (short) 100,
                (byte) 1,
                (byte) 5,
                (short) 10,
                2_000,
                (short) 20,
                (short) 30,
                (byte) 2,
                List.of(new SkillLevelOptionAsset((short) 15, (byte) 0)));
        SkillTemplateAsset template = new SkillTemplateAsset(
                (byte) 3, "Chem", (byte) 10, (byte) 1,
                (short) 25, "Mo ta", List.of(level));
        return new SkillAssetBundle(
                (byte) 26,
                List.of("Sat thuong"),
                List.of(new SkillClassAsset("Kiem", List.of(template))));
    }
}
