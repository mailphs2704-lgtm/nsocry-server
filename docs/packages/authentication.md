# Package authentication

## Mục đích

Xác thực LoginRequest bằng dữ liệu account tối thiểu mà không phụ thuộc JDBC, không log credential và không chứa gameplay.

## Thành phần

| Type | Trách nhiệm |
|---|---|
| AccountStatus | ACTIVE, LOCKED hoặc BANNED |
| AccountCredential | Snapshot identity/hash/status/activation/lockedUntil |
| AccountRepository | Port tìm credential và ghi success/failure |
| PasswordHashingPort | Port hash/verify password |
| Pbkdf2PasswordHasher | PBKDF2-HMAC-SHA256 có salt, version và constant-time compare |
| AuthenticationService | Triển khai AuthenticationPort và quyết định ACCEPTED/REJECTED |

## Luồng AuthenticationService

1. Tìm username chính xác qua AccountRepository.
2. Sao chép password sang char[] tạm.
3. Nếu account không tồn tại, verify với dummy hash.
4. Luôn xóa char[] trong finally.
5. Account tồn tại nhưng password sai: recordFailedLogin và REJECTED.
6. Password đúng nhưng chưa activated, status không ACTIVE hoặc còn lockedUntil: REJECTED.
7. Hợp lệ: recordSuccessfulLogin và ACCEPTED.

Mọi trường hợp từ chối trả cùng AuthenticationDecision.REJECTED; service không cung cấp lý do chi tiết cho client.

## Password hash

Định dạng: pbkdf2-sha256$iterations$salt$hash.

Production mặc định 600.000 vòng, salt 16 byte và output 256 bit. Unit test dùng work factor thấp qua constructor package-private để bộ test nhanh; điều này không đổi mặc định runtime.

## Test

- Hai lần hash cùng password tạo chuỗi khác nhau.
- Password đúng/sai và hash hỏng.
- Account active được chấp nhận và ghi success.
- Password sai ghi failure.
- Username không tồn tại vẫn gọi verify.
- Account khóa tạm bị từ chối.

Mục tiêu tổng sau pull: 29 test. Trạng thái PENDING cho đến khi Maven trên máy người dùng thành công.

## Chưa triển khai

- JdbcAccountRepository.
- Connection pool và transaction.
- Chính sách tăng failed_login_count/locked_until cụ thể.
- Tạo tài khoản và reset password.
- Character selection/gameplay.
