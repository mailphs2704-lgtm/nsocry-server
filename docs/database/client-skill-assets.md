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

Các số trên chưa phải manifest chính thức. V003 chưa chạy và chưa có seed/import SKILL.
