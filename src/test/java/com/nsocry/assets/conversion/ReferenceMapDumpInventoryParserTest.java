package com.nsocry.assets.conversion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class ReferenceMapDumpInventoryParserTest {
    @Test
    void inventoriesValidatedMapCatalogsAndNpcMenu() {
        MapDumpInventoryReport report = ReferenceMapDumpInventoryParser.parse(validDump());

        assertEquals(1, report.mapCount());
        assertEquals(1, report.npcCount());
        assertEquals(1, report.mobCount());
        assertEquals(2, report.maximumNpcMenuRows());
        assertEquals(2, report.maximumNpcMenuChoices());
        assertEquals(0, report.signedByteOverflowValueCount());
        assertEquals(List.of(), report.rawByteDifferences());
    }

    @Test
    void rejectsGapInImplicitMapIds() {
        assertThrows(IllegalArgumentException.class,
                () -> ReferenceMapDumpInventoryParser.parse(validDump().replace(
                        "(0, 'Map Cry'", "(1, 'Map Cry'")));
    }

    @Test
    void rejectsNpcMenuOutsideExpectedSchema() {
        assertThrows(IllegalArgumentException.class,
                () -> ReferenceMapDumpInventoryParser.parse(validDump().replace(
                        "[[\"Mua\",\"Bán\"],[\"Nói chuyện\"]]", "[{\"name\":\"Mua\"}]")));
    }

    @Test
    void rejectsUnescapedControlCharacterInNpcMenuJson() {
        assertThrows(IllegalArgumentException.class,
                () -> ReferenceMapDumpInventoryParser.parse(validDump().replace(
                        "Nói chuyện", "Nói\nchuyện")));
    }

    @Test
    void capturesMonsterRangeOutsideSignedByteRange() {
        MapDumpInventoryReport report = ReferenceMapDumpInventoryParser.parse(
                validDump().replace(", 500000, 33, 2,", ", 500000, 200, 2,"));

        assertEquals(List.of(new MapRawByteDifference("monster", 0, "moveRange", 200)),
                report.rawByteDifferences());
    }

    @Test
    void rejectsMonsterValueOutsideRawByteRange() {
        assertThrows(IllegalArgumentException.class,
                () -> ReferenceMapDumpInventoryParser.parse(
                        validDump().replace(", 500000, 33, 2,", ", 500000, 256, 2,")));
    }

    @Test
    void reportCopiesDifferenceListAndValidatesCount() {
        List<MapRawByteDifference> differences = new ArrayList<>();
        differences.add(new MapRawByteDifference("monster", 0, "moveRange", 200));
        MapDumpInventoryReport report = new MapDumpInventoryReport(
                1, 1, 1, 0, 0, 0, 0, 0, 0, 2, 2, 1, differences);

        differences.clear();
        assertEquals(1, report.rawByteDifferences().size());
        assertThrows(IllegalArgumentException.class,
                () -> new MapDumpInventoryReport(
                        1, 1, 1, 0, 0, 0, 0, 0, 0, 2, 2, 0, report.rawByteDifferences()));
    }

    @Test
    void inventoriesReferenceDumpWithinMapWireLimits() throws IOException {
        String dump = Files.readString(Path.of("source-reference/database.sql"), StandardCharsets.UTF_8);
        MapDumpInventoryReport report = ReferenceMapDumpInventoryParser.parse(dump);

        assertTrue(report.mapCount() >= 159, "dump thực tế phải chứa các map ID đã quan sát tới ít nhất 158");
        assertEquals(0, report.minimumMapId());
        assertEquals(report.mapCount() - 1, report.maximumMapId());
        assertEquals(0, report.minimumNpcId());
        assertEquals(report.npcCount() - 1, report.maximumNpcId());
        assertEquals(0, report.minimumMobId());
        assertEquals(report.mobCount() - 1, report.maximumMobId());
        assertTrue(report.npcCount() <= 127);
        assertTrue(report.mobCount() <= 32_767);
    }

    private static String validDump() {
        return """
                INSERT INTO `map` (`id`, `name`, `npc`, `waypoint`, `monster`, `zone_number`, `locationStand`, `tileId`, `bgId`, `type`, `item`, `behind`, `betwen`, `front`) VALUES
                (0, 'Map Cry', '[]', '[]', '[]', 30, '[]', 1, 0, 0, '[]', '[]', '[]', '[]');
                INSERT INTO `npc` (`id`, `name`, `head`, `body`, `leg`, `menu`) VALUES
                (0, 'NPC Cry', 56, 57, 58, '[["Mua","Bán"],["Nói chuyện"]]');
                INSERT INTO `monster` (`id`, `name`, `level`, `boss`, `type`, `hp`, `range_move`, `speed`, `type_fly`, `n_img`, `move`, `attack`, `sprites`, `frames`, `sequence`, `frame_char`, `index_splash`) VALUES
                (0, 'Mob Cry', 1, 0, 4, 500000, 33, 2, 0, 3, '[]', '[]', '[]', '[]', '[]', '[]', '[]');
                """;
    }
}
