# Đóng gói và chạy NSOCry

## Trạng thái

PENDING — cần xác minh mvn package, 44 test và lệnh help của JAR.

## Artifact

Maven Shade Plugin tạo một executable uber-JAR chứa dependency runtime, gồm MariaDB Connector/J. Main class là com.nsocry.bootstrap.NsocryLauncher.

Artifact dự kiến:

target/nsocry-server-0.1.0-SNAPSHOT.jar

## Command

| Lệnh | Hành vi |
|---|---|
| java -jar target/nsocry-server-0.1.0-SNAPSHOT.jar help | In trợ giúp, không mở server/database |
| java -jar ... server [config-path] | Nạp cấu hình, ghép database/auth và mở TCP listener |
| java -jar ... create-admin [config-path] | Mở console tương tác tạo administrator đầu tiên |
| java -jar ... item-seed-dry-run &lt;archive-path&gt; | Kiểm định ITEM seed archive, chỉ in metadata và không mở database |

Không có argument sẽ in help. Command lạ hoặc quá nhiều argument bị từ chối.

## Bảo mật đóng gói

- Password database không nằm trong JAR.
- Password account không được nhận qua command-line argument.
- Chữ ký META-INF của dependency được loại khi tạo uber-JAR để tránh chữ ký không còn hợp lệ sau khi gộp.
- Không relocate namespace MariaDB driver.
- createDependencyReducedPom=false để không sửa pom.xml trong working tree.

## Xác minh checkpoint

Chạy:

mvn package

Sau BUILD SUCCESS, chạy:

java -jar target/nsocry-server-0.1.0-SNAPSHOT.jar help

Kết quả mong đợi:

- 44 test, không failure/error/skipped.
- JAR in đúng ba command.
- Không yêu cầu database credential ở lệnh help.

Chưa chạy server/create-admin cho đến khi database nsocry và migration được chuẩn bị.

## ITEM seed dry-run

Archive chứa đúng hai entry `item.bin` và `item.manifest`. Lệnh dry-run giới hạn payload
16 MiB, manifest 4 KiB, từ chối entry lạ/trùng/directory rồi kiểm tra codec round-trip,
count, length và SHA-256. Kết quả chỉ in version, count, length và checksum.

`ItemAssetSeedArchiveService.export` ghi vào file tạm cùng thư mục rồi atomic move sang
đích và không ghi đè file có sẵn. Đây mới là foundation để command export sau này nhận
bundle từ nguồn được duyệt; chưa có command tự lấy dữ liệu reference hoặc database.
