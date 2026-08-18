# Lộ trình lệnh bài Admin NSOCry

## Mục tiêu trải nghiệm

Người vận hành chạy run.bat và thấy menu tiếng Việt rõ ràng. Menu không yêu cầu kiến thức Java/Maven/SQL trong vận hành thường ngày.

Ví dụ định hướng:

1. Khởi động server với Admin Console
2. Tạo administrator đầu tiên
3. Kiểm tra database
4. Xem trạng thái server
5. Quản lý tài khoản
6. Gửi thông báo
7. Quản lý sự kiện
8. Cấp vật phẩm
9. Quản lý giftcode
10. Xem audit log
0. Thoát an toàn

## Nguyên tắc

- run.bat chỉ kiểm tra Java/JAR/config rồi chuyển quyền cho Java launcher.
- Menu và nghiệp vụ nằm trong source com.nsocry.administration.
- Không cho nhập câu SQL.
- Không có master password hard-code.
- Đăng nhập bằng administrator trong bảng accounts.
- Mỗi command có mã, mô tả, permission, validation, confirmation và audit.
- Command chưa có module nền sẽ hiển thị chưa khả dụng, không giả lập thành công.
- Stop server phải đóng listener/session/database theo lifecycle.

## Các giai đoạn

### A. Nền tảng

- run.bat launcher.
- command admin trong NsocryLauncher.
- LocalAdminConsole và AdminCommandRegistry.
- đăng nhập administrator.
- status/help/exit.
- audit schema và AuditPort.

### B. Quản trị tài khoản

- activate/lock/unlock.
- tra cứu account.
- thay đổi role có xác nhận mạnh.
- reset password an toàn.

### C. Runtime

- số session online.
- thông báo toàn server.
- kick session.
- maintenance mode.

### D. Gameplay

Chỉ làm sau khi module tương ứng tồn tại:

- cấp vật phẩm qua InventoryService;
- mở sự kiện qua EventService;
- giftcode qua GiftCodeService;
- thay đổi dữ liệu người chơi qua command chuyên biệt.

## Trạng thái hiện tại

- Ý tưởng và kiến trúc: ACCEPTED.
- Database nsocry + accounts V001: đã tạo cục bộ.
- Executable JAR: VERIFIED 44/44.
- FirstAdministratorCommand: VERIFIED.
- Admin console code: chưa triển khai.
