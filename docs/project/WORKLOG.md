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

## 2026-08-18 — Inventory command và Controller routing

### Phạm vi

- `CMD.java` (311 constant).
- `Controller.java` (outer dispatch và ba nested envelope).

### Kết quả VERIFIED

- 126 constant có route client→server trong Controller.
- DIRECT: 71.
- NOT_LOGIN: 2.
- NOT_MAP: 31.
- SUB_COMMAND: 22.
- 185 declaration không có route trong Controller; hướng truyền vẫn UNKNOWN.
- 69 giá trị byte được nhiều symbol dùng lại.
- Numeric byte không đủ để định danh command; phải kèm envelope/scope và session phase.
- `NEW_MESSAGE` có raw nested subcommand 0 không có tên trong CMD.

### Artifact

- `docs/protocol/command-inventory.md`
- Bao gồm toàn bộ 311 declarations, 126 routed cases, handler evidence, collision table và analysis gaps.

### Quy tắc an toàn áp dụng

Không gán server→client cho constant không có Controller case. Không đặt payload field dựa trên tên symbol. Legacy symbol chỉ là mapping reference; tên NSOCry sẽ theo ADR-0005.

### Next exact action

Phân tích `MessageCollector`, key transform, `setClientType` và `login`; viết `docs/protocol/handshake-login.md`.

## 2026-08-18 — Handshake và login server-side

### Kết quả VERIFIED

- First inbound frame trước key được đọc nhưng không dispatch; nó kích hoạt `sendKey()`.
- Key response dùng `GET_SESSION_ID`, truyền key0 và XOR delta của các byte tiếp theo.
- Sau handshake, command, 2-byte length và payload dùng rolling XOR với read/write cursor độc lập.
- CLIENT_INFO có 13 field được đọc; 2 field chưa rõ semantics.
- LOGIN có 7 field; 3 field chưa rõ semantics/use.
- Login yêu cầu key + CLIENT_INFO trước, sau đó load User và gửi UPDATE_VERSION.
- CLIENT_OK dẫn đến load và gửi character list.
- Tạo `docs/protocol/handshake-login.md`.

### Legacy defects không được copy

- Log plaintext password.
- So sánh plaintext password.
- Key generation yếu/predictable.
- Exception bị nuốt.
- First frame bị discard ngầm.
- Session state bằng nhiều boolean.
- Login throttle chưa có bounded concurrency contract.
- Sender queue không thread-safe/bounded.

### Remaining UNKNOWN

Client-side trigger/key behavior, unnamed payload fields, Server.version bytes, error response matrix, SELECT_PLAYER/enter-map.

### Next exact action

Decompile/inspect `V7_217_X1.jar`, đối chiếu handshake/login và tạo protocol fixture.


## 2026-08-18 — Đối chiếu tĩnh client V7_217_X1

### Quyết định phạm vi

Người dùng xác nhận NSOKISS hiện đang chạy tốt. Từ checkpoint này, không build/run/test runtime NSOKISS; trạng thái đó là baseline do người dùng xác nhận. Reference chỉ được đọc tĩnh để hiểu hành vi và giao thức.

### Kết quả VERIFIED

- JAR có 180 class Java ME bị obfuscate; phân tích bằng bytecode, không chạy client.
- Client mở socket/streams rồi gửi ngay `GET_SESSION_ID (-27)` với payload rỗng.
- Client tái tạo key từ byte đầu và XOR delta; sau đó bật rolling XOR.
- Cursor mã hóa chiều gửi và giải mã chiều nhận độc lập, tiếp tục qua nhiều frame.
- Client outbound dùng length 2 byte; client inbound dùng length 4 byte khi command giải mã là `-32`.
- CLIENT_INFO được gửi trước LOGIN.
- Phát hiện client ghi field 9–10 của CLIENT_INFO theo thứ tự byte + int, còn server đọc int + byte. Tổng độ rộng vẫn 5 byte; client build này gửi cả hai bằng 0.
- LOGIN gửi username, password, version, hai UTF rỗng, một UTF do helper sinh và một server byte.
- CLIENT_OK là NOT_MAP/-101 không payload.
- SELECT_PLAYER là NOT_MAP/-126 + một UTF tên nhân vật.
- Ghi `docs/protocol/client-jar-analysis.md` và cập nhật `handshake-login.md`.

### Không thực hiện

- Không chạy/test NSOKISS.
- Không sửa reference.
- Không viết hoặc sao chép class legacy vào NSOCry.
- Không đưa tên legacy thành package/class/method mới.

### Git

- Branch: `agent/document-nsokiss-runtime`
- Draft PR: #1
- Checkpoint bắt đầu: `f13b3d91205a900df85dd1a15fd160b2b0e1e381`
- Commit tài liệu client đầu tiên: `b5719ab8c419211a858723caffe0ce8328c3abcf`

### Next exact action

Tạo protocol fixture deterministic đầu tiên cho trigger, key, rolling XOR, CLIENT_INFO, LOGIN, CLIENT_OK, SELECT_PLAYER và full-size `-32`; sau đó mới chốt skeleton server NSOCry.
