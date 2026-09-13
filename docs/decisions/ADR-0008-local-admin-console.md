# ADR-0008: Quản trị cục bộ phát triển song hành

## Trạng thái

ACCEPTED — đã sửa theo quyết định của người dùng: không xây Admin Console thành một phase độc lập.

## Bối cảnh

NSOCry chưa có website quản trị và về sau cần run.bat/lệnh bài Admin. Tuy nhiên làm toàn bộ console trước gameplay sẽ trì hoãn luồng game chính, trong khi command vật phẩm, sự kiện hoặc giftcode chưa thể đúng nếu service nền chưa tồn tại.

## Quyết định

- Ưu tiên tuyệt đối luồng server/game chính.
- Không triển khai một giai đoạn Admin Console riêng ở thời điểm hiện tại.
- Mỗi khi một module nghiệp vụ hoàn thành, bổ sung command quản trị liên quan ngay sau service đó nếu thực sự cần vận hành.
- Command quản trị chỉ gọi application/domain service đã tồn tại; không viết logic song song và không thao tác SQL/JSON trực tiếp.
- run.bat cuối cùng là launcher mỏng; business logic vẫn ở Java.
- Khi có nền console, bắt buộc ADMINISTRATOR, permission, confirmation và audit.
- Các yêu cầu mở sự kiện, cấp vật phẩm, thông báo và giftcode được giữ trong backlog, chỉ kích hoạt theo tiến độ module tương ứng.

## Thứ tự áp dụng

| Module chính hoàn thành | Phần quản trị được bổ sung |
|---|---|
| Account/authentication | activate, lock, role, reset password |
| Session/runtime | status, online count, kick, announcement |
| Inventory/item | cấp vật phẩm qua InventoryService |
| Event | mở/đóng/lập lịch qua EventService |
| Giftcode | tạo, vô hiệu hóa, giới hạn và thống kê |
| Server lifecycle | maintenance và stop an toàn |

## Hệ quả

- Gameplay không bị chậm bởi UI quản trị chưa cần thiết.
- Command admin luôn dùng đúng service đã kiểm thử.
- Website/API sau này có thể tái sử dụng cùng service.
- run.bat và console hoàn chỉnh được ghép dần thay vì làm giả trước chức năng game.

## Ràng buộc vẫn giữ nguyên

- Không hard-code master password hoặc database secret.
- Không cho chạy SQL tùy ý.
- Không log password/token.
- Thao tác nguy hiểm phải xác nhận và audit.
