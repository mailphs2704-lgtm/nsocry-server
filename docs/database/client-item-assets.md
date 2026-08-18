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

## Cổng kiểm định seed

`ItemAssetSeedManifest` khóa bốn giá trị cần phê duyệt: version, số option, số item và
SHA-256 của payload hoàn chỉnh. `ItemAssetSeedValidator` thực hiện lần lượt:

1. đối chiếu version và hai count;
2. encode bundle bằng codec thật;
3. parse lại và yêu cầu read model không đổi;
4. tính SHA-256 rồi so với manifest;
5. chỉ trả metadata vận hành, không trả hoặc log nội dung seed.

## Kế hoạch import an toàn

Import chưa được tự động chạy. Khi có seed đã duyệt, thực hiện theo checkpoint riêng:

1. backup database `nsocry` và ghi checksum file backup;
2. chạy V002 trên database NSOCry, không chạy trên database tham chiếu;
3. chuyển dữ liệu tĩnh sang staging trong một transaction;
4. kiểm tra ID liên tục, count và mọi giới hạn wire;
5. load bằng `JdbcItemAssetSource` và validate với manifest đã khóa;
6. commit khi checksum khớp; nếu sai thì rollback toàn bộ;
7. build snapshot mới và chỉ sau đó mới tăng/publish ITEM version.

Không dùng `DELETE`, `TRUNCATE` hoặc thay bảng hiện hành ngoài transaction import đã
được duyệt. Không đưa inventory/player data vào hai bảng asset.
