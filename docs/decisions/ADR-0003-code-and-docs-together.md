# ADR-0003: Code và documentation phát triển cùng nhau

- Status: ACCEPTED
- Date: 2026-08-18

## Context

Người dùng cần hiểu và kiểm soát source, đồng thời dự án phải tiếp tục được qua nhiều phiên ChatGPT có hạn mức.

## Decision

Mọi task thay đổi code/kiến trúc phải cập nhật tài liệu module, STATUS và WORKLOG trong cùng PR/checkpoint.

## Consequences

- PR thiếu documentation chưa được DONE.
- Tiến độ có thể chậm hơn một chút nhưng tránh reverse-engineering lại.
- AI mới phải đọc tài liệu trước khi thao tác.
- Documentation sai được coi là defect.
