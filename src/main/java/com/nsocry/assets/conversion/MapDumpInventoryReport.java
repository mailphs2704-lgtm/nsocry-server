package com.nsocry.assets.conversion;

import java.util.List;
import java.util.Objects;

/** Inventory đã kiểm tra của ba nguồn MAP trong dump tham chiếu. */
public record MapDumpInventoryReport(
        int mapCount,
        int npcCount,
        int mobCount,
        int minimumMapId,
        int maximumMapId,
        int minimumNpcId,
        int maximumNpcId,
        int minimumMobId,
        int maximumMobId,
        int maximumNpcMenuRows,
        int maximumNpcMenuChoices,
        int signedByteOverflowValueCount,
        List<MapRawByteDifference> rawByteDifferences) {

    /** Sao chép difference list và giữ count đồng bộ. */
    public MapDumpInventoryReport {
        Objects.requireNonNull(rawByteDifferences, "rawByteDifferences");
        rawByteDifferences = List.copyOf(rawByteDifferences);
        if (signedByteOverflowValueCount != rawByteDifferences.size()) {
            throw new IllegalArgumentException("difference count không khớp danh sách");
        }
    }
}
