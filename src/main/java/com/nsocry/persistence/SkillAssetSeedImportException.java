package com.nsocry.persistence;

/** Lỗi validation hoặc transaction khi thay thế SKILL seed. */
public final class SkillAssetSeedImportException extends Exception {
    /** Giữ thông báo nghiệp vụ và nguyên nhân gốc của validation/transaction failure. */
    public SkillAssetSeedImportException(String message, Throwable cause) {
        super(message, cause);
    }
}
