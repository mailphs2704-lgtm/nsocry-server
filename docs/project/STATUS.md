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
- Commit continuity/roadmap: `59b7365a`
- Draft PR: [#1](https://github.com/mailphs2704-lgtm/nsocry-server/pull/1)
- Trạng thái merge: chưa merge; không coi tài liệu đã có trên main trước khi PR được hợp nhất.

## Đã hoàn thành (VERIFIED)

- [x] Cài và kiểm tra Git trên máy người dùng.
- [x] Cài GitHub CLI và đăng nhập tài khoản `mailphs2704-lgtm`.
- [x] Tạo/thiết lập repository `nsocry-server`.
- [x] Tạo cấu trúc documentation/reference.
- [x] Kiểm kê source NSOKISS: 250 file Java và các package.
- [x] Lưu source inspection/reference lên GitHub.
- [x] Loại `NSOKISS.zip` khoảng 269 MB khỏi Git để push thành công.
- [x] Push checkpoint reference lên `main` tại `98723d12`.
- [x] Xác định `NinjaSchool.main()` là entry point.
- [x] Xác định chuỗi cấu hình → DB → init → start.
- [x] Xác định luồng `ServerSocket.accept() -> Session -> Controller/Service`.
- [x] Xác định frame mức cao: command + length + payload, có key transform.
- [x] Xác định SQL reference có 44 bảng.
- [x] Tạo tài liệu runtime NSOKISS.
- [x] Tạo START-HERE, REQUIREMENTS, STATUS, ROADMAP và WORKLOG.
- [x] Tạo workflow, documentation standard và AI handoff.
- [x] Tạo architecture overview và package index.
- [x] Ghi bốn ADR nền tảng.
- [x] Chốt chuẩn định danh dự án: chỉ dùng NSOCry/nsocry/Cry/cry; loại nsoz/nsotien khỏi source mới.
- [x] Mở/cập nhật Draft PR #1.

## Đang thực hiện

Protocol fixture đầu tiên đã hoàn thành và được đọc/parse lại từ GitHub. PR #1 vẫn là Draft và chưa merge.

## Chưa thực hiện

### Discovery

- [x] Inventory toàn bộ 311 constant command trong `CMD.java`.
- [x] Ghép 126 command với nhánh xử lý trong `Controller.java`; 185 declaration còn cần truy usage.
- [x] Phân tích `MessageCollector`, sender queue và close/error behavior ở mức server reference.
- [x] Phân tích handshake/key transform phía server; phía client còn cần JAR/fixture.
- [x] Lập layout 13 field CLIENT_INFO; 2 field vẫn UNKNOWN semantics.
- [x] Lập layout LOGIN và luồng đến `User.login`; mapping SQL đầy đủ còn pending.
- [x] Phân tích tĩnh client JAR để xác nhận protocol; không chạy/test NSOKISS.
- [ ] Ghép 44 bảng với class/method đọc ghi.
- [ ] Lập bản đồ game data/map/mob/NPC/item/skill/task.

### Thiết kế và xây dựng NSOCry

- [ ] Chốt Java/build tool (next).
- [ ] Chốt package/module architecture.
- [ ] Chốt concurrency và session lifecycle.
- [ ] Thiết kế schema `nsocry`, migration và seed.
- [ ] Thiết kế logging/config/error handling.
- [ ] Viết skeleton server và protocol tests.
- [ ] Kết nối client thật.

## Naming policy đang hiệu lực

- Product: `NSOCry`.
- Technical namespace: `nsocry`; package root định hướng: `com.nsocry`.
- Short form được phép: `Cry`/`cry`.
- `nsoz` và `nsotien` chỉ được tồn tại trong reference hoặc mô tả reference.
- Mọi source/config/schema/artifact mới phải vượt legacy-name scan trước khi merge.
- Đổi tên phải theo trách nhiệm mới, không chỉ search/replace.

## Không được làm lại nếu không có bằng chứng trạng thái đổi

- Kiểm tra NSOKISS có chạy được không.
- Liệt kê lại toàn bộ file/package.
- Cài lại Git/GitHub CLI hoặc tạo lại repository.
- Push lại ZIP lớn.
- Yêu cầu người dùng dán toàn bộ source đã có trong reference.

## Blocker hiện tại

Không có blocker kỹ thuật. Không cần chạy/test NSOKISS vì người dùng đã xác nhận hệ thống reference đang hoạt động tốt.

## Kết quả command inventory

- 311 constant được khai báo.
- 126 constant được route trong Controller: DIRECT 71, NOT_LOGIN 2, NOT_MAP 31, SUB_COMMAND 22.
- 185 constant chưa được Controller route; chưa được phép kết luận là server→client.
- 69 giá trị byte có collision giữa nhiều symbol; scope/envelope là một phần của identity.
- Tài liệu: `docs/protocol/command-inventory.md`.

## Kết quả handshake/login server-side

- Xác định first-frame trigger bị đọc nhưng không dispatch.
- Xác định key frame GET_SESSION_ID và thuật toán reconstruction.
- Xác định XOR rolling cipher với read/write cursor độc lập.
- Xác định normal inbound frame và bất đối xứng FULL_SIZE.
- Lập layout 13 field CLIENT_INFO.
- Lập layout 7 field LOGIN.
- Lập luồng User authentication, updateVersion, CLIENT_OK và character list.
- Ghi 8 legacy defects không được copy, gồm plaintext credential logging/password comparison.
- Tài liệu: `docs/protocol/handshake-login.md`.

## Kết quả đối chiếu client JAR

- Client gửi frame đầu `GET_SESSION_ID (-27)`, payload rỗng.
- Xác nhận key reconstruction bằng XOR delta và cursor đọc/ghi độc lập.
- Xác nhận CLIENT_INFO, LOGIN, CLIENT_OK và SELECT_PLAYER.
- Hai UTF chưa đặt tên của LOGIN là chuỗi rỗng trong client build này.
- Phát hiện lệch kiểu field CLIENT_INFO: client ghi byte + int, server đọc int + byte; tổng 5 byte nên vẫn căn hàng khi cả hai bằng 0.
- Client đọc length 4 byte cho command `-32`.
- Tài liệu: `docs/protocol/client-jar-analysis.md`.
- Không chạy client/server NSOKISS.

## Protocol fixture checkpoint

- Fixture: `docs/protocol/fixtures/handshake-login-v1.json`.
- Dùng test key `CryTestKey` và credential tổng hợp, không có dữ liệu thật.
- Có plain/encrypted bytes cho CLIENT_INFO, LOGIN, CLIENT_OK và SELECT_PLAYER.
- Có trigger/key frame và vector full-size `-32` 32 KiB kèm SHA-256.
- JSON đã được đọc và parse lại thành công từ GitHub.

## Next exact action

Chốt Java/build/module skeleton cho NSOCry, sau đó tạo codec tối thiểu và automated tests đọc trực tiếp fixture `handshake-login-v1.json`. Không kết nối database/gameplay ở bước này.
