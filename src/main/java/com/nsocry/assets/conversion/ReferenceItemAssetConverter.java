package com.nsocry.assets.conversion;

import com.nsocry.assets.ItemAssetBundle;
import com.nsocry.assets.ItemOptionAsset;
import com.nsocry.assets.ItemTemplateAsset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/** Chuyển row tham chiếu thành ITEM read model NSOCry mà không truy cập database. */
public final class ReferenceItemAssetConverter {
    private ReferenceItemAssetConverter() {
    }

    /** Sắp theo ID, kiểm tra contract wire và trả bundle cùng báo cáo khác biệt. */
    public static ItemAssetConversionResult convert(
            byte version,
            List<ReferenceItemOptionRow> optionRows,
            List<ReferenceItemTemplateRow> itemRows) {
        Objects.requireNonNull(optionRows, "optionRows");
        Objects.requireNonNull(itemRows, "itemRows");
        List<ReferenceItemOptionRow> sortedOptions = optionRows.stream()
                .map(row -> Objects.requireNonNull(row, "option row"))
                .sorted(Comparator.comparingInt(ReferenceItemOptionRow::id))
                .toList();
        List<ReferenceItemTemplateRow> sortedItems = itemRows.stream()
                .map(row -> Objects.requireNonNull(row, "item row"))
                .sorted(Comparator.comparingInt(ReferenceItemTemplateRow::id))
                .toList();

        requireMaximum(sortedOptions.size(), 255, "item option count");
        requireMaximum(sortedItems.size(), 32_767, "item template count");
        List<ItemOptionAsset> options = new ArrayList<>(sortedOptions.size());
        Range optionTypes = new Range();
        for (int index = 0; index < sortedOptions.size(); index++) {
            ReferenceItemOptionRow row = sortedOptions.get(index);
            requireId(row.id(), index, "item option");
            byte type = checkedByte(row.type(), "item option type");
            optionTypes.include(row.type());
            options.add(new ItemOptionAsset(row.name(), type));
        }

        List<ItemTemplateAsset> items = new ArrayList<>(sortedItems.size());
        Range itemTypes = new Range();
        Range icons = new Range();
        int upgradableCount = 0;
        int fashionDifferenceCount = 0;
        for (int index = 0; index < sortedItems.size(); index++) {
            ReferenceItemTemplateRow row = sortedItems.get(index);
            requireId(row.id(), index, "item template");
            boolean upgradable = checkedBoolean(row.upgradableValue());
            itemTypes.include(row.type());
            icons.include(row.icon());
            if (upgradable) upgradableCount++;
            if (row.fashion() != -1) fashionDifferenceCount++;
            items.add(new ItemTemplateAsset(
                    checkedByte(row.type(), "item type"),
                    checkedByte(row.gender(), "item gender"),
                    row.name(),
                    row.description(),
                    checkedByte(row.level(), "item level"),
                    checkedShort(row.icon(), "item icon"),
                    checkedShort(row.part(), "item part"),
                    upgradable));
        }

        ItemAssetBundle bundle = new ItemAssetBundle(version, options, items);
        ItemAssetConversionReport report = new ItemAssetConversionReport(
                options.size(), items.size(),
                optionTypes.minimum(), optionTypes.maximum(),
                itemTypes.minimum(), itemTypes.maximum(),
                icons.minimum(), icons.maximum(),
                upgradableCount, fashionDifferenceCount);
        return new ItemAssetConversionResult(bundle, report);
    }

    /** Bắt buộc ID liên tục từ 0 vì client dùng array index làm ID. */
    private static void requireId(int actual, int expected, String name) {
        if (actual != expected) {
            throw new IllegalArgumentException(name + " id phải liên tục từ 0; expected="
                    + expected + ", actual=" + actual);
        }
    }

    /** Kiểm tra count trước khi tạo payload. */
    private static void requireMaximum(int value, int maximum, String name) {
        if (value > maximum) {
            throw new IllegalArgumentException(name + " vượt giới hạn " + maximum);
        }
    }

    /** Thu hẹp signed byte mà không tràn im lặng. */
    private static byte checkedByte(int value, String name) {
        if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
            throw new IllegalArgumentException(name + " vượt giới hạn byte: " + value);
        }
        return (byte) value;
    }

    /** Thu hẹp signed short mà không tràn im lặng. */
    private static short checkedShort(int value, String name) {
        if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
            throw new IllegalArgumentException(name + " vượt giới hạn short: " + value);
        }
        return (short) value;
    }

    /** Chỉ chấp nhận đúng biểu diễn boolean 0/1 từ nguồn tham chiếu. */
    private static boolean checkedBoolean(int value) {
        if (value != 0 && value != 1) {
            throw new IllegalArgumentException("upgradableValue phải là 0 hoặc 1");
        }
        return value == 1;
    }

    /** Bộ gom min/max, trả 0/0 khi tập dữ liệu rỗng. */
    private static final class Range {
        private int minimum = Integer.MAX_VALUE;
        private int maximum = Integer.MIN_VALUE;

        void include(int value) {
            minimum = Math.min(minimum, value);
            maximum = Math.max(maximum, value);
        }

        int minimum() {
            return minimum == Integer.MAX_VALUE ? 0 : minimum;
        }

        int maximum() {
            return maximum == Integer.MIN_VALUE ? 0 : maximum;
        }
    }
}
