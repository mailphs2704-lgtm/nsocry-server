package com.nsocry.assets.conversion;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Parse và kiểm kê đúng ba catalog MAP client từ MariaDB dump tham chiếu. */
public final class ReferenceMapDumpInventoryParser {
    static final String MAP_MARKER = "INSERT INTO `map` (`id`, `name`, `npc`, `waypoint`, `monster`, `zone_number`, `locationStand`, `tileId`, `bgId`, `type`, `item`, `behind`, `betwen`, `front`) VALUES";
    static final String NPC_MARKER = "INSERT INTO `npc` (`id`, `name`, `head`, `body`, `leg`, `menu`) VALUES";
    static final String MOB_MARKER = "INSERT INTO `monster` (`id`, `name`, `level`, `boss`, `type`, `hp`, `range_move`, `speed`, `type_fly`, `n_img`, `move`, `attack`, `sprites`, `frames`, `sequence`, `frame_char`, `index_splash`) VALUES";

    private ReferenceMapDumpInventoryParser() {
    }

    /** Kiểm tra count, ID, menu NPC và mọi field thực sự đi lên wire MAP. */
    public static MapDumpInventoryReport parse(String dump) {
        Objects.requireNonNull(dump, "dump");
        List<List<String>> maps = ReferenceItemSqlDumpParser.parseValues(dump, MAP_MARKER);
        List<List<String>> npcs = ReferenceItemSqlDumpParser.parseValues(dump, NPC_MARKER);
        List<List<String>> mobs = ReferenceItemSqlDumpParser.parseValues(dump, MOB_MARKER);

        requireMaximum(maps.size(), 255, "map count");
        requireMaximum(npcs.size(), 127, "npc count");
        requireMaximum(mobs.size(), 32_767, "mob count");
        requireSequential(maps, 14, "map");
        requireSequential(npcs, 6, "npc");
        requireSequential(mobs, 17, "monster");

        int maximumMenuRows = 0;
        int maximumMenuChoices = 0;
        for (List<String> npc : npcs) {
            checkedShort(integer(npc.get(2), "npc head"), "npc head");
            checkedShort(integer(npc.get(3), "npc body"), "npc body");
            checkedShort(integer(npc.get(4), "npc leg"), "npc leg");
            List<List<String>> menu = parseNpcMenu(npc.get(5));
            requireMaximum(menu.size(), 127, "npc menu rows");
            maximumMenuRows = Math.max(maximumMenuRows, menu.size());
            for (List<String> row : menu) {
                requireMaximum(row.size(), 127, "npc menu choices");
                maximumMenuChoices = Math.max(maximumMenuChoices, row.size());
            }
        }

        List<MapRawByteDifference> byteDifferences = new ArrayList<>();
        for (List<String> mob : mobs) {
            int id = integer(mob.get(0), "monster id");
            checkWireByte(integer(mob.get(4), "monster type"), id, "type", byteDifferences);
            integer(mob.get(5), "monster hp");
            checkWireByte(integer(mob.get(6), "monster range move"), id, "moveRange", byteDifferences);
            checkWireByte(integer(mob.get(7), "monster speed"), id, "speed", byteDifferences);
        }

        return new MapDumpInventoryReport(
                maps.size(), npcs.size(), mobs.size(),
                minimumId(maps), maximumId(maps),
                minimumId(npcs), maximumId(npcs),
                minimumId(mobs), maximumId(mobs),
                maximumMenuRows, maximumMenuChoices,
                byteDifferences.size(), byteDifferences);
    }

    /** Parse đúng schema JSON array-of-arrays-of-strings của menu NPC, không nhận object/runtime data. */
    static List<List<String>> parseNpcMenu(String json) {
        return new NpcMenuParser(json).parse();
    }

    private static int minimumId(List<List<String>> rows) {
        return rows.isEmpty() ? 0 : integer(rows.get(0).get(0), "minimum id");
    }

    private static int maximumId(List<List<String>> rows) {
        return rows.isEmpty() ? 0 : integer(rows.get(rows.size() - 1).get(0), "maximum id");
    }

    private static void requireSequential(List<List<String>> rows, int arity, String name) {
        for (int index = 0; index < rows.size(); index++) {
            List<String> row = rows.get(index);
            if (row.size() != arity || integer(row.get(0), name + " id") != index) {
                throw new IllegalArgumentException(name + " row/ID không liên tục từ 0");
            }
        }
    }

    private static int integer(String value, String name) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(name + " không phải integer: " + value, exception);
        }
    }

    private static void requireMaximum(int count, int maximum, String name) {
        if (count > maximum) {
            throw new IllegalArgumentException(name + " vượt giới hạn " + maximum);
        }
    }

    private static short checkedShort(int value, String name) {
        if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
            throw new IllegalArgumentException(name + " vượt short: " + value);
        }
        return (short) value;
    }

    private static void checkWireByte(
            int value,
            int entityId,
            String field,
            List<MapRawByteDifference> differences) {
        if (value < Byte.MIN_VALUE || value > 255) {
            throw new IllegalArgumentException(field + " vượt raw byte: " + value);
        }
        if (value > Byte.MAX_VALUE) {
            differences.add(new MapRawByteDifference("monster", entityId, field, value));
        }
    }

    /** JSON parser nhỏ chỉ cho cấu trúc menu client, có kiểm tra escape và byte dư. */
    private static final class NpcMenuParser {
        private final String source;
        private int index;

        NpcMenuParser(String source) {
            this.source = Objects.requireNonNull(source, "source");
        }

        List<List<String>> parse() {
            skipWhitespace();
            expect('[');
            List<List<String>> rows = new ArrayList<>();
            skipWhitespace();
            if (takeIf(']')) {
                requireEnd();
                return rows;
            }
            while (true) {
                rows.add(row());
                skipWhitespace();
                if (takeIf(']')) break;
                expect(',');
            }
            requireEnd();
            return rows;
        }

        private List<String> row() {
            skipWhitespace();
            expect('[');
            List<String> values = new ArrayList<>();
            skipWhitespace();
            if (takeIf(']')) return values;
            while (true) {
                skipWhitespace();
                values.add(string());
                skipWhitespace();
                if (takeIf(']')) return values;
                expect(',');
            }
        }

        private String string() {
            expect('"');
            StringBuilder value = new StringBuilder();
            while (index < source.length()) {
                char current = source.charAt(index++);
                if (current == '"') return value.toString();
                if (current != '\\') {
                    if (current <= 0x1F) {
                        throw error("JSON string chứa control character chưa escape");
                    }
                    value.append(current);
                    continue;
                }
                if (index >= source.length()) throw error("JSON escape bị thiếu ký tự");
                char escaped = source.charAt(index++);
                switch (escaped) {
                    case '"', '\\', '/' -> value.append(escaped);
                    case 'b' -> value.append('\b');
                    case 'f' -> value.append('\f');
                    case 'n' -> value.append('\n');
                    case 'r' -> value.append('\r');
                    case 't' -> value.append('\t');
                    case 'u' -> value.append(unicode());
                    default -> throw error("JSON escape không hợp lệ");
                }
            }
            throw error("JSON string chưa đóng");
        }

        private char unicode() {
            if (index + 4 > source.length()) throw error("JSON unicode escape bị thiếu");
            int value = 0;
            for (int offset = 0; offset < 4; offset++) {
                int digit = Character.digit(source.charAt(index++), 16);
                if (digit < 0) throw error("JSON unicode escape không hợp lệ");
                value = value * 16 + digit;
            }
            return (char) value;
        }

        private void requireEnd() {
            skipWhitespace();
            if (index != source.length()) throw error("JSON menu có byte dư");
        }

        private boolean takeIf(char expected) {
            if (index < source.length() && source.charAt(index) == expected) {
                index++;
                return true;
            }
            return false;
        }

        private void expect(char expected) {
            skipWhitespace();
            if (index >= source.length() || source.charAt(index++) != expected) {
                throw error("Cần ký tự " + expected);
            }
        }

        private void skipWhitespace() {
            while (index < source.length() && Character.isWhitespace(source.charAt(index))) index++;
        }

        private IllegalArgumentException error(String message) {
            return new IllegalArgumentException(message + " tại offset " + index);
        }
    }
}
