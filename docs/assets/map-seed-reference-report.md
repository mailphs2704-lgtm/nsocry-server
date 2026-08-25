# MAP seed reference report

## Mục tiêu

Pipeline MAP chỉ dựng **candidate asset offline** từ `source-reference/database.sql`. Không thực thi SQL, không ghi database, không tạo zone/runtime và chưa thay seed production.

## Nguồn authoritative

Converter chỉ đọc đúng ba `INSERT`:

| Bảng | Cột đi lên payload MAP | Cột cố ý bỏ qua |
| --- | --- | --- |
| `map` | `id`, `name` | `npc`, `waypoint`, `monster`, `zone_number`, `locationStand`, `tileId`, `bgId`, `type`, `item`, `behind`, `betwen`, `front` |
| `npc` | `id`, `name`, `head`, `body`, `leg`, `menu` | không có cột runtime nào được đưa thêm |
| `monster` | `id`, `name`, `type`, `hp`, `range_move`, `speed` | `level`, `boss`, `type_fly`, `n_img`, animation/sprite/frame data |

`map.npc`, `map.waypoint` và `map.monster` là dữ liệu placement/runtime của map, không thuộc catalog MAP mà client nhận ở bước asset này.

## Inventory và wire guard

`ReferenceMapDumpInventoryParser` tính count trực tiếp từ tuple của dump và yêu cầu ID liên tục từ `0`. Không dùng comment `~N rows` của công cụ export làm nguồn sự thật. Dump hiện đã quan sát map ID tới ít nhất `158`, vì vậy comment cũ `~148 rows` không đủ tin cậy.

Giới hạn theo `MapAssetCodec`/client V7:

- map count: `0..255` (`readUnsignedByte`);
- NPC count: tối đa `127` (signed byte không âm);
- mob count: tối đa `32767` (signed short không âm);
- số dòng menu NPC: tối đa `127`;
- số choice trong một dòng menu: tối đa `127`;
- `head/body/leg`: phải nằm trong `short`;
- `monster.type`, `range_move`, `speed`: raw byte; bit pattern `128..255` được bảo toàn khi encode/decode.

Menu NPC chỉ chấp nhận schema JSON `array<array<string>>`; object hoặc byte dư bị từ chối.

## Converter checkpoint — VERIFIED

`ReferenceMapAssetConverter.convert(version, dump)` chạy inventory validation trước, sau đó dựng:

1. `List<String> mapNames` từ `map.name`;
2. `NpcTemplateAsset` từ `name/head/body/leg/menu`;
3. `MobTemplateAsset` từ `type/name/hp/range_move/speed`;
4. `MapAssetBundle` + `MapDumpInventoryReport` trong `MapAssetConversionResult`.

Không có dữ liệu spawn/zone được đưa vào `MapAssetBundle`.

Ngày 2026-08-25, người dùng chạy nhóm kiểm chứng MAP trước và xác nhận **13 tests PASS**. Vì vậy inventory/parser/converter/codec checkpoint này được coi là VERIFIED.

## MAP seed artifact checkpoint — PENDING 5 tests

Bước kế tiếp bổ sung candidate artifact xác định, vẫn hoàn toàn offline:

- `MapAssetSeedManifest`: khóa `version`, `mapCount`, `npcCount`, `mobCount`, `payloadLength`, `sha256` và wire limits;
- `MapAssetSeedValidator`: encode lại bundle rồi đối chiếu toàn bộ manifest;
- `MapAssetSeedArtifact`: giữ payload bằng defensive copy;
- `MapAssetSeedArtifactGenerator`: tạo payload + manifest text format `nsocry-map-seed-v1`, tính SHA-256 và validate lại trước khi trả candidate.

Checkpoint này **chưa tạo ZIP**, chưa import database và chưa publish runtime. Archive/command chỉ được làm sau khi 5 test artifact pass.

## Nhóm test hiện tại

Chính xác 5 test trong `MapAssetSeedArtifactGeneratorTest`:

1. artifact và manifest deterministic;
2. checksum mismatch bị từ chối;
3. count vượt wire limit bị từ chối;
4. payload artifact là defensive copy;
5. payload round-trip giữ raw byte `200`.

Chạy riêng nhóm này:

```powershell
mvn "-Dtest=MapAssetSeedArtifactGeneratorTest" test
```

Trạng thái: **PENDING Windows Maven verification — tối đa 5 test cho lượt này**.
