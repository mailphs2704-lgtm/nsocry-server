package com.nsocry.persistence;

/** Lỗi validation hoặc transaction khi thay thế MAP seed. */
public final class MapAssetSeedImportException extends Exception {
    /** Giữ thông báo nghiệp vụ và nguyên nhân gốc. */
    public MapAssetSeedImportException(String message, Throwable cause) { super(message, cause); }
}
