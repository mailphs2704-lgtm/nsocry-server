package com.nsocry.assets.conversion;

import com.nsocry.assets.ProgressionTable;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/** Đọc đúng mười mảng progression từ source GameData reference mà không compile source legacy. */
public final class ReferenceGameDataProgressionParser {
    private static final int MAX_SIGNED_BYTE_COUNT = 127;
    private static final Map<ProgressionTable, String> SYMBOLS = Map.ofEntries(
            Map.entry(ProgressionTable.CRYSTAL_REQUIREMENT, "UP_CRYSTAL"),
            Map.entry(ProgressionTable.CLOTHE_REQUIREMENT, "UP_CLOTHE"),
            Map.entry(ProgressionTable.ADORN_REQUIREMENT, "UP_ADORN"),
            Map.entry(ProgressionTable.WEAPON_REQUIREMENT, "UP_WEAPON"),
            Map.entry(ProgressionTable.CRYSTAL_COIN_COST, "COIN_UP_CRYSTAL"),
            Map.entry(ProgressionTable.CLOTHE_COIN_COST, "COIN_UP_CLOTHE"),
            Map.entry(ProgressionTable.ADORN_COIN_COST, "COIN_UP_ADORN"),
            Map.entry(ProgressionTable.WEAPON_COIN_COST, "COIN_UP_WEAPON"),
            Map.entry(ProgressionTable.GOLD_COST, "GOLD_UP"),
            Map.entry(ProgressionTable.MAX_PERCENT, "MAX_PERCENT"));

    private ReferenceGameDataProgressionParser() {
    }

    /** Parse đủ mười declaration int[] theo mapping cố định và trả defensive arrays. */
    public static EnumMap<ProgressionTable, int[]> parse(String javaSource) {
        Objects.requireNonNull(javaSource, "javaSource");
        EnumMap<ProgressionTable, int[]> result = new EnumMap<>(ProgressionTable.class);
        for (ProgressionTable table : ProgressionTable.values()) {
            result.put(table, parseArray(javaSource, SYMBOLS.get(table)));
        }
        return result;
    }

    /** Yêu cầu declaration xuất hiện đúng một lần và chỉ chứa literal integer phân tách bằng dấu phẩy. */
    private static int[] parseArray(String source, String symbol) {
        String marker = "public static final int[] " + symbol;
        int declaration = source.indexOf(marker);
        if (declaration < 0) {
            throw error("GameData thiếu " + symbol);
        }
        if (source.indexOf(marker, declaration + marker.length()) >= 0) {
            throw error("GameData trùng declaration " + symbol);
        }
        int equals = source.indexOf('=', declaration + marker.length());
        int open = equals < 0 ? -1 : source.indexOf('{', equals + 1);
        int close = open < 0 ? -1 : source.indexOf('}', open + 1);
        int semicolon = close < 0 ? -1 : skipWhitespace(source, close + 1);
        if (equals < 0 || open < 0 || close < 0
                || semicolon >= source.length() || source.charAt(semicolon) != ';') {
            throw error("Declaration " + symbol + " không đúng dạng int[]");
        }
        String body = source.substring(open + 1, close).trim();
        if (body.isEmpty()) {
            throw error(symbol + " không được rỗng");
        }
        String[] tokens = body.split(",", -1);
        if (tokens.length > MAX_SIGNED_BYTE_COUNT) {
            throw error(symbol + " vượt giới hạn 127 phần tử");
        }
        int[] values = new int[tokens.length];
        for (int index = 0; index < tokens.length; index++) {
            String token = tokens[index].trim();
            try {
                values[index] = Integer.parseInt(token);
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException(
                        symbol + " có literal integer không hợp lệ tại index " + index, exception);
            }
        }
        return values;
    }

    private static int skipWhitespace(String source, int index) {
        while (index < source.length() && Character.isWhitespace(source.charAt(index))) {
            index++;
        }
        return index;
    }

    private static IllegalArgumentException error(String message) {
        return new IllegalArgumentException(message);
    }
}
