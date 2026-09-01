# ADR-0004: Hiểu protocol tối thiểu trước khi viết gameplay

- Status: ACCEPTED
- Date: 2026-08-18

## Context

Client dùng protocol nhị phân riêng. Viết gameplay/server framework trước khi hiểu frame, command, handshake và login dễ dẫn đến kiến trúc không tương thích.

## Decision

Hoàn thành inventory command và đặc tả handshake/login tối thiểu trước khi viết network/gameplay implementation của NSOCry.

## Consequences

- Giai đoạn đầu chủ yếu là discovery/documentation.
- Không đánh giá tiến độ chỉ bằng số dòng code mới.
- Protocol facts phải có evidence.
- Skeleton chỉ bắt đầu khi ranh giới codec/session đã đủ rõ để test.
