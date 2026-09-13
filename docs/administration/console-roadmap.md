# Lộ trình quản trị song hành NSOCry

## Quyết định hiện hành

Admin Console không phải phase độc lập. Phần chính của server được làm trước. Khi một module hoàn thành, command quản trị của module đó mới được thêm bằng cách gọi lại service chính.

## Backlog theo module

| Tiến độ core | Command quản trị theo sau |
|---|---|
| Account/authentication | tạo account, activate, lock/unlock, role, reset password |
| Session/runtime | status, online, kick, announcement |
| Player/inventory | tra cứu player, cấp vật phẩm có audit |
| Event | mở, đóng và lập lịch sự kiện |
| Giftcode | tạo, vô hiệu hóa, quota, thời hạn |
| Lifecycle | maintenance và stop an toàn |

## Không làm lúc này

- Không dựng menu đầy đủ với mục giả.
- Không viết command sự kiện/vật phẩm/giftcode trước service.
- Không làm Admin Console thành blocker trước character/map/gameplay.
- Không cho batch file truy cập database trực tiếp.

## Kiến trúc cuối vẫn giữ

run.bat chỉ gọi NsocryLauncher. Java admin command gọi application service, kiểm tra role/permission, xác nhận thao tác và ghi audit.

## Luồng chính hiện tại

1. Hoàn thiện login runtime với database nsocry.
2. Hiểu protocol danh sách/chọn nhân vật từ reference tĩnh.
3. Thiết kế character domain/schema mới.
4. Trả danh sách nhân vật cho client.
5. Chọn nhân vật.
6. Nạp game data tối thiểu.
7. Vào map thử nghiệm.

## Trạng thái

DEFERRED theo module — không có công việc Admin Console độc lập trong Next exact action.
