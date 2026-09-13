# Bootstrap, configuration và observability

## Phạm vi checkpoint

Checkpoint này tạo điểm khởi động tối thiểu cho NSOCry. Server có thể đọc cấu hình, bind TCP listener, tạo khóa riêng cho từng phiên và ghi sự kiện mạng đã làm sạch. Xác thực mặc định vẫn từ chối mọi tài khoản; database và gameplay chưa được nối.

## Package configuration

### ServerConfiguration

| Thành phần | Vai trò |
|---|---|
| from(Properties) | Đọc khóa có namespace nsocry, áp dụng mặc định và kiểm tra phạm vi |
| tcp | Cấu hình listener đã chuyển thành TcpServerConfig |
| sessionKeyLength | Độ dài khóa phiên, hợp lệ 1–255 byte |
| number | Parser nội bộ gắn đúng tên property vào lỗi cấu hình |

Mặc định: host 0.0.0.0, port 14444, backlog 128, tối đa 500 phiên, read timeout 15000 ms, shutdown timeout 5000 ms và khóa 32 byte.

### ServerConfigurationLoader

- File tồn tại: chỉ đọc regular file bằng try-with-resources.
- File không tồn tại: dùng cấu hình mặc định.
- Không ghi log nội dung Properties.
- File mẫu: config/nsocry.properties.example.

## Package observability

### SanitizedNetworkEventSink

- SESSION_FAILED: chỉ ghi địa chỉ từ xa và tên class exception.
- SESSION_REJECTED: chỉ ghi địa chỉ từ xa.
- ACCEPT_FAILED: chỉ ghi tên class exception.
- Không ghi exception message, stack trace, payload, password hoặc token.
- Chấp nhận giá trị chẩn đoán null và chuyển thành nhãn cố định.

## Package bootstrap

### NsocryServerApplication

| Method | Trách nhiệm |
|---|---|
| constructor | Ghép configuration, key provider, authentication port, event sink và TcpServer |
| start | Bắt đầu listener sau khi dependency đã tạo thành công |
| server | Truy cập trạng thái/địa chỉ bind phục vụ quản trị và test |
| close | Dừng tài nguyên runtime theo thứ tự do TcpServer quản lý |
| main | Đọc đường dẫn config, dùng auth từ chối tạm thời, cài shutdown hook và start |
| closeQuietly | Đóng trong shutdown hook mà không phát sinh log nội bộ ngoài ý muốn |

Main class trong manifest: com.nsocry.bootstrap.NsocryServerApplication.

## Cấu hình hỗ trợ

| Property | Mặc định | Phạm vi |
|---|---:|---:|
| nsocry.server.host | 0.0.0.0 | Không rỗng |
| nsocry.server.port | 14444 | 0–65535; 0 dành cho test |
| nsocry.server.backlog | 128 | 1–65535 |
| nsocry.server.max-sessions | 500 | 1–100000 |
| nsocry.server.read-timeout-millis | 15000 | Số nguyên dương |
| nsocry.server.shutdown-timeout-millis | 5000 | Số nguyên dương |
| nsocry.session.key-length | 32 | 1–255 |

## Test mới

- ServerConfigurationTest: mặc định, override, số sai và độ dài khóa ngoài phạm vi.
- SanitizedNetworkEventSinkTest: không rò exception message và xử lý null.
- NsocryServerApplicationTest: ghép, start trên loopback port tạm và close.

Tổng dự kiến sau pull: 23 test. Trạng thái PENDING cho đến khi Maven trên máy người dùng báo thành công.

## Chưa triển khai

- Adapter xác thực database.
- Password hashing và migration.
- Gameplay, player, map, NPC hoặc mob.
- Hệ thống log có rotation/structured backend.
