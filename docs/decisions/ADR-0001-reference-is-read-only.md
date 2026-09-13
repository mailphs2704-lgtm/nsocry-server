# ADR-0001: NSOKISS là reference chỉ đọc

- Status: ACCEPTED
- Date: 2026-08-18

## Context

NSOKISS hiện chạy được và là cơ sở duy nhất để quan sát hành vi/server-client. Sửa trực tiếp có thể làm mất baseline và gây nhầm giữa hệ thống cũ với NSOCry.

## Decision

Giữ NSOKISS nguyên trạng trong `source-reference/`. Không phát triển feature NSOCry trong package/source này.

## Consequences

- Có baseline để đối chiếu.
- Mọi code mới phải nằm ngoài reference.
- Fix cho NSOCry không được áp trực tiếp vào NSOKISS.
- Khi cần thử nghiệm instrumentation, phải dùng bản sao rõ ràng và ghi lại.
