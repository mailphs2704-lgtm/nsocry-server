# Chuẩn documentation bắt buộc

## 1. Mục tiêu

Tài liệu phải cho phép người dùng và AI mới hiểu hệ thống mà không đọc lại toàn bộ lịch sử chat hoặc reverse-engineer lại phần đã hoàn thành.

## 2. Ngôn ngữ và độ phủ bắt buộc

- Javadoc, ghi chú trong source, tài liệu kỹ thuật, STATUS và WORKLOG phải viết bằng tiếng Việt.
- Giữ nguyên tên package, class, method, command, protocol và thuật ngữ kỹ thuật cần thiết để tra cứu chính xác.
- Mỗi package phải có package-info.java mô tả trách nhiệm và phần bị cấm.
- Mỗi class, interface, record và enum phải có Javadoc mô tả vai trò.
- Mỗi constructor, method công khai và helper nội bộ có logic phải có Javadoc mô tả mục đích, trạng thái hoặc lỗi quan trọng.
- Mỗi thay đổi logic phải cập nhật Javadoc và tài liệu tra cứu trong cùng checkpoint.

## 3. Chuẩn tên và namespace

- Tên sản phẩm: NSOCry; namespace/artifact/database: nsocry; dạng ngắn: Cry/cry.
- Package root: com.nsocry.
- Cấm nsoz và nsotien trong source/config/schema/artifact mới.
- Tên reference phải kèm ngữ cảnh NSOKISS/reference.
- Review phải quét legacy-name ngoài source-reference.

## 4. Tài liệu package/module

Mỗi module phải ghi: mục đích, phạm vi, phần không thuộc trách nhiệm, API, dependency, state/lifecycle, concurrency, database, protocol, lỗi, bảo mật, test và ADR liên quan.

## 5. Tài liệu class và method

Mỗi class phải có đường dẫn, nhiệm vụ, lý do tồn tại, dependency, state sở hữu, API, lifecycle và rủi ro khi sửa.

Mỗi method có logic phải ghi:

| Trường | Nội dung |
|---|---|
| Location | File, class và method; số dòng chỉ là gợi ý |
| Purpose | Nhiệm vụ |
| Preconditions | Điều kiện trước |
| Input | Kiểu, encoding, nullable và phạm vi |
| Output | Giá trị trả về, message hoặc state mutation |
| Side effects | DB, socket, cache hoặc global state |
| Flow | Các bước theo thứ tự |
| Errors | Lỗi và cách xử lý |
| Dependencies | Class, table hoặc command liên quan |
| Reasoning | Lý do thiết kế |
| Verification | Test, log hoặc fixture |
| Status | VERIFIED, PROPOSED hoặc UNKNOWN |

Developer Manual dùng bộ nhãn mở rộng tại `docs/developer-manual/maintenance-standard.md`.
Phần chưa đủ evidence bắt buộc dùng `TRACE_REQUIRED` và có ID/điều kiện đóng trong
`trace-register.md`; không được viết hành vi dự kiến như code đã hoạt động.

`code-catalog.md` phải chứa mọi file production và được tái sinh sau khi thêm/xóa/đổi source.
`DocumentationCoverageTest` là gate bắt buộc cùng Architecture Lock test.

## 6. Protocol và database

Mỗi command phải có giá trị, hướng truyền, phase hợp lệ, payload byte-level, handler, phản hồi, thay đổi state, lỗi, evidence, fixture và trạng thái xác minh.

Mỗi bảng phải có ownership, khóa, ý nghĩa cột, đơn vị/encoding/default/nullability, method đọc/ghi, transaction, migration/seed, dữ liệu nhạy cảm và khác biệt với schema reference.

## 7. STATUS, WORKLOG và ADR

- STATUS là snapshot mới nhất; WORKLOG là lịch sử append-only.
- Mỗi task hoàn thành cập nhật cả hai.
- STATUS chỉ có đúng một Next exact action ưu tiên cao nhất.
- DONE phải có bằng chứng; không suy đoán VERIFIED.
- Tạo ADR cho quyết định ảnh hưởng nhiều module, khó đảo ngược hoặc AI sau dễ tự thay đổi.
