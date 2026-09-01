package com.nsocry.assets;

/** Cổng đọc read model SKILL độc lập với schema và công nghệ lưu trữ. */
@FunctionalInterface
public interface SkillAssetSource {
    /** Đọc một bundle SKILL hoàn chỉnh tại thời điểm rebuild. */
    SkillAssetBundle load() throws ClientAssetSourceException;
}
