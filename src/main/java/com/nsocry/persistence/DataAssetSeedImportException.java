package com.nsocry.persistence;

/** Lỗi fail-closed khi transaction import hoặc read-back DATA không đạt contract. */
public final class DataAssetSeedImportException extends Exception {
    /** Tạo lỗi có nguyên nhân gốc để vận hành có thể chẩn đoán mà không mất stack trace. */
    public DataAssetSeedImportException(String message, Throwable cause) {
        super(message, cause);
    }

    /** Tạo lỗi policy không cần bọc SQLException. */
    public DataAssetSeedImportException(String message) {
        super(message);
    }
}
