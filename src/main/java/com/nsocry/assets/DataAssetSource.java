package com.nsocry.assets;

/** Cổng đọc read model DATA; implementation có thể dùng JDBC, file hoặc fixture. */
@FunctionalInterface
public interface DataAssetSource {
    /** Đọc một bundle DATA hoàn chỉnh tại thời điểm rebuild. */
    DataAssetBundle load() throws ClientAssetSourceException;
}
