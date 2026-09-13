package com.nsocry.persistence;

import java.util.List;
import java.util.Objects;

/** Kết quả đối chiếu schema MAP V004, gồm trạng thái và toàn bộ sai khác. */
public record MapAssetSchemaPreflightReport(boolean ready, List<String> differences) {

    /** Sao chép danh sách để báo cáo không thể bị sửa sau khi kiểm tra. */
    public MapAssetSchemaPreflightReport {
        Objects.requireNonNull(differences, "differences");
        differences = List.copyOf(differences);
    }
}
