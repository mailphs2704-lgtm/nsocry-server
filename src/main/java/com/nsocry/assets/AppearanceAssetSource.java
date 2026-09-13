package com.nsocry.assets;

/** Cổng đọc read model ngoại hình dùng trong payload thương lượng phiên bản. */
@FunctionalInterface
public interface AppearanceAssetSource {
    /** Đọc một bundle ngoại hình hoàn chỉnh tại thời điểm rebuild. */
    AppearanceAssetBundle load() throws ClientAssetSourceException;
}
