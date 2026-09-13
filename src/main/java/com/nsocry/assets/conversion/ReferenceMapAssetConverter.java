package com.nsocry.assets.conversion;

import com.nsocry.assets.MapAssetBundle;
import com.nsocry.assets.MobTemplateAsset;
import com.nsocry.assets.NpcTemplateAsset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Chuyển đúng ba catalog MAP trong dump thành read model theo wire client V7. */
public final class ReferenceMapAssetConverter {
    private ReferenceMapAssetConverter() {
    }

    /** Validate toàn dump trước, sau đó chỉ lấy các cột thực sự thuộc payload MAP client. */
    public static MapAssetConversionResult convert(byte version, String dump) {
        Objects.requireNonNull(dump, "dump");
        MapDumpInventoryReport report = ReferenceMapDumpInventoryParser.parse(dump);
        List<List<String>> mapRows = ReferenceItemSqlDumpParser.parseValues(
                dump, ReferenceMapDumpInventoryParser.MAP_MARKER);
        List<List<String>> npcRows = ReferenceItemSqlDumpParser.parseValues(
                dump, ReferenceMapDumpInventoryParser.NPC_MARKER);
        List<List<String>> mobRows = ReferenceItemSqlDumpParser.parseValues(
                dump, ReferenceMapDumpInventoryParser.MOB_MARKER);

        List<String> mapNames = mapRows.stream().map(row -> row.get(1)).toList();
        List<NpcTemplateAsset> npcs = new ArrayList<>(npcRows.size());
        for (List<String> row : npcRows) {
            npcs.add(new NpcTemplateAsset(
                    row.get(1),
                    checkedShort(integer(row.get(2), "npc head"), "npc head"),
                    checkedShort(integer(row.get(3), "npc body"), "npc body"),
                    checkedShort(integer(row.get(4), "npc leg"), "npc leg"),
                    ReferenceMapDumpInventoryParser.parseNpcMenu(row.get(5))));
        }

        List<MobTemplateAsset> mobs = new ArrayList<>(mobRows.size());
        for (List<String> row : mobRows) {
            mobs.add(new MobTemplateAsset(
                    rawByte(integer(row.get(4), "monster type"), "monster type"),
                    row.get(1),
                    integer(row.get(5), "monster hp"),
                    rawByte(integer(row.get(6), "monster range move"), "monster range move"),
                    rawByte(integer(row.get(7), "monster speed"), "monster speed")));
        }

        return new MapAssetConversionResult(new MapAssetBundle(version, mapNames, npcs, mobs), report);
    }

    private static int integer(String value, String name) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(name + " không phải integer: " + value, exception);
        }
    }

    private static short checkedShort(int value, String name) {
        if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
            throw new IllegalArgumentException(name + " vượt short: " + value);
        }
        return (short) value;
    }

    /** Giữ nguyên bit pattern raw byte 128..255 thay vì hiểu sai thành giá trị âm nghiệp vụ. */
    private static byte rawByte(int value, String name) {
        if (value < Byte.MIN_VALUE || value > 255) {
            throw new IllegalArgumentException(name + " vượt raw byte: " + value);
        }
        return (byte) value;
    }
}
