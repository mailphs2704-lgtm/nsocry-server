# ADR-0008: Console quản trị cục bộ qua run.bat

## Trạng thái

ACCEPTED về kiến trúc; các command gameplay sẽ được triển khai khi module tương ứng tồn tại.

## Bối cảnh

NSOCry chưa có website quản trị. Người vận hành cần một giao diện Windows dễ dùng để khởi động server và thực hiện nghiệp vụ quản trị như mở sự kiện, cấp vật phẩm, gửi thông báo, quản lý giftcode và tài khoản.

Nếu đặt logic trực tiếp trong batch file hoặc cho console chạy SQL tùy ý, hệ thống sẽ khó kiểm thử, không có phân quyền/audit và dễ làm hỏng dữ liệu.

## Quyết định

- Tạo run.bat ở repository root làm launcher Windows mỏng.
- Giao diện lệnh bài Admin được viết trong Java, không viết business logic trong batch.
- Chế độ admin khởi động server và console trong cùng JVM để command có thể tác động runtime an toàn.
- Mọi thao tác đi qua AdminCommand và application service; cấm SQL tùy ý.
- Yêu cầu đăng nhập account role ADMINISTRATOR trước khi mở menu.
- Thao tác thay đổi dữ liệu phải xác nhận; thao tác nguy hiểm yêu cầu nhập lại mật khẩu hoặc mã xác nhận.
- Mọi hành động ghi audit: admin id, command, target, timestamp, result; không ghi password/token.
- Command phải kiểm tra input, quyền, trạng thái server và hỗ trợ idempotency khi phù hợp.
- run.bat không chứa database password; secret lấy từ environment/config cục bộ đã bị Git ignore.

## Cấu trúc dự kiến

run.bat
→ NsocryLauncher admin
→ LocalAdminConsole
→ AdminCommandRegistry
→ application services
→ domain/repository/runtime ports

## Nhóm command

1. Hệ thống: start, stop an toàn, trạng thái, bảo trì, reload dữ liệu cho phép.
2. Tài khoản: kích hoạt, khóa/mở khóa, đổi role, reset password có kiểm soát.
3. Người chơi: tra cứu, cấp vật phẩm, điều chỉnh có giới hạn.
4. Runtime: gửi thông báo, kick session, xem số người online.
5. Sự kiện: mở/đóng/lập lịch sự kiện khi event module tồn tại.
6. Giftcode: tạo, giới hạn lượt dùng/thời gian, vô hiệu hóa, thống kê.
7. Audit: xem lịch sử thao tác quản trị.

## Hệ quả

- Có thể vận hành trước khi website tồn tại.
- Logic quản trị tái sử dụng được khi xây website/API sau này.
- Cần thêm schema audit và permission.
- Command gameplay chỉ xuất hiện khi service tương ứng đã được triển khai; không tạo command giả thao tác trực tiếp lên JSON/database.

## Giả định cần xác nhận

Cụm từ mở sk được hiểu là mở sự kiện.
