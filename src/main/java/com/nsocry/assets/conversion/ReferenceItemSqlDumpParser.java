package com.nsocry.assets.conversion;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Parser giới hạn cho đúng hai INSERT ITEM trong MariaDB dump tham chiếu. */
public final class ReferenceItemSqlDumpParser {
    private static final String OPTION_MARKER =
            "INSERT INTO `item_option` (`id`, `type`, `name`) VALUES";
    private static final String ITEM_MARKER =
            "INSERT INTO `item` (`id`, `name`, `type`, `gender`, `description`, `level`, `icon`, `part`, `fashion`, `isUpToUp`) VALUES";

    private ReferenceItemSqlDumpParser() {
    }

    /** Parse hai statement bắt buộc; không thực thi SQL và không đọc bảng khác. */
    public static ReferenceItemDumpRows parse(String dump) {
        Objects.requireNonNull(dump, "dump");
        List<List<String>> optionValues = parseStatement(dump, OPTION_MARKER);
        List<List<String>> itemValues = parseStatement(dump, ITEM_MARKER);
        List<ReferenceItemOptionRow> options = new ArrayList<>(optionValues.size());
        for (List<String> values : optionValues) {
            requireArity(values, 3, "item_option");
            options.add(new ReferenceItemOptionRow(
                    integer(values.get(0), "option id"),
                    integer(values.get(1), "option type"),
                    values.get(2)));
        }
        List<ReferenceItemTemplateRow> items = new ArrayList<>(itemValues.size());
        for (List<String> values : itemValues) {
            requireArity(values, 10, "item");
            items.add(new ReferenceItemTemplateRow(
                    integer(values.get(0), "item id"),
                    values.get(1),
                    integer(values.get(2), "item type"),
                    integer(values.get(3), "item gender"),
                    values.get(4),
                    integer(values.get(5), "item level"),
                    integer(values.get(6), "item icon"),
                    integer(values.get(7), "item part"),
                    integer(values.get(8), "item fashion"),
                    integer(values.get(9), "item upgradable")));
        }
        return new ReferenceItemDumpRows(options, items);
    }

    /** Tách phần VALUES đến dấu chấm phẩy nằm ngoài chuỗi quoted. */
    private static List<List<String>> parseStatement(String dump, String marker) {
        int markerIndex = dump.indexOf(marker);
        if (markerIndex < 0 || dump.indexOf(marker, markerIndex + marker.length()) >= 0) {
            throw new IllegalArgumentException("Dump phải chứa đúng một statement: " + marker);
        }
        int start = markerIndex + marker.length();
        int end = findStatementEnd(dump, start);
        return new TupleParser(dump.substring(start, end)).parse();
    }

    /** Tìm dấu chấm phẩy kết thúc statement, bỏ qua dấu nằm trong quoted string. */
    private static int findStatementEnd(String dump, int start) {
        boolean quoted = false;
        boolean escaped = false;
        for (int index = start; index < dump.length(); index++) {
            char value = dump.charAt(index);
            if (escaped) {
                escaped = false;
            } else if (quoted && value == '\\') {
                escaped = true;
            } else if (value == '\'') {
                if (quoted && index + 1 < dump.length() && dump.charAt(index + 1) == '\'') {
                    index++;
                } else {
                    quoted = !quoted;
                }
            } else if (!quoted && value == ';') {
                return index;
            }
        }
        throw new IllegalArgumentException("Statement INSERT không có dấu kết thúc");
    }

    /** Parse integer decimal nghiêm ngặt. */
    private static int integer(String value, String field) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(field + " không phải integer: " + value, exception);
        }
    }

    /** Kiểm tra đúng số cột trước khi ánh xạ row. */
    private static void requireArity(List<String> values, int expected, String table) {
        if (values.size() != expected) {
            throw new IllegalArgumentException(table + " row phải có " + expected + " cột");
        }
    }

    /** State machine nhỏ cho tuple và quoted value, không phải SQL parser tổng quát. */
    private static final class TupleParser {
        private final String source;
        private int index;

        TupleParser(String source) {
            this.source = source;
        }

        List<List<String>> parse() {
            List<List<String>> rows = new ArrayList<>();
            skipSeparators();
            while (index < source.length()) {
                expect('(');
                List<String> row = new ArrayList<>();
                while (true) {
                    skipWhitespace();
                    row.add(peek() == '\'' ? quoted() : raw());
                    skipWhitespace();
                    char delimiter = take();
                    if (delimiter == ')') break;
                    if (delimiter != ',') throw error("Cần dấu phẩy hoặc đóng ngoặc");
                }
                rows.add(row);
                skipSeparators();
            }
            if (rows.isEmpty()) throw error("Statement không có row");
            return rows;
        }

        private String quoted() {
            expect('\'');
            StringBuilder value = new StringBuilder();
            while (index < source.length()) {
                char current = take();
                if (current == '\'') {
                    if (index < source.length() && source.charAt(index) == '\'') {
                        index++;
                        value.append('\'');
                    } else {
                        return value.toString();
                    }
                } else if (current == '\\') {
                    if (index >= source.length()) throw error("Escape bị thiếu ký tự");
                    value.append(unescape(take()));
                } else {
                    value.append(current);
                }
            }
            throw error("Chuỗi quoted chưa đóng");
        }

        private String raw() {
            int start = index;
            while (index < source.length() && source.charAt(index) != ',' && source.charAt(index) != ')') {
                index++;
            }
            String value = source.substring(start, index).trim();
            if (value.isEmpty()) throw error("Giá trị rỗng");
            return value;
        }

        private static char unescape(char value) {
            return switch (value) {
                case '0' -> '\0';
                case 'b' -> '\b';
                case 'n' -> '\n';
                case 'r' -> '\r';
                case 't' -> '\t';
                case 'Z' -> 26;
                default -> value;
            };
        }

        private void skipSeparators() {
            skipWhitespace();
            if (index < source.length() && source.charAt(index) == ',') {
                index++;
                skipWhitespace();
            }
        }

        private void skipWhitespace() {
            while (index < source.length() && Character.isWhitespace(source.charAt(index))) index++;
        }

        private char peek() {
            if (index >= source.length()) throw error("Thiếu giá trị");
            return source.charAt(index);
        }

        private char take() {
            if (index >= source.length()) throw error("Kết thúc tuple bất ngờ");
            return source.charAt(index++);
        }

        private void expect(char expected) {
            if (take() != expected) throw error("Cần ký tự " + expected);
        }

        private IllegalArgumentException error(String message) {
            return new IllegalArgumentException(message + " tại offset " + index);
        }
    }
}
