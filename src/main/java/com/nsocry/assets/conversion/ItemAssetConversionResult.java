package com.nsocry.assets.conversion;

import com.nsocry.assets.ItemAssetBundle;
import java.util.Objects;

/** Kết quả chuyển đổi gồm bundle có thể encode và báo cáo đối chiếu đi kèm. */
public record ItemAssetConversionResult(ItemAssetBundle bundle, ItemAssetConversionReport report) {
    /** Bảo đảm kết quả không thiếu bundle hoặc report. */
    public ItemAssetConversionResult {
        Objects.requireNonNull(bundle, "bundle");
        Objects.requireNonNull(report, "report");
    }
}
