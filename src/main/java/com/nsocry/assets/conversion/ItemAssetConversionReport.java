package com.nsocry.assets.conversion;

/** Báo cáo count/range/difference của một lần chuyển dữ liệu ITEM tham chiếu. */
public record ItemAssetConversionReport(
        int optionCount,
        int itemCount,
        int minimumOptionType,
        int maximumOptionType,
        int minimumItemType,
        int maximumItemType,
        int minimumIconId,
        int maximumIconId,
        int upgradableItemCount,
        int fashionValueNotTransferredCount) {
}
