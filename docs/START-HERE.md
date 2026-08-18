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
- Chưa bắt đầu viết server NSOCry.
- Bước kỹ thuật tiếp theo: lập inventory command từ `CMD.java`, đối chiếu `Controller.java`, sau đó mô tả handshake và login.

Đọc chi tiết tại [project/STATUS.md](project/STATUS.md).

## Ràng buộc không được vi phạm

1. Không sửa source NSOKISS.
2. Không dùng database NSOKISS làm database phát triển NSOCry.
3. Không copy hàng loạt class NSOKISS sang namespace mới rồi gọi đó là “viết lại”.
4. Không bắt người dùng thực hiện lại kiểm tra đã được đánh dấu VERIFIED.
5. Không viết module mới trước khi protocol/dependency cần thiết đã được hiểu.
6. Mỗi thay đổi code phải cập nhật documentation và worklog.
7. Không tự quyết gameplay hoặc business rule chưa được người dùng mô tả.

## Lệnh bàn giao cho AI

> Đọc toàn bộ `docs/START-HERE.md`, `docs/project/REQUIREMENTS.md`, `docs/project/STATUS.md`, phần mới nhất của `docs/project/WORKLOG.md`, các ADR đang hiệu lực và tài liệu module liên quan. Tiếp tục từ “Next exact action”. Không làm lại mục VERIFIED; nếu nghi ngờ, nêu bằng chứng trạng thái đã thay đổi trước khi kiểm tra lại.

## Trước khi kết thúc mỗi phiên

- Cập nhật STATUS.
- Thêm một entry vào WORKLOG.
- Ghi commit/branch/PR và kết quả kiểm chứng.
- Ghi rõ điểm dừng nếu đang dở.
- Ghi đúng một “Next exact action” có thể thực hiện ngay.
