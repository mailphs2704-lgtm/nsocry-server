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
