# ADR-0005: NSOCry/Cry là chuẩn đặt tên duy nhất của source mới

- Status: ACCEPTED
- Date: 2026-08-18

## Context

NSOCry được viết mới dựa trên logic/hành vi đã hiểu từ NSOKISS. Nếu giữ package, class, method hoặc định danh `nsoz`/`nsotien`, source mới sẽ tiếp tục mang cấu trúc và nhận diện của hệ thống cũ, gây nhầm lẫn giữa implementation và reference.

## Decision

- Tên sản phẩm chuẩn là `NSOCry`.
- Dạng kỹ thuật chữ thường là `nsocry`.
- Dạng rút gọn được phép là `Cry`/`cry`.
- Package root định hướng của implementation là `com.nsocry`.
- Không cho phép `nsoz` hoặc `nsotien` trong package, class, interface, method, field, constant, module, artifact, cấu hình, schema hoặc tài liệu implementation mới.
- Tên mới phải phản ánh trách nhiệm kiến trúc/nghiệp vụ; không thực hiện blind search-and-replace.
- Tên legacy chỉ tồn tại trong `source-reference/` hoặc tài liệu mô tả/trích dẫn reference rõ ràng.

## Consequences

- Source NSOCry và NSOKISS có ranh giới nhận diện rõ.
- Review/CI sau này phải có legacy-name scan với ngoại lệ có kiểm soát cho reference.
- Khi chuyển logic, phải thiết kế lại tên theo trách nhiệm mới.
- Tài liệu mapping có thể ghi cả legacy symbol và NSOCry symbol nhưng phải phân biệt rõ hai phía.

## Verification rule

Trước khi merge implementation, tìm case-insensitive `nsoz|nsotien` ngoài vùng reference/allowlist. Kết quả không được có định danh implementation.
