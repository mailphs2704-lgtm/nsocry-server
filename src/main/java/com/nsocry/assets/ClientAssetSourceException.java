package com.nsocry.assets;

/** Lỗi đọc dữ liệu asset từ nguồn lưu trữ, không làm lộ công nghệ lưu trữ cho tầng build. */
public final class ClientAssetSourceException extends Exception {
    /** Tạo lỗi kèm nguyên nhân gốc để tầng vận hành có thể ghi nhận an toàn. */
    public ClientAssetSourceException(String message, Throwable cause) {
        super(message, cause);
    }
}
