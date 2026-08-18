# Trạng thái hiện tại của NSOCry

**Cập nhật:** 2026-08-18 UTC  
**Trạng thái dự án:** Protocol bootstrap  
**Source NSOCry:** đã bắt đầu — codec compatibility tối thiểu  
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

Protocol bootstrap đã có codec, payload decoder, state machine, bounded session transport và TCP acceptor. Bước kế tiếp là wiring end-to-end loopback. PR #1 vẫn là Draft và chưa merge.

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

- [x] Chốt Java 17 + Maven; JUnit 6 cho automated tests.
- [x] Chốt single-module bootstrap và package root `com.nsocry`; compatibility boundary `com.nsocry.protocol.compat`.
- [ ] Chốt concurrency và session lifecycle.
- [ ] Thiết kế schema `nsocry`, migration và seed.
- [ ] Thiết kế logging/config/error handling.
- [x] Tạo build skeleton, key/cipher/frame codec và fixture-based protocol tests.
- [x] Viết explicit handshake state machine, bounded stream transport và TCP acceptor lifecycle.
- [ ] Wire TCP socket vào handshake processor và loopback integration test.
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

## Client V7 safety checkpoint

- Không phát hiện analytics/telemetry, hidden HTTP tracker, IMEI/IMSI, danh bạ hoặc location collection trong static scan.
- Có SMS payment helper do server message kích hoạt; chưa có bằng chứng đây là tracking.
- Không repack JAR vì chưa có defect đủ bằng chứng và sửa cipher một phía sẽ phá tương thích.
- Audit: `docs/security/client-v7-static-audit.md`.
- Original SHA-256: `affd33efffe2962c90c7e1da696d273ef9ac07ce27b81623afe8f364d2f32dd1`.

## Protocol bootstrap checkpoint

- Java 17, Maven, JUnit 6; ADR-0006.
- Source: `com.nsocry.protocol.compat`.
- Key delta codec, rolling XOR cursor, short/full-size frame codec và immutable frame value.
- Main source compile VERIFIED bằng JDK 17.
- Manual key/full-size vector verification PASSED.
- User-machine verification: Java 19.0.2 compiling with release 17; Maven test BUILD SUCCESS.
- Maven/JUnit suite đã chạy trên máy Windows người dùng: 3 tests, 0 failures, 0 errors, 0 skipped, BUILD SUCCESS.

## Session/TCP checkpoint

- Explicit phases: CONNECTED → KEY_SENT → CLIENT_INFO_RECEIVED → LOGIN_PENDING → AUTHENTICATED → CLOSED.
- Login rejection quay lại CLIENT_INFO_RECEIVED để có thể retry.
- Bounded streaming reader/writer; client→server full-size bị từ chối.
- CLIENT_INFO decoder dùng đúng byte+int order của V7.
- LOGIN object redacts password/client token khỏi `toString()`.
- Authentication là port, chưa nối database.
- TCP acceptor có max sessions, zero-capacity handoff, read timeout, named threads và graceful shutdown.
- Network failure/rejection được đưa qua event sink, không bị nuốt im lặng.
- Work compile/manual checks PASSED; Windows Maven compiled 22 source + 7 test source and all 15 tests PASSED.
- User-machine Maven verification: 15 tests, 0 failures, 0 errors, 0 skipped, BUILD SUCCESS.

## Next exact action

Wire accepted socket → `LegacySessionTransport` → `HandshakeProcessor`, thêm secure key-provider port và integration test trigger → CLIENT_INFO → LOGIN trên loopback. Dùng fake authentication; chưa kết nối database/gameplay.


## Checkpoint: TCP handshake integration implemented

- Added `SessionKeyProvider` and bounded `SecureRandomSessionKeyProvider`.
- Added `LegacyHandshakeConnectionHandler` to compose accepted sockets with transport and handshake processing.
- Added a real loopback integration test covering trigger → key reconstruction → CLIENT_INFO → LOGIN → fake authentication.
- No database or gameplay coupling was introduced.
- Expected suite after pull: 16 tests.
- Verification state: source checkpoint committed; Windows Maven verification is PENDING because the Work environment has Java Runtime but no Maven/JDK compiler.

## Next exact action

Run `mvn test` once after pulling. If all 16 tests pass, mark this checkpoint VERIFIED and proceed to application composition/configuration plus sanitized network event reporting.


## Chuẩn tài liệu và xác minh 16 test

- Người dùng đã xác nhận bộ test mới chạy đủ 16 test.
- Trạng thái checkpoint TCP handshake loopback: VERIFIED, 16/16.
- Mọi Javadoc, ghi chú code, tài liệu kỹ thuật, STATUS và WORKLOG mới phải viết bằng tiếng Việt.
- Chỉ giữ nguyên tiếng Anh đối với định danh code, tên protocol/command và thuật ngữ không nên dịch.
- Đã bổ sung package-info cho ba package, Javadoc trách nhiệm cho toàn bộ 25 type và tài liệu tra cứu đến cấp method.
- Việc chuẩn hóa Javadoc chi tiết cho từng method được xem là công việc tài liệu bắt buộc, không phải tính năng tùy chọn.


## Hoàn tất chuẩn hóa Javadoc tiếng Việt

- Toàn bộ 3 package hiện tại có package-info.java tiếng Việt.
- Toàn bộ 25 type hiện tại có mô tả trách nhiệm tiếng Việt.
- Constructor, public method và helper private có logic trong protocol.compat, session và network đã có Javadoc tiếng Việt.
- Tài liệu tra cứu class/method đã được liên kết từ chỉ mục package và START-HERE.
- documentation-standard.md đã khóa ngôn ngữ và độ phủ bắt buộc.
- Đây là thay đổi chỉ có comment/tài liệu; kết quả logic 16/16 vẫn là checkpoint VERIFIED gần nhất.

## Next exact action hiện tại

Thiết kế và triển khai application bootstrap tối thiểu: nạp cấu hình TCP đã kiểm tra, tạo SecureRandomSessionKeyProvider và NetworkEventSink làm sạch dữ liệu; chưa nối database hoặc gameplay.


## Checkpoint bootstrap/configuration/observability

- Đã thêm composition root NsocryServerApplication và main class trong JAR manifest.
- Đã thêm ServerConfiguration/Loader, file properties mẫu và validation theo từng property.
- Đã thêm SanitizedNetworkEventSink không ghi exception message, stack trace, password, token hoặc payload.
- Xác thực runtime hiện cố ý REJECTED cho đến khi module account/database tồn tại.
- Đã thêm 7 test; tổng dự kiến 23.
- Trạng thái kiểm chứng: PENDING vì source và pom.xml đã thay đổi.

## Next exact action hiện tại

Người dùng pull và chạy mvn test. Nếu 23/23 thành công, ghi VERIFIED rồi thiết kế schema nsocry tối thiểu cho account/authentication; chưa làm gameplay trước khi đăng nhập ổn định.


## Xác minh checkpoint bootstrap

- Người dùng đã chạy Maven sau khi pull source và pom.xml mới.
- Kết quả: 23 test, 0 failure, 0 error, 0 skipped.
- Bootstrap/configuration/observability: VERIFIED.
- Không chạy lại 23 test nếu source hoặc cấu hình build chưa thay đổi.

## Next exact action hiện tại

Thiết kế migration đầu tiên cho database nsocry và contract authentication an toàn dựa trên hành vi cần thiết; không sao chép bảng users reference với các cột hỗn hợp hoặc mật khẩu yếu.


## Thiết kế database nsocry V001

- Phân tích tĩnh bảng account/player reference, không chạy NSOKISS và không đọc database đang hoạt động.
- Đã tạo script database nsocry và migration accounts tối thiểu.
- Không sao chép credential, token, điện thoại, email, IP hoặc dữ liệu thanh toán reference.
- accounts chỉ sở hữu dữ liệu xác thực, trạng thái, role, lockout và timestamp.
- Username có unique constraint; password chỉ lưu chuỗi hash có version.
- Trạng thái migration: PROPOSED, chưa chạy trên MariaDB.

## Next exact action hiện tại

Tạo PasswordHashingPort, AccountRepository port và AuthenticationService bằng fake repository; chọn password hashing theo ADR trước khi viết JDBC adapter.


## Checkpoint authentication domain/service

- Đã thêm AccountCredential, AccountStatus và AccountRepository port.
- Đã thêm PasswordHashingPort và Pbkdf2PasswordHasher thuần Java 17.
- Đã thêm AuthenticationService triển khai AuthenticationPort.
- Password tạm được xóa trong finally; username thiếu dùng dummy-hash verification; client chỉ nhận quyết định REJECTED chung.
- ADR-0007 chốt PBKDF2-HMAC-SHA256, mặc định 600.000 vòng, salt 16 byte và hash 256 bit.
- Đã thêm 6 test; tổng mục tiêu 29.
- Trạng thái: PENDING vì Java source mới đã được thêm.

## Next exact action hiện tại

Người dùng pull và chạy mvn test. Nếu 29/29 thành công, ghi VERIFIED rồi triển khai JdbcAccountRepository cùng cấu hình kết nối database nsocry; không dùng database reference.


## Xác minh checkpoint authentication

- Người dùng đã chạy bộ Maven sau khi pull authentication source.
- Kết quả: 29 test, 0 failure, 0 error, 0 skipped.
- Authentication domain/service và PBKDF2 password hashing: VERIFIED.
- Không chạy lại checkpoint 29 test nếu Java source hoặc pom.xml chưa thay đổi.

## Next exact action hiện tại

Triển khai JdbcAccountRepository bằng DataSource/prepared statement và unit test adapter; sau đó mới ghép MariaDB driver/pool vào bootstrap.


## Checkpoint JdbcAccountRepository

- Đã thêm package com.nsocry.persistence và Javadoc tiếng Việt.
- JdbcAccountRepository chỉ phụ thuộc DataSource/java.sql, dùng prepared statement và try-with-resources.
- Đã triển khai find credential, ghi login thành công và tăng bộ đếm thất bại.
- AccountPersistenceException chỉ công bố mã thao tác cố định, không chứa SQL/credential.
- Đã thêm 3 unit test bằng JDBC proxy; tổng mục tiêu 32.
- Trạng thái: PENDING vì Java source mới đã được thêm.

## Next exact action hiện tại

Người dùng pull và chạy mvn test. Nếu 32/32 thành công, ghi VERIFIED rồi bổ sung MariaDB driver, DataSource configuration và composition; database credential chỉ lấy từ môi trường/config không commit.


## Checkpoint MariaDB composition

- Đã thêm MariaDB Connector/J 3.5.10 chính thức.
- DatabaseConfiguration ưu tiên NSOCRY_DB_URL/USER/PASSWORD và che password trong toString.
- MariaDbDataSourceFactory tạo DataSource nhưng không mở connection sớm.
- Main đã ghép JdbcAccountRepository, Pbkdf2PasswordHasher và AuthenticationService thật trước khi mở listener.
- File config mẫu không chứa database password.
- Thêm 4 test cấu hình; tổng mục tiêu 36.
- Trạng thái: PENDING vì Java source và pom.xml đã thay đổi.

## Next exact action hiện tại

Người dùng pull và chạy mvn test. Nếu 36/36 thành công, ghi VERIFIED rồi thêm công cụ tạo account đầu tiên và kiểm tra migration trên database nsocry cục bộ, không đụng database reference.


## Xác minh checkpoint MariaDB composition

- Người dùng đã chạy Maven sau khi pull Connector/J và database composition.
- Kết quả: 36 test, 0 failure, 0 error, 0 skipped.
- DatabaseConfiguration, MariaDbDataSourceFactory và authentication bootstrap: VERIFIED.
- Không chạy lại checkpoint 36 test nếu Java source hoặc pom.xml chưa thay đổi.

## Next exact action hiện tại

Tạo công cụ khởi tạo account đầu tiên: nhập password ẩn qua Console, hash bằng PasswordHashingPort và insert bằng prepared statement; không nhận password từ argument hoặc log.


## Checkpoint first administrator provisioning

- Đã thêm AccountRole, AccountProvisioningRepository và FirstAdministratorService.
- Đã thêm JdbcAccountProvisioningRepository với COUNT/INSERT prepared statement.
- FirstAdministratorCommand chỉ nhận password ẩn từ System.console, yêu cầu xác nhận và xóa các mảng password.
- Chỉ cho phép bootstrap khi bảng accounts chưa có dữ liệu.
- Đã thêm 4 test service; tổng mục tiêu 40.
- Chưa chạy migration/database thật và chưa yêu cầu người dùng nhập credential.
- Trạng thái: PENDING vì Java source mới đã được thêm.

## Next exact action hiện tại

Người dùng pull và chạy mvn test. Nếu 40/40 thành công, ghi VERIFIED rồi chuẩn bị fat JAR/lệnh vận hành và hướng dẫn tạo database nsocry cục bộ từng bước.


## Xác minh checkpoint first administrator

- Người dùng đã chạy Maven sau khi pull provisioning source.
- Kết quả: 40 test, 0 failure, 0 error, 0 skipped.
- FirstAdministratorService và provisioning flow: VERIFIED.
- Không chạy lại checkpoint 40 test nếu source hoặc pom.xml chưa thay đổi.

## Next exact action hiện tại

Tạo JAR chạy độc lập chứa dependency, entry command an toàn cho server/create-admin và tài liệu vận hành Windows; sau đó mới yêu cầu người dùng tạo database nsocry cục bộ.


## Checkpoint executable JAR/launcher

- Đã thêm NsocryLauncher với command server, create-admin và help.
- Không argument mặc định về help, không tự mở server.
- Đã thêm Maven Shade Plugin 3.6.2 và manifest main class mới.
- Uber-JAR chứa MariaDB driver nhưng không chứa database/account credential.
- Đã thêm 4 parser test; tổng mục tiêu 44.
- Trạng thái: PENDING vì Java source và pom.xml đã thay đổi.

## Next exact action hiện tại

Người dùng pull, chạy mvn package và chạy java -jar target/nsocry-server-0.1.0-SNAPSHOT.jar help. Nếu 44/44 và help thành công, ghi VERIFIED rồi hướng dẫn tạo database nsocry/migration cục bộ.


## Xác minh executable JAR

- Windows Maven chạy 44 test: 0 failure, 0 error, 0 skipped.
- mvn package: BUILD SUCCESS.
- Shade tạo target/nsocry-server-0.1.0-SNAPSHOT.jar.
- java -jar ... help in đúng server/create-admin/help.
- Cảnh báo overlapping META-INF/MANIFEST.MF là cảnh báo gộp resource; ManifestResourceTransformer đã tạo main manifest đúng.
- Executable JAR/launcher: VERIFIED.

## Next exact action hiện tại

Tạo database nsocry cục bộ, chạy V001 migration, tạo database user riêng và chạy create-admin; không đụng database reference.

- Security guard: config/nsocry.properties đã được thêm vào .gitignore; chỉ file example được commit.


## Database cục bộ và yêu cầu Admin Console

- Người dùng đã tạo database nsocry và chạy V001 thành công; SHOW TABLES/DESCRIBE accounts đúng schema.
- Migration V001 trên MariaDB cục bộ: VERIFIED.
- Ý tưởng lệnh bài Admin qua run.bat đã được ACCEPTED.
- ADR-0008 quy định batch chỉ là launcher, business logic ở Java, bắt buộc role/confirmation/audit.
- Giả định mở sk nghĩa là mở sự kiện.
- Command vật phẩm/sự kiện/giftcode sẽ dùng service thật khi module đó tồn tại, không thao tác SQL/JSON trực tiếp.

## Next exact action hiện tại

Tạo database user nsocry riêng và administrator đầu tiên bằng công cụ VERIFIED; sau đó triển khai nền Admin Console gồm login, help, status, audit và exit.
