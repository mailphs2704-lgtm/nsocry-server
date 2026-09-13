# Sổ tay quản trị source NSOCry

Đây là điểm vào dành cho chủ server khi cần tra cứu code, sửa chức năng, điều tra lỗi hoặc
giao việc cho AI/lập trình viên. Đọc file này trước, sau đó đi theo liên kết đúng nhu cầu;
không cần đọc tuần tự toàn bộ source.

## Tra cứu nhanh

| Nhu cầu | Tài liệu |
| --- | --- |
| Dự án đang ở đâu, bước kế tiếp | [STATUS](../project/STATUS.md) |
| Lịch sử thay đổi và bằng chứng | [WORKLOG](../project/WORKLOG.md) |
| Tìm package/class/method hiện có | [Code catalog](code-catalog.md) |
| Hiểu luồng server và dependency | [Kiến trúc và luồng](architecture-and-flows.md) |
| Thêm/sửa field, payload, DB, command | [Playbook thay đổi](change-playbooks.md) |
| Điều tra lỗi vận hành | [Vận hành và xử lý lỗi](operations-troubleshooting.md) |
| Biết phần nào chưa đủ dữ liệu | [Sổ truy vết](trace-register.md) |
| Biết luật cập nhật tài liệu | [Chuẩn bảo trì manual](maintenance-standard.md) |
| Hiểu DATA candidate authoritative | [DATA inventory/candidate](../assets/data-authoritative-inventory.md) |
| Khung package/type/method đã khóa | [Architecture Lock](../architecture/architecture-lock.md) |
| Contract máy đọc được | [planned-contracts.tsv](../architecture/planned-contracts.tsv) |

## Cách đọc một hồ sơ code

Mỗi hồ sơ phải trả lời được:

1. **Chức năng:** code cung cấp hành vi gì.
2. **Vai trò:** nằm ở tầng nào và vì sao tồn tại.
3. **Đầu vào/đầu ra:** dữ liệu nhận, dữ liệu trả, mutation có thể xảy ra.
4. **Luồng gọi:** ai gọi nó và nó gọi tiếp thành phần nào.
5. **Bất biến:** điều kiện luôn phải đúng.
6. **Lỗi:** exception/failure state và ảnh hưởng.
7. **Cách sửa:** file/contract/test/tài liệu phải đổi cùng nhau.
8. **Độ tin cậy:** VERIFIED, IMPLEMENTED_PENDING hoặc TRACE_REQUIRED.

Nếu một mục chưa đủ bằng chứng, tài liệu phải ghi `TRACE_REQUIRED` thay vì suy đoán.

## Tầng trách nhiệm

| Tầng | Package chính | Không được chứa |
| --- | --- | --- |
| Composition | `com.nsocry.bootstrap` | gameplay rule, SQL trực tiếp |
| Transport | `com.nsocry.network`, `protocol.compat`, `session` | database/game economy |
| Application/operations | `com.nsocry.operations`, authentication service | socket codec chi tiết |
| Assets | `com.nsocry.assets`, `.conversion` | mutation player/world |
| Adapter | `com.nsocry.persistence` | rule gameplay |
| Foundation | configuration, observability | feature-specific global state |

Gameplay package trong Architecture Lock hiện chủ yếu là `RESERVED`; xem sổ truy vết trước
khi tạo code.

## Quy tắc sử dụng khi sửa server

- Bắt đầu từ command/use-case, không sửa class đầu tiên tìm thấy theo tên.
- Xác định contract wire/database và test đang bảo vệ trước khi đổi field.
- Không sửa seed/database thật nếu chưa có backup, checksum, preflight và xác nhận.
- Không đổi contract `LOCKED` nếu chưa có ADR và xác nhận chủ dự án.
- Sau sửa phải cập nhật code catalog/module manual, STATUS và WORKLOG.
- Chỉ gọi VERIFIED khi có output chạy thật; catalog không phải bằng chứng test.
- Khi STATUS là `PAUSED_BY_OWNER`, không triển khai tiếp nếu chưa có yêu cầu rõ từ chủ dự án.
