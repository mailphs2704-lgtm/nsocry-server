# Báo cáo candidate ITEM seed từ dump tham chiếu

**Ngày phân tích:** 2026-08-18 UTC

**Chế độ:** offline/read-only; không kết nối hoặc ghi database

**ITEM version dùng để dựng candidate:** 26

## Kết quả

| Chỉ số | Giá trị |
|---|---:|
| Item option | 161 |
| Item template | 1213 |
| Option type | 0–9 |
| Item type | 0–38 |
| Icon ID | 155–26120 |
| Item có cờ nâng cấp | 431 |
| Row có fashion chưa chuyển vào ITEM wire | 79 |
| Payload length | 66811 byte |
| SHA-256 | `abb320fb8a940fc28c49c6d0c5b84e09e83d28248130884881845b9dd5bea6f8` |

## Manifest candidate

```text
format=nsocry-item-seed-v1
version=26
optionCount=161
itemCount=1213
payloadLength=66811
sha256=abb320fb8a940fc28c49c6d0c5b84e09e83d28248130884881845b9dd5bea6f8
```

## Diễn giải

Parser chỉ đọc đúng hai statement `item_option` và `item`; các bảng khác trong dump bị
bỏ qua. Row được converter sắp theo ID, xác minh liên tục từ 0, kiểm tra giới hạn wire,
encode bằng ITEM codec, parse round-trip và tính checksum.

79 giá trị fashion không nằm trong ITEM payload của client. Chúng chưa được nhập vào
read model ITEM và phải được đối chiếu ở checkpoint appearance/gameplay trước khi coi
dữ liệu liên quan đã migrate đầy đủ.

## Xác minh Windows

Candidate đã được tạo lại bằng executable JAR trên Windows và dry-run thành công với
đúng 161 option, 1213 item, 66811 byte và SHA-256 nêu trên. Trạng thái artifact:
`VERIFIED_CROSS_PLATFORM`.

Đây vẫn chưa phải phê duyệt chạy V002 hoặc import database. Schema preflight và backup
phải hoàn tất trước mọi thao tác ghi.
