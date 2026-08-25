# Code reference — MAP asset conversion

Package: `com.nsocry.assets.conversion`

Line ranges dưới đây áp dụng cho source tại checkpoint MAP này; khi source thay đổi phải cập nhật lại tài liệu.

## `MapRawByteDifference.java`

- **Lines 6–15 — `MapRawByteDifference`**: value object ghi evidence khi field wire byte có giá trị `128..255`. Constructor lines 8–14 từ chối entity/field null, ID âm và giá trị không thực sự nằm ngoài signed-byte dương.

## `MapDumpInventoryReport.java`

- **Lines 7–30 — `MapDumpInventoryReport`**: snapshot inventory của map/NPC/mob, min/max ID, maxima menu và raw-byte differences.
- **Lines 23–29 — compact constructor**: copy bất biến `rawByteDifferences` và bắt buộc `signedByteOverflowValueCount` khớp kích thước list.

## `ReferenceMapDumpInventoryParser.java`

- **Lines 8–227 — class**: parser inventory giới hạn đúng ba INSERT MAP; tái sử dụng state machine SQL của `ReferenceItemSqlDumpParser` thay vì thực thi SQL.
- **Lines 17–61 — `parse(String)`**: parse `map/npc/monster`, kiểm tra arity + ID liên tục, wire count, NPC short/menu và raw-byte fields của monster; trả `MapDumpInventoryReport`.
- **Lines 64–66 — `parseNpcMenu(String)`**: entry point package-private cho cùng parser menu được inventory và converter sử dụng.
- **Lines 68–104 — scalar/inventory guards**: min/max ID, sequential ID, integer, maximum count và short range.
- **Lines 106–117 — `checkWireByte(...)`**: nhận raw byte đến `255`; giá trị `128..255` được ghi thành `MapRawByteDifference`.
- **Lines 120–226 — `NpcMenuParser`**: state machine JSON chuyên biệt cho `array<array<string>>`; xử lý escape chuẩn, `\uXXXX`, whitespace và từ chối trailing data/schema khác.

## `MapAssetConversionResult.java`

- **Lines 7–13 — `MapAssetConversionResult`**: gói `MapAssetBundle` candidate cùng inventory report để caller không mất validation evidence.
- **Lines 9–12 — compact constructor**: từ chối bundle/report null.

## `ReferenceMapAssetConverter.java`

- **Lines 11–72 — class**: converter offline từ dump reference sang read model MAP.
- **Lines 16–48 — `convert(byte, String)`**: gọi inventory parser trước; chỉ ánh xạ `map.name`, NPC client fields và monster wire fields. Không đọc placement/spawn/runtime vào bundle.
- **Lines 50–56 — `integer(...)`**: decimal integer strict.
- **Lines 58–63 — `checkedShort(...)`**: bảo vệ `head/body/leg` trước khi cast.
- **Lines 66–71 — `rawByte(...)`**: giữ bit pattern `128..255` theo wire semantics, đồng bộ với inventory evidence.

## Quan hệ với package `com.nsocry.assets`

`ReferenceMapAssetConverter` chỉ dựng các model đã có: `MapAssetBundle`, `NpcTemplateAsset`, `MobTemplateAsset`. Serialization tiếp tục do `MapAssetCodec` sở hữu; checkpoint này không đổi codec và không đưa trách nhiệm database/runtime vào model client asset.
