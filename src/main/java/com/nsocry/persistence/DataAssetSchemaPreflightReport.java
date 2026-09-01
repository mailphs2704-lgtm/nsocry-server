package com.nsocry.persistence;

import java.util.List;
import java.util.Objects;

/** Kết quả đối chiếu schema DATA V005 chỉ đọc. */
public record DataAssetSchemaPreflightReport(boolean ready, List<String> differences) {
    /** Sao chép danh sách difference để report bất biến. */
    public DataAssetSchemaPreflightReport {
        differences = List.copyOf(Objects.requireNonNull(differences, "differences"));
    }
}
