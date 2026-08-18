# Nhật ký làm việc NSOCry

Nhật ký append-only. Không sửa lịch sử để làm đẹp tiến độ; nếu thông tin cũ sai, thêm entry đính chính và liên kết.

## 2026-08-18 — Khởi tạo repository và bảo toàn reference

### Đã làm

- Thiết lập Git và GitHub CLI trên máy người dùng.
- Xác nhận tài khoản GitHub `mailphs2704-lgtm`.
- Tạo repository `nsocry-server`.
- Tạo cấu trúc `docs/` và `source-reference/`.
- Kiểm kê source NSOKISS.
- Tạo các file reference về tree, package, source và network core.
- Stage/commit bộ reference.
- Xử lý lỗi GitHub từ chối file `NSOKISS.zip` khoảng 269 MB.
- Giữ ZIP trên máy nhưng loại khỏi Git.
- Push reference thành công lên main.

### Kết quả đã kiểm chứng

- NSOKISS reference có 250 file Java.
- Repository main cập nhật đến `98723d12`.
- NSOKISS ZIP không còn chặn push.
- Reference và inspection cần thiết đã ở GitHub.

### Không cần làm lại

- Cài Git/GitHub CLI.
- Kiểm kê package/source.
- Push ZIP.
- Xác minh NSOKISS đang hoạt động.

## 2026-08-18 — Bản đồ runtime NSOKISS đầu tiên

### Đã làm

- Đọc `NinjaSchool.java`, `Server.java`, `Session.java` và network core.
- Xác định entry point và chuỗi startup.
- Xác định vòng accept socket và tạo session.
- Xác định vai trò mức cao của Controller/Service.
- Xác định framing mức cao của message.
- Nhóm 44 bảng SQL reference.
- Viết `docs/architecture/nsokiss-runtime.md`.

### Git

- Branch: `agent/document-nsokiss-runtime`
- Commit: `a2e6d6293c481e14b17c9b4111bd060ac5ba16a3`
- Draft PR: #1
- Main base: `98723d12beee21c39a8a76e54a02eea019c3f31c`

### Kiểm chứng

Đối chiếu trực tiếp source reference và database SQL. Chưa kiểm chứng byte-level protocol bằng client JAR.

## 2026-08-18 — Chuẩn hóa continuity và định hướng tổng thể

### Mục tiêu

Đảm bảo ChatGPT Work, Chat thường hoặc một phiên AI mới có thể tiếp tục đúng hướng mà không phụ thuộc trí nhớ hội thoại.

### Nội dung bổ sung

- Điểm vào START-HERE.
- Requirements và ràng buộc.
- Status với next exact action.
- Roadmap theo stage gate.
- Quy trình Git/documentation.
- Architecture overview.
- ADR cho các quyết định nền tảng.
- Hướng dẫn AI handoff.
- Package documentation index.

### Trạng thái

Đang được thêm vào Draft PR #1. Sau khi commit hoàn tất, entry này phải được cập nhật trong STATUS nếu SHA/PR thay đổi.

### Điểm tiếp tục

Command inventory từ `CMD.java` và `Controller.java`.
