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
| 8 | EXP | bảng `others`, row `name='exp'`, cột JSON `value` | `byte count`, rồi `long` big-endian |
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
- Mọi count được client đọc bằng signed byte phải nằm trong `0..127`.
- ID/offset raw byte cho phép bit pattern `0..255`; converter phải báo cáo giá trị `128..255`
  thay vì làm mất bit khi ép kiểu.
- JSON phải được parse nghiêm ngặt, đúng schema từng cột, từ chối byte dư và mảng lệch chiều dài.
- Năm graphics block phải được encode riêng và so khớp lại qua `DataAssetCodec`.
- Effect-template tail phải có parser riêng; không coi toàn bộ tail là blob chưa kiểm định.
- Candidate phải xác định: cùng dump + version + max-percent-add sinh cùng payload và SHA-256.
- Candidate không được mở `DataSource`, ghi database hoặc publish runtime.

## Ranh giới chưa làm

- Chưa tái tạo payload DATA từ dump.
- Chưa chốt số lượng thực tế, payload length và SHA-256 bằng Windows verification.
- Chưa tạo seed archive/manifest, schema JDBC, importer hoặc runtime publisher.
- Appearance vẫn là pipeline độc lập, không thuộc converter DATA.

## Next exact action

Viết `ReferenceDataDumpInventoryParser` và test trên fixture nhỏ để khóa marker, arity, thứ tự,
JSON/count/raw-byte contract. Sau khi parser pass mới viết các graphics/effect-template encoder và
`ReferenceDataAssetConverter` cho dump thật.
