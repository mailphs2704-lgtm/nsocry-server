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

## Định dạng seed artifact

Generator không sinh SQL. Nó tạo hai nội dung logic:

- payload `.bin`: chính là byte ITEM mà codec/client hiểu;
- manifest UTF-8: `format`, `version`, `optionCount`, `itemCount`, `payloadLength`,
  `sha256`, mỗi khóa một dòng và luôn dùng ký tự xuống dòng `LF`.

Format hiện tại là `nsocry-item-seed-v1`. Cùng một bundle phải luôn tạo đúng cùng
payload và manifest trên Windows/Linux. Importer tương lai sẽ parse payload bằng
`ItemAssetCodec`, kiểm định manifest rồi insert bằng prepared statement. Nhờ đó tên và
mô tả Unicode không được ghép trực tiếp vào câu SQL.

## Parser và importer transaction

`ItemAssetSeedManifestParser` chỉ chấp nhận format v1 canonical: đúng sáu khóa, đúng
thứ tự, không có dòng thừa và bắt buộc LF cuối file. Version được biểu diễn 0–255 trong
manifest rồi chuyển về byte wire.

`JdbcItemAssetSeedImporter` kiểm định hoàn toàn trước khi gọi `DataSource`. Sau đó nó:

1. mở transaction `SERIALIZABLE` và tắt auto-commit;
2. xóa hai read-model table bằng DML trong transaction;
3. batch insert option và template bằng prepared statement;
4. yêu cầu driver trả đúng số kết quả và không có `EXECUTE_FAILED`;
5. commit một lần; mọi SQLException/runtime failure đều rollback.

Importer không chạy migration, không tự tăng version và không publish snapshot. Ba
quyền này thuộc các bước vận hành riêng để tránh một lệnh vừa đổi schema, seed và
runtime state.

## Chuyển đổi dữ liệu tham chiếu

`ReferenceItemAssetConverter` chỉ nhận các row đã đọc sẵn và không biết JDBC, tên
database hoặc cú pháp dump SQL. Nó sắp xếp theo ID rồi kiểm tra:

- ID liên tục từ 0 và không trùng;
- option count tối đa 255, item count tối đa 32767;
- type/gender/level vừa signed byte;
- icon/part vừa signed short;
- cờ nâng cấp chỉ nhận 0 hoặc 1.

Report ghi count, min/max type, min/max icon, số item nâng cấp được và số row có
`fashion != -1`. Fashion không thuộc wire ITEM nên không đưa vào
`client_item_templates`; report giữ chênh lệch này để checkpoint appearance/gameplay
sau xử lý có chủ đích, không được âm thầm coi là dữ liệu đã chuyển xong.

Dump tham chiếu được cung cấp mô tả khoảng 161 option và 1213 item. Đây mới là số liệu
inventory tĩnh; count/checksum chính thức chỉ được chốt sau khi parser nguồn tạo bundle
và artifact validator chạy thành công.
