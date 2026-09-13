package com.nsocry.persistence;

/** Lỗi kiểm định hoặc ghi seed ITEM; transaction phải được rollback trước khi lỗi thoát ra. */
public final class ItemAssetSeedImportException extends Exception {
    /** Tạo lỗi import và giữ nguyên nguyên nhân kỹ thuật. */
    public ItemAssetSeedImportException(String message, Throwable cause) {
        super(message, cause);
    }
}
