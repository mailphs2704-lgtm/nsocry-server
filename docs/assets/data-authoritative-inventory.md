# DATA authoritative inventory

**Checkpoint:** 2026-08-26 UTC  
**Phạm vi:** nguồn tham chiếu `NSOKISS-inspection` và `database.sql`; chưa import database,
chưa publish runtime, chưa nối startup.

## Nguồn tạo payload

`Server.setData()` là byte contract authoritative của DATA client V7. `Service.updateData()`
gửi nguyên `Server.data`, vì vậy converter NSOCry phải tái tạo đúng payload này thay vì suy luận
từ model gameplay.

| Thứ tự wire | Thành phần | Nguồn authoritative | Quy tắc |
|---:|---|---|---|
| 1 | version | `config.properties`: `game.data.version`; cộng 1 khi `game.upgrade.percent.add > 0` | raw byte |
| 2 | arrow | bảng `nj_arrow` | encode theo `Server.setDataArrow()`, bọc `int length` |
| 3 | effect paint | bảng `nj_effect` | encode theo `Server.setDataEffect()`, bọc `int length` |
| 4 | small image | bảng `nj_image` | encode theo `Server.setDataImage()`, bọc `int length` |
| 5 | part | bảng `nj_part` | encode theo `Server.setDataPart()`, bọc `int length` |
| 6 | skill paint | bảng `nj_skill` | encode theo `Server.setDataSkill()`, bọc `int length` |
| 7 | task route | bảng `task`, cột JSON `npcs` và `maps` | giữ thứ tự row; hai mảng phải cùng chiều dài |
| 8 | EXP | bảng `others`, row `name='exp'`, cột JSON `value` | unsigned `byte count`, rồi `long` big-endian |
| 9 | progression | hằng số trong `GameData.java` | đúng thứ tự 10 bảng bên dưới |
| 10 | effect template | bảng `effect` | tail encode theo `EffectTemplateManager.setData()` |

Thứ tự progression cố định:

1. `UP_CRYSTAL`
2. `UP_CLOTHE`
3. `UP_ADORN`
4. `UP_WEAPON`
5. `COIN_UP_CRYSTAL`
6. `COIN_UP_CLOTHE`
7. `COIN_UP_ADORN`
8. `COIN_UP_WEAPON`
9. `GOLD_UP`
10. `MAX_PERCENT`

`MAX_PERCENT` là trường hợp duy nhất chịu cấu hình: khi `game.upgrade.percent.add > 0`, mỗi
giá trị wire là `num + (num * configuredValue)` sau phép ép về `int`; version DATA đồng thời
tăng 1. Converter đầu tiên phải yêu cầu explicit giá trị cấu hình này, không đọc cấu hình server
ngầm và không mặc định thay đổi dữ liệu.

## Contract converter

- Chỉ parse đúng các `INSERT` đã liệt kê; không thực thi SQL.
- Mỗi marker bắt buộc xuất hiện đúng một lần và mọi row phải đúng arity.
- Giữ thứ tự row của dump ở nơi reference giữ thứ tự truy vấn; không tự sắp xếp khi byte contract
  không có `ORDER BY`.
- Mọi count được client đọc bằng signed byte phải nằm trong `0..127`; riêng EXP đã được database
  live/client behavior xác nhận là unsigned byte `0..255` và candidate thật có 131 phần tử.
- ID/offset raw byte cho phép bit pattern `0..255`; converter phải báo cáo giá trị `128..255`
  thay vì làm mất bit khi ép kiểu.
- JSON phải đúng schema từng cột, từ chối byte dư và mảng lệch chiều dài. Compatibility duy nhất
  được phép là hành vi `json-simple 1.1` thiếu comma giữa hai member object khi token kế tiếp là
  key string; evidence là `nj_part.id=295` trong cả dump và database live. Array vẫn strict.
- Năm graphics block phải được encode riêng và so khớp lại qua `DataAssetCodec`.
- Effect-template tail phải có parser riêng; không coi toàn bộ tail là blob chưa kiểm định.
- Candidate phải xác định: cùng dump + version + max-percent-add sinh cùng payload và SHA-256.
- Candidate không được mở `DataSource`, ghi database hoặc publish runtime.

## Thành phần đã triển khai

- `ReferenceDataDumpInventoryParser` và bảy fixture test đã đạt full suite Windows **276/276**.
- `ReferenceDataWireEncoder.encodeGraphics(...)` tái tạo riêng năm block ARROW/EFFECT/IMAGE/
  PART/SKILL. Encoder gọi inventory gate trước, giữ thứ tự row dump và ghi số theo big-endian
  của `DataOutputStream`.
- Skill frame ghi đúng thứ tự: status, ba cụm effect-id/dx/dy, arrow-id/adx/ady; thiếu bất kỳ
  field nào đều bị từ chối, không tự điền 0.
- Effect paint chấp nhận tên khóa `imgId` và alias reference `id`. Reference nạp image id
  bằng `Long.shortValue()` rồi `writeShort`, nên converter giữ đúng 16 bit thấp kể cả giá trị
  source vượt signed-short (đã gặp `260910 -> 0xFB2E`); dx/dy vẫn theo raw byte.
- `encodeEffectTemplates(...)` ghi signed-byte count, id/type raw byte, modified UTF-8 name và
  icon short đúng contract `DataOutputStream.writeUTF`.
- Sáu fixture test bảo vệ byte chính xác, thứ tự skill field, UTF tail, alias và range; full
  suite Windows **282/282 VERIFIED**.
- `ReferenceDataAssetConverter.convert(...)` yêu cầu explicit dump, DATA base-version,
  `maxPercentAdd` và đủ mười bảng progression; không đọc cấu hình hoặc database ngầm.
- Converter giữ task group/route order, bảo toàn raw-byte NPC/map, đọc EXP thành `long[]`, sao
  chép progression và chỉ áp dụng `(int) (num + num * maxPercentAdd)` cho `MAX_PERCENT`.
- Khi `maxPercentAdd > 0`, DATA version tăng đúng một đơn vị; version vượt raw byte, hệ số âm/
  không hữu hạn, thiếu bảng hoặc count vượt wire boundary đều bị chặn.
- Bốn test converter đã đạt full suite Windows **286/286 VERIFIED**.
- `DataAssetSeedArtifactGenerator` encode bundle thành candidate deterministic; manifest format
  `nsocry-data-seed-v1` khóa version, task-group count, EXP count, payload length và SHA-256.
- `DataAssetSeedValidator` encode lại cùng bundle và từ chối mọi mismatch; artifact sao chép
  payload khi nhận/trả để checksum không bị caller làm sai.
- Năm test DATA candidate đã đạt full suite Windows **291/291 VERIFIED**.
- `ReferenceGameDataProgressionParser` ánh xạ trực tiếp mười hằng `GameData.java` sang
  `ProgressionTable`; parser không compile/chạy legacy và không sao chép bộ số vào production.
- Chỉ declaration `public static final int[]` duy nhất với literal integer được nhận; thiếu,
  trùng, biểu thức Java, mảng rỗng hoặc count vượt 127 đều bị chặn.
- Năm test progression parser đã đạt full suite Windows **296/296 VERIFIED**.
- Command `data-seed-dry-run <data-properties-path>` yêu cầu explicit `dump.path`,
  `game-data.path`, `data.version` và `max-percent-add`; path tương đối resolve cạnh config.
- Command chỉ đọc file bounded, tạo candidate trong bộ nhớ và in version/task/EXP count,
  payloadLength/SHA-256 cùng ba cờ tác động false; không ghi archive/JDBC/runtime.
- Bốn test command và một route test đã đạt full suite Windows **301/301 VERIFIED**.
- Config authoritative đã xác nhận `game.data.version=7` và
  `game.upgrade.percent.add=0`; converter không dùng mặc định ngầm.
- Full suite Windows gần nhất **314/314 VERIFIED** khóa thêm DATA manifest parser, archive
  read-back/tamper gate, defensive payload copy và launcher command bên cạnh các compatibility
  gate `json-simple 1.1`, effect image low-16-bit narrowing và EXP unsigned count 131.

## Ranh giới chưa làm

- Đã ghép task route, EXP và progression, tái tạo và xác minh candidate authoritative thực tế.
- DATA manifest parser canonical, archive service và hai command convert/archive-dry-run đã
  **VERIFIED_END_TO_END_OFFLINE** cùng full suite 314/314. Archive dùng `data.bin` +
  `data.manifest`, atomic move, không ghi đè, giới hạn giải nén và decode/encode/checksum
  read-back. Archive authoritative thật đã tạo và dry-run độc lập, giữ nguyên toàn bộ metadata.
- Chưa thiết kế schema JDBC DATA, importer, database verifier hoặc runtime publisher.
- Appearance vẫn là pipeline độc lập, không thuộc converter DATA.

## DATA candidate authoritative VERIFIED

- Version: 7.
- Task group count: 43.
- EXP count: 131.
- Payload length: 85154 byte.
- SHA-256: `242a3551cc110c4eda9f8e40f06fcd0f0b0b2d32bcab6f1b07669dbd0c9b148b`.
- `databaseChanged=false`, `runtimeSnapshotPublished=false`,
  `serverStartupWired=false`.

## Next exact action

Thiết kế DATA persistence contract và migration V005 draft kèm schema preflight test; chưa chạy
migration/import database, chưa publish runtime hoặc nối startup.
