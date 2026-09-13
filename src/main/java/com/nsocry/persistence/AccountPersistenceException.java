package com.nsocry.persistence;

/** Lỗi truy cập account đã làm sạch, không đưa SQL hoặc credential vào message công khai. */
public final class AccountPersistenceException extends RuntimeException {
    /** Bọc nguyên nhân JDBC bằng mã thao tác cố định để tầng trên có thể phân loại an toàn. */
    public AccountPersistenceException(String operation, Throwable cause) {
        super("account persistence operation failed: " + operation, cause);
    }
}
