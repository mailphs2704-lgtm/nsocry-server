# DATA asset persistence V005

## Mục tiêu

Lưu đúng DATA candidate authoritative đã qua archive validation để bước import/runtime tương lai
không phải đọc lại dump NSOKISS. V005 đã chạy và đạt READY trên database NSOCry thật.

## Contract

Bảng `client_data_assets` có một row cho mỗi DATA version và bảy cột bắt buộc:

| Cột | Vai trò |
| --- | --- |
| `version` | Version raw byte, primary key |
| `task_group_count` | Count signed-byte `0..127` |
| `experience_count` | Count unsigned-byte `0..255` |
| `payload_length` | Số byte phải bằng `OCTET_LENGTH(payload)` |
| `payload_sha256` | SHA-256 lowercase 64 ký tự |
| `payload` | Payload DATA canonical từ archive |
| `manifest_text` | Manifest canonical dùng để audit/read-back |

Không có cờ active, timestamp hoặc dữ liệu gameplay suy đoán. Importer tương lai phải nhận
`ValidatedDataAssetSeedArchive`, chạy transaction và từ chối overwrite ngoài quy trình xác nhận.

## Preflight

`data-schema-preflight [config-path]` chỉ đọc `information_schema.columns`, bật connection
read-only và so đúng type/unsigned/nullability. Command không thực thi V005 và luôn báo
`databaseChanged=false`.

## Trạng thái và giới hạn

- Migration V005 draft, schema contract, JDBC inspector và command:
  `VERIFIED_BY_FULL_SUITE 321/321`.
- Test đã khóa exact schema, field thiếu/thừa/trùng/sai unsigned/nullability, connection
  read-only, SQL `information_schema`, output READY/NOT_READY và launcher routing.
- Database thật: chưa thay đổi.
- Preflight trên database NSOCry thật: `NOT_READY VERIFIED`; kết nối
  `127.0.0.1:3306/nsocry` thành công, báo đúng bảy cột `client_data_assets` còn thiếu và
  `databaseChanged=false`.
- Sau xác nhận chủ dự án, V005 đã chạy thành công; preflight sau migration `READY VERIFIED`.
- Full regression sau migration: 321/321 PASS.
- `databaseChanged=true`, `dataImported=false`, runtime/startup false.
- Importer, database checksum verifier, runtime publisher và startup wiring: chưa làm.
- Trước khi chạy V005 phải backup, xác nhận database đích và được chủ dự án cho phép riêng.

## Backup trước V005

Chạy `tools/backup-nsocry-before-v005.ps1` từ root repo. Script khóa URL đúng
`jdbc:mariadb://127.0.0.1:3306/nsocry`, đọc credential từ properties mà không in password,
dùng file `.partial`, không ghi đè và chỉ đổi tên thành `.sql` sau khi dump thành công, file
không rỗng. Output bắt buộc có size, SHA-256, `databaseChanged=false`, `v005Executed=false`.

Các file `.sql` 0 byte do lần dump thất bại không phải backup hợp lệ và không được dùng để
khôi phục hoặc làm bằng chứng trước migration.

Backup hợp lệ trước V005 đã VERIFIED trên Windows:

- File: `backups/nsocry-before-v005-20260901-174146.sql`.
- Size: 234839 byte.
- SHA-256: `9cea61d3482ec08a727b71f11c4400dd2c6144cc55b9450baf27bd6dd71983c6`.
- `databaseChanged=false`, `v005Executed=false`.

Sau xác nhận của chủ dự án, migration phải chạy qua `tools/apply-data-v005.ps1`. Script khóa
backup size/SHA-256, database URL, migration file và JAR; yêu cầu preflight trước là NOT_READY,
chạy đúng V005 rồi yêu cầu preflight sau là READY. Script không import DATA và không publish
runtime/startup. Script đã chạy thành công trên Windows và khóa pre/post state.

Lần chạy đầu dừng tại preflight NOT_READY vì PowerShell nâng stderr dự kiến thành terminating
error; script chưa đi tới MariaDB client nên `databaseChanged=false`. Hàm preflight đã được sửa
để thu stdout/stderr và exit code rồi tự đánh giá state, không nới các gate backup/target/READY.

## Luồng và xử lý lỗi

1. `NsocryLauncher` route `data-schema-preflight` tới command.
2. Command nạp cấu hình database nhưng không nhận password qua argument.
3. `JdbcDataAssetSchemaInspector` đặt connection read-only và chỉ đọc metadata bảy cột.
4. `DataAssetSchemaContract` fail closed nếu thiếu, thừa, trùng hoặc sai type contract.
5. Command in `databaseChanged=false`; trạng thái NOT_READY kết thúc bằng lỗi rõ ràng.

Khi sửa V005 phải cập nhật đồng thời migration, expected columns, inspector query, test malformed,
tài liệu này, STATUS và code catalog. Không sửa contract chỉ để ép preflight READY.
