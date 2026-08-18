# Nhật ký làm việc NSOCry

Nhật ký append-only. Không sửa lịch sử để làm đẹp tiến độ; nếu thông tin cũ sai, thêm entry đính chính và liên kết.

## 2026-08-18 — Khởi tạo repository và bảo toàn reference

### Đã làm

- Thiết lập Git và GitHub CLI; xác nhận tài khoản `mailphs2704-lgtm`.
- Tạo repository, cấu trúc `docs/` và `source-reference/`.
- Kiểm kê source NSOKISS và tạo các file reference.
- Xử lý GitHub từ chối `NSOKISS.zip` khoảng 269 MB.
- Giữ ZIP trên máy nhưng loại khỏi Git.
- Push reference thành công lên main.

### Kết quả VERIFIED

- NSOKISS reference có 250 file Java.
- Main cập nhật đến `98723d12`.
- ZIP lớn không còn chặn push.
- Reference/inspection cần thiết đã ở GitHub.

### Không cần làm lại

Cài Git/GitHub CLI, kiểm kê package/source, push ZIP và xác minh NSOKISS đang hoạt động.

## 2026-08-18 — Bản đồ runtime NSOKISS đầu tiên

### Đã làm

- Đọc `NinjaSchool.java`, `Server.java`, `Session.java` và network core.
- Xác định entry point, startup, vòng accept socket, tạo session và vai trò Controller/Service.
- Xác định framing mức cao của message.
- Nhóm 44 bảng SQL reference.
- Viết `docs/architecture/nsokiss-runtime.md`.

### Git

- Branch: `agent/document-nsokiss-runtime`
- Commit: `a2e6d6293c481e14b17c9b4111bd060ac5ba16a3`
- Draft PR: #1
- Main base: `98723d12beee21c39a8a76e54a02eea019c3f31c`

### Kiểm chứng

Đối chiếu trực tiếp source reference và SQL. Chưa kiểm chứng byte-level protocol bằng client JAR.

## 2026-08-18 — Chuẩn hóa continuity và định hướng tổng thể

### Mục tiêu

Đảm bảo ChatGPT Work, Chat thường hoặc AI mới tiếp tục đúng hướng mà không phụ thuộc trí nhớ hội thoại.

### Đã làm

- Tạo `START-HERE.md`.
- Ghi yêu cầu/ràng buộc vào `REQUIREMENTS.md`.
- Tạo snapshot `STATUS.md` với đúng một next action.
- Xây dựng roadmap theo stage gate.
- Chuẩn hóa workflow và documentation.
- Viết architecture overview và package index.
- Tạo bốn ADR nền tảng.
- Tạo hướng dẫn AI handoff.
- Cập nhật README tài liệu.

### Git

- Branch: `agent/document-nsokiss-runtime`
- Commit nội dung chính: `59b7365abaceec83267573c353e5212f420fd0e1`
- Commit hoàn tất STATUS: `4f3e2a9f6f9caebf8c5f52faf477d40295d208de`
- Draft PR: #1

### Kết quả VERIFIED

- Các file đã được ghi trên nhánh GitHub và đọc lại được qua GitHub.
- Không có code NSOCry nào được viết trong task này.
- NSOKISS reference không bị sửa.
- Next exact action là command inventory từ `CMD.java` và `Controller.java`.

### Trạng thái bàn giao

Checkpoint continuity hoàn tất. Phiên sau phải bắt đầu ở `docs/START-HERE.md` và không lặp các mục VERIFIED trong STATUS.

## 2026-08-18 — Chốt chuẩn đặt tên NSOCry/Cry

### Yêu cầu người dùng xác nhận

- NSOCry được viết mới hoàn toàn dựa trên logic đã hiểu từ NSOKISS.
- Mọi package, class, method và định danh liên quan đến `nsoz` hoặc `nsotien` trong source mới phải đổi.
- `NSOCry`/`nsocry` hoặc `Cry`/`cry` là chuẩn nhận diện duy nhất của dự án.

### Cập nhật

- Bổ sung naming policy vào REQUIREMENTS và STATUS.
- Bổ sung quy tắc kiểm tra legacy name vào documentation standard.
- Ghi ADR-0005.
- Xác định package root định hướng là `com.nsocry`.

### Lưu ý

Tên legacy vẫn được phép trong `source-reference/` và tài liệu trích dẫn reference; không được dùng làm namespace hoặc định danh implementation NSOCry.
