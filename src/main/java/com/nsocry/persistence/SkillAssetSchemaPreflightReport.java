package com.nsocry.persistence;

import java.util.List;
import java.util.Objects;

/** Báo cáo chỉ đọc cho biết schema SKILL đã đúng V003 hay chưa. */
public record SkillAssetSchemaPreflightReport(boolean ready, List<String> differences) {
    /** Giữ difference list bất biến và khóa ready theo trạng thái danh sách. */
    public SkillAssetSchemaPreflightReport {
        differences = List.copyOf(Objects.requireNonNull(differences, "differences"));
        if (ready != differences.isEmpty()) {
            throw new IllegalArgumentException("ready phải phản ánh đúng differences");
        }
    }
}
