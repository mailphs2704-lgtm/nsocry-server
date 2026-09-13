# ADR-0007: Password hashing cho NSOCry

## Trạng thái

ACCEPTED cho implementation mới; cần benchmark lại trên máy production trước khi phát hành.

## Bối cảnh

NSOCry dùng Java 17 và cần password hashing không phụ thuộc định dạng yếu của reference. Chuỗi lưu phải có version để nâng cấp work factor hoặc thuật toán sau này. Không được lưu plaintext.

OWASP ưu tiên Argon2id cho hệ thống mới. Tuy nhiên Java 17 không cung cấp Argon2id trong JCA chuẩn; thêm thư viện/native binding ngay ở checkpoint nền làm tăng rủi ro đóng gói và vận hành. JCA chuẩn hỗ trợ PBKDF2WithHmacSHA256.

## Quyết định

- Dùng PBKDF2-HMAC-SHA256 qua SecretKeyFactory của Java.
- Work factor production mặc định: 600.000 vòng.
- Salt: 16 byte SecureRandom riêng cho mỗi lần hash.
- Output: 256 bit.
- Định dạng: pbkdf2-sha256$iterations$base64-salt$base64-hash.
- Verify dùng MessageDigest.isEqual.
- Giới hạn password: 1–256 ký tự.
- Từ chối hash lưu trữ có iterations lớn hơn 2.000.000 để giảm rủi ro DoS do dữ liệu hỏng.
- Work factor được lưu trong chuỗi để có thể nâng cấp khi đăng nhập.
- Không dùng pepper cho đến khi dự án có secret manager tách khỏi database.

## Hệ quả

- Không thêm dependency crypto ngoài JDK.
- JAR dễ chạy trên Windows/Linux với Java 17.
- PBKDF2 không memory-hard như Argon2id; phải benchmark và có thể bổ sung Argon2id implementation sau.
- Tài khoản cũ không được nhập trực tiếp; cần reset password hoặc migration riêng có kiểm soát.

## Phương án đã cân nhắc

- Argon2id: bảo vệ tốt hơn trước GPU nhưng cần dependency/provider ngoài JDK.
- bcrypt: giới hạn input và phù hợp hơn với hệ thống legacy.
- SHA-256/MD5: bị loại vì quá nhanh và không phải password hashing hiện đại.

## Bằng chứng

- OWASP Password Storage Cheat Sheet: ưu tiên Argon2id; PBKDF2-HMAC-SHA256 600.000 vòng khi dùng PBKDF2.
- Oracle Java Security Standard Algorithm Names: PBKDF2WithHmacSHA256 là tên thuật toán JCA chuẩn.
