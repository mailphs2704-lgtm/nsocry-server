# Trạng thái hiện tại của NSOCry

**Cập nhật:** 2026-08-18 UTC  
**Trạng thái dự án:** Discovery / reverse engineering  
**Source NSOCry:** chưa bắt đầu  
**Nguồn sự thật:** repository và thư mục `docs/`

## Snapshot Git

- Repository: `mailphs2704-lgtm/nsocry-server`
- Default branch: `main`
- Main checkpoint đã xác nhận: `98723d12`
- Nhánh tài liệu đang làm: `agent/document-nsokiss-runtime`
- Commit kiến trúc runtime: `a2e6d629`
- Draft PR: [#1](https://github.com/mailphs2704-lgtm/nsocry-server/pull/1)
- Trạng thái merge: chưa merge, không được coi tài liệu đã có trên main cho đến khi PR được hợp nhất.

## Đã hoàn thành (VERIFIED)

- [x] Cài và kiểm tra Git trên máy người dùng.
- [x] Cài GitHub CLI và đăng nhập tài khoản `mailphs2704-lgtm`.
- [x] Tạo/thiết lập repository `nsocry-server`.
- [x] Tạo cấu trúc thư mục documentation/reference ban đầu.
- [x] Kiểm kê source NSOKISS: 250 file Java.
- [x] Kiểm kê package cấp cao và package con.
- [x] Lưu source inspection và các file reference lên GitHub.
- [x] Loại `NSOKISS.zip` khoảng 269 MB khỏi Git/lịch sử push cần thiết.
- [x] Push checkpoint reference thành công lên `main` tại `98723d12`.
- [x] Xác định entry point: `NinjaSchool.main()`.
- [x] Xác định chuỗi khởi động cấu hình → DB → init → start.
- [x] Xác định luồng `ServerSocket.accept() -> Session -> Controller/Service`.
- [x] Xác định frame message mức cao: command + length + payload, có key transform.
- [x] Xác định SQL reference có 44 bảng.
- [x] Tạo tài liệu `docs/architecture/nsokiss-runtime.md`.
- [x] Mở Draft PR #1 cho tài liệu kiến trúc.

## Đang thực hiện

- [ ] Hoàn thiện bộ hồ sơ bàn giao và định hướng dự án trong Draft PR #1.

## Chưa thực hiện

### Discovery

- [ ] Inventory toàn bộ constant command trong `CMD.java`.
- [ ] Ghép từng command với nhánh xử lý trong `Controller.java`.
- [ ] Phân tích `MessageCollector`, sender queue, close/error behavior.
- [ ] Phân tích handshake và trao đổi key hai chiều.
- [ ] Phân tích client metadata/client type.
- [ ] Phân tích luồng login đầy đủ.
- [ ] Truy dependency login đến `User`, `Char`, DAO và SQL.
- [ ] Phân tích client JAR để xác nhận protocol.
- [ ] Ghép 44 bảng với class/method đọc ghi.
- [ ] Lập bản đồ tải game data, map, mob, NPC, item, skill, task.

### Thiết kế NSOCry

- [ ] Chốt Java/build tool.
- [ ] Chốt package/module architecture.
- [ ] Chốt concurrency và session lifecycle.
- [ ] Thiết kế schema database `nsocry`.
- [ ] Thiết kế migration và seed data.
- [ ] Thiết kế logging, config và error handling.
- [ ] Viết skeleton server.
- [ ] Viết test protocol.
- [ ] Kết nối client thật.

## Không được làm lại nếu không có lý do

- Kiểm tra NSOKISS có chạy được không.
- Liệt kê lại toàn bộ file/package.
- Cài lại Git/GitHub CLI.
- Tạo lại repository.
- Push lại ZIP lớn.
- Yêu cầu người dùng dán toàn bộ `Server.java` hoặc source đã có trong reference.

## Blocker hiện tại

Không có blocker kỹ thuật cho bước discovery kế tiếp. Việc merge Draft PR #1 cần người dùng duyệt khi nội dung hoàn chỉnh.

## Next exact action

Đọc:

- `source-reference/NSOKISS-inspection/src/main/java/com/nsoz/constants/CMD.java`
- `source-reference/NSOKISS-inspection/src/main/java/com/nsoz/network/Controller.java`

Sau đó tạo `docs/protocol/command-inventory.md` với mỗi command gồm: tên, giá trị byte/int, hướng client/server, handler, service gọi ra, trạng thái xác minh và ghi chú payload. Chưa viết network code NSOCry trước khi inventory và handshake tối thiểu hoàn thành.
