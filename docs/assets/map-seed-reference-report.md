# MAP seed reference report

## Mục tiêu

Checkpoint này chỉ dựng **candidate MAP asset offline** từ `source-reference/database.sql`. Không thực thi SQL, không ghi database, không tạo zone/runtime và chưa thay seed production.

## Nguồn authoritative

Converter chỉ đọc đúng ba `INSERT`:

| Bảng | Cột đi lên payload MAP | Cột cố ý bỏ qua |
| --- | --- | --- |
| `map` | `id`, `name` | `npc`, `waypoint`, `monster`, `zone_number`, `locationStand`, `tileId`, `bgId`, `type`, `item`, `behind`, `betwen`, `front` |
| `npc` | `id`, `name`, `head`, `body`, `leg`, `menu` | không có cột runtime nào được đưa thêm |
| `monster` | `id`, `name`, `type`, `hp`, `range_move`, `speed` | `level`, `boss`, `type_fly`, `n_img`, animation/sprite/frame data |

`map.npc`, `map.waypoint` và `map.monster` là dữ liệu placement/runtime của map, không thuộc catalog MAP mà client nhận ở bước asset này.

## Inventory và wire guard

`ReferenceMapDumpInventoryParser` tính count trực tiếp từ tuple của dump và yêu cầu ID liên tục từ `0`. Không dùng comment `~N rows` của công cụ export làm nguồn sự thật. Dump hiện đã quan sát map ID tới ít nhất `158`, vì vậy comment cũ `~148 rows` rõ ràng không đủ tin cậy.

Giới hạn theo `MapAssetCodec`/client V7:

- map count: `0..255` (`readUnsignedByte`);
- NPC count: tối đa `127` (signed byte không âm);
- mob count: tối đa `32767` (signed short không âm);
- số dòng menu NPC: tối đa `127`;
- số choice trong một dòng menu: tối đa `127`;
- `head/body/leg`: phải nằm trong `short`;
- `monster.type`, `range_move`, `speed`: raw byte. Giá trị `128..255` được giữ bit pattern khi cast sang Java `byte` và đồng thời ghi vào report để không bị hiểu nhầm thành số âm nghiệp vụ.

Menu NPC chỉ chấp nhận schema JSON `array<array<string>>`; object hoặc byte dư bị từ chối.

## Candidate converter

`ReferenceMapAssetConverter.convert(version, dump)` luôn chạy inventory validation trước, sau đó dựng:

1. `List<String> mapNames` từ `map.name`;
2. `NpcTemplateAsset` từ `name/head/body/leg/menu`;
3. `MobTemplateAsset` từ `type/name/hp/range_move/speed`;
4. `MapAssetBundle` + `MapDumpInventoryReport` trong `MapAssetConversionResult`.

Không có dữ liệu spawn/zone được đưa vào `MapAssetBundle`.

## Test checkpoint

Các test mới:

- `ReferenceMapDumpInventoryParserTest`: schema, ID gap, menu JSON, raw-byte range và smoke test trực tiếp trên `source-reference/database.sql`;
- `ReferenceMapAssetConverterTest`: chứng minh runtime columns bị bỏ qua, raw byte `200` được bảo toàn và bundle round-trip qua `MapAssetCodec`;
- `MapAssetCodecTest`: codec wire đã tồn tại và được giữ nguyên.

Lệnh kiểm chứng tập trung:

```powershell
mvn -Dtest=ReferenceMapDumpInventoryParserTest,ReferenceMapAssetConverterTest,MapAssetCodecTest test
```

Trạng thái tài liệu tại checkpoint này: **candidate implementation; cần Maven pass trước khi coi MAP inventory là verified**.
