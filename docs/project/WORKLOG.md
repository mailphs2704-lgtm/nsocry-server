Warning: truncated output (original token count: 20179)
Total output lines: 1863

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


## 2026-08-18 — Database V001 VERIFIED và yêu cầu lệnh bài Admin

### VERIFIED

- Database nsocry tồn tại.
- Bảng accounts được tạo đúng V001 với primary key, unique username, password_hash, status, activated, role, lockout và timestamps.

### Yêu cầu mới

- Người dùng đề xuất run.bat có giao diện lệnh bài Admin trước khi có website.
- Các chức năng định hướng: hệ thống, mở sự kiện, cấp vật phẩm, thông báo, giftcode và quản trị game.
- Đã tạo ADR-0008 cùng administration roadmap.
- Chọn kiến trúc batch launcher mỏng + Java admin console + application services + audit.

### Next exact action

Tạo database user/application administrator đầu tiên, rồi code Admin Console foundation; gameplay command chờ service nền.


## 2026-08-18 — Điều chỉnh Admin thành lộ trình song hành

### Quyết định của người dùng

- Dự án chính làm đến đâu thì chức năng Admin liên quan hoàn thiện đến đó.
- Bỏ qua việc xây toàn bộ Admin Console ở thời điểm hiện tại.
- Tiếp tục luồng chính đang làm.

### Đã cập nhật

- ADR-0008 và console roadmap không còn coi Admin là phase độc lập.
- Core service là nguồn sự thật; command admin chỉ là adapter theo sau.
- Next exact action quay lại protocol character sau login.

## 2026-08-18 — Codec danh sách/chọn/tạo nhân vật

### Phân tích và quyết định

- Chỉ phân tích tĩnh reference, không chạy hoặc test NSOKISS.
- Xác minh server phải đi qua `UPDATE_VERSION`, chờ `CLIENT_OK`, rồi mới gửi danh sách nhân vật.
- Xác minh bố cục wire của danh sách nhân vật, chọn theo tên và yêu cầu tạo mới.
- Không sao chép quy tắc tối đa nhân vật hoặc regex tên vì reference có hành vi không nhất quán và đây là business rule của NSOCry.
- Không sao chép hành vi log username/password dạng rõ của reference.

### Đã triển khai

- Thêm package `com.nsocry.character` với Javadoc tiếng Việt.
- Thêm immutable character summary và create request.
- Thêm codec encode danh sách, decode chọn và decode tạo nhân vật.
- Thêm kiểm tra envelope/command/byte dư và giới hạn count một byte.
- Thêm 6 unit test cùng byte vector cố định.
- Thêm `docs/protocol/post-login-character-v1.md` mô tả byte layout, độ tin cậy và phần UNKNOWN.

### Kiểm chứng

- Mốc gần nhất: executable JAR VERIFIED 44/44 trên Windows.
- Source mới: PENDING vì Work environment không có Maven/JDK compiler.
- Suite mục tiêu sau pull: 50 test.

### Next exact action

Pull và chạy `mvn test`. Nếu 50/50 thành công, đánh dấu codec VERIFIED rồi phân tích client V7 để giải mã chính xác blob `UPDATE_VERSION` trước khi nối runtime.

## 2026-08-18 — Xác minh 50 test và giải mã UPDATE_VERSION

### VERIFIED

- Người dùng đã chạy suite: 50 test, không failure/error/skip.
- Codec danh sách, chọn và tạo nhân vật được nâng từ PENDING thành VERIFIED.

### Phân tích tĩnh client V7

- Dùng bytecode JAR, không chạy client hoặc NSOKISS.
- Xác minh payload `UPDATE_VERSION` mở đầu bằng data/map/skill/item version.
- Xác minh bốn request cập nhật rỗng: DATA `-122`, MAP `-121`, SKILL `-120`, ITEM `-119`.
- Xác minh client chỉ gửi `CLIENT_OK` sau khi bốn version khớp.
- Dữ liệu ngoại hình nằm nối tiếp bốn byte version và được đọc khi data version đã khớp.

### Đã triển khai

- Thêm `ClientVersionManifest`, `ClientDataSet` và `PostLoginVersionPayloadCodec`.
- Codec không tự tạo asset giả; chỉ mã hóa contract đã có bằng chứng.
- Thêm 5 unit test; suite mục tiêu 55.
- Cập nhật tài liệu protocol bằng tiếng Việt.

### Next exact action

Pull và chạy `mvn test`. Nếu 55/55 thành công, inventory nguồn tạo payload data/map/skill/item và appearance để thiết kế asset pipeline NSOCry.

## 2026-08-18 — Tạm dừng tại checkpoint 55/55

### VERIFIED

- Người dùng xác nhận 55 test đều thành công.
- Version negotiation codec và bốn data request đã được kiểm chứng trên máy Windows.

### Điểm dừng an toàn

- Không có thay đổi source chưa kiểm chứng đang chờ trên máy người dùng.
- Chưa nối version negotiation vào runtime và chưa tạo asset giả.
- Người dùng tạm dừng để khởi động lại máy.

### Next exact action

Kiểm kê nguồn tạo data/map/skill/item/appearance và viết tài liệu byte layout trước khi thiết kế asset pipeline NSOCry.

## 2026-08-18 — Asset pipeline foundation

### Inventory

- Appearance lấy từ metadata head/body/leg/mount.
- DATA tổng hợp graphics paint, task-map, EXP, upgrade tables và effect template.
- MAP gồm map name, NPC/menu và mob template; client 217 dùng `short` cho mob count.
- SKILL gồm option, class, template, level và option từng level.
- ITEM gồm item option và item template.

### Thiết kế và triển khai

- Chọn snapshot bất biến để session không truy SQL hoặc build byte.
- Snapshot kiểm tra đủ bốn payload và version nhất quán.
- Provider port yêu cầu thay snapshot nguyên tử khi reload.
- Protocol codec có thể tạo response theo data request đã giải mã.
- Thêm tài liệu pipeline tiếng Việt và 5 test; suite mục tiêu 60.

### Kiểm chứng

- Checkpoint trước: 55/55 VERIFIED.
- Source mới: PENDING Maven verification vì người dùng đang dùng điện thoại.

### Next exact action

Triển khai ITEM asset codec đối xứng trước, sau đó mới đến SKILL, MAP, DATA và appearance.

## 2026-08-18 — ITEM asset codec

### Đã triển khai

- Read model riêng cho item option và item template phía client.
- ITEM encoder đúng thứ tự version/options/items.
- Validator parser đối xứng, từ chối byte dư.
- Giới hạn count đúng kiểu unsigned byte/unsigned short.
- Năm test mới; suite tích lũy mục tiêu 65.

### Trạng thái

- Code và tài liệu: IMPLEMENTED.
- Maven verification: PENDING đến khi người dùng trở lại máy.
- Không thay đổi runtime/session/database.

### Next exact action

Đối chiếu SKILL parser client với reference encoder, ghi layout rồi mới triển khai SKILL codec.

## 2026-08-18 — SKILL asset codec

### Đã xác minh tĩnh

- Layout class → template → level → option khớp giữa encoder và client parser.
- Option/template/level/level-option count là signed byte.
- Class count là unsigned byte.
- ITEM template count thực tế được client đọc signed short; giới hạn codec đã sửa về 32767.

### Đã triển khai

- Sáu read model SKILL bất biến.
- Encoder/parser validator đối xứng.
- Bốn test mới; suite tích lũy mục tiêu 69.

### Trạng thái

- Source: IMPLEMENTED.
- Maven verification: PENDING khi người dùng trở lại máy.

### Next exact action

Đối chiếu MAP parser V7 và triển khai MAP asset codec sau khi chốt mọi count.

## 2026-08-18 — MAP asset codec

### Đã triển khai

- `MapAssetBundle`, `NpcTemplateAsset`, `MobTemplateAsset`.
- Menu NPC được sao chép sâu để giữ bất biến.
- MAP encoder/parser đối xứng theo client 217.
- Bốn test mới; suite tích lũy mục tiêu 73.

### Ranh giới

- Chỉ template tĩnh nằm trong asset.
- Không chứa zone/player/mob instance/combat state.
- Không thay đổi runtime hoặc database schema.

### Next exact action

Phân tích DATA payload theo từng block và xác định phần nào là client graphics, progression hay effect metadata.

## 2026-08-18 — DATA container codec

### Đã triển khai

- Enum năm graphics block và mười progression table.
- Task route read model.
- Data asset bundle sao chép sâu.
- Container encoder/parser cho length-prefix, task route, EXP, progression và effect tail.
- Bốn test mới; suite tích lũy mục tiêu 77.

### Kiểm chứng trong Work

- Main source assets + protocol compatibility compile thành công bằng javac 17.
- Maven/JUnit vẫn PENDING do không có Maven/JUnit runtime trong Work.

### Next exact action

Đối chiếu appearance parser với version blob và triển khai container head/body/leg/mount.

## 2026-08-18 — Appearance asset codec và điểm dừng kiểm chứng

### Đã triển khai

- Read model part layer, head/body, leg và mount.
- Appearance encoder/parser đối xứng.
- Validation count ba biến thể, descriptor và đúng sáu mount frame group.
- Bốn test mới; suite tích lũy mục tiêu 81.

### Kiểm chứng Work

- Main source assets + protocol compatibility compile thành công với javac 17.
- Không có legacy namespace trong source/test mới.

### Điểm dừng

- Checkpoint VERIFIED gần nhất: 55/55.
- Các checkpoint asset mới có 26 test đang PENDING Maven/JUnit.
- Cần người dùng pull và chạy `mvn test`; chỉ sau 81/81 mới triển khai database-backed builders/runtime wiring.

## 2026-08-18 — Xác minh 81 test và snapshot assembly

### VERIFIED

- Người dùng chạy 81 test: 0 failure, 0 error, 0 skipped.
- Asset snapshot, ITEM, SKILL, MAP, DATA container và appearance codec đều VERIFIED.

### Đã triển khai tiếp

- `ClientAssetSnapshotAssembler` tạo manifest và đủ năm payload theo kiểu all-or-nothing.
- `AtomicClientAssetSnapshotProvider` thay snapshot nguyên tử.
- Bốn test mới; suite mục tiêu 85.

### Next exact action

Xác minh 85 test rồi tạo source ports/builder orchestration, không nối thẳng session với JDBC.

## 2026-08-18 — Xác minh 85 test và asset source orchestration

### VERIFIED

- Người dùng chạy 85 test: 0 failure, 0 error, 0 skipped.
- Snapshot assembler và atomic provider đã được kiểm chứng trên máy Windows.

### Đã triển khai tiếp

- Năm source port độc lập cho DATA/MAP/SKILL/ITEM/appearance.
- Publisher port tách thao tác publish khỏi nguồn lưu trữ.
- Build service đọc đủ năm nguồn, assemble rồi mới publish.
- Lỗi nguồn hoặc kết quả null giữ nguyên snapshot hiện hành.
- Bốn test mới; suite mục tiêu 89.

### Next exact action

Xác minh 89 test rồi thiết kế JDBC adapter theo schema NSOCry, bắt đầu từ ITEM; không truy vấn database reference và không nối JDBC vào session.

## 2026-08-18 — Xác minh 89 test và JDBC ITEM source

### VERIFIED

- Người dùng chạy 89 test: 0 failure, 0 error, 0 skipped.
- Năm source port và build orchestration đều được kiểm chứng.

### Đã triển khai tiếp

- Migration V002 tạo hai read-model table ITEM mới, không chứa inventory gameplay.
- `JdbcItemAssetSource` đọc option/template bằng transaction read-only repeatable-read.
- Adapter bắt buộc ID liên tục từ 0 vì client dùng vị trí danh sách làm ID.
- Mọi trường số được kiểm tra trước khi thu hẹp sang byte/short.
- Lỗi JDBC hoặc dữ liệu lệch contract được bọc thành source error, làm snapshot cũ được giữ lại.
- Ba unit test bằng JDBC proxy; không mở database thật. Suite mục tiêu 92.

### Next exact action

Xác minh 92 test, sau đó tạo validator/import plan cho seed ITEM; chưa chạy V002 hoặc nhập dữ liệu khi chưa có checkpoint backup và count/checksum.

## 2026-08-18 — Xác minh 92 test và ITEM seed validator

### VERIFIED

- Người dùng chạy 92 test: 0 failure, 0 error, 0 skipped.
- JDBC ITEM source, transaction và mapping: VERIFIED.

### Đã triển khai tiếp

- Manifest khóa version, option count, item count và payload SHA-256.
- Validator encode bằng codec thật, parse round-trip và so checksum.
- Kết quả validation chỉ chứa metadata vận hành, không lộ payload.
- Viết kế hoạch import có backup, staging, transaction, rollback và kiểm tra sau load.
- Không chạy V002 và không ghi database thật.
- Bốn test mới; suite mục tiêu 96.

### Next exact action

Xác minh 96 test rồi tạo công cụ sinh seed ITEM có đầu ra xác định từ dữ liệu tĩnh được phê duyệt; chưa thực hiện import vào MariaDB.

## 2026-08-18 — Xác minh 96 test và generator ITEM seed artifact

### VERIFIED

- Người dùng chạy 96 test: 0 failure, 0 error, 0 skipped.
- ITEM seed manifest, codec round-trip và checksum validator: VERIFIED.

### Đã triển khai tiếp

- Generator tạo payload nhị phân trực tiếp từ ITEM codec, không sinh SQL động.
- Manifest key=value có format version, count, length và SHA-256; newline cố định LF.
- Artifact sao chép phòng vệ payload và tự validation trước khi được trả về.
- Importer tương lai sẽ decode artifact và dùng prepared statement.
- Bốn test mới; suit…179 tokens truncated…ng transaction SERIALIZABLE và prepared batch; lỗi rollback toàn bộ.
- Importer không chạy migration, không tăng version và không publish snapshot.
- Năm test mới; suite mục tiêu 105.

### Next exact action

Xác minh 105 test rồi tạo command xuất artifact ra file an toàn và dry-run import report; chưa chạy V002/import thật.

## 2026-08-18 — Xác minh 105 test và ITEM seed archive dry-run

### VERIFIED

- Người dùng chạy lại 105 test: 0 failure, 0 error, 0 skipped.
- Manifest parser và transactional JDBC importer: VERIFIED.

### Đã triển khai tiếp

- Archive service đóng gói payload/manifest trong một ZIP và xuất bằng atomic move.
- Không ghi đè archive đã tồn tại; file tạm được dọn trong finally.
- Reader chỉ nhận đúng hai entry, chống entry lạ/trùng và giới hạn kích thước giải nén.
- Thêm launcher command `item-seed-dry-run`, chỉ in metadata và không mở database.
- Bốn archive test và một launcher parser test; suite mục tiêu 110.

### Next exact action

Xác minh 110 test rồi thiết kế bộ chuyển đổi dữ liệu ITEM tham chiếu thành read model NSOCry có báo cáo khác biệt; chưa import database.

## 2026-08-18 — Xác minh 110 test và ITEM reference converter

### VERIFIED

- Người dùng chạy 110 test: 0 failure, 0 error, 0 skipped.
- ITEM seed archive export/dry-run và launcher command: VERIFIED.

### Đã triển khai tiếp

- Row model tách biệt cho option/template tham chiếu, không mang tên database cũ.
- Converter offline sắp theo ID và từ chối gap, duplicate, overflow, boolean ngoài 0/1.
- Report chứa count/range cùng số fashion value không nằm trên ITEM wire.
- Fashion được đánh dấu để xử lý ở appearance/gameplay checkpoint, không âm thầm mất.
- Năm test mới; suite mục tiêu 115.

### Next exact action

Xác minh 115 test rồi tạo parser nguồn xác định cho hai bảng ITEM trong dump được cung cấp và sinh conversion report thực tế; chưa ghi database.

## 2026-08-18 — Xác minh 115 test và parse ITEM dump thực tế

### VERIFIED

- Người dùng chạy 115 test: 0 failure, 0 error, 0 skipped.
- ITEM reference converter và difference report: VERIFIED.

### Đã triển khai/chạy offline

- Parser giới hạn đúng hai INSERT `item_option`/`item`, hỗ trợ quote, backslash và Unicode.
- Năm parser test mới; suite mục tiêu 120.
- Main source compile thành công Java 17.
- Chạy read-only trên dump được cung cấp: 161 option, 1213 item, 431 upgradable, 79 fashion difference.
- Candidate payload: 66811 byte; SHA-256 `abb320fb8a940fc28c49c6d0c5b84e09e83d28248130884881845b9dd5bea6f8`.
- Không kết nối MariaDB, không chạy V002 và không ghi seed.

### Next exact action

Xác minh 120 test rồi thêm launcher command chuyển dump thành candidate archive và kiểm tra checksum tái lập trên Windows.

## 2026-08-18 — Xác minh 120 test và command tạo ITEM candidate

### VERIFIED

- Người dùng chạy 120 test: 0 failure, 0 error, 0 skipped.
- Parser dump và candidate report thực tế: VERIFIED.

### Đã triển khai tiếp

- Launcher command `item-seed-convert <dump-path>` chạy toàn bộ pipeline offline.
- Dump phải là regular UTF-8 file tối đa 64 MiB.
- Output cạnh source, tên xác định và không ghi đè file đã có.
- Báo cáo chỉ in count/difference/length/checksum và `databaseChanged=false`.
- Bốn command test và một launcher parser test; suite mục tiêu 125.

### Next exact action

Xác minh 125 test rồi chạy convert + dry-run trên Windows để chứng minh checksum candidate tái lập; chưa chạy V002/import.

## 2026-08-18 — Candidate cross-platform và V002 schema preflight

### VERIFIED

- Người dùng chạy 125 test: 0 failure, 0 error, 0 skipped.
- Windows convert + dry-run khớp Work: 161/1213, 66811 byte và cùng SHA-256.
- Candidate ITEM: VERIFIED_CROSS_PLATFORM.

### Đã triển khai tiếp

- Model/report cho metadata column và V002 schema readiness.
- Contract yêu cầu đúng 12 cột, data type, unsigned và NOT NULL.
- JDBC inspector chỉ đọc information_schema và đặt connection read-only.
- Năm test mới; suite mục tiêu 130.
- Chưa chạy V002, backup hoặc importer.

### Next exact action

Xác minh 130 test rồi thêm launcher preflight command dùng config database hiện có; command chỉ đọc schema và không chạy migration.

## 2026-08-18 — Xác minh 130 test và launcher schema preflight

### VERIFIED

- Người dùng chạy 130 test: 0 failure, 0 error, 0 skipped.
- V002 schema contract và JDBC information_schema inspector: VERIFIED.

### Đã triển khai tiếp

- Launcher command `item-schema-preflight [config-path]`.
- Dùng config mặc định `config/nsocry.properties` hoặc một path chỉ định.
- In READY/NOT_READY, từng difference và `databaseChanged=false`.
- NOT_READY làm command kết thúc lỗi để automation không hiểu nhầm là sẵn sàng.
- Ba report test và hai launcher parser test; suite mục tiêu 135.

### Next exact action

Xác minh 135 test rồi package và chạy preflight trên database NSOCry hiện tại. Kỳ vọng NOT_READY vì V002 chưa chạy; không tự động migration.

## 2026-08-18 — Xác minh 135 test, backup và V002 READY

### VERIFIED

- Người dùng chạy 135 test: 0 failure, 0 error, 0 skipped.
- Backup trước V002: 2960 byte; SHA-256 `021575bfed0d4a34e751c68df1b489e4d8aefeef0e51c4a4a7b7fa00716c1348`.
- Preflight trước migration báo đúng 12 cột thiếu và không đổi database.
- V002 đã chạy; preflight sau migration: READY.

### Đã triển khai tiếp

- Archive service trả validated content bằng defensive copy.
- Interactive import command yêu cầu archive valid, schema READY và nhập đủ SHA-256.
- Sai/hủy xác nhận dừng trước transaction import.
- Import thành công không tự publish runtime snapshot.
- Năm test mới; suite mục tiêu 140.

### Next exact action

Xác minh 140 test rồi package; chỉ sau đó mới xin xác nhận cuối và chạy interactive import candidate đã VERIFIED_CROSS_PLATFORM.

## 2026-08-18 — Xác minh 140 test và import ITEM local

### VERIFIED

- Người dùng chạy 140 test: 0 failure, 0 error, 0 skipped.
- Người dùng xác nhận rõ `ĐỒNG Ý IMPORT ITEM` sau backup/schema/count preflight.
- Interactive import thành công: 161 option, 1213 item, đúng candidate SHA-256.
- SQL post-check: option ID 0–160, item ID 0–1212, 431 upgradable.
- Runtime snapshot chưa publish.

### Đã triển khai tiếp

- Command `item-seed-db-verify` load lại JDBC source và dựng payload end-to-end.
- Schema phải READY và payload phải khớp manifest candidate.
- Command chỉ đọc database, không publish runtime.
- Bốn verification test và một launcher parser test; suite mục tiêu 145.

### Next exact action

Xác minh 145 test rồi package và chạy database payload verify trên Windows. Chỉ sau checksum match mới nối ITEM source vào snapshot bootstrap.

## 2026-08-18 — ITEM end-to-end VERIFIED và SKILL foundation

### VERIFIED

- Người dùng chạy 145 test: 0 failure, 0 error, 0 skipped.
- Database ITEM load → encode → validate khớp candidate 66811 byte/SHA-256.
- ITEM pipeline: VERIFIED_END_TO_END.

### Quyết định

- Không publish snapshot bán phần khi DATA/MAP/SKILL/appearance chưa có source hoàn chỉnh.
- Giữ JdbcItemAssetSource sẵn sàng và tiếp tục SKILL theo cùng pipeline an toàn.

### Đã triển khai tiếp

- Migration V003 draft với năm bảng SKILL chuẩn hóa, không JSON runtime.
- Structural validator kiểm tra nested count, duplicate ID và option reference.
- Năm test mới; suite mục tiêu 150.
- V003 chưa chạy và chưa import SKILL.

### Next exact action

Xác minh 150 test rồi xây parser/converter SKILL từ dump và tạo report thực tế; chưa chạy V003.

## 2026-08-18 — Xác minh 150 test và SKILL dump inventory

### VERIFIED

- Người dùng chạy 150 test: 0 failure, 0 error, 0 skipped.
- SKILL structural validator và V003 draft foundation: VERIFIED.

### Inventory offline

- 7 class, 72 option template, 91 skill template, 967 level.
- 3883 level option; tối đa 6 option trong một level.
- Template ID 0–90, level ID 0–966, reference đều hợp lệ.
- Phát hiện 4 giá trị 128–255 trong field dùng raw byte; không loại hoặc truncate.
- V003 point đổi thành SMALLINT UNSIGNED + CHECK <=255 để bảo toàn dữ liệu trước converter.
- Năm parser test mới; suite mục tiêu 155.

### Next exact action

Xác minh 155 test rồi tạo converter SKILL và báo cáo chính xác bốn raw-byte overflow; chưa chạy V003.

## 2026-08-18 — Xác minh 155 test và định danh raw-byte SKILL

### VERIFIED

- Người dùng chạy 155 test: 0 failure, 0 error, 0 skipped.
- SKILL dump inventory foundation: VERIFIED.

### Raw-byte differences

- `level[957].point=150`.
- `level[958].point=150`.
- `level[962].point=140`.
- `level[966].point=140`.
- Không có difference trong template, maxFight hoặc requiredLevel.

### Đã triển khai tiếp

- Inventory report mang danh sách difference immutable với entity/ID/field/value.
- Parser từ chối giá trị ngoài 0–255 và bảo toàn giá trị 128–255.
- Năm test mới bao phủ nhận diện, giới hạn raw-byte và defensive copy; suite mục tiêu 160.
- Chưa chạy V003, chưa import SKILL và chưa publish runtime snapshot.

### Next exact action

Xác minh 160 test rồi xây full SKILL converter/read-model candidate và fixture wire tương thích.

## 2026-08-18 — Xác minh 160 test và full SKILL converter candidate

### VERIFIED

- Người dùng chạy 160 test: 0 failure, 0 error, 0 skipped.
- Raw-byte difference report: VERIFIED.

### Đã triển khai tiếp

- Dựng toàn bộ class/template/level/option tree từ row chuẩn hóa trong dump.
- Cột cache `skillTemplates` không được dùng làm nguồn authoritative.
- Raw-byte 128–255 giữ nguyên bit pattern khi đi qua Java signed byte và codec.
- Candidate dump thật: 42402 byte; SHA-256 `4f13faa5d95653ff9d04945d0fe8a5146030526383944d22de1786c497155cf5`.
- Structural validation và encode/decode round-trip thành công.
- Năm test mới đang PENDING; suite mục tiêu 165.
- V003/database/runtime snapshot không thay đổi.

### Next exact action

Xác minh 165 test rồi thêm command đóng gói convert/dry-run SKILL và kiểm tra checksum chéo Windows.

## 2026-08-18 — Xác minh 165 và khóa checkpoint bàn giao

### VERIFIED

- Người dùng chạy 165 test: 0 failure, 0 error, 0 skipped.
- Full SKILL converter, structural validation và codec round-trip: VERIFIED.

### Trạng thái an toàn

- Không chạy V003.
- Không import SKILL.
- Không thay đổi database hoặc runtime snapshot.
- Candidate v26 vẫn là 42402 byte, SHA-256 `4f13faa5d95653ff9d04945d0fe8a5146030526383944d22de1786c497155cf5`.

### Next exact action

Tạo command `skill-seed-convert` và `skill-seed-dry-run` theo mẫu pipeline ITEM,
thêm năm test để suite mục tiêu 170. Chỉ sau khi người dùng xác nhận 170/170 mới
chạy hai command trên Windows; chưa được chạy V003 hoặc import dữ liệu.

## 2026-08-25 — Tiếp tục từ 165 và dựng SKILL seed commands

### Đã đối chiếu

- Nhánh `agent/document-nsokiss-runtime` vẫn ở checkpoint 165, không có code AI lạ phía sau.
- Full SKILL candidate đầu vào giữ nguyên count/payload/checksum đã VERIFIED.

### Đã triển khai

- Artifact và manifest SKILL xác định, payload được defensive copy.
- Validator kiểm tra structure/count/version/payload length/SHA-256/raw-byte differences.
- Archive atomic, không overwrite, đúng hai entry và có hard limit giải nén.
- Command `skill-seed-convert` và `skill-seed-dry-run` đã nối launcher.
- Offline dump thật convert/dry-run cùng trả 42402 byte và SHA-256
  `4f13faa5d95653ff9d04945d0fe8a5146030526383944d22de1786c497155cf5`.
- Năm test mới; suite mục tiêu 170.

### Trạng thái an toàn

- Không chạy V003, không JDBC, không import và không publish runtime snapshot.

### Next exact action

Người dùng pull, chạy 170 test rồi package và đối chiếu hai command trên Windows.

## 2026-08-25 — Windows xác minh 170/170

### VERIFIED

- HEAD local/remote: `ba503279`.
- Working tree clean sau pull.
- Maven compile 143 source file và 44 test source file thành công.
- 170 test: 0 failure, 0 error, 0 skipped; BUILD SUCCESS.
- Lỗi unlink Git pack cũ không ảnh hưởng source hoặc lịch sử nhánh.

### Next exact action

Package JAR và chạy SKILL convert/dry-run chéo Windows; so count, raw-byte difference,
payload length và SHA-256. Không chạy V003 hoặc import.

## 2026-08-25 — SKILL VERIFIED_CROSS_PLATFORM và schema foundation

### VERIFIED_CROSS_PLATFORM

- Windows convert/dry-run khớp Work về toàn bộ count và bốn raw-byte difference.
- Payload 42402 byte; SHA-256 `4f13faa5d95653ff9d04945d0fe8a5146030526383944d22de1786c497155cf5`.
- Cả hai phía báo `databaseChanged=false`.

### Đã triển khai tiếp

- Contract metadata cho đúng 26 cột V003.
- Read-only JDBC inspector cho năm bảng SKILL.
- Difference report bao phủ cột thiếu/thừa/trùng/type/unsigned/nullability.
- Năm test mới; suite mục tiêu 175.
- Chưa thêm command launcher, chưa chạy V003 và chưa import.

### Next exact action

Người dùng xác minh 175 test rồi thêm `skill-schema-preflight` command chỉ đọc.

## 2026-08-25 — Xác minh 175 và thêm SKILL schema command

### VERIFIED

- Người dùng xác nhận 175 test: 0 failure, 0 error, 0 skipped.
- V003 schema contract và JDBC read-only inspector: VERIFIED.

### Đã triển khai

- Launcher nhận `skill-schema-preflight [config-path]`.
- READY/NOT_READY report không lộ credential và luôn báo database không đổi.
- NOT_READY trả lỗi rõ ràng.
- Ba command test và hai launcher test; suite mục tiêu 180.
- Tiến độ ước tính đến gameplay cơ bản: 15%.

### Next exact action

Người dùng xác minh 180 test rồi chạy preflight chỉ đọc trên database hiện tại;
chưa chạy V003 hoặc import.

## 2026-08-25 — Xác minh 180 và chuyển sang database preflight

- Người dùng xác nhận 180 test sạch.
- SKILL schema command: VERIFIED.
- Next: package và chạy read-only preflight trên database hiện tại.
- Không chạy V003/import; tiến độ gameplay cơ bản: 15%.

## 2026-08-25 — Database SKILL preflight NOT_READY đúng kỳ vọng

- Lần đầu không kết nối vì MariaDB chưa chạy; không phải lỗi source.
- Sau khi bật MariaDB, inspector báo đủ 26 cột V003 còn thiếu.
- `databaseChanged=false`; NOT_READY exception đúng contract automation.
- Chưa chạy migration/import.
- Next: backup database và ghi checksum trước khi xin xác nhận V003.

## 2026-08-25 — Backup trước V003

- File: `backup/nsocry-before-v003-20260825-114548.sql`.
- Kích thước: 95731 byte.
- SHA-256: `a20218e737aeeb5814dd727637e186deed145ca901cf7572a475b0ec079e720d`.
- Backup hoàn tất trước migration; V003 chưa chạy.
- Next: chỉ chạy V003 sau xác nhận rõ ràng của người dùng.

## 2026-08-25 — V003 READY và transactional SKILL importer

### VERIFIED

- Người dùng xác nhận chạy V003 sau backup 95731 byte và checksum đã khóa.
- Post-migration `skill-schema-preflight`: READY.
- Năm bảng SKILL mới còn rỗng; chưa import.

### Đã triển khai tiếp

- Importer validate payload/manifest trước khi mở JDBC connection.
- SERIALIZABLE transaction, delete/insert theo đúng foreign-key order.
- Prepared batches cho option/class/template/level/level-option.
- Point raw-byte được mở rộng unsigned khi ghi SMALLINT UNSIGNED.
- Failure hoặc batch count sai rollback toàn transaction.
- Năm test mới; suite mục tiêu 185.
- Tiến độ gameplay cơ bản: 16%.

### Next exact action

Người dùng xác minh 185 test rồi xây interactive import guard; chưa tự import SKILL.

## 2026-08-25 — Xác minh 185 và interactive SKILL import guard

### VERIFIED

- Người dùng xác nhận 185 test sạch.
- Transactional SKILL importer: VERIFIED.

### Đã triển khai

- Validated archive giữ payload bất biến bằng defensive copy.
- Command `skill-seed-import` kiểm tra archive và V003 trước confirmation.
- Full SHA-256 constant-time confirmation là gate cuối trước transaction.
- Sai/hủy confirmation không import; import thành công không publish snapshot.
- Năm test mới; suite mục tiêu 190.
- Tiến độ gameplay cơ bản: 16%.

### Next exact action

Người dùng xác minh 190 test rồi mới xin xác nhận cuối để import SKILL candidate.

## 2026-08-25 — Xác minh 190 và khóa candidate import SKILL

### VERIFIED

- Người dùng xác nhận 190 test sạch.
- Interactive import guard, archive defensive copy và launcher routing: VERIFIED.
- Schema V003 hiện READY; backup trước migration đã có kích thước/checksum đầy đủ.

### Candidate đã khóa

- Version 26.
- 72 option, 7 class, 91 template, 967 level, 3883 level-option.
- Bốn raw-byte point: 957=150, 958=150, 962=140, 966=140.
- Payload 42402 byte.
- SHA-256 `4f13faa5d95653ff9d04945d0fe8a5146030526383944d22de1786c497155cf5`.

### Trạng thái an toàn

- SKILL chưa import; năm bảng V003 đang rỗng.
- Runtime snapshot chưa publish.
- Sai/hủy checksum confirmation dừng trước transaction.
- Tiến độ gameplay cơ bản: 16%.

### Quy tắc tài liệu được tái xác nhận

Sau mỗi bước phải cập nhật tài liệu chi tiết bằng tiếng Việt, gồm trạng thái kiểm thử,
thay đổi kỹ thuật, database/checksum/count, rủi ro, tiến độ và bước tiếp theo.

### Next exact action

Chờ người dùng xác nhận rõ `ĐỒNG Ý IMPORT SKILL`, sau đó mới chạy command tương tác.

## 2026-08-25 — Import SKILL local thành công

### Xác nhận và kết quả

- Người dùng cấp xác nhận rõ `ĐỒNG Ý IMPORT SKILL`.
- Console xác minh đúng SHA-256 candidate trước transaction.
- Importer commit 72 option, 7 class, 91 template, 967 level và 3883 level-option.
- Payload 42402 byte; SHA-256
  `4f13faa5d95653ff9d04945d0fe8a5146030526383944d22de1786c497155cf5`.
- Bốn raw point 150/150/140/140 được giữ trong report.
- Runtime snapshot chưa publish.

### Trạng thái và tiến độ

- SKILL local import: SUCCESS nhưng chưa VERIFIED_END_TO_END từ JDBC payload.
- Tiến độ gameplay cơ bản: 17%.

### Next exact action

Post-check SQL count/ID/raw point, sau đó xây JDBC source và database payload verifier.

## 2026-08-25 — SKILL post-check VERIFIED và JDBC payload verifier

### Post-check VERIFIED

- Count và miền ID của cả năm bảng khớp candidate.
- Bốn raw point trong database giữ đúng 150, 150, 140, 140.

### Đã triển khai

- JDBC source dùng read-only repeatable-read transaction cho năm bảng.
- Kiểm tra ID liên tục, sort order theo parent, foreign reference và numeric range.
- Tái dựng đúng class → template → level → option tree.
- Command `skill-seed-db-verify` so bundle database với candidate manifest end-to-end.
- Report gồm count, raw-byte differences, payload length, SHA-256 và trạng thái không mutation.
- Năm test mới; suite mục tiêu 195.
- Tiến độ gameplay cơ bản: 17%.

### Next exact action

Người dùng xác minh 195 test rồi chạy DB payload verify trên Windows. Runtime snapshot
vẫn chưa publish.

### Sửa lỗi kiểm thử Windows

- Lần chạy đầu đạt 104/105; importer không lỗi runtime.
- Assertion của test mong `Integer(-1)` trong khi `PreparedStatement.setShort` bind đúng `Short(-1)`.
- Đã sửa expected value sang `short`; không thay đổi production code hoặc contract database.
- Suite mục tiêu vẫn là 105.

## 2026-08-25 — Xây và khóa khung xương kiến trúc NSOCry v1

### Mục tiêu

Tạo một nguồn sự thật đủ chi tiết để các phiên AI tiếp theo tiếp tục cùng kiến trúc, không
lặp lại phân tích NSOKISS, không tự đổi package/class/method và không tạo code khung rỗng.

### Công việc đã thực hiện

- Kiểm kê tĩnh 250 class thuộc 39 package NSOKISS và ánh xạ theo nhóm trách nhiệm; không
  chạy runtime reference.
- Kiểm kê 154 kiểu Java production thuộc 12 package NSOCry.
- Sinh ảnh chụp 393 khai báo API public hiện hữu kèm source location.
- Viết Architecture Lock v1 với bảy tầng và luật phụ thuộc một chiều.
- Viết manifest 99 contract/package rows, mỗi type RESERVED có trách nhiệm và required
  methods dự kiến để triển khai dần.
- Bao phủ toàn bộ nhóm game chính: người chơi, chiến đấu, kỹ năng, vật phẩm, bản đồ, quái,
  NPC, nhiệm vụ, tổ đội, gia tộc, giao dịch, cửa hàng, chat, giftcode, xếp hạng, sự kiện,
  bot, quản trị và lịch chạy.
- Ghi rõ các god class, event/zone/bot class explosion và singleton NSOKISS không được port
  cơ học; thay bằng component, service, data và strategy có ranh giới.
- Thêm `AGENTS.md` và cập nhật README, handoff, workflow, requirements, overview để luật
  khóa được áp dụng cho cả người và AI.
- Thêm năm test kiến trúc tự động.

### Kiểm chứng và giới hạn

- Manifest đúng sáu cột, không trùng identity: PASS.
- Tất cả package production hiện tại có khai báo: PASS.
- Không có package/import `com.nsoz` hoặc `com.nsotien`: PASS.
- Domain game không import persistence/network/bootstrap/operations/JDBC: PASS.
- `git diff --check`: PASS.
- Chưa chạy Maven trong Work; năm test mới PENDING Windows, mục tiêu suite 200.
- Không thay đổi production runtime, database hoặc runtime snapshot.

### Quyết định

- Khóa contract thay vì sinh hàng trăm stub là lựa chọn chính thức: vẫn giữ được hướng phát
  triển nhưng compiler không bị ô nhiễm bởi API suy đoán và tiến độ không bị thổi phồng.
- Thay đổi `LOCKED` hoặc thêm package mới cần ADR, cập nhật manifest/test/tài liệu và xác
  nhận rõ của chủ dự án.
- Tiến độ đến gameplay cơ bản giữ nguyên 17%.

### Next exact action

Commit/push checkpoint; người dùng pull và chạy `mvn test` kỳ vọng 200/200. Khi VERIFIED,
tiếp tục runtime snapshot SKILL theo contract đã khóa.

## 2026-08-25 — Architecture Lock v1 VERIFIED trên Windows

- Người dùng xác nhận **200/200 test** thành công, không failure/error/skipped.
- Năm test kiến trúc chuyển từ PENDING sang VERIFIED.
- Khung package/type/method, quy tắc namespace, chiều phụ thuộc và bàn giao AI chính thức
  trở thành checkpoint bắt buộc cho mọi phiên phát triển tiếp theo.
- Database và runtime snapshot không thay đổi.
- Tiến độ gameplay cơ bản giữ nguyên 17%.
- Bước kế tiếp: hiện thực publish runtime snapshot SKILL có kiểm soát theo khung đã khóa.

## 2026-08-25 — Khóa giao thức cộng tác giữa AI và chủ dự án

- Thêm tài liệu bắt buộc mô tả chi tiết cách giao tiếp, chu trình làm việc và mẫu trả result.
- Ghi rõ nguyên tắc tiếng Việt, ngắn gọn, không lặp VERIFIED, báo chức năng đang xây và
  phần trăm tổng thể ở mỗi checkpoint.
- Chuẩn hóa cách đưa lệnh Windows, bằng chứng test, tác động database/runtime và lỗi/blocker.
- Thêm chiến lược tiết kiệm token khi nhiều AI làm nối tiếp: commit nhỏ, nhật ký liên tục,
  review theo commit/PR và architecture gate.
- Liên kết quy tắc vào AGENTS, README, REQUIREMENTS và AI-HANDOFF.
- Không thay đổi production/database/runtime; tiến độ gameplay cơ bản giữ nguyên 17%.

## 2026-08-25 — Khóa quy tắc nhánh và quyền merge `main`

- Chủ dự án chốt mọi AI tiếp tục trên `agent/document-nsokiss-runtime`.
- Cấm push trực tiếp hoặc tự ý merge vào `main`.
- Chu kỳ review được chốt khoảng 5–10 commit/lần, lấy mốc hiện tại từ commit `747958b`.
- Merge chỉ hợp lệ sau review Codex đạt, gate VERIFIED và xác nhận rõ của chủ dự án:
  `ĐỒNG Ý MERGE VÀO MAIN`.
- Đã đưa quy tắc vào AGENTS, workflow, requirements, giao thức cộng tác và STATUS.
- Không thay đổi production/database/runtime; tiến độ gameplay cơ bản giữ nguyên 17%.

## 2026-08-25 — Runtime snapshot SKILL có kiểm soát

### Đã triển khai

- Snapshot runtime chỉ chứa payload SKILL cùng version/count/length/SHA-256 đã xác minh.
- Payload được sao chép phòng vệ khi tạo và khi đọc.
- Atomic store khởi tạo rỗng; chỉ swap toàn bộ snapshot sau khi mọi gate thành công.
- Service đọc `SkillAssetSource`, đối chiếu manifest candidate và giữ snapshot cũ khi lỗi.
- Không nối tự động vào startup; không phá quy tắc cấm `ClientAssetSnapshot` bán phần.
- Thêm năm test; suite mục tiêu 205 đang PENDING Windows.

### Tác động

- Database không thay đổi.
- Runtime server chưa publish snapshot vì composition/startup chưa được nối.
- Tiến độ gameplay cơ bản giữ nguyên 17%.

### Next exact action

Người dùng xác minh 205/205, sau đó xây command publish thử read-only từ JDBC với report
version/count/checksum; chưa tự động publish khi startup.

## 2026-08-25 — 205 VERIFIED và command publish thử SKILL

### VERIFIED

- Người dùng xác nhận 205/205 test sạch.
- Snapshot/runtime publish service của checkpoint trước chuyển sang VERIFIED Windows.

### Đã triển khai

- Launcher route mới `skill-runtime-publish <archive-path>`.
- Command kiểm tra archive và V003 trước khi đọc JDBC source.
- Atomic publish chỉ xảy ra sau version/count/raw difference/length/SHA-256 gate.
- Report ghi rõ database không đổi, snapshot đã publish cục bộ và startup chưa nối.
- Thêm bốn command test và một launcher parser test; suite mục tiêu 210 PENDING Windows.

### Tác động

- Database không thay đổi.
- Server startup chưa publish runtime snapshot.
- Tiến độ gameplay cơ bản giữ nguyên 17%.

### Next exact action

Người dùng xác minh 210/210 rồi chạy command trên Windows với archive candidate đã khóa;
đối chiếu version 26, count, payload 42402 byte và SHA-256.

## 2026-08-25 — Sửa exception contract của SKILL publish report

- Windows chạy 210 test: 209 pass, một failure, không error/skipped.
- Test chứng minh store rỗng phát `NoSuchElementException` thay vì `IllegalStateException`.
- Production command đã đổi sang exception có chủ đích và thông báo rõ trạng thái chưa publish.
- Test hiện có giữ vai trò regression gate; không thêm test mới, mục tiêu vẫn 210.
- Database/runtime snapshot không thay đổi; tiến độ giữ nguyên 17%.
- Next exact action: pull checkpoint và chạy lại `mvn test` kỳ vọng 210/210.

## 2026-08-25 — Checkpoint tạm dừng 210 VERIFIED

- Người dùng xác nhận 210 test, 0 failure/error/skipped, BUILD SUCCESS.
- Exception contract của report và toàn bộ command publish thử SKILL: VERIFIED Windows.
- Database không thay đổi; server startup chưa publish snapshot.
- Không có file/source dở dang tại điểm dừng.
- Branch `agent/document-nsokiss-runtime`; PR #1 giữ Draft; không merge `main`.
- Tiến độ gameplay cơ bản: 17%.
- Next exact action khi tiếp tục: package JAR, chạy `skill-runtime-publish` với candidate đã
  khóa và đối chiếu version/count/42402 byte/SHA-256; chưa nối startup.

## 2026-08-25 — Khắc phục kết quả review và chuẩn hóa bộ quy tắc

### Phạm vi review

- Base: `747958b754f0b3c265dc35b6734d71a9f8522a54`.
- Head đã rà soát: `399646a5a3310f77753f3032c5fcd5af112ef51a`.
- Bảy commit, 31 file thay đổi; chưa merge `main`.

### Vấn đề đã khắc phục

- `SkillAssetRuntimeSnapshot` không còn constructor public nhận payload tùy ý.
- Factory duy nhất tính lại SHA-256 từ payload; cùng length nhưng sai byte bị từ chối trước
  atomic publish.
- NPC menu JSON từ chối control character chưa escape theo chuẩn JSON.
- STATUS cũ hơn 1.000 dòng, có nhiều next action mâu thuẫn, đã thay bằng snapshot hiện tại
  ngắn gọn; lịch sử được giữ tại WORKLOG.
- Architecture test mới buộc STATUS có đúng một next action và kiểm tra quy tắc review độc lập.
- Cấm AI tự đánh dấu commit mình viết là REVIEWED; review phải có reviewer độc lập cùng
  base/head/findings/test evidence.
- Cấm suy số test VERIFIED bằng cách đếm annotation hoặc dùng group test thay full suite.

### Test mới

- Snapshot từ chối payload bị sửa nhưng giữ nguyên length.
- Parser từ chối newline/control character chưa escape trong JSON string.
- Architecture gate bảo vệ cấu trúc STATUS và review ownership.
- Source hiện có inventory 227 method `@Test`; toàn bộ checkpoint mới vẫn PENDING Windows.

### Tác động

- Database không đổi; server startup chưa nối snapshot.
- MAP artifact vẫn offline, chưa archive/import/runtime publish.
- Tiến độ gameplay cơ bản giữ nguyên 17%.

### Next exact action

Push checkpoint, cập nhật Draft PR #1 rồi người dùng chạy full `mvn test`. Chỉ output thật
sạch mới được đánh dấu VERIFIED; chưa merge `main`.

## 2026-08-25 — Review fix và MAP artifact đạt 227/227

- Người dùng xác nhận full Maven suite **227/227**, không failure/error/skipped.
- Checksum snapshot regression, strict JSON regression và STATUS/review architecture gate:
  VERIFIED Windows.
- Năm test MAP seed artifact chuyển từ PENDING sang VERIFIED.
- Checkpoint review fix `6ba4308a` được chấp nhận về test; chưa phải xác nhận merge `main`.
- Database không đổi; server startup chưa nối runtime snapshot.
- Tiến độ gameplay cơ bản giữ nguyên 17%.
- Next: xây MAP seed archive/command + dry-run validation offline; chưa migration/import.

## 2026-08-25 — MAP seed archive và command offline

### Đã triển khai

- Manifest parser MAP schema đóng, từ chối field thiếu/thừa/trùng và version sai miền.
- Archive service ghi `map.bin` + `map.manifest` xác định qua temporary file/atomic move.
- Cấm ghi đè; giới hạn payload 16 MiB, manifest 8 KiB; từ chối entry lạ/trùng/directory.
- ReadValidated decode bundle rồi so version/count/length/SHA-256 trước khi trả defensive copy.
- Command `map-seed-convert` đọc dump tối đa 64 MiB, tạo candidate v7 cạnh nguồn và report
  rõ database/runtime không đổi.
- Command `map-seed-dry-run` xác minh archive offline; launcher route/help đã cập nhật.
- Thêm sáu test; full suite mục tiêu 233 đang PENDING Windows.

### Tác động

- Không tạo schema/migration/importer; database không đổi.
- Không publish MAP/runtime snapshot và không nối server startup.
- Tiến độ gameplay cơ bản giữ nguyên 17%.

### Next exact action

Push checkpoint; người dùng chạy full Maven suite kỳ vọng 233/233. Sau VERIFIED mới bắt đầu
MAP schema preflight/migration draft; chưa chạy migration/import.

## 2026-08-25 — MAP archive/command đạt 233/233

- Người dùng xác nhận full Maven suite **233/233**, không failure/error/skipped.
- Manifest parser, archive limits/schema, tamper rejection, defensive copy và launcher route:
  VERIFIED Windows.
- Database không đổi; MAP runtime snapshot chưa publish; startup chưa nối.
- Tiến độ gameplay cơ bản giữ nguyên 17%.
- Next: xây MAP schema contract, migration draft và read-only preflight; chưa chạy migration.

## 2026-08-25 — Xây Developer Manual quản trị toàn source

### Mục tiêu

Cho phép chủ server tìm được chức năng, vai trò, luồng, lỗi, cách mở rộng và test liên quan
mà không phải đọc toàn bộ chat hoặc tự reverse-engineer lại source.

### Đã triển khai

- Manual index điều hướng theo nhu cầu quản trị.
- Kiến trúc và luồng JAR/network/auth/client asset/database safety.
- Playbook sửa field/payload/schema/command/checksum/parser/gameplay module.
- Troubleshooting config, MariaDB, schema, checksum, archive, test, IntelliJ và restore.
- Maintenance standard với hồ sơ class/method và nhãn evidence chuẩn.
- Sổ 17 TRACE_REQUIRED, mỗi mục có dữ liệu còn thiếu và điều kiện đóng.
- Catalog sinh cơ học bao phủ 173 file production trong 12 package, kèm Javadoc summary,
  source path và API public/protected phát hiện được.
- Generator catalog có thể chạy lại khi source đổi.
- Hai test mới bắt buộc catalog chứa mọi Java production và manual chứa đủ trường quản trị.
- START-HERE/README/AGENTS/REQUIREMENTS/package index đã liên kết manual bắt buộc.

### Trạng thái

- Không đổi production, database hoặc runtime.
- Hai test mới PENDING Windows; full suite mục tiêu 235.
- Tiến độ gameplay cơ bản giữ nguyên 17%.

### Next exact action

Push checkpoint và chạy full Maven suite 235/235. Sau VERIFIED mới tiếp tục MAP schema draft.

## 2026-08-25 — Developer Manual đạt 235/235

- Người dùng xác nhận full Maven suite **235/235**, không failure/error/skipped.
- Catalog coverage 173/173 source và required management fields: VERIFIED Windows.
- Developer Manual, 17 TRACE_REQUIRED entries và generator catalog được chấp nhận làm điểm
  tra cứu bắt buộc cho chủ server/AI tiếp quản.
- Database/runtime không đổi; tiến độ gameplay cơ bản giữ nguyên 17%.
- Next: MAP schema contract, migration draft và read-only preflight; chưa chạy migration.

## 2026-08-25 — MAP schema V004 draft và read-only preflight

### Đang xây dựng

- Chốt bốn bảng catalog MAP với 18 cột: tên map, NPC template, NPC menu entry, mob template.
- Menu NPC lưu chuẩn hóa theo NPC/row/choice order để bảo toàn `List<List<String>>` trên wire
  nhưng không khóa database vào JSON.
- `menu_row_count` bảo toàn được hàng menu rỗng, không phụ thuộc việc có choice entry.
- Thêm migration `V004__client_map_assets.sql` ở trạng thái DRAFT; file chưa được chạy.
- Thêm schema column/report/contract và JDBC inspector chỉ đọc `information_schema`.
- Thêm launcher command `map-schema-preflight [config-path]`; output không lộ credential và
  luôn khai báo `databaseChanged=false`.
- Thêm bảy test cho exact contract, sai khác metadata, JDBC read-only, report và launcher.
- Developer Manual/catalog, database manual, runnable JAR và TR-003 đã cập nhật tiếng Việt.

### Trạng thái

- Full suite Windows mục tiêu **242** đang PENDING.
- `databaseChanged=false`; `runtimeSnapshotPublished=false`; server startup không đổi.
- Tiến độ đến gameplay cơ bản giữ nguyên 17% vì chưa có database/runtime MAP hoạt động.

### Next exact action

Push checkpoint và chạy full Maven suite 242/242. Sau VERIFIED mới chạy preflight read-only;
không chạy V004/import nếu chưa backup và chưa có xác nhận riêng của chủ dự án.

## 2026-08-25 — Sửa catalog GitHub bị mất đoạn

- Full suite Windows chạy 242 test nhưng có một failure tại `DocumentationCoverageTest`:
  catalog trên GitHub thiếu path `SkillDumpInventoryReport.java`.
- Source và MAP schema test không báo failure; database/runtime không đổi.
- Đối chiếu xác nhận generator/local catalog có đủ path, nhưng blob GitHub chỉ còn 1.497 dòng
  do nội dung lớn bị cắt giữa khi publish qua connector.
- Bản vá publish lại catalog theo nhiều chunk để giữ nguyên toàn bộ nội dung; không sửa source.
- Trạng thái vẫn PENDING, không gọi 242/242 VERIFIED trước khi người dùng chạy lại full suite.

### Next exact action

Push catalog đầy đủ và chạy lại full Maven suite, kỳ vọng 242/242; chưa chạy migration V004.

## 2026-08-25 — MAP V004 preflight đạt 242/242

- Người dùng xác nhận full Maven suite **242/242**, không failure/error/skipped.
- MAP schema contract, JDBC read-only inspector, launcher route/report và catalog coverage:
  VERIFIED Windows.
- Lỗi catalog mất đoạn đã đóng sau khi publish lại đủ 1.910 dòng.
- `databaseChanged=false`; `runtimeSnapshotPublished=false`; migration V004 chưa chạy.
- Tiến độ gameplay cơ bản giữ nguyên 17% vì schema database MAP chưa được áp dụng.

### Next exact action

Chạy `map-schema-preflight` read-only trên database hiện tại; kỳ vọng NOT_READY cùng danh sách
cột thiếu trước V004. Không chạy migration/import nếu chưa backup và xác nhận riêng.

## 2026-08-25 — MAP database preflight xác nhận NOT_READY

- Người dùng chạy command trên database thật; preflight báo thiếu đúng toàn bộ 18 cột V004.
- Không báo cột thừa hoặc sai contract, phù hợp trạng thái migration chưa chạy.
- Command in `databaseChanged=false` rồi trả exit lỗi có chủ đích để chặn quy trình tiếp tục
  khi schema chưa READY.
- Database không đổi; runtime snapshot chưa publish; tiến độ gameplay giữ nguyên 17%.

### Next exact action

Tạo backup `nsocry-before-v004-<timestamp>.sql`, kiểm tra file size và SHA-256. Chỉ sau khi
backup hợp lệ và chủ dự án xác nhận riêng mới được chạy migration V004.

## 2026-08-25 — Backup trước MAP V004 hợp lệ

- File: `backup/nsocry-before-v004-20260825-223913.sql`.
- Kích thước: 207887 byte.
- SHA-256: `0F6DA143638EA358837A478C18B8094C06E027ED4EB413FAA901B4808E666541`.
- Công cụ: Laragon MariaDB 10.11.10 `mysqldump.exe`; exit code 0.
- Database chưa thay đổi; migration V004 chưa chạy; runtime snapshot chưa publish.

### Next exact action

Chờ xác nhận riêng `ĐỒNG Ý CHẠY V004`. Sau xác nhận mới chạy đúng migration draft đã review,
rồi chạy lại `map-schema-preflight`; chưa import MAP seed.

## 2026-08-25 — MAP schema V004 READY

- Chủ dự án xác nhận rõ `ĐỒNG Ý CHẠY V004` sau khi backup hợp lệ.
- Migration `V004__client_map_assets.sql` đã được chạy trên database `nsocry`.
- Preflight sau migration trả `MAP schema preflight READY` và không có difference.
- Tác động migration: `databaseChanged=true` vì bốn bảng/18 cột MAP đã được tạo.
- Tác động preflight: `databaseChanged=false` vì command chỉ đọc metadata.
- Chưa import MAP seed; runtime snapshot chưa publish; tiến độ gameplay giữ nguyên 17%.

### Next exact action

Xây importer transaction, JDBC source và database payload verifier cho MAP V004. Chưa import
candidate nếu chưa có full SHA-256 confirmation riêng của chủ dự án.

## 2026-08-26 — Xây MAP importer và JDBC payload verification

- Thêm importer thay bốn bảng MAP trong một transaction SERIALIZABLE; artifact lỗi bị chặn
  trước khi mở connection và batch lỗi rollback toàn bộ.
- Ghi `menu_row_count` để bảo toàn hàng rỗng, menu entry giữ đúng row/choice order.
- Thêm JDBC source đọc repeatable-read snapshot, kiểm tra ID liên tục, row/choice order và
  miền signed byte/short trước khi dựng `MapAssetBundle`.
- Thêm database verifier encode lại bundle JDBC rồi đối chiếu manifest count/length/SHA-256.
- Thêm command import có full-checksum confirmation và command verify chỉ đọc.
- Thêm mười test cho checksum gate, verifier report, launcher route, transaction/rollback và
  JDBC snapshot.
- Database/runtime không đổi bởi checkpoint code; V004 vẫn READY, MAP seed chưa import.
- Tiến độ gameplay giữ nguyên 17%; full suite Windows mục tiêu 252 đang PENDING.

### Next exact action

Push checkpoint và chạy full Maven suite 252/252. Chỉ sau VERIFIED mới chuẩn bị xác nhận
import MAP candidate; chưa publish runtime snapshot.

## 2026-08-26 — MAP database pipeline đạt 252/252

- Người dùng xác nhận full Maven suite **252/252**, không failure/error/skipped.
- Transaction importer, rollback gate, checksum confirmation, JDBC repeatable-read source,
  database payload verifier và launcher route: VERIFIED Windows.
- Database không đổi bởi test; MAP seed chưa import; runtime snapshot chưa publish.
- Tiến độ gameplay giữ nguyên 17% vì database MAP chưa chứa candidate.

### Next exact action

Chạy dry-run đúng MAP candidate để khóa version/count/payloadLength/SHA-256. Sau đó mới xin
xác nhận import riêng; không publish runtime.

## 2026-08-26 — MAP candidate dry-run VERIFIED

- Version: 7.
- Count: 177 map, 44 NPC, 258 mob.
- Payload length: 14401 byte.
- SHA-256: `1d97991f932960340e4097b86b39ffd6b67bccdca158d025a018ff4af344a8de`.
- Dry-run chỉ đọc archive: `databaseChanged=false`, `runtimeSnapshotPublished=false`.
- Schema V004 vẫn READY; MAP seed chưa được import.

### Next exact action

Chờ xác nhận riêng `ĐỒNG Ý IMPORT MAP`. Sau xác nhận, command import vẫn bắt nhập lại đúng
toàn bộ SHA-256; chưa publish runtime snapshot.

## 2026-08-26 — MAP seed đã import, chờ database verification

- Chủ dự án xác nhận rõ `ĐỒNG Ý IMPORT MAP`.
- Command hiển thị candidate 177 map, 44 NPC, 258 mob, payload 14401 byte và yêu cầu nhập
  đúng toàn bộ SHA-256.
- Transaction import thành công; checksum khớp
  `1d97991f932960340e4097b86b39ffd6b67bccdca158d025a018ff4af344a8de`.
- `databaseChanged=true`; `runtimeSnapshotPublished=false`.
- Lần thử trước khi MariaDB chạy bị chặn ở schema preflight, không mở transaction/ghi dữ liệu.

### Next exact action

Chạy `map-seed-db-verify` bằng đúng candidate để chứng minh JDBC source tái tạo payload giống
archive; chưa publish runtime snapshot.

## 2026-08-26 — MAP database pipeline VERIFIED_END_TO_END

- JDBC source đọc ngược database và verifier báo `MAP database payload VERIFIED`.
- Version 7; 177 map; 44 NPC; 258 mob; payload length 14401 byte.
- SHA-256 khớp candidate:
  `1d97991f932960340e4097b86b39ffd6b67bccdca158d025a018ff4af344a8de`.
- Verifier chỉ đọc: `databaseChanged=false`, `runtimeSnapshotPublished=false`.
- MAP database pipeline chuyển sang `VERIFIED_END_TO_END`; TR-003 được đóng.
- Tiến độ đến gameplay cơ bản tăng từ 17% lên 18% vì schema, import và JDBC payload thật đã
  hoàn tất; startup/runtime MAP vẫn chưa nối.

### Next exact action

Xây MAP runtime snapshot bất biến, atomic store và publish service có validation/checksum gate;
chưa nối server startup.

## 2026-08-26 — Xây MAP runtime snapshot và atomic publish

- Thêm snapshot bất biến tự kiểm tra payload length/SHA-256 và trả defensive copy.
- Thêm atomic store chỉ nhận snapshot hoàn chỉnh.
- Publish service thực hiện JDBC source → manifest validation → encode → snapshot → atomic swap.
- Checksum/source failure không thay snapshot đang phục vụ.
- Thêm command `map-runtime-publish` có schema gate và report database/runtime/startup rõ ràng.
- Thêm mười một test cho publish thành công, checksum/source failure, defensive copy,
  dependency null, tamper, report/current snapshot và launcher route.
- Database không đổi; server startup chưa nối; tiến độ gameplay giữ nguyên 18%.
- Full suite Windows mục tiêu 263 đang PENDING.

### Next exact action

Push checkpoint và chạy full Maven suite 263/263. Sau VERIFIED mới chạy publish command trên
JDBC thật; chưa nối startup.

## 2026-08-26 — MAP runtime publish đạt 263/263

- Người dùng xác nhận full Maven suite **263/263**, không failure/error/skipped.
- Snapshot integrity, defensive copy, atomic store, failure preservation, command report và
  launcher route: VERIFIED Windows.
- Database không đổi; startup chưa nối; chưa có snapshot MAP sống trong server process.
- Tiến độ gameplay giữ nguyên 18% cho đến khi runtime composition/startup được triển khai.

### Next exact action

Chạy `map-runtime-publish` trên JDBC thật bằng đúng candidate đã khóa; xác minh count/length/
SHA-256 và `serverStartupWired=false`.

## 2026-08-26 — MAP runtime publish VERIFIED trên JDBC thật

- Command tạo `MAP runtime snapshot PUBLISHED` từ dữ liệu V004.
- Version 7; 177 map; 44 NPC; 258 mob; payload length 14401 byte.
- SHA-256 khớp candidate:
  `1d97991f932960340e4097b86b39ffd6b67bccdca158d025a018ff4af344a8de`.
- `databaseChanged=false`, `runtimeSnapshotPublished=true`, `serverStartupWired=false`.
- Snapshot chỉ thuộc tiến trình command và không còn sống sau khi command kết thúc.
- Tiến độ gameplay giữ nguyên 18% vì startup/session client chưa nhận snapshot.

### Next exact action

Xây composition snapshot client tĩnh cho startup với gate cấm DATA/MAP/SKILL/ITEM/appearance
bán phần; chưa thay đổi startup trước khi contract/test hoàn chỉnh.

## 2026-08-26 — Xây gate composition asset trước startup

- Tái sử dụng `ClientAssetSnapshot`/assembler/build service hiện hữu; không tạo pipeline song
  song và không viết lại contract đã ổn định.
- Thêm `ClientAssetStartupExpectation` để khóa manifest dự kiến, kích thước tối thiểu của từng
  payload DATA/MAP/SKILL/ITEM và kích thước tối thiểu appearance từ cấu hình quản trị.
- Thêm `ClientAssetStartupGate` làm publisher decorator: kiểm tra đủ toàn snapshot rồi mới
  chuyển đúng một lần sang atomic provider.
- Manifest sai, payload ngắn, appearance ngắn hoặc dependency thiếu đều bị chặn trước publish;
  snapshot đang phục vụ không đổi.
- Thêm sáu test cho happy path, sai manifest, payload thiếu, appearance thiếu, expectation sai
  và dependency null. Full suite Windows mục tiêu **269/269** đang PENDING.
- Không đổi database, không publish snapshot production và chưa nối startup. Tiến độ gameplay
  giữ 18% cho đến khi test đạt và DATA/appearance production có nguồn authoritative.

### Next exact action

Push checkpoint, chạy full Maven suite 269/269 trên Windows. Sau VERIFIED mới xây nguồn
authoritative cho DATA; không tạo expectation giả và chưa nối startup.

## 2026-08-26 — Gate composition asset đạt 269/269

- Người dùng xác nhận full Maven suite Windows **269/269**, không failure/error/skipped.
- Gate đúng manifest, kích thước tối thiểu của DATA/MAP/SKILL/ITEM/appearance, failure
  preservation và dependency validation đã VERIFIED.
- Database không đổi, không publish runtime production và startup chưa nối.
- Tiến độ gameplay giữ 18% vì checkpoint này khóa an toàn kiến trúc, chưa thêm lớp gameplay.

### Next exact action

Xây inventory/converter cho nguồn DATA authoritative từ reference, bắt đầu bằng kiểm kê chính
xác cấu trúc và byte contract; chưa import database, chưa nối startup.

## 2026-08-26 — Chốt inventory nguồn DATA authoritative

- Đối chiếu `Server.setData()` và `Service.updateData()` với dump/reference.
- Chốt nguồn: `nj_arrow`, `nj_effect`, `nj_image`, `nj_part`, `nj_skill`, `task`,
  `others.exp`, mười bảng progression trong `GameData` và bảng `effect`.
- Chốt thứ tự container, nguồn version và nhánh biến đổi `MAX_PERCENT`.
- Ghi contract converter tại `docs/assets/data-authoritative-inventory.md`.
- Database/runtime/startup không đổi; tiến độ gameplay giữ 18%.

### Next exact action

Viết inventory parser và fixture test trước khi encode graphics/effect-template hoặc tạo DATA
candidate; chưa merge `main`.

## 2026-08-26 — Xây DATA dump inventory parser

- Thêm `ReferenceDataDumpInventoryParser` chỉ đọc đúng tám `INSERT` authoritative bằng SQL
  tuple parser hiện hữu; không thực thi SQL và không mở database.
- Khóa marker duy nhất, arity, ID tăng theo thứ tự dump, JSON integer/array/object nghiêm ngặt,
  count signed-byte, task `npcs/maps` cùng chiều dài và raw-byte `128..255` report.
- Thêm `DataDumpInventoryReport` và bảy fixture test cho success/failure boundary.
- Regenerate Developer Manual code catalog và `git diff --check` pass.
- Production classes mới compile pass bằng module `jdk.compiler`; Linux Maven/JUnit vẫn PENDING
  vì runtime hiện tại không có Maven. Full suite Windows mục tiêu **276/276**; không suy diễn kết
  quả từ số test.
- Database không đổi, runtime snapshot không publish, startup không đổi; tiến độ gameplay giữ 18%.

### Next exact action

Push checkpoint và chạy full Maven suite Windows 276/276. Sau VERIFIED mới viết graphics và
effect-template encoder cho converter DATA.

## 2026-08-26 — DATA dump inventory parser đạt 276/276

- Người dùng xác nhận full Maven suite Windows **276/276**, không failure/error/skipped.
- Marker/arity/order, JSON nghiêm ngặt, signed-byte count, task route alignment và raw-byte
  reporting: VERIFIED Windows.
- Database không đổi, runtime snapshot không publish và startup không đổi.
- Tiến độ gameplay giữ 18%; parser là gate an toàn cho converter, chưa tạo DATA production.
- Chủ dự án yêu cầu tạm dừng phát triển NSOCry để chuyển sang một việc hỗ trợ khác.

### Next exact action

Chờ yêu cầu hỗ trợ mới của chủ dự án; chưa viết graphics/effect-template encoder, chưa merge
`main`.

## 2026-08-26 — Xây DATA graphics/effect-template wire encoder

- Chủ dự án yêu cầu tiếp tục NSOCry; tiếp quản từ parser **276/276 VERIFIED**, không làm lại.
- Phân tích tĩnh byte contract tại năm hàm graphics và effect-template manager của reference.
- Thêm `ReferenceDataWireEncoder`: encode ARROW/EFFECT/IMAGE/PART/SKILL độc lập và encode
  effect-template tail; luôn chạy inventory gate trước khi tạo byte.
- Skill frame khóa đúng 13 field/thứ tự; effect image hỗ trợ `imgId` và alias `id`; raw-byte,
  signed-count và short đều được kiểm tra range trước khi ghi.
- Thêm sáu fixture test; full suite Windows mục tiêu **282/282** đang PENDING vì môi trường Work
  không có Maven/JDK.
- Database không đổi, runtime snapshot không publish, startup không đổi; tiến độ gameplay giữ
  18% vì chưa có DATA candidate hoàn chỉnh.

### Next exact action

Push checkpoint và chạy full Maven suite Windows 282/282. Sau VERIFIED mới viết
`ReferenceDataAssetConverter` ghép task route, EXP và progression.

## 2026-08-26 — DATA wire encoder đạt 282/282 và xây asset converter

- Người dùng xác nhận full Maven suite Windows **282/282**, không failure/error/skipped.
- Graphics/effect-template byte contract, skill frame 13 field, UTF tail, alias và range gate:
  VERIFIED Windows.
- Lỗi compile trước verification do production và test dùng enum không tồn tại
  `ClientGraphicBlock.SKILL`; enum authoritative là `SKILL_PAINT`. Hai tham chiếu đã được sửa,
  không thay đổi wire order hay payload.
- Thêm `ReferenceDataAssetConverter` ghép graphics, task route, EXP, mười bảng progression và
  effect-template thành `DataAssetBundle`.
- Converter yêu cầu version/hệ số/progression explicit, bảo toàn raw byte task route, chỉ biến
  đổi MAX_PERCENT khi hệ số dương và chặn version overflow.
- Thêm bốn test; full suite Windows mục tiêu **286/286** đang PENDING.
- Database/runtime/startup không đổi; tiến độ gameplay giữ 18%; chưa merge `main`.

### Next exact action

Pull nhánh và chạy full Maven suite 286/286. Sau VERIFIED mới tạo DATA candidate deterministic
và khóa payload length/SHA-256; chưa ghi database hoặc nối startup.
