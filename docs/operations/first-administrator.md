# Khởi tạo administrator đầu tiên

## Trạng thái

PENDING — source mới cần xác minh tổng 40 test. Chưa chạy trên database thật.

## Thành phần

| Type | Trách nhiệm |
|---|---|
| AccountRole | Role đúng theo constraint schema |
| AccountProvisioningRepository | Port đếm và tạo account |
| FirstAdministratorService | Validation, one-time guard, hashing và xóa password |
| JdbcAccountProvisioningRepository | COUNT/INSERT bằng prepared statement |
| FirstAdministratorCommand | Nhập tương tác username và password ẩn |

## Quy tắc an toàn

- Không nhận password qua command-line argument.
- Yêu cầu System.console để tránh password hiện trên terminal.
- Nhập password hai lần.
- Xóa cả password và confirmation trong finally.
- Service cũng xóa mảng password do bên gọi truyền.
- Chỉ repository nhận password hash.
- Không log username kèm credential.
- Từ chối chạy nếu bảng accounts đã có bất kỳ account nào.
- Username chỉ gồm chữ ASCII, số hoặc dấu gạch dưới, dài 3–32.
- Password dài 8–256 ký tự.

## Luồng

1. Người vận hành chạy command từ terminal tương tác.
2. Command đọc config database với environment override.
3. Tạo MariaDbDataSource.
4. Service kiểm tra countAccounts bằng JDBC.
5. Kiểm tra username/password.
6. PBKDF2 tạo salt/hash mới.
7. Insert account activated với role ADMINISTRATOR.
8. In duy nhất generated account id.
9. Xóa password tạm.

## Giới hạn

- Chưa cung cấp bản fat JAR/lệnh phát hành thuận tiện.
- Chưa chạy migration tự động.
- Guard đếm account phù hợp bootstrap một người vận hành; chưa thiết kế provisioning đồng thời.
- Chưa có lệnh tạo PLAYER thông thường hoặc reset password.

## Test

FirstAdministratorServiceTest gồm 4 trường hợp: tạo thành công, từ chối lần hai, username sai, password ngắn; tất cả kiểm tra xóa password.

Mục tiêu toàn suite: 40 test.
