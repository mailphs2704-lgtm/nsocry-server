

## Thiết kế mới đang hoạt động

- [Tài khoản và xác thực NSOCry](account-authentication.md)
- Script tạo database: database/00-create-database.sql.
- Migration đầu tiên: database/migrations/V001__account_authentication.sql.
- Chưa chạy migration; trạng thái hiện tại là PROPOSED.

- [JdbcAccountRepository](jdbc-account-repository.md): adapter AccountRepository dùng DataSource/prepared statement; đang chờ xác minh 32 test.

- [Ghép MariaDB vào bootstrap](mariadb-bootstrap.md): driver chính thức, cấu hình secret và composition root; đang chờ 36 test.
