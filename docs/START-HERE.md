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
- Trạng thái quản trị: `PAUSED_BY_OWNER`; không tự tiếp tục implementation khi chưa có yêu cầu mới.
- Windows full suite gần nhất: **321/321 VERIFIED** tại DATA V005/preflight checkpoint.
- ITEM và SKILL seed/database pipeline: VERIFIED_END_TO_END.
- MAP schema/import/JDBC payload và runtime publish command: VERIFIED; startup ownership chưa nối.
- DATA authoritative candidate: version 7, 43 task group, 131 EXP, payload 85154 byte,
  SHA-256 `242a3551cc110c4eda9f8e40f06fcd0f0b0b2d32bcab6f1b07669dbd0c9b148b`.
- DATA archive convert + read-back độc lập: VERIFIED_END_TO_END_OFFLINE; persistence/runtime
  DATA chưa làm.
- DATA checkpoint không đổi database/runtime/startup.
- Tiến độ đến gameplay cơ bản: 18%.
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

Khi STATUS là `PAUSED_BY_OWNER`, chỉ được đọc/giải thích hoặc cập nhật checkpoint theo yêu cầu;
không tự bắt đầu Next exact action. Dự án hiện đã tiếp tục; DATA V005/preflight đạt 321/321,
không làm lại candidate hoặc full suite nếu input chưa đổi.

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
