# Tổng quan kiến trúc định hướng NSOCry

**Trạng thái:** SUPERSEDED một phần. Khung package, tầng phụ thuộc và quy trình thay đổi
được thay bằng [architecture-lock.md](architecture-lock.md) cùng
[planned-contracts.tsv](planned-contracts.tsv). Các mô tả nội bộ gameplay chưa được khóa
trong hai tài liệu đó vẫn là PROPOSED.

## 1. Mục tiêu kiến trúc

- Tương thích client theo contract đã kiểm chứng.
- Dễ lần theo request từ socket đến game logic và database.
- Tách lifecycle, transport, protocol và nghiệp vụ.
- Có thể test codec/handler mà không cần chạy toàn server.
- Giảm global mutable state và exception bị bỏ qua.
- Cho phép phát triển module theo vertical slice.

## 2. Kiến trúc lớp

```text
Client
  -> Network Transport
  -> Frame Codec
  -> Protocol Dispatcher
  -> Application Use Case
  -> Domain Model
  -> Repository
  -> MariaDB (nsocry)
```

Thành phần ngang:

- Configuration
- Observability/logging
- Scheduler
- Administration
- Security/rate limiting
- Migration/seed

## 3. Ranh giới đề xuất

### bootstrap

Chỉ điều phối startup/shutdown, health và ownership. Không chứa spawn boss, war timer hoặc gameplay trực tiếp.

### network.transport

Accept connection, read/write bytes, backpressure, timeout, close và connection limits. Không hiểu gameplay.

### protocol

Frame, key transform, command registry, payload reader/writer và version variants.

### authentication

Account lookup, credential validation, login policy và session binding.

### player

Character state, load/save, online lifecycle và player-centric use cases.

### game-data

Nạp dữ liệu tĩnh/reference cần thiết, validation và cache immutable.

### world

Map/zone/mob/NPC và ownership của world state.

### persistence

Connection pool, transaction boundary, repository implementation và migration.

### scheduler

Tác vụ định kỳ được khai báo/cấu hình; không nhúng timer rải rác trong bootstrap.

### administration

Maintenance, shutdown, save checkpoint và thao tác quản trị có audit.

### observability

Structured log, metrics, correlation/session ID và error reporting.

## 4. Quy tắc dependency

Hướng phụ thuộc mong muốn:

```text
transport -> protocol -> application/domain <- persistence
bootstrap -> module lifecycle contracts
administration -> application use cases
```

Domain không phụ thuộc socket, SQL driver hoặc UI quản trị.

## 5. State và concurrency

Chưa chốt implementation, nhưng phải bảo đảm:

- Mỗi mutable state có owner rõ.
- Session close idempotent.
- Không tạo thread tùy ý trong constructor.
- Queue có giới hạn/backpressure.
- Shutdown dừng nhận kết nối trước, drain/save theo chính sách, rồi đóng resource.
- Scheduled tasks có lifecycle và error isolation.
- Shared collections có concurrency contract.

## 6. Database

- Database đích: `nsocry`.
- Schema thiết kế mới từ intent, không clone 44 bảng.
- Migration có version.
- Seed tách khỏi migration.
- Account/player data tách hợp lý khỏi static game data.
- Mọi write quan trọng có transaction boundary.
- Password/secret không lưu plain text trong thiết kế mới.

## 7. Chiến lược triển khai

Không xây toàn bộ ngang một lần. Dùng vertical slice:

1. bootstrap/config/logging;
2. transport/frame codec;
3. handshake;
4. login;
5. character load;
6. test map;
7. disconnect/save.

Sau khi slice chạy thật mới mở rộng gameplay.

## 8. Quan hệ với NSOKISS

Xem [nsokiss-runtime.md](nsokiss-runtime.md). Tài liệu reference mô tả “hệ thống cũ đang làm gì”; tài liệu này mô tả “NSOCry dự kiến tổ chức thế nào”. Không trộn hai loại thành một quyết định.
