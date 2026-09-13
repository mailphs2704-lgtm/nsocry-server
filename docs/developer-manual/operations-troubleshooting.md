# Vận hành và xử lý lỗi

## Nguyên tắc đầu tiên

- Không chạy lại migration/import khi chưa xác định command trước đã commit hay rollback.
- Không xóa database/artifact để “thử lại”.
- Ghi command, output đã lọc, commit và config path; không lưu password.
- Phân biệt lỗi build, config, network, schema, validation và runtime wiring.

## `database url is required`

Nguyên nhân: config mặc định không tồn tại/thiếu URL và environment override không có.

Kiểm tra:

1. Command có nhận config path hay luôn dùng `config/nsocry.properties`.
2. File tồn tại và đúng working directory.
3. Biến môi trường được loader hỗ trợ.
4. Không dán credential vào chat/log.

Không sửa code để hard-code URL/password.

## `Connection refused 127.0.0.1`

Đây là lỗi TCP đến MariaDB, chưa phải lỗi schema:

- kiểm tra MariaDB service đang chạy;
- kiểm tra host/port config;
- kiểm tra firewall/bind address;
- sau khi kết nối được mới chạy lại read-only preflight.

## `schema preflight NOT_READY`

- Đọc toàn bộ difference table/column/index.
- Xác nhận đúng migration version dự kiến.
- Tạo backup + checksum trước khi xin chạy migration.
- Không tự thêm cột thủ công vì sẽ làm migration history sai lệch.

## Checksum candidate không khớp

- Dừng trước import/publish.
- So đúng archive path và SHA-256 report.
- Chạy dry-run; không sửa manifest/checksum bằng tay.
- Nếu dump nguồn đổi, tạo candidate mới và ghi nhận version/checkpoint mới.

## Archive bị từ chối

Các nguyên nhân hợp lệ: entry lạ/trùng/thiếu, directory, vượt size, decode lỗi, trailing bytes,
manifest sai schema hoặc checksum/count/length lệch. Không nới gate để nhận file chưa rõ nguồn.

## Test failure

1. Ghi test class/method, expected và actual.
2. Xác định production defect hay assertion/type mismatch.
3. Chạy test hẹp để sửa, sau đó bắt buộc chạy full suite.
4. Chỉ full suite output mới cập nhật VERIFIED.
5. Nếu test thay contract, kiểm tra Architecture Lock/ADR.

## IntelliJ chưa cập nhật sau `git pull`

- Git đã thay file trên ổ đĩa; IntelliJ thường tự refresh.
- Nếu chưa thấy: đồng bộ project/reload Maven, không clone lại repository.
- Kiểm tra đúng branch và commit bằng `git status` + `git log -1 --oneline`.

## Server chưa sử dụng snapshot mới

Report `runtimeSnapshotPublished=true` của command thử chỉ tồn tại trong tiến trình command.
Nếu `serverStartupWired=false`, server thật chưa dùng snapshot đó. Không coi command thử là
startup integration.

## Khi cần khôi phục database

- Dừng thao tác ghi mới.
- Xác định backup path, size, checksum và thời điểm.
- Xác nhận target database chính xác.
- Lập restore command riêng và xin xác nhận chủ dự án trước khi chạy.
- Sau restore chạy schema preflight và payload verifier read-only.
