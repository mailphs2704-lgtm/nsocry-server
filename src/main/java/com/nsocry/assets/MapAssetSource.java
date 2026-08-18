package com.nsocry.assets;

/** Cổng đọc read model MAP mà không để tầng build phụ thuộc trực tiếp vào JDBC. */
@FunctionalInterface
public interface MapAssetSource {
    /** Đọc một bundle MAP hoàn chỉnh tại thời điểm rebuild. */
    MapAssetBundle load() throws ClientAssetSourceException;
}
