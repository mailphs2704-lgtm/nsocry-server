# Code reference — MAP asset conversion

Package: `com.nsocry.assets` và `com.nsocry.assets.conversion`

Line ranges dưới đây áp dụng cho source tại checkpoint MAP seed artifact này; khi source thay đổi phải cập nhật lại tài liệu.

## `MapRawByteDifference.java`

- **Lines 6–15 — `MapRawByteDifference`**: value object ghi evidence khi field wire byte có giá trị `128..255`.

## `MapDumpInventoryReport.java`

- **Lines 7–30 — `MapDumpInventoryReport`**: snapshot inventory của map/NPC/mob, min/max ID, maxima menu và raw-byte differences.
- **Lines 23–29 — compact constructor**: copy bất biến `rawByteDifferences` và bắt buộc count khớp kích thước list.

## `ReferenceMapDumpInventoryParser.java`

- **Lines 8–227 — class**: parser inventory giới hạn đúng ba INSERT MAP; tái sử dụng state machine SQL của `ReferenceItemSqlDumpParser` thay vì thực thi SQL.
- **Lines 17–61 — `parse(String)`**: parse `map/npc/monster`, kiểm tra arity + ID liên tục, wire count, NPC short/menu và raw-byte fields của monster; trả `MapDumpInventoryReport`.
- **Lines 64–66 — `parseNpcMenu(String)`**: entry point package-private cho cùng parser menu được inventory và converter sử dụng.
- **Lines 68–117 — scalar/inventory guards**: min/max ID, sequential ID, integer, maximum count, short và raw-byte range.
- **Lines 120–229 — `NpcMenuParser`**: state machine JSON chuyên biệt cho `array<array<string>>`; xử lý escape, `\uXXXX`, whitespace; từ chối control character chưa escape, trailing data và schema khác.

## `MapAssetConversionResult.java`

- **Lines 7–13 — `MapAssetConversionResult`**: gói `MapAssetBundle` candidate cùng inventory report.

## `ReferenceMapAssetConverter.java`

- **Lines 11–72 — class**: converter offline từ dump reference sang read model MAP.
- **Lines 16–48 — `convert(byte, String)`**: gọi inventory parser trước; chỉ ánh xạ `map.name`, NPC client fields và monster wire fields. Không đọc placement/spawn/runtime vào bundle.
- **Lines 50–71 — scalar guards**: integer, short và raw-byte conversion.

## `MapAssetSeedManifest.java`

- **Lines 8–35 — record**: manifest candidate gồm version/count/payload length/SHA-256.
- **Lines 17–28 — compact constructor**: áp dụng đúng wire limits `255/127/32767`, chuẩn hóa SHA-256 lowercase và xác minh 64 hex chars.
- **Lines 30–34 — `requireRange(...)`**: guard count/payload metadata trước khi candidate được dùng.

## `MapAssetSeedValidationResult.java`

- **Lines 6–18 — record**: kết quả validation đã khóa version, ba count, payload length và checksum.
- **Lines 14–17 — compact constructor**: từ chối checksum null.

## `MapAssetSeedValidator.java`

- **Lines 9–56 — class**: validator không dùng JDBC; chỉ encode bundle và so metadata/checksum.
- **Lines 14–39 — `validate(...)`**: đối chiếu version, map/NPC/mob count, payload length và SHA-256; mismatch nào cũng fail closed.
- **Lines 41–50 — `sha256(...)`**: SHA-256 deterministic bằng JDK `MessageDigest`.
- **Lines 52–55 — `require(...)`**: tạo lỗi có tên field khi manifest lệch.

## `MapAssetSeedArtifact.java`

- **Lines 7–34 — class**: container bất biến cho payload, manifest text và validation result.
- **Lines 12–20 — constructor**: defensive-copy payload và từ chối thành phần null.
- **Lines 23–25 — `payload()`**: luôn trả defensive copy, không lộ backing array candidate.

## `MapAssetSeedArtifactGenerator.java`

- **Lines 7–44 — class**: generator candidate MAP format `nsocry-map-seed-v1`.
- **Lines 13–32 — `generate(...)`**: encode bundle, tạo manifest từ count + SHA-256, validate lại rồi mới trả artifact.
- **Lines 34–43 — `manifestText(...)`**: serialize manifest text deterministic theo thứ tự field cố định.

## `MapAssetSeedArtifactGeneratorTest.java`

- **Lines 13–83 — class**: đúng 5 test của checkpoint artifact: determinism, checksum mismatch, wire limit, defensive copy và raw-byte round-trip.

## Quan hệ với runtime/database

MAP seed artifact hiện chỉ là lớp build/validation offline. Checkpoint này không thêm archive service, launcher command, JDBC source, migration, runtime store hoặc startup wiring. Các phần đó chỉ được mở ở checkpoint kế tiếp sau khi 5 test artifact được VERIFIED.
