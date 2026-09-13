package com.nsocry.persistence;

import java.util.List;
import java.util.Objects;

/** Báo cáo chỉ đọc cho biết schema ITEM đã đúng V002 hay còn chênh lệch. */
public record ItemAssetSchemaPreflightReport(boolean ready, List<String> differences) {
    /** Sao chép danh sách và bảo đảm ready chỉ đúng khi không có difference. */
    public ItemAssetSchemaPreflightReport {
        Objects.requireNonNull(differences, "differences");
        differences = List.copyOf(differences);
        if (ready != differences.isEmpty()) {
            throw new IllegalArgumentException("ready phải phản ánh đúng differences");
        }
    }
}
