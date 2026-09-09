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
