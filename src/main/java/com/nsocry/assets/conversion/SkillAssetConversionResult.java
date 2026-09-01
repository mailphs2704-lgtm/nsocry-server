package com.nsocry.assets.conversion;

import com.nsocry.assets.SkillAssetBundle;
import java.util.Objects;

/** Kết quả chuyển đổi SKILL gồm read model wire-ready và inventory đối chiếu. */
public record SkillAssetConversionResult(
        SkillAssetBundle bundle,
        SkillDumpInventoryReport report) {

    /** Không cho phép converter trả kết quả thiếu bundle hoặc report. */
    public SkillAssetConversionResult {
        Objects.requireNonNull(bundle, "bundle");
        Objects.requireNonNull(report, "report");
    }
}
