# Code reference — MAP seed archive và command offline

## Mục tiêu

Đóng gói MAP catalog candidate thành artifact có thể chuyển giữa môi trường mà vẫn tự xác
minh. Toàn bộ luồng chỉ đọc dump/file, không mở database và không publish runtime snapshot.

## Package `com.nsocry.assets`

### `MapAssetSeedManifestParser`

- Parse đúng format `nsocry-map-seed-v1`.
- Schema đóng gồm version, ba count, payload length và SHA-256.
- Từ chối field thiếu, thừa, trùng, integer sai hoặc version ngoài raw byte 0–255.
- Kết quả tiếp tục chịu wire count limit và kiểm tra 64 ký tự hex của manifest.

## Package `com.nsocry.operations`

### `MapAssetSeedArchiveService`

- `export(...)`: ghi đúng hai entry `map.bin` và `map.manifest` qua file tạm; đặt ZIP time về
  0 để artifact xác định; atomic move và cấm ghi đè candidate cũ.
- `dryRun(...)`: trả validation result sau full read/decode/manifest/checksum gate.
- `readValidated(...)`: chỉ trả payload/manifest sau khi codec và validator thành công.
- Từ chối directory, entry lạ/trùng/thiếu; payload giới hạn 16 MiB, manifest 8 KiB.

### `ValidatedMapAssetSeedArchive`

- Container bất biến cho payload, manifest text và validation result.
- Payload được copy khi tạo và mỗi lần đọc, không lộ backing array cho importer tương lai.

## Package `com.nsocry.bootstrap`

### `MapAssetSeedConvertCommand`

- Command: `map-seed-convert <dump-path>`.
- Chỉ nhận regular file tối đa 64 MiB và đọc UTF-8.
- Dùng converter → artifact generator → archive service.
- Candidate: `<base>-map-seed-v7-candidate.zip` cạnh dump.
- Report count, raw-byte differences, length, checksum và không mutation.

### `MapAssetSeedDryRunCommand`

- Command: `map-seed-dry-run <archive-path>`.
- Decode/validate lại archive và report version/count/length/checksum.
- Không dùng JDBC, migration, importer hoặc runtime store.

## Test checkpoint

`MapAssetSeedCommandsTest` có sáu test: round-trip command, payload tamper, entry lạ,
defensive copy, manifest field lạ/trùng và launcher routing.

Trạng thái: VERIFIED trong full suite Windows 233/233.
