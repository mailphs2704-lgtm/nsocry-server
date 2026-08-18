package com.nsocry.assets;

/** Báo bộ seed ITEM không khớp manifest đã phê duyệt hoặc không qua được codec. */
public final class ItemAssetSeedValidationException extends Exception {
    /** Tạo lỗi validation không chứa nội dung payload. */
    public ItemAssetSeedValidationException(String message) {
        super(message);
    }

    /** Tạo lỗi validation và giữ nguyên nguyên nhân kỹ thuật. */
    public ItemAssetSeedValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
