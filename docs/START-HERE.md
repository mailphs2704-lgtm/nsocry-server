# START HERE — Bàn giao nhanh dự án NSOCry

> Đây là file đầu tiên phải đọc khi bắt đầu phiên làm việc mới.

## Mục tiêu

Xây dựng server game **NSOCry** mới, tương thích với client được cung cấp, dựa trên việc hiểu hành vi của NSOKISS nhưng không sao chép nguyên kiến trúc/source. Người dùng không phải lập trình viên, vì vậy source và tài liệu phải đủ rõ để họ có thể lần theo và quản lý dự án.

## Tài sản hiện có

- Repository: `mailphs2704-lgtm/nsocry-server`.
- NSOKISS reference: `source-reference/NSOKISS-inspection/`.
- 250 file Java reference.
- 44 bảng trong SQL reference.
- Hơn 21.000 file/tài nguyên trong bộ source đã kiểm kê.
- Client tham khảo: `V7_217_X1.jar` (không nhất thiết nằm trong Git do giới hạn/kích thước).
- NSOKISS hiện vẫn chạy bình thường trên PC và điện thoại trong cùng mạng.
- Database NSOKISS đang chạy không phải database đích của NSOCry.

## Trạng thái hiện tại

- Nhánh phát triển: `agent/document-nsokiss-runtime`; Draft PR #1; chưa merge `main`.
- Architecture Lock v1 đang hiệu lực; cấm tự đổi package/contract LOCKED.
- Windows full suite gần nhất: 233/233 VERIFIED.
- ITEM và SKILL seed/database pipeline: VERIFIED_END_TO_END.
- MAP converter/artifact/archive/convert/dry-run: VERIFIED; schema/import/runtime chưa làm.
- Database hiện không thay đổi bởi checkpoint MAP; startup chưa nối full client snapshot.
- Tiến độ đến gameplay cơ bản: 17%.
- Developer Manual và code catalog là điểm tra cứu source dành cho chủ server.

Đọc chi tiết tại [project/STATUS.md](project/STATUS.md).
Khi cần hiểu hoặc sửa code, bắt đầu tại [developer-manual/README.md](developer-manual/README.md).

## Ràng buộc không được vi phạm

1. Không sửa source NSOKISS.
2. Không dùng database NSOKISS làm database phát triển NSOCry.
3. Không copy hàng loạt class NSOKISS sang namespace mới rồi gọi đó là “viết lại”.
4. Không bắt người dùng thực hiện lại kiểm tra đã được đánh dấu VERIFIED.
5. Không viết module mới trước khi protocol/dependency cần thiết đã được hiểu.
6. Mỗi thay đổi code phải cập nhật Javadoc tiếng Việt, documentation và worklog.
7. Không tự quyết gameplay hoặc business rule chưa được người dùng mô tả.
8. Không build/run/test NSOKISS; người dùng đã xác nhận reference hiện chạy tốt. Chỉ phân tích tĩnh khi cần bằng chứng protocol.
9. Không sửa/repack client JAR chỉ dựa trên nghi ngờ; phải có evidence, checksum và compatibility plan.

## Lệnh bàn giao cho AI

> Đọc `docs/START-HERE.md`, REQUIREMENTS, STATUS, Developer Manual, Architecture Lock,
> phần mới nhất WORKLOG, ADR và tài liệu module liên quan. Tiếp tục đúng “Next exact action”.
> Không làm lại VERIFIED; phần thiếu evidence phải dùng TRACE_REQUIRED.

## Trước khi kết thúc mỗi phiên

- Cập nhật STATUS.
- Thêm một entry vào WORKLOG.
- Ghi commit/branch/PR và kết quả kiểm chứng.
- Ghi rõ điểm dừng nếu đang dở.
- Ghi đúng một “Next exact action” có thể thực hiện ngay.


## Yêu cầu mới đã chấp nhận

- Xây lệnh bài Admin cục bộ qua run.bat trước khi có website.
- run.bat chỉ là launcher; console Java quản lý phân quyền, command, xác nhận và audit.
- Đọc ADR-0008 và docs/administration/console-roadmap.md trước khi làm administration.

- Quyết định mới nhất: Admin phát triển song hành theo module, không phải Next exact action độc lập; ưu tiên login/character/map.
