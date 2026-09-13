package com.nsocry.assets.conversion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class ReferenceItemAssetConverterTest {
    @Test
    void sortsRowsAndReportsRangesAndDifferences() {
        ItemAssetConversionResult result = ReferenceItemAssetConverter.convert(
                (byte) 26,
                List.of(option(1, 2, "MP"), option(0, 0, "HP")),
                List.of(
                        item(1, "Kiếm", 3, 1, 20, 300, -1, 8, 0),
                        item(0, "Áo", 2, 0, 10, 100, 4, -1, 1)));

        assertEquals("HP", result.bundle().options().get(0).name());
        assertEquals("Áo", result.bundle().items().get(0).name());
        assertTrue(result.bundle().items().get(0).upgradable());
        assertFalse(result.bundle().items().get(1).upgradable());
        assertEquals(0, result.report().minimumOptionType());
        assertEquals(2, result.report().maximumOptionType());
        assertEquals(100, result.report().minimumIconId());
        assertEquals(300, result.report().maximumIconId());
        assertEquals(1, result.report().upgradableItemCount());
        assertEquals(1, result.report().fashionValueNotTransferredCount());
    }

    @Test
    void rejectsGapInImplicitWireIds() {
        assertThrows(IllegalArgumentException.class, () -> ReferenceItemAssetConverter.convert(
                (byte) 1, List.of(option(1, 0, "Sai")), List.of()));
    }

    @Test
    void rejectsDuplicateIds() {
        assertThrows(IllegalArgumentException.class, () -> ReferenceItemAssetConverter.convert(
                (byte) 1,
                List.of(option(0, 0, "A"), option(0, 1, "B")),
                List.of()));
    }

    @Test
    void rejectsNumericValueOutsideWireRange() {
        assertThrows(IllegalArgumentException.class, () -> ReferenceItemAssetConverter.convert(
                (byte) 1,
                List.of(),
                List.of(item(0, "Icon lỗi", 1, 0, 1, 40_000, -1, -1, 0))));
    }

    @Test
    void rejectsNonBooleanUpgradableValue() {
        assertThrows(IllegalArgumentException.class, () -> ReferenceItemAssetConverter.convert(
                (byte) 1,
                List.of(),
                List.of(item(0, "Boolean lỗi", 1, 0, 1, 1, -1, -1, 2))));
    }

    private static ReferenceItemOptionRow option(int id, int type, String name) {
        return new ReferenceItemOptionRow(id, type, name);
    }

    private static ReferenceItemTemplateRow item(
            int id,
            String name,
            int type,
            int gender,
            int level,
            int icon,
            int part,
            int fashion,
            int upgradable) {
        return new ReferenceItemTemplateRow(
                id, name, type, gender, "Mô tả", level, icon, part, fashion, upgradable);
    }
}
