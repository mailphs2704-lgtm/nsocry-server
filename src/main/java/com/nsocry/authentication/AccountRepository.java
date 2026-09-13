package com.nsocry.authentication;

import java.time.Instant;
import java.util.Optional;

/** Port lưu trữ tối thiểu mà authentication service cần, chưa phụ thuộc JDBC. */
public interface AccountRepository {
    /** Tìm credential theo username phân biệt hoa thường. */
    Optional<AccountCredential> findByUsername(String username);

    /** Ghi nhận đăng nhập thành công và đặt lại bộ đếm lỗi trong cùng transaction adapter. */
    void recordSuccessfulLogin(long accountId, Instant occurredAt);

    /** Ghi nhận một lần password không khớp để adapter áp dụng chính sách khóa. */
    void recordFailedLogin(long accountId, Instant occurredAt);
}
