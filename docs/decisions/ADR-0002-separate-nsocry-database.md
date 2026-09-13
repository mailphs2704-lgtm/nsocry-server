# ADR-0002: NSOCry dùng database riêng

- Status: ACCEPTED
- Date: 2026-08-18

## Context

Database NSOKISS đang phục vụ server hoạt động. Dùng chung schema/data cho quá trình viết lại có nguy cơ hỏng dữ liệu và che giấu dependency cũ.

## Decision

Database phát triển mới tên `nsocry`. Không migration trực tiếp trên database NSOKISS.

## Consequences

- Schema mới phải có migration/seed.
- Reference SQL chỉ dùng để hiểu intent.
- Test NSOCry không được thay đổi dữ liệu NSOKISS.
- Dữ liệu cần chuyển sau này phải có quy trình migration riêng, không copy tùy tiện.
