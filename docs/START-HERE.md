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

- Git/GitHub đã thiết lập và reference đã push thành công.
- ZIP NSOKISS khoảng 269 MB đã được loại khỏi Git.
- Bản đồ runtime đầu tiên đã được viết tại [architecture/nsokiss-runtime.md](architecture/nsokiss-runtime.md).
- Draft PR đang dùng cho bộ tài liệu nền tảng: PR #1.
- Source NSOCry đã bắt đầu với Java 17/Maven và package root `com.nsocry`.
- Command inventory, handshake/login, client static analysis và fixture v1 đã hoàn thành.
- Codec key/rolling-XOR/frame compatibility cùng fixture-based tests đã có.
- Client V7 chưa bị chỉnh/repack vì không phát hiện tracking hoặc defect đủ bằng chứng.
- TCP/session/handshake loopback đã VERIFIED 16/16.
- Toàn bộ package/type/method hiện tại đã được bổ sung Javadoc tiếng Việt; tài liệu tra cứu source đã có.
- Bootstrap, configuration và NetworkEventSink đã được triển khai; đang chờ xác minh bộ 23 test.
- Schema account V001 và authentication service không phụ thuộc JDBC đã được viết.
- Authentication và JdbcAccountRepository đã VERIFIED 32/32.
- MariaDB composition đã VERIFIED 36/36.
- Công cụ tạo administrator đầu tiên đã VERIFIED 40/40.
- Executable uber-JAR và launcher server/create-admin/help đã được thêm; đang chờ mvn package và 44 test.

Đọc chi tiết tại [project/STATUS.md](project/STATUS.md).

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

> Đọc toàn bộ `docs/START-HERE.md`, `docs/project/REQUIREMENTS.md`, `docs/project/STATUS.md`, phần mới nhất của `docs/project/WORKLOG.md`, các ADR đang hiệu lực và tài liệu module liên quan. Tiếp tục từ “Next exact action”. Không làm lại mục VERIFIED; nếu nghi ngờ, nêu bằng chứng trạng thái đã thay đổi trước khi kiểm tra lại.

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
