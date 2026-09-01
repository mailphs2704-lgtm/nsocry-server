package com.nsocry.authentication;

import java.time.Instant;
import java.util.Objects;

/** Dữ liệu xác thực tối thiểu được repository tải cho một tài khoản. */
public record AccountCredential(
        long id,
        String username,
        String passwordHash,
        AccountStatus status,
        boolean activated,
        Instant lockedUntil) {

    /** Kiểm tra identity, hash và status ngay tại biên domain. */
    public AccountCredential {
        if (id < 1) {
            throw new IllegalArgumentException("id must be positive");
        }
        Objects.requireNonNull(username, "username");
        Objects.requireNonNull(passwordHash, "passwordHash");
        Objects.requireNonNull(status, "status");
    }

    /** Cho biết tài khoản có bị khóa tạm tại thời điểm được cung cấp hay không. */
    public boolean isTemporarilyLockedAt(Instant instant) {
        Objects.requireNonNull(instant, "instant");
        return lockedUntil != null && lockedUntil.isAfter(instant);
    }
}
