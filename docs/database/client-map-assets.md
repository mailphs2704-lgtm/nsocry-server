# Schema read model MAP V004

## Chức năng và phạm vi

V004 lưu đúng catalog tĩnh mà `MapAssetCodec` truyền cho client V7 build 217:

- tên map theo ID/thứ tự wire;
- NPC template và cấu trúc menu hai chiều;
- mob template gồm type, tên, HP, phạm vi di chuyển và tốc độ.

V004 **không** chứa placement, waypoint, zone, spawn, instance hoặc trạng thái runtime.
Các dữ liệu đó thuộc gameplay/world và vẫn `TRACE_REQUIRED`.

## Vai trò các bảng

| Bảng | Vai trò | Khóa/thứ tự |
| --- | --- | --- |
| `client_map_names` | Tên map client | `id` là vị trí trong danh sách wire unsigned byte |
| `client_npc_templates` | Hình thể, tên và số hàng menu NPC | `id` là vị trí NPC signed-byte-safe |
| `client_npc_menu_entries` | Từng lựa chọn menu chuẩn hóa | `(npc_id,row_order,choice_order)` bảo toàn thứ tự hai chiều |
| `client_mob_templates` | Metadata mob client | `id` là vị trí mob signed-short-safe |

Menu được chuẩn hóa thành từng entry thay vì khóa vào JSON. Vì vậy có thể đổi storage/query
mà vẫn dựng lại chính xác `List<List<String>>`; wire codec không phụ thuộc trực tiếp schema.
`menu_row_count` giữ được cả hàng menu rỗng, trường hợp không thể suy ra chỉ từ các entry.

## Contract kiểu dữ liệu

- ID/order dùng unsigned chỉ để lưu miền không âm; CHECK giữ giới hạn signed/unsigned thực tế
  mà client parser chấp nhận.
- `type`, `move_range`, `speed` dùng `TINYINT` signed để bảo toàn raw byte khi JDBC source
  được triển khai.
- `head/body/leg` dùng `SMALLINT`; `health` dùng `INT`, khớp kiểu wire.
- Tất cả cột `NOT NULL`; text dùng `utf8mb4`.
- V004 có 18 cột. Preflight từ chối cột thiếu, thừa, trùng, sai type, unsigned hoặc nullable.

## Luồng preflight read-only

`map-schema-preflight [config-path]` nạp database config, mở connection read-only, đọc
`information_schema.columns`, gọi `MapAssetSchemaContract.evaluate(...)` và in:

- `MAP schema preflight READY` khi metadata khớp hoàn toàn;
- `MAP schema preflight NOT_READY` cùng từng `difference=...` khi chưa chạy/lệch V004;
- luôn in `databaseChanged=false`.

Command không chạy file SQL. `database/migrations/V004__client_map_assets.sql` hiện là
**DRAFT**, chỉ được chạy sau backup có size/SHA-256 và xác nhận riêng của chủ dự án.

## Cách sửa hoặc mở rộng

Khi đổi field MAP:

1. xác minh field thật sự có trong payload client;
2. sửa model + codec + fixture trước;
3. tạo migration version mới, không sửa V004 nếu V004 đã được áp dụng;
4. sửa contract, inspector, fake metadata test và tài liệu này;
5. sau migration phải preflight READY, import có checksum và JDBC payload verification.

Không đưa placement/runtime field vào bốn bảng catalog chỉ vì field có trong dump tham chiếu.

## Lỗi và xử lý

- `database url is required`: config/env chưa đủ; xem `docs/developer-manual/operations-troubleshooting.md`.
- `Connection refused`: MariaDB chưa chạy hoặc host/port sai; database chưa bị đổi.
- `NOT_READY`: tạo backup rồi xin xác nhận chạy V004; không tự chạy migration.
- `Sai contract cột`: không sửa dữ liệu để né gate; đối chiếu migration và metadata thật.

## Trạng thái evidence

- Schema contract, migration draft, JDBC inspector, launcher command: `IMPLEMENTED_PENDING`.
- Unit test Linux/Windows full suite: `PENDING` tại checkpoint tạo V004.
- Migration V004: `VERIFIED` trên database thật; preflight sau migration READY.
- Importer transaction, JDBC source và database payload verifier: `VERIFIED_END_TO_END` với
  version 7, 177/44/258, payload 14401 byte và SHA-256
  `1d97991f932960340e4097b86b39ffd6b67bccdca158d025a018ff4af344a8de`.
- Runtime publish/startup integration: `TRACE_REQUIRED`, chưa thực hiện.

## Import và xác minh database

- `map-seed-import <archive-path>` đọc archive đã xác minh, yêu cầu V004 READY và bắt nhập
  toàn bộ SHA-256 trước transaction SERIALIZABLE.
- Importer xóa/ghi lại đủ bốn bảng trong một transaction; batch lỗi sẽ rollback toàn bộ.
- `menu_row_count` được ghi riêng nên hàng menu rỗng không bị mất.
- `JdbcMapAssetSource` đọc bốn bảng trong repeatable-read snapshot, buộc ID/order liên tục và
  kiểm tra range trước khi dựng bundle bất biến.
- `map-seed-db-verify <archive-path>` encode bundle JDBC rồi so toàn bộ count/length/SHA-256
  với manifest candidate; command chỉ đọc và không publish runtime.
