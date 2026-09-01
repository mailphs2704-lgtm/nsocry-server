# DATA asset persistence V005

## Mục tiêu

Lưu đúng DATA candidate authoritative đã qua archive validation để bước import/runtime tương lai
không phải đọc lại dump NSOKISS. V005 hiện là **DRAFT**; chưa được chạy trên database thật.

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
- Importer, database checksum verifier, runtime publisher và startup wiring: chưa làm.
- Trước khi chạy V005 phải backup, xác nhận database đích và được chủ dự án cho phép riêng.

## Luồng và xử lý lỗi

1. `NsocryLauncher` route `data-schema-preflight` tới command.
2. Command nạp cấu hình database nhưng không nhận password qua argument.
3. `JdbcDataAssetSchemaInspector` đặt connection read-only và chỉ đọc metadata bảy cột.
4. `DataAssetSchemaContract` fail closed nếu thiếu, thừa, trùng hoặc sai type contract.
5. Command in `databaseChanged=false`; trạng thái NOT_READY kết thúc bằng lỗi rõ ràng.

Khi sửa V005 phải cập nhật đồng thời migration, expected columns, inspector query, test malformed,
tài liệu này, STATUS và code catalog. Không sửa contract chỉ để ép preflight READY.
