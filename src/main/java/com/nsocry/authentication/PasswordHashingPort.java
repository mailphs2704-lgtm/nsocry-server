package com.nsocry.authentication;

/** Port tạo và xác minh password hash có version mà không làm lộ thuật toán cho service. */
public interface PasswordHashingPort {
    /** Tạo chuỗi hash tự mô tả từ password; implementation phải dùng salt ngẫu nhiên riêng. */
    String hash(char[] password);

    /** So sánh password với chuỗi hash đã lưu bằng phép so sánh chống timing leak. */
    boolean verify(char[] password, String encodedHash);
}
