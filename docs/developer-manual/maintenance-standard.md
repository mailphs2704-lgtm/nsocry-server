# Chuẩn bảo trì Developer Manual

## Khi nào bắt buộc cập nhật

- Thêm/xóa/đổi package, class, record, interface, enum hoặc public/protected method.
- Đổi command, payload, schema, migration, manifest, checksum hoặc runtime lifecycle.
- Thay trạng thái PENDING/TRACE_REQUIRED/VERIFIED.
- Phát hiện lỗi vận hành hoặc cách khôi phục mới.

## Hồ sơ tối thiểu của class/method

- Source path và symbol chính xác.
- Chức năng, vai trò/tầng, caller/callee.
- Input/output/mutation.
- Bất biến và failure modes.
- Hướng dẫn sửa/mở rộng.
- Test và tài liệu liên quan.
- Trạng thái evidence.

## Nhãn hợp lệ

- `VERIFIED`: implementation có bằng chứng test/runtime đã ghi.
- `IMPLEMENTED`: code tồn tại và được full suite biên dịch; mức xác minh hành vi xem manual
  module/STATUS, không mặc nhiên đồng nghĩa end-to-end.
- `IMPLEMENTED_PENDING`: code tồn tại nhưng checkpoint mới chưa được xác nhận đầy đủ.
- `TRACE_REQUIRED`: chưa đủ dữ liệu để mô tả/hiện thực chuẩn.
- `RESERVED`: contract kiến trúc đã giữ chỗ, chưa phải code.
- `REFERENCE_ONLY`: chỉ dùng để truy hành vi tĩnh, không port trực tiếp.
- `SUPERSEDED`: nội dung cũ, phải liên kết nội dung thay thế.

## Không dùng line range làm định danh duy nhất

Line number thay đổi sau edit. Tài liệu phải ưu tiên `package.Type#method(signature)` và source
path; line range chỉ là hỗ trợ tại checkpoint. Khi signature đổi phải cập nhật catalog/manual.

## Quy trình cập nhật

1. Sửa code và test.
2. Cập nhật module manual/playbook/trace register liên quan.
3. Cập nhật `code-catalog.md` cho mọi source path/symbol mới.
4. Chạy `DocumentationCoverageTest` và Architecture Lock test.
5. Cập nhật STATUS hiện tại và append WORKLOG.
6. Review diff, commit/push đúng nhánh.

## Quy tắc chống tài liệu giả

- Không mô tả hành vi dự kiến bằng thì hiện tại; dùng TRACE_REQUIRED/PROPOSED.
- Không gọi group test là full suite.
- Không ghi runtime/database đã đổi nếu chỉ chạy unit test.
- Không tự đánh dấu review độc lập cho commit do cùng AI tạo.
- Không xóa lịch sử WORKLOG để làm trạng thái trông sạch hơn.
