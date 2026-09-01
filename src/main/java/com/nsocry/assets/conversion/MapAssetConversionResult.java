package com.nsocry.assets.conversion;

import com.nsocry.assets.MapAssetBundle;
import java.util.Objects;

/** Kết quả convert MAP gồm bundle candidate và inventory evidence từ dump tham chiếu. */
public record MapAssetConversionResult(MapAssetBundle bundle, MapDumpInventoryReport report) {
    /** Từ chối kết quả thiếu bundle hoặc report để checkpoint luôn tự mô tả đủ evidence. */
    public MapAssetConversionResult {
        Objects.requireNonNull(bundle, "bundle");
        Objects.requireNonNull(report, "report");
    }
}
