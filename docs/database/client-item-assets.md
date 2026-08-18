# ITEM asset trong database NSOCry

## Mục tiêu

Hai bảng `client_item_options` và `client_item_templates` là read model tĩnh để dựng
payload ITEM cho client. Đây không phải inventory của nhân vật và không lưu giá, số
lượng, khóa giao dịch hay option ngẫu nhiên của một vật phẩm đang sở hữu.

## Quy tắc ID

Wire ITEM không gửi ID từng bản ghi. Client dùng vị trí trong mảng làm ID, vì vậy:

- mỗi bảng phải bắt đầu từ ID `0`;
- ID phải liên tục, không được có khoảng trống;
- mọi truy vấn phải dùng `ORDER BY id`;
- xóa cứng một hàng ở giữa là không hợp lệ; cần migration có chủ đích nếu đổi ID.

`JdbcItemAssetSource` kiểm tra lại quy tắc này khi đọc. Dữ liệu sai làm rebuild thất
bại và snapshot cũ tiếp tục phục vụ.

## Transaction và version

Option và template được đọc trong cùng transaction read-only, isolation
`REPEATABLE_READ`. ITEM version được composition root truyền vào adapter; chỉ tăng
version sau khi dữ liệu mới đã build và validate thành công. Migration V002 chỉ tạo
schema, không tự nhập dữ liệu từ database tham chiếu.

## Ranh giới

- JDBC adapter chỉ hiện thực `ItemAssetSource`.
- Build service không biết SQL hoặc schema.
- Session không mở connection database.
- Không chạy runtime trên database tham chiếu.
- Seed/import dữ liệu sẽ là một checkpoint riêng có kiểm tra count và checksum.
