package com.nsocry.assets;

/** Cổng đọc read model ITEM độc lập với repository gameplay. */
@FunctionalInterface
public interface ItemAssetSource {
    /** Đọc một bundle ITEM hoàn chỉnh tại thời điểm rebuild. */
    ItemAssetBundle load() throws ClientAssetSourceException;
}
