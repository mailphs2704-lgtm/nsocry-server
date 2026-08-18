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


## 2026-08-18 — Protocol fixture v1

### Đã làm

- Tạo `docs/protocol/fixtures/handshake-login-v1.json`.
- Dùng key tổng hợp `CryTestKey`, tài khoản/mật khẩu giả và tên nhân vật `CryNinja`.
- Ghi cả plain frame và encrypted frame cho CLIENT_INFO, LOGIN, CLIENT_OK và SELECT_PLAYER.
- Ghi trigger/key response và vector server→client full-size `-32` với payload 32 KiB.
- Đọc lại artifact từ GitHub và parse JSON thành công.

### Kết quả VERIFIED

- Cursor client→server sau bốn frame lần lượt: 6, 2, 6, 0.
- Full-size encrypted frame dài 32.773 byte.
- SHA-256 full-size vector: `40e7b93f17d5318417174f2f5ce321e1b2078cd7e2420f24e91073a772fe278c`.
- Commit fixture: `a350776316aa5086cfa3d7b087b116a9f33e3a55`.

### Next exact action

Chốt Java/build/module skeleton và tạo codec tests đọc fixture này. Chưa kết nối database hoặc gameplay.


## 2026-08-18 — V7 safety audit và NSOCry protocol bootstrap

### Client audit

- Chỉ phân tích bytecode/resources; không chạy client hoặc NSOKISS.
- Không tìm thấy analytics/telemetry, tracker HTTP ẩn, IMEI/IMSI, danh bạ hoặc location collection.
- Xác định module SMS là flow thanh toán do server message kích hoạt; chưa có bằng chứng tracking.
- Xác định `platformRequest` và RMS tồn tại nhưng chưa có evidence exfiltration.
- Không chỉnh/repack JAR vì chưa có defect cụ thể và việc sửa cipher một phía sẽ phá protocol.
- Ghi audit tại `docs/security/client-v7-static-audit.md`.
- SHA-256 JAR gốc: `affd33efffe2962c90c7e1da696d273ef9ac07ce27b81623afe8f364d2f32dd1`.

### Source NSOCry đầu tiên

- Chốt Java 17 + Maven + JUnit 6 trong ADR-0006.
- Tạo package `com.nsocry.protocol.compat`.
- Viết `RollingXorCipher`, `LegacyKeyCodec`, `LegacyFrameCodec` và `ProtocolFrame`.
- Viết `ProtocolFixtureTest` đọc trực tiếp fixture JSON đã lưu.
- Không dùng legacy product/vendor identifiers trong source mới.

### Verification

- Main source compile thành công bằng OpenJDK 17.0.19 với `--release 17`.
- Manual key-frame vector PASSED.
- Full-size 32 KiB encrypt/decode và SHA-256 vector PASSED.
- Legacy-name scan trên source/pom không có match.
- Maven/JUnit chưa chạy tại Work environment vì không có Maven CLI; cần chạy `mvn test` sau khi pull.

### Git

- Branch: `agent/document-nsokiss-runtime`
- Protocol bootstrap commit: `c2751c538df00f9828a79a66bc0d33232b87196f`
- Draft PR: #1

### Pull readiness

Checkpoint này có thể pull về máy để cài Maven/JDK và chạy test. Không cần merge PR để kiểm tra nhánh.

### Next exact action

Viết TCP session skeleton và explicit handshake state machine; chưa nối database/gameplay.


## 2026-08-18 — Windows pull/build verification

### User-machine result

- Branch `agent/document-nsokiss-runtime` fetched and switched successfully.
- Working tree clean and synchronized with origin.
- Java runtime/compiler: 19.0.2; Maven compiled with `release 17`.
- Maven resolved project dependencies successfully.
- Main compile: 4 source files.
- Test compile: 1 test source file.
- Test class: `com.nsocry.protocol.compat.ProtocolFixtureTest`.
- Tests run: 3.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- Final result: `BUILD SUCCESS`.

### Meaning

The first NSOCry source checkpoint is now VERIFIED both in Work static/manual checks and by the real Windows checkout. The protocol fixture, build configuration and automated tests are pull-safe.

### Next exact action

Proceed with TCP session skeleton and explicit handshake state machine.


## 2026-08-18 — Autonomous protocol/session/TCP continuation

### Authorization boundary

Người dùng yêu cầu tiếp tục các công việc có thể làm độc lập trong lúc vắng mặt. Không có quyết định gameplay/database nào được tự đặt; không chạy NSOKISS và không sửa JAR V7.

### Implemented

- Explicit atomic `HandshakeStateMachine` với terminal/idempotent close.
- `ProtocolLimits`, bounded `LegacyFrameReader` và synchronized `LegacyFrameWriter`.
- `LegacySessionTransport`: validate trigger rỗng -27, gửi key, bật cipher hai chiều độc lập và deterministic close.
- CLIENT_INFO decoder theo đúng byte order client V7.
- LOGIN decoder và `LoginRequest.toString()` redaction cho password/token.
- `AuthenticationPort`/decision/event và `HandshakeProcessor`, chưa phụ thuộc database.
- Bounded `TcpServer`: backlog, max sessions, zero-capacity handoff, read timeout, TCP no-delay/keepalive, named threads và graceful shutdown.
- `NetworkEventSink` để không nuốt lỗi mạng.
- Package documentation: `docs/packages/protocol-session.md`.

### Verification in Work environment

- 23 main class files compile bằng `--release 17`.
- Legacy-name scan trên source/pom: no matches.
- Manual session transport verification: PASSED.
- Manual CLIENT_INFO/LOGIN decode and secret-redaction verification: PASSED.
- Manual TCP loopback accept/shutdown verification: PASSED.
- Automated suite mở rộng từ 3 lên dự kiến 15 tests; cần user pull và chạy Maven để xác nhận chính thức.

### Commits

- State machine: `05b61e0771a2b8099d8bfedf3e78f3ec6711a317`.
- Bounded session transport: `be0ec617b6570bdc019e96905dc4efbe0f7837d7`.
- Safe payload decoding: `079243eac955ec795f4e9f1237484d4e6e6748a2`.
- Handshake orchestration: `776409d71c146c3a0b2d50a45a864b41331e7813`.
- TCP acceptor: `8a46b5841378c567c77362451c6e198910f2dc77`.

### Next exact action

Wire accepted socket to transport/processor with a secure key-provider port and loopback handshake integration test. Fake authentication only; no database/gameplay.


## 2026-08-18 — Windows verification of session/TCP checkpoint

### VERIFIED result

- Pulled branch successfully.
- Maven compiled 22 main source files with release 17.
- Maven compiled 7 test source files.
- Test classes: TCP server, frame stream, protocol fixture, handshake payload, handshake processor, handshake state machine and session transport.
- Tests run: 15.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- Final result: `BUILD SUCCESS`.
- Total time shown: 1.826 seconds.

### Continuity rule

Do not rerun this exact 15-test checkpoint unless protocol/session/network source or build configuration changes. The next verification target is the full loopback handshake integration.


## 2026-08-18 — Full loopback handshake wiring

### Implemented

- Introduced a `SessionKeyProvider` port so networking does not own key-generation policy.
- Added `SecureRandomSessionKeyProvider` with an explicit 1–255-byte key length boundary.
- Added `LegacyHandshakeConnectionHandler`, connecting each accepted socket to `LegacySessionTransport` and `HandshakeProcessor`.
- The handler requires CLIENT_INFO before LOGIN and accepts only AUTHENTICATED or LOGIN_REJECTED as terminal bootstrap results.
- Added `LegacyHandshakeLoopbackTest` using a real loopback socket, transmitted key reconstruction, a continuous client outbound cipher cursor, CLIENT_INFO, LOGIN and fake authentication.
- Authentication assertions use non-production fixture values; credentials/tokens are not logged.
- Database and gameplay remain outside this checkpoint.

### Verification

- Static/source review completed.
- Work environment cannot execute Maven or Java compilation because it contains a JRE only (`java` present; `mvn` and `javac` absent).
- Windows verification target is exactly 16 tests. Do not claim VERIFIED until `mvn test` succeeds on the user machine.

### Next exact action

Pull the branch and run `mvn test`. After 16/16 pass, record the result and continue to application composition/configuration and a sanitized `NetworkEventSink` implementation.


## 2026-08-18 — Xác minh 16 test và chuẩn hóa ngôn ngữ tài liệu

### VERIFIED

- Người dùng xác nhận bộ Maven mới đã chạy đủ 16 test.
- Checkpoint loopback handshake được chuyển từ PENDING sang VERIFIED.
- Không cần chạy lại checkpoint này nếu source hoặc cấu hình build không thay đổi.

### Quy tắc tài liệu mới

- Toàn bộ Javadoc, ghi chú code, tài liệu kỹ thuật, STATUS và WORKLOG dùng tiếng Việt.
- Tên package, class, method, command và protocol giữ nguyên để tra cứu chính xác.
- Đã thêm package-info cho network, session và protocol.compat.
- Đã thêm mô tả trách nhiệm trực tiếp vào toàn bộ 25 type hiện tại.
- Đã tạo docs/code-reference/protocol-session-network.md, ánh xạ class/method, hợp đồng, trạng thái, lỗi và test bảo vệ.
- Tiếp tục bổ sung Javadoc chi tiết cho từng method; không thay đổi logic đã VERIFIED.


## 2026-08-18 — Hoàn tất Javadoc tiếng Việt đến cấp method

### Đã thực hiện

- Bổ sung Javadoc tiếng Việt cho toàn bộ package, type, constructor, public API và helper có logic của protocol.compat, session và network.
- Ghi rõ tác động state, giới hạn cấp phát, điều kiện cipher, vòng đời socket và quy tắc không log password/token.
- Chuyển tài liệu tra cứu source sang tiếng Việt.
- Cập nhật documentation-standard.md, packages/index.md và START-HERE.md để phiên AI sau không bỏ sót yêu cầu này.
- Không sửa biểu thức, control flow, API hay cấu hình build.

### Kiểm chứng

- Không chạy lại Maven vì checkpoint chỉ thay đổi comment và Markdown.
- Kết quả chức năng gần nhất vẫn là VERIFIED 16/16 do người dùng xác nhận.
- Source hoặc build configuration chưa thay đổi kể từ lần xác minh đó.

### Next exact action

Triển khai application bootstrap tối thiểu với cấu hình TCP được kiểm tra, SecureRandomSessionKeyProvider và NetworkEventSink làm sạch; chưa kết nối database/gameplay.


## 2026-08-18 — Bootstrap, cấu hình và observability

### Đã triển khai

- Thêm ServerConfiguration với namespace property nsocry, mặc định rõ ràng và giới hạn số.
- Thêm ServerConfigurationLoader đọc file an toàn hoặc dùng mặc định khi file chưa tồn tại.
- Thêm SanitizedNetworkEventSink chỉ xuất mã sự kiện, địa chỉ và loại exception; không xuất exception message/stack trace/payload/credential.
- Thêm NsocryServerApplication làm composition root, shutdown hook và main class.
- Authentication mặc định trả REJECTED; chưa kết nối database/gameplay.
- Thêm file config/nsocry.properties.example.
- Cấu hình maven-jar-plugin với main class NSOCry.
- Thêm 7 test cho configuration, log sanitization và application lifecycle.

### Kiểm chứng

- Bộ 16 test trước vẫn VERIFIED.
- Source và pom.xml đã thay đổi nên checkpoint mới là PENDING.
- Mục tiêu Maven sau pull: 23 test, 0 failure, 0 error.

### Next exact action

Người dùng chạy mvn test. Sau khi 23/23 VERIFIED, bắt đầu schema nsocry tối thiểu và authentication adapter.


## 2026-08-18 — Windows xác minh checkpoint bootstrap

### VERIFIED

- Tests run: 23.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- Bootstrap, configuration, observability và 16 test protocol/session/network trước đó đều đạt.

### Quy tắc liên tục

Không chạy lại checkpoint 23 test nếu source hoặc pom.xml chưa thay đổi. Bước tiếp theo là migration database nsocry và contract authentication mới, không nhập dữ liệu tài khoản nhạy cảm từ reference.


## 2026-08-18 — Thiết kế migration account đầu tiên

### Phân tích

- Chỉ đọc tĩnh database.sql reference.
- Bảng tài khoản reference trộn xác thực, thanh toán, web, event và quyền; không phù hợp để sao chép.
- Không sử dụng hoặc ghi lại dữ liệu mẫu nhạy cảm.

### Đã tạo

- database/00-create-database.sql tạo database chuẩn nsocry.
- database/migrations/V001__account_authentication.sql tạo bảng accounts tối thiểu.
- docs/database/account-authentication.md mô tả cột, ownership, bảo mật và khác biệt với reference.

### Kiểm chứng

- SQL chưa chạy; trạng thái PROPOSED.
- Không thay đổi Java/pom sau checkpoint VERIFIED 23/23, vì vậy không yêu cầu chạy lại Maven.

### Next exact action

Tạo các port và authentication service bằng fake repository, sau khi chốt password hashing trong ADR.


## 2026-08-18 — Authentication domain và password hashing

### Đã triển khai

- AccountCredential/AccountStatus và AccountRepository port không phụ thuộc JDBC.
- PasswordHashingPort và PBKDF2-HMAC-SHA256 implementation thuần Java 17.
- Hash tự mô tả version/work factor/salt/output; mặc định 600.000 vòng, salt riêng 16 byte.
- Constant-time compare bằng MessageDigest.isEqual và giới hạn password/hash parameters chống dữ liệu hỏng gây DoS.
- AuthenticationService dùng dummy hash cho username thiếu, xóa char[] password trong finally và trả REJECTED đồng nhất.
- Thêm ADR-0007 và tài liệu package tiếng Việt.
- Thêm 6 unit test; mục tiêu toàn suite là 29.

### Kiểm chứng

- Checkpoint 23 test trước vẫn VERIFIED.
- Authentication source mới là PENDING cho đến khi người dùng chạy Maven.

### Next exact action

Pull và chạy mvn test; sau 29/29 mới triển khai JdbcAccountRepository cho database nsocry.


## 2026-08-18 — Windows xác minh authentication

### VERIFIED

- Tests run: 29.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- AuthenticationService, fake AccountRepository và Pbkdf2PasswordHasher đều qua test.

### Next exact action

Viết JdbcAccountRepository dựa trên DataSource, prepared statement và lỗi persistence đã làm sạch; chưa ghép credential database vào source.


## 2026-08-18 — JdbcAccountRepository

### Đã triển khai

- Adapter AccountRepository dựa trên javax.sql.DataSource.
- Prepared statement cho username và mọi update.
- Mapping status 0/1/2 sang ACTIVE/LOCKED/BANNED; dữ liệu ngoài contract bị từ chối.
- try-with-resources cho Connection, PreparedStatement và ResultSet.
- AccountPersistenceException với message đã làm sạch.
- Ba test dùng JDBC proxy, không kết nối database reference hoặc MariaDB thật.

### Kiểm chứng

- Checkpoint authentication trước đó VERIFIED 29/29.
- Persistence source mới PENDING; mục tiêu 32 test.

### Next exact action

Pull và chạy mvn test; sau 32/32 mới ghép MariaDB DataSource/configuration vào bootstrap.


## 2026-08-18 — Ghép MariaDB vào bootstrap

### Đã triển khai

- MariaDB Connector/J 3.5.10.
- DatabaseConfiguration/Loader với environment override và password redaction.
- MariaDbDataSourceFactory dùng driver chính thức.
- Composition root tạo repository, password hasher và AuthenticationService trước khi start TCP.
- File properties mẫu chỉ nêu tên NSOCRY_DB_PASSWORD, không chứa secret.
- Bốn test cấu hình mới; mục tiêu suite 36.

### Kiểm chứng

- JdbcAccountRepository trước đó VERIFIED 32/32.
- Source/pom mới PENDING cho đến khi người dùng chạy Maven.
- Unit test không mở kết nối database thật.

### Next exact action

Pull và chạy mvn test; sau 36/36 mới tạo công cụ bootstrap account và kiểm tra migration nsocry cục bộ.


## 2026-08-18 — Windows xác minh MariaDB composition

### VERIFIED

- Tests run: 36.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- Connector/J dependency, database configuration và bootstrap authentication đều compile/test thành công.

### Next exact action

Viết account bootstrap command an toàn và unit test repository insert trước khi yêu cầu chạy migration/database thật.


## 2026-08-18 — Công cụ tạo administrator đầu tiên

### Đã triển khai

- Domain role và provisioning port tách khỏi JDBC.
- FirstAdministratorService kiểm tra one-time guard, username, password và tạo hash.
- JdbcAccountProvisioningRepository đếm/insert bằng prepared statement và lấy generated key.
- Command tương tác không nhận password qua args, yêu cầu nhập hai lần và xóa password trong mọi nhánh.
- Javadoc và tài liệu vận hành tiếng Việt.
- Bốn test service; mục tiêu suite 40.

### Kiểm chứng

- MariaDB composition trước đó VERIFIED 36/36.
- Provisioning source mới PENDING.
- Chưa chạy migration hoặc kết nối database thật.

### Next exact action

Pull và chạy mvn test; sau 40/40 mới đóng gói lệnh vận hành và hướng dẫn tạo database nsocry cục bộ.


## 2026-08-18 — Windows xác minh first administrator

### VERIFIED

- Tests run: 40.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- Provisioning validation, one-time guard và password clearing đều đạt.

### Next exact action

Đóng gói runnable JAR đầy đủ dependency và chuẩn hóa lệnh server/create-admin trước khi thao tác MariaDB thật.


## 2026-08-18 — Executable JAR và launcher

### Đã triển khai

- NsocryLauncher phân luồng server/create-admin/help.
- Không args chỉ in help; command sai/quá nhiều args bị từ chối.
- Maven Shade Plugin 3.6.2 tạo uber-JAR chứa runtime dependencies.
- Loại signature files không còn hợp lệ sau khi gộp JAR.
- Manifest trỏ đến NsocryLauncher.
- Bốn parser test; mục tiêu suite 44.

### Kiểm chứng

- Provisioning trước đó VERIFIED 40/40.
- Build/package mới PENDING.

### Next exact action

Chạy mvn package và java -jar target/nsocry-server-0.1.0-SNAPSHOT.jar help. Chưa chạy server/create-admin trước khi chuẩn bị database.


## 2026-08-18 — Windows xác minh executable JAR

### VERIFIED

- Tests run: 44.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- mvn package BUILD SUCCESS trong 3.874 giây.
- Executable JAR được tạo.
- Lệnh help chạy và in đúng ba command.
- Shade warning chỉ liên quan MANIFEST trùng khi gộp dependency; launcher hoạt động chứng minh manifest cuối đúng.

### Next exact action

Chuẩn bị database nsocry cục bộ và administrator đầu tiên bằng migration/tool mới; không chạy hoặc sửa database reference.

- Bổ sung .gitignore cho config/nsocry.properties trước khi hướng dẫn nhập database secret; không thay đổi source/build.
