package com.nsocry.authentication;

/** Port tối thiểu dùng riêng cho quá trình tạo account ban đầu. */
public interface AccountProvisioningRepository {
    /** Đếm account hiện có để bảo đảm bootstrap administrator chỉ chạy một lần. */
    long countAccounts();

    /** Tạo account từ username và password hash; không nhận password rõ. */
    long create(String username, String passwordHash, AccountRole role, boolean activated);
}
