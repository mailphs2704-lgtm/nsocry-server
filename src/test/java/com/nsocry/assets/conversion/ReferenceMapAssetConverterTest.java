package com.nsocry.assets.conversion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nsocry.assets.MapAssetBundle;
import com.nsocry.assets.MapAssetCodec;
import com.nsocry.assets.MobTemplateAsset;
import com.nsocry.assets.NpcTemplateAsset;
import java.util.List;

import org.junit.jupiter.api.Test;

class ReferenceMapAssetConverterTest {
    @Test
    void convertsOnlyMapWireColumnsAndRoundTripsCodec() throws Exception {
        MapAssetConversionResult result = ReferenceMapAssetConverter.convert((byte) 7, validDump());
        MapAssetBundle bundle = result.bundle();

        assertEquals(List.of("Map Cry"), bundle.mapNames());
        assertEquals(List.of(new NpcTemplateAsset(
                "NPC Cry", (short) 56, (short) 57, (short) 58,
                List.of(List.of("Mua", "Bán"), List.of("Nói chuyện")))), bundle.npcs());
        assertEquals(1, bundle.mobs().size());
        MobTemplateAsset mob = bundle.mobs().get(0);
        assertEquals(4, Byte.toUnsignedInt(mob.type()));
        assertEquals("Mob Cry", mob.name());
        assertEquals(500000, mob.health());
        assertEquals(200, Byte.toUnsignedInt(mob.moveRange()));
        assertEquals(2, Byte.toUnsignedInt(mob.speed()));
        assertEquals(List.of(new MapRawByteDifference("monster", 0, "moveRange", 200)),
                result.report().rawByteDifferences());
        assertEquals(bundle, MapAssetCodec.decode(MapAssetCodec.encode(bundle)));
    }

    @Test
    void rejectsInvalidWireValueBeforeCreatingCandidate() {
        assertThrows(IllegalArgumentException.class,
                () -> ReferenceMapAssetConverter.convert((byte) 7,
                        validDump().replace(", 500000, 200, 2,", ", 500000, 256, 2,")));
    }

    private static String validDump() {
        return """
                INSERT INTO `map` (`id`, `name`, `npc`, `waypoint`, `monster`, `zone_number`, `locationStand`, `tileId`, `bgId`, `type`, `item`, `behind`, `betwen`, `front`) VALUES
                (0, 'Map Cry', '[{\"runtime\":true}]', '[{\"next\":1}]', '[{\"templateId\":99}]', 30, '[]', 1, 0, 0, '[]', '[]', '[]', '[]');
                INSERT INTO `npc` (`id`, `name`, `head`, `body`, `leg`, `menu`) VALUES
                (0, 'NPC Cry', 56, 57, 58, '[["Mua","Bán"],["Nói chuyện"]]');
                INSERT INTO `monster` (`id`, `name`, `level`, `boss`, `type`, `hp`, `range_move`, `speed`, `type_fly`, `n_img`, `move`, `attack`, `sprites`, `frames`, `sequence`, `frame_char`, `index_splash`) VALUES
                (0, 'Mob Cry', 99, 1, 4, 500000, 200, 2, 1, 9, '[1]', '[2]', '[3]', '[4]', '[5]', '[6]', '[7]');
                """;
    }
}
