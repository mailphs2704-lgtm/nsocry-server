# Worklog bổ sung — Windows one-number runner

## 2026-09-09 — triển khai chờ kiểm chứng Windows

### Đã làm

- Thêm `NSOCRY_WORK.bat` làm menu duy nhất cho chủ dự án.
- Thêm `tools/nsocry-work.ps1` để khóa nhánh, pull fast-forward, chạy Maven, tạo báo cáo và push lên GitHub.
- Báo cáo PASS hoặc FAIL đều gắn commit source được test và lưu tại `reports/windows/`.
- Chỉ hai file báo cáo được stage; công cụ không tự commit source và không push `main`.
- Thêm tài liệu vận hành tiếng Việt chi tiết.

### Kiểm chứng hiện có

- Review tĩnh luồng và guard: hoàn tất.
- Chạy thật trên Windows: `PENDING_OWNER_WINDOWS_RUN`.
- Không thay đổi production Java; số 321/321 cũ không được dùng để tuyên bố runner mới đã VERIFIED.

### Tác động

- `databaseChanged=false`
- `dataImported=false`
- `runtimeSnapshotPublished=false`
- `serverStartupWired=false`
- Tiến độ gameplay giữ nguyên 18%.

### Next exact action

Chủ dự án pull commit này, mở `NSOCRY_WORK.bat`, chọn `1`, chờ `REPORT_PUBLISHED` rồi báo “xong” để AI kiểm tra báo cáo trên GitHub.


## 2026-09-09 — Windows runner VERIFIED và bắt đầu DATA importer

- Báo cáo GitHub xác nhận đúng commit `3279ce2e`: Maven exit code 0, BUILD SUCCESS,
  321 test, 0 failure/error/skipped; JAR shaded đã tạo.
- Runner chuyển từ `PENDING_OWNER_WINDOWS_RUN` sang `VERIFIED_END_TO_END`.
- Phát hiện tiếng Việt mojibake trên PowerShell 5.1; đã thêm UTF-8 BOM và đơn giản hóa khối log.
- Thêm transactional importer, explicit overwrite mode, rollback và database read-back verifier.
- Chưa tạo command import; `databaseChanged=false`, `dataImported=false`.
- Tranche importer mới đang chờ full suite Windows qua menu số 1.


## 2026-09-09 — sửa full-suite 324 và vòng lặp menu

- Báo cáo commit `d549274e`: 324 test, importer 3/3 PASS, tổng suite FAILURE do
  DocumentationCoverageTest phát hiện code catalog thiếu DataAssetOverwriteMode.
- Đã bổ sung catalog cho toàn bộ năm production source DATA importer/verifier.
- Đã đổi nhánh menu tương tác từ exit sang quay lại MENU sau pause; direct-argument vẫn trả exit code.
- Không có lỗi transaction được ghi nhận; `databaseChanged=false`, `dataImported=false`.


## 2026-09-09 — importer 324/324 và DATA verifier command

- GitHub report đúng commit `dcb9401a`: BUILD SUCCESS, 324/324 PASS.
- Transactional importer chuyển sang VERIFIED_BY_FULL_SUITE.
- Thêm `data-seed-db-verify`: archive validation, schema READY, JDBC read-only checksum verify.
- Thêm launcher route và test output side-effect false.
- Chưa chạy verifier thật vì DATA chưa import; database không thay đổi.


## 2026-09-09 — command 327/327 và JDBC verifier tests

- Báo cáo GitHub đúng commit `1ae33c51`: BUILD SUCCESS, 327/327 PASS.
- Command read-only và launcher route chuyển sang VERIFIED_BY_FULL_SUITE.
- Bổ sung ba test trực tiếp cho `JdbcDataAssetSeedVerifier`: success/read-only, missing row,
  altered payload.
- Không kết nối hoặc thay đổi database thật.


## 2026-09-09 — sửa testCompile JDBC verifier

- Báo cáo commit `d6b29fa1`: FAILURE tại testCompile vì test gọi accessor
  `DataAssetSeedArtifact.validation()` không tồn tại.
- Production importer/verifier không phát sinh failure; Maven chưa chạy test.
- Đã sửa fixture dùng `DataAssetCodec.decode` và `DataAssetSeedValidator.validate` với
  `artifact.manifest()`, đúng API hiện hữu.
- Database không được mở hoặc thay đổi.


## 2026-09-09 — DATA verifier 330/330 và safety gate offline

- GitHub report: BUILD SUCCESS, 330/330 PASS; DATA persistence/read-back hoàn tất.
- Thêm authorization record và safety gate kiểm tra backup file, size, SHA-256, candidate
  confirmation constant-time và explicit overwrite mode.
- Thêm bốn test offline; chưa mở JDBC, chưa tạo command import, database không thay đổi.


## 2026-09-09 — chặn Git auto-maintenance giữ pack.idx

- Option 1 fast-forward source nhưng chưa build vì Git auto-gc hỏi retry khi Windows giữ pack idx.
- Hướng dẫn fail-safe: trả lời `n`, không xóa object hoặc .git.
- Runner truyền `-c gc.auto=0 -c maintenance.auto=false` cho pull/commit/push.
- Build 334 chưa có kết quả; database không thay đổi.


## 2026-09-09 — safety gate 334/334 và exact confirmation

- Báo cáo commit `631bc9e7`: BUILD SUCCESS, 334/334 PASS; không còn workflow bị auto-gc chặn.
- Backup/checksum/overwrite safety gate chuyển VERIFIED_BY_FULL_SUITE.
- Thêm exact confirmation parser với INSERT_ONLY/OVERWRITE, version và toàn SHA-256.
- Bốn test khóa success và mọi câu thiếu/sai/không rõ nghĩa.
- Chưa nối launcher/import DML; database không thay đổi.


## 2026-09-09 — confirmation 338/338 và import workflow

- Báo cáo commit `f17f32e8`: BUILD SUCCESS, 338/338 PASS.
- Exact operator confirmation chuyển VERIFIED_BY_FULL_SUITE.
- Thêm workflow buộc authorization + schema trước importer và read-back verifier sau commit.
- Bốn test khóa thứ tự, schema fail, candidate mismatch và read-back failure.
- Không có launcher route; database không thay đổi.


## 2026-09-09 — orchestration 342/342 và offline import plan

- Báo cáo commit `cac0d584`: BUILD SUCCESS, 342/342 PASS.
- DATA import workflow orchestration chuyển VERIFIED_BY_FULL_SUITE.
- Thêm `data-seed-import-plan`, properties example, launcher route và side-effect report test.
- Command chỉ đọc file archive/backup; không nạp database config, không tạo DataSource, không DML.


## 2026-09-09 — offline plan 344/344 và BAT option 5

- Báo cáo commit `e986c1da`: BUILD SUCCESS, 344/344 PASS.
- Offline plan command chuyển VERIFIED_BY_FULL_SUITE.
- Sửa archive/backup relative path thành `../` vì plan nằm trong config.
- Thêm menu số 5 chạy plan offline; không tạo database connection hoặc DML.


## 2026-09-09 — BAT option 5 VERIFIED_END_TO_END

- GitHub report đúng commit `6dfedd5a`, status `AUTHORIZED_OFFLINE`.
- Candidate DATA v7 SHA-256 khớp authoritative.
- Backup thật 234839 byte, SHA-256 khớp checkpoint trước V005.
- Overwrite mode `REJECT_EXISTING`.
- Database connection/change/import/runtime/startup đều false.
- Dừng tại authorization boundary; cần quyền riêng mới trước DATA import thật.


## 2026-09-09 — nhận quyền DATA v7 REJECT_EXISTING

- Chủ dự án cấp quyền riêng cho DATA v7 import vào database NSOCry bằng REJECT_EXISTING.
- Thêm command thật với archive/backup/checksum/schema/transaction/read-back gates.
- Command từ chối OVERWRITE, runtime/startup vẫn false.
- Thêm hai command tests và launcher route test; chưa chạy DML, đang chờ full suite.


## 2026-09-10 — command import 347/347 và BAT option 6

- Rebase recovery thành công; GitHub report đúng commit `a04c6f9c`, BUILD SUCCESS 347/347.
- Command DATA v7 REJECT_EXISTING chuyển VERIFIED_BY_FULL_SUITE.
- Thêm BAT option 6: pull, full pre-import build, command import, read-back và report push.
- Failure sau khi command bắt đầu được phân loại FAILED_OR_UNCERTAIN; runtime/startup không nối.
- Tại checkpoint code này DATA chưa được import.


## 2026-09-10 — DATA v7 IMPORTED_AND_VERIFIED

- BAT option 6 chạy đúng commit `0b956467`; pre-import gate trước đó 347/347 PASS.
- Command exit 0; version 7, 43 task group, 131 EXP, 85154 byte, checksum authoritative.
- `overwritten=false`; row mới được transaction commit và read-back xác minh.
- `databaseChanged=true`, `dataImported=true`.
- Runtime snapshot/startup vẫn false; tiến độ gameplay tăng 18% -> 19%.


## 2026-09-10 — bắt đầu DATA runtime snapshot

- Tái sử dụng DataAssetSource port đã tồn tại.
- Thêm JdbcDataAssetSource repeatable-read/read-only với metadata/checksum validation.
- Thêm immutable snapshot, atomic store và publish service.
- Thêm sáu unit tests về publish gate, checksum failure, source failure và defensive copy.
- Chưa publish runtime thật hoặc nối startup; database không đổi trong tranche này.


## 2026-09-10 — sửa typo DATA runtime testCompile

- Báo cáo commit `296bad67`: production compile đạt, testCompile fail do tên generator typo.
- Sửa về đúng `DataAssetSeedArtifactGenerator`.
- Maven chưa chạy sáu runtime tests; database/runtime không thay đổi.


## 2026-09-10 — DATA runtime snapshot 353/353 và JDBC source tests

- Báo cáo commit `bbb62e16`: BUILD SUCCESS, 353/353 PASS.
- Snapshot/store/publish service chuyển VERIFIED_BY_FULL_SUITE.
- Thêm ba test JdbcDataAssetSource cho read-only repeatable-read, missing row và metadata mismatch.
- Chưa publish runtime thật; database không thay đổi thêm; startup chưa nối.


## 2026-09-10 — JDBC source 356/356 và DATA publish command

- Báo cáo commit `2ac17c8f`: BUILD SUCCESS, 356/356 PASS.
- JDBC DATA source chuyển VERIFIED_BY_FULL_SUITE.
- Thêm isolated runtime publish command, bốn command tests và launcher route test.
- Chưa chạy command trên database thật; server startup chưa nối.


## 2026-09-10 — DATA publish command 361/361 và BAT option 7

- Báo cáo Windows đúng commit `f7fa350b`: BUILD SUCCESS, 361/361 PASS, không skipped.
- Isolated DATA runtime publish command và launcher route chuyển `VERIFIED_BY_FULL_SUITE`.
- Thêm BAT option 7: pull, chạy command trên database read-only, kiểm tra
  `PUBLISHED_ISOLATED` và tự push báo cáo riêng.
- Runner khóa `databaseChanged=false`, `runtimeSnapshotPublished=true`,
  `serverStartupWired=false`; store chỉ sống trong process command.
- Lần chạy thật option 7 trên Windows: `PENDING_OWNER_WINDOWS_RUN`.
- Tiến độ gameplay giữ 19% vì chưa nối snapshot vào startup/server lifecycle.


## 2026-09-10 — BAT option 7 VERIFIED và readiness gate

- Báo cáo thật tại `reports/windows/latest-data-runtime-publish.md`: exit 0,
  `PUBLISHED_ISOLATED`, DATA v7/count/length/SHA-256 đều khớp authoritative.
- `databaseChanged=false`, `runtimeSnapshotPublished=true`, `serverStartupWired=false`.
- BAT option 7 chuyển `VERIFIED_END_TO_END`.
- Thêm `AtomicDataAssetRuntimeSnapshotStore.requireCurrent`: fail closed nếu store rỗng,
  version sai hoặc checksum sai; đúng identity mới trả snapshot hiện hành.
- Bốn test readiness mới đang chờ full suite Windows; chưa gắn gate vào TCP listener.


## 2026-09-10 — readiness 365/365 và TCP startup hook

- Báo cáo commit `8458c327`: BUILD SUCCESS, 365/365 PASS.
- Atomic store readiness gate chuyển `VERIFIED_BY_FULL_SUITE`.
- Thêm `StartupReadiness` vào `NsocryServerApplication`; `start()` gọi gate trước
  `TcpServer.start()`.
- Ba test khóa gate chạy trước bind, lỗi gate giữ listener đóng và dependency null bị từ chối.
- Production main chưa inject DATA readiness; database không đổi, server thật chưa khởi động.


## 2026-09-10 — TCP hook 368/368 và DATA startup composition

- Báo cáo commit `fa0662a2`: BUILD SUCCESS, 368/368 PASS.
- TCP readiness hook chuyển `VERIFIED_BY_FULL_SUITE`.
- Thêm `DataAssetServerStartupReadiness`: publish từ source, validate manifest, atomic swap và
  require đúng version/SHA-256 trước khi trả quyền bind.
- Hai test khóa success và database/source failure giữ store rỗng.
- Production main chưa inject component; không kết nối database hoặc khởi động server trong tranche.


## 2026-09-10 — composition 370/370 và production main wiring

- Báo cáo commit `946fbf87`: BUILD SUCCESS, 370/370 PASS.
- DATA startup composition chuyển `VERIFIED_BY_FULL_SUITE`.
- Khóa identity authoritative v7: 43 task group, 131 EXP, 85154 byte và SHA-256 checkpoint.
- Production `main` tạo atomic store, JDBC readiness và truyền vào application trước TCP bind.
- Server chỉ in DATA READY sau khi store vượt version/checksum gate.
- Chưa chạy server smoke test; database không đổi trong build/test.


## 2026-09-10 — production wiring 372/372 và BAT option 8

- Báo cáo commit `a5858bfe`: BUILD SUCCESS, 372/372 PASS.
- Production DATA startup wiring chuyển `VERIFIED_BY_FULL_SUITE`.
- Thêm BAT option 8 với full pre-smoke build, timeout 15 giây, stdout/stderr riêng và cleanup
  đúng PID trong `finally`.
- Success yêu cầu đồng thời DATA READY version 7, TCP started, process từng chạy và đã được dừng.
- Database chỉ đọc; smoke thật đang chờ chủ dự án chạy.


## 2026-09-10 — production startup smoke VERIFIED và application ownership

- Smoke report commit `a9ebc146`: `STARTED_READY_AND_STOPPED`.
- Server bind `0.0.0.0:14444`, DATA snapshot READY version 7, stderr rỗng.
- Runner dừng đúng PID; database không đổi, không import lại.
- Tiến độ gameplay cơ bản tăng 19% → 20% vì DATA đã thực sự tham gia production startup.
- Chuyển atomic DATA store thành tài nguyên do `NsocryServerApplication` sở hữu.
- Application chỉ expose `Optional<DataAssetRuntimeSnapshot>`; không expose hàm publish/mutation
  cho session/gameplay.
- Hai ownership tests mới đang chờ full suite Windows.


## 2026-09-10 — DATA ownership 374/374 và post-login sync service

- Sau recovery report-only divergence, báo cáo commit `e175a37b`: BUILD SUCCESS, 374/374 PASS.
- Application DATA ownership chuyển `VERIFIED_BY_FULL_SUITE`.
- Thêm `PostLoginDataSyncService` dùng accessor snapshot read-only; không giữ DataSource/store publisher.
- Service chỉ nhận envelope -28/request -122, trả nested DATA response từ payload defensive copy.
- MAP/SKILL/ITEM, malformed request và snapshot rỗng đều fail closed.
- Bốn test mới đang chờ Windows full suite; chưa nối socket/session loop.


## 2026-09-10 — DATA sync 378/378 và full asset sync orchestration

- Báo cáo commit `6e825090`: BUILD SUCCESS, 378/378 PASS.
- DATA-only post-login service chuyển `VERIFIED_BY_FULL_SUITE`.
- Thêm `PostLoginAssetSyncService` dùng full `ClientAssetSnapshotProvider`.
- Service gửi UPDATE_VERSION gồm bốn version + appearance và trả đúng payload cho bốn request.
- Mỗi operation đọc provider đúng một lần; malformed request fail trước khi đọc snapshot.
- Bốn test mới đang chờ Windows full suite; transport loop chưa nối.


## 2026-09-10 — full sync 382/382 và response sizing plan

- Báo cáo commit `fb1bf820`: BUILD SUCCESS, 382/382 PASS.
- Full post-login asset sync chuyển `VERIFIED_BY_FULL_SUITE`.
- DATA authoritative 85154 byte vượt short payload 65535 nên bắt buộc full-size transport.
- Thêm `ResponsePlan`: chọn SHORT/FULL_SIZE theo `ProtocolLimits`, từ chối vượt max full.
- Ba test khóa small, large 65537-byte logical response và over-limit.
- Chưa encode/gửi command -32 vì layout wrapper cần evidence từ reference; không đoán wire.


## 2026-09-10 — sizing 385/385 và sửa full-size wire từ reference

- Báo cáo commit `2f46c234`: BUILD SUCCESS, 385/385 PASS.
- Đối chiếu `source-reference/NSOKISS/.../Session.java:136-175`.
- Routing threshold authoritative là `Short.MAX_VALUE` 32767.
- Full-size layout: encrypted -32, encrypted original command, encrypted int32 length, encrypted payload.
- Sửa codec/writer/reader/transport để giữ command gốc; update fixture 32768-byte SHA-256.
- Thêm reader round-trip test; đang chờ Windows full suite, chưa nối production session loop.

## 2026-09-13 — máy mới: sửa startup smoke khi Maven không có trong PATH

- Database mới đã dựng V001–V005 và ITEM/SKILL/MAP/DATA đã được import/read-back thành công cục bộ.
- Lần chạy mục 8 dừng trước startup vì PowerShell không tìm thấy `mvn`; database không đổi và server chưa mở.
- Lỗi phụ `Contains` trên stdout null đã được xác định.
- Commit `a6b5f625` thêm Maven auto-discovery từ PATH, MAVEN_HOME hoặc thư mục tools cùng ổ repo, kiểm tra Java trước build và xử lý stdout rỗng an toàn.
- Trạng thái: chờ chủ dự án pull và chạy lại mục 8 trên Windows; chưa tuyên bố smoke PASS từ thay đổi này.
