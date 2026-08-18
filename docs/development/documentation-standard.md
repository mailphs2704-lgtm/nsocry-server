# Chuẩn documentation bắt buộc

## 1. Mục tiêu

Tài liệu phải cho phép người dùng và AI mới hiểu hệ thống mà không đọc lại toàn bộ lịch sử chat hoặc reverse-engineer lại phần đã hoàn thành.

## 2. Tài liệu cho package/module

Mỗi module cần:

- Mục đích.
- Phạm vi chịu trách nhiệm.
- Điều không thuộc trách nhiệm.
- Public API.
- Dependency vào/ra.
- State và lifecycle.
- Threading/concurrency.
- Database tables.
- Protocol commands.
- Error handling.
- Security considerations.
- Test/verification.
- Các ADR liên quan.

## 3. Tài liệu cho class

Mỗi class quan trọng cần:

- Đường dẫn source và package.
- Nhiệm vụ duy nhất/chính.
- Lý do tồn tại.
- Constructor/dependencies.
- State sở hữu.
- Method quan trọng.
- Class gọi nó và class nó gọi.
- Điều kiện lifecycle.
- Rủi ro khi sửa.

## 4. Tài liệu cho method/flow

Với method có logic nghiệp vụ hoặc protocol:

| Trường | Nội dung |
|---|---|
| Location | file, class, method; dòng chỉ dùng như gợi ý vì có thể dịch chuyển |
| Purpose | nhiệm vụ |
| Preconditions | điều kiện trước |
| Input | kiểu, đơn vị, encoding, nullable/range |
| Output | return/message/state mutation |
| Side effects | DB, socket, cache, global state |
| Flow | các bước theo thứ tự |
| Errors | lỗi và cách xử lý |
| Dependencies | class/table/command liên quan |
| Reasoning | vì sao thiết kế như vậy |
| Verification | test/log/fixture |
| Status | VERIFIED/PROPOSED/UNKNOWN |

Không dựa duy nhất vào số dòng; luôn có symbol/method để tìm lại.

## 5. Tài liệu protocol

Mỗi command cần:

- Tên symbolic và giá trị.
- Hướng truyền.
- Giai đoạn session hợp lệ.
- Payload byte-level theo thứ tự.
- Điều kiện/variant theo client version.
- Handler nhận.
- Service phản hồi.
- State thay đổi.
- Lỗi/response.
- Source evidence.
- Fixture hoặc cách tái hiện.
- Trạng thái xác minh.

## 6. Tài liệu database

Mỗi bảng cần:

- Mục đích.
- Ownership module.
- Primary key/unique/foreign key.
- Ý nghĩa từng cột.
- Đơn vị/encoding/default/nullability.
- Method đọc/ghi.
- Transaction boundary.
- Migration/seed.
- Dữ liệu nhạy cảm.
- Chênh lệch với schema NSOKISS.

## 7. STATUS và WORKLOG

- STATUS là snapshot mới nhất, có thể sửa.
- WORKLOG là lịch sử append-only.
- Mỗi task hoàn thành phải cập nhật cả hai.
- STATUS chỉ có đúng một “Next exact action” ưu tiên cao nhất.
- Mục DONE phải có bằng chứng kiểm chứng.

## 8. ADR

Tạo ADR khi quyết định ảnh hưởng nhiều module, khó đảo ngược hoặc AI sau dễ tự thay đổi. ADR gồm Context, Decision, Consequences, Alternatives và Status.
