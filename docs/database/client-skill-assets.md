# SKILL asset trong database NSOCry

## Ranh giới

V003 thiết kế năm read-model table: option, class, template, level và option của level.
Schema mới chuẩn hóa quan hệ thay vì lưu mảng level/option trong JSON. Đây chỉ là dữ
liệu client tĩnh; skill đang học, điểm kỹ năng và cooldown runtime của người chơi không
nằm trong các bảng này.

## Giới hạn wire

- Option/template/level/level-option count dùng signed byte: tối đa 127.
- Class count dùng unsigned byte: tối đa 255.
- Template ID hiện được giữ trong miền không âm của signed byte: 0–127.
- Level ID dùng miền không âm của signed short: 0–32767.
- Point giữ raw byte 0–255 trong database; converter mới quyết định cách cast tương thích wire.
- Thứ tự con bắt đầu từ 0 và tối đa 126.
- Option reference phải trỏ đến option template tồn tại.

`SkillAssetStructureValidator` kiểm tra count, ID template/level toàn cục không trùng và
option reference hợp lệ trước codec/seed validation.

## Nguồn tham chiếu đã quan sát

- 7 class.
- 72 skill option template.
- 91 skill template.
- Khoảng 967 skill level.
- Level option nằm trong JSON của nguồn tham chiếu và sẽ được chuẩn hóa thành row riêng.

Inventory offline thực tế xác nhận 7 class, 72 option, 91 template, 967 level và 3883
level option; tối đa 6 option/level. Có 4 giá trị nằm trong 128–255 ở field dùng một
byte. Chúng được giữ và đánh dấu, không cắt hoặc từ chối âm thầm.

| Entity | ID | Field | Giá trị raw | Signed byte tương thích |
|---|---:|---|---:|---:|
| level | 957 | point | 150 | -106 |
| level | 958 | point | 150 | -106 |
| level | 962 | point | 140 | -116 |
| level | 966 | point | 140 | -116 |

Không có difference ở `maxPoint`, `type`, `maxFight` hoặc `requiredLevel`.
Read model giữ miền nghiệp vụ 0–255; việc chuyển sang signed byte chỉ diễn ra tại
wire boundary sau khi fixture client xác nhận cách tương thích.

Các số trên chưa phải manifest chính thức. V003 chưa chạy và chưa có seed/import SKILL.
Mốc kiểm thử gần nhất do người dùng xác nhận là 160/160.

## Full converter candidate

Converter mới dùng các row `clazz`, `skill_option`, `skill_template` và `skill` làm
nguồn authoritative, rồi dựng đúng cây class → template → level → option. Cột JSON
`skillTemplates` cũ chỉ là cache trùng lặp nên không được dùng làm nguồn runtime.

Kết quả chạy offline trên dump thật với version candidate 26:

- 72 option, 7 class, 91 template, 967 level, 3883 level-option.
- Payload dài 42402 byte.
- SHA-256: `4f13faa5d95653ff9d04945d0fe8a5146030526383944d22de1786c497155cf5`.
- Encode/decode round-trip khớp hoàn toàn.
- Bốn giá trị point 140/150 giữ nguyên bit raw-byte trên wire.

Đây mới là candidate offline; cần Windows cross-platform verification trước khi tạo
archive/manifest và trước mọi migration/import.

## Checkpoint command convert/dry-run

- `skill-seed-convert <dump-path>` tạo archive cạnh dump, không mở JDBC.
- `skill-seed-dry-run <archive-path>` đọc lại payload/manifest và xác minh cấu trúc,
  count, codec, SHA-256 cùng danh sách raw-byte difference.
- Archive chỉ chấp nhận `skill.bin` và `skill.manifest`, có hard limit khi giải nén,
  timestamp cố định và không ghi đè candidate đã tồn tại.
- Chạy offline trên dump thật giữ nguyên payload 42402 byte và SHA-256
  `4f13faa5d95653ff9d04945d0fe8a5146030526383944d22de1786c497155cf5`.
- Hai command đều báo `databaseChanged=false`; V003 và database chưa thay đổi.
