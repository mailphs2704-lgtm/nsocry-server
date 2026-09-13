# Thiết kế tài khoản và xác thực NSOCry

## Trạng thái

PROPOSED — migration đã viết, chưa chạy trên MariaDB và chưa nối JDBC.

## Bằng chứng reference

Phân tích tĩnh database.sql cho thấy reference tách nhân vật khỏi tài khoản nhưng bảng tài khoản trộn xác thực, thanh toán, quyền quản trị, sự kiện và dữ liệu web. Password có độ dài cố định ngắn và dữ liệu mẫu không chứng minh cơ chế hash an toàn.

NSOCry không sao chép bảng đó. Không nhập credential, token, số điện thoại, email hoặc lịch sử IP từ database reference.

## Phạm vi V001

V001 chỉ tạo bảng accounts để trả lời:

- Username có tồn tại không?
- Password hash có khớp không?
- Tài khoản đã kích hoạt, bị khóa hoặc bị cấm không?
- Role nội bộ là gì?
- Khi nào đăng nhập gần nhất?
- Có bao nhiêu lần đăng nhập sai liên tiếp?

Nhân vật, tiền tệ, thanh toán, OTP, website và gameplay thuộc migration/module khác.

## Bảng accounts

| Cột | Kiểu | Ý nghĩa |
|---|---|---|
| id | BIGINT UNSIGNED | Khóa chính nội bộ |
| username | VARCHAR(32), utf8mb4_bin | Tên đăng nhập phân biệt hoa thường, unique |
| password_hash | VARCHAR(255), ascii_bin | Chuỗi hash có version/parameters; không lưu plaintext |
| status | SMALLINT | 0 ACTIVE, 1 LOCKED, 2 BANNED |
| activated | BOOLEAN | Tài khoản đã được phép vào game |
| role | VARCHAR(24) | PLAYER, MODERATOR hoặc ADMINISTRATOR |
| failed_login_count | SMALLINT UNSIGNED | Bộ đếm phục vụ khóa tạm |
| locked_until | DATETIME(3) | Thời điểm hết khóa tạm |
| last_login_at | DATETIME(3) | Lần xác thực thành công gần nhất |
| created_at / updated_at | DATETIME(3) | Audit timestamp tối thiểu |

## Quy tắc bảo mật

- Không log username kèm password/token.
- Không lưu password rõ hoặc hash không có salt.
- password_hash phải tự mô tả thuật toán và tham số để nâng cấp.
- So sánh hash phải chống timing leak theo khả năng của thư viện.
- Lỗi trả cho client không được phân biệt username không tồn tại và password sai.
- Transaction xác thực chỉ cập nhật bộ đếm/last_login sau khi quyết định rõ ràng.
- Không dùng database NSOKISS đang chạy để test NSOCry.

## Khác biệt có chủ đích với reference

- Một bảng chỉ có một trách nhiệm: account authentication.
- Không có cột tiền, nạp thẻ, gift, clan, player list dạng JSON hoặc thông tin website.
- Tên và role có constraint.
- Username có unique constraint thật.
- Mốc thời gian dùng DATETIME(3), không trộn text và epoch.
- Schema/database chuẩn là nsocry.

## Next exact action

Chọn thuật toán password hash, tạo AccountRepository port và authentication service bằng fake repository trước khi viết JDBC adapter.
