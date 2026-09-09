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
