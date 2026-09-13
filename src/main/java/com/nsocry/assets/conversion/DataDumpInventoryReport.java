package com.nsocry.assets.conversion;

/** Kết quả kiểm kê các row authoritative cần để dựng payload DATA. */
public record DataDumpInventoryReport(
        int arrowCount,
        int effectPaintCount,
        int imageCount,
        int partCount,
        int skillPaintCount,
        int taskGroupCount,
        int experienceCount,
        int effectTemplateCount,
        int rawByteDifferences) {
}
