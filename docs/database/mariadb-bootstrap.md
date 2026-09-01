# Ghép MariaDB vào bootstrap

## Trạng thái

PENDING — source và pom.xml đã thay đổi; cần xác minh bộ 36 test. Chưa yêu cầu database thật cho unit test.

## Dependency

- org.mariadb.jdbc:mariadb-java-client:3.5.10.
- Đây là MariaDB Connector/J chính thức.
- JdbcAccountRepository vẫn phụ thuộc javax.sql.DataSource, không phụ thuộc trực tiếp driver.

## Cấu hình

| Property | Biến môi trường ưu tiên | Bắt buộc |
|---|---|---|
| nsocry.database.url | NSOCRY_DB_URL | Có |
| nsocry.database.user | NSOCRY_DB_USER | Có |
| nsocry.database.password | NSOCRY_DB_PASSWORD | Có |

URL phải bắt đầu bằng jdbc:mariadb://. Biến môi trường có giá trị cao hơn file properties. DatabaseConfiguration.toString luôn hiển thị password=<redacted>.

File config được commit không chứa password. Production phải cấp secret qua biến môi trường hoặc file cục bộ không được Git theo dõi.

## Composition trong main

1. Đọc ServerConfiguration.
2. Đọc DatabaseConfiguration.
3. Tạo MariaDbDataSource.
4. Tạo JdbcAccountRepository.
5. Tạo Pbkdf2PasswordHasher và dummy hash.
6. Tạo AuthenticationService.
7. Tạo NsocryServerApplication.
8. Cài shutdown hook.
9. Mở TCP listener.

Nếu cấu hình database không hợp lệ, listener chưa được mở.

## Giới hạn hiện tại

- MariaDbDataSource chưa phải connection pool chuyên dụng.
- Chưa tự chạy migration.
- Chưa kiểm tra kết nối database trong unit test.
- Chưa có CLI tạo account/hash ban đầu.
- Không có credential mặc định.

## Test mới

DatabaseConfigurationTest kiểm tra ưu tiên environment, fallback properties, che password, thiếu password và URL sai.

Mục tiêu toàn suite: 36 test.
