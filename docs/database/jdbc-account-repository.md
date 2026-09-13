# JDBC account persistence

## Phạm vi

JdbcAccountRepository là adapter cho AccountRepository trên bảng accounts của database nsocry. Adapter nhận DataSource từ composition root, không tự đọc credential và không quyết định loại connection pool.

## Truy vấn

| Method | SQL effect | Hợp đồng |
|---|---|---|
| findByUsername | SELECT credential theo username, LIMIT 1 | Prepared statement; trả Optional |
| recordSuccessfulLogin | Đặt failed_login_count=0, locked_until=NULL và last_login_at | Yêu cầu đúng một row |
| recordFailedLogin | Tăng failed_login_count, chặn tràn tại 65535 | Yêu cầu đúng một row |

## Mapping status

- 0 → ACTIVE.
- 1 → LOCKED.
- 2 → BANNED.
- Giá trị khác bị từ chối bằng SQLException rồi bọc thành AccountPersistenceException.

## Bảo mật và lỗi

- Mọi giá trị đều bind bằng PreparedStatement.
- Không nối username vào SQL.
- Exception công khai chỉ chứa mã thao tác cố định.
- Không đưa SQL, password, hash hoặc database credential vào message.
- Connection, PreparedStatement và ResultSet đều đóng bằng try-with-resources.
- Repository chưa tự tạo DataSource hoặc đọc password database.

## Test

JdbcAccountRepositoryTest dùng DataSource/JDBC proxy trong bộ nhớ để kiểm tra mapping, Optional.empty và parameter binding mà không cần chạm database thật.

Mục tiêu toàn suite sau pull: 32 test. Trạng thái PENDING cho đến khi Maven trên máy người dùng thành công.

## Chưa triển khai

- MariaDB JDBC driver và connection pool.
- Database properties/secret injection.
- Chạy migration thật.
- Chính sách tự đặt locked_until khi nhiều lần sai.
