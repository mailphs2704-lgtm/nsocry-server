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
| java -jar ... item-seed-convert &lt;dump-path&gt; | Chuyển hai bảng ITEM trong dump thành candidate archive cạnh file nguồn |
| java -jar ... item-schema-preflight [config-path] | Chỉ đọc information_schema và báo V002 READY/NOT_READY |
| java -jar ... item-seed-import &lt;archive-path&gt; | Import tương tác sau validation, schema READY và xác nhận đủ SHA-256 |
| java -jar ... item-seed-db-verify &lt;archive-path&gt; | Load lại DB, dựng ITEM payload và so checksum candidate |

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
đích và không ghi đè file có sẵn. Command convert dùng service này sau khi parser và
validator đã chấp nhận bundle; không có command đọc trực tiếp database.

## ITEM seed convert

```powershell
java -jar target/nsocry-server-0.1.0-SNAPSHOT.jar item-seed-convert "source-reference\database.sql"
```

Command chỉ nhận regular file tối đa 64 MiB, dùng UTF-8 và version candidate 26. Với
`database.sql`, output là `database-item-seed-v26-candidate.zip` trong cùng thư mục.
Nếu output đã tồn tại, command dừng thay vì ghi đè. Báo cáo luôn có
`databaseChanged=false` vì command không khởi tạo DataSource.

Sau khi convert, chạy dry-run trên đúng archive vừa tạo và đối chiếu SHA-256 với báo
cáo candidate trước khi cân nhắc migration/import.

### Kiểm tra schema MAP V004

```powershell
java -jar target/nsocry-server-0.1.0-SNAPSHOT.jar map-schema-preflight
```

Command chỉ đọc `information_schema`, luôn báo `databaseChanged=false`. Trước khi V004 được
chạy, kết quả đúng là `MAP schema preflight NOT_READY`; không tự chạy migration draft.

### Import và xác minh MAP seed

```powershell
java -jar target/nsocry-server-0.1.0-SNAPSHOT.jar map-seed-import "source-reference\database-map-seed-v7-candidate.zip"
java -jar target/nsocry-server-0.1.0-SNAPSHOT.jar map-seed-db-verify "source-reference\database-map-seed-v7-candidate.zip"
```

Import chỉ được chạy sau backup + V004 READY + xác nhận full SHA-256. Verifier đọc ngược
database và phải trả đúng checksum candidate; cả hai command chưa publish runtime snapshot.

## ITEM schema preflight

```powershell
java -jar target/nsocry-server-0.1.0-SNAPSHOT.jar item-schema-preflight
```

Không truyền path sẽ dùng `config/nsocry.properties`; có thể truyền một config path
khác giống command server/create-admin. Command chỉ đọc metadata và luôn in
`databaseChanged=false`. Trước khi V002 được chạy, `NOT_READY` cùng danh sách cột thiếu
là kết quả dự kiến; command không tự sửa schema.

## ITEM seed import

Command import luôn dùng `config/nsocry.properties`, yêu cầu terminal tương tác và không
nhận checksum qua argument. Trình tự bắt buộc: archive validation → schema preflight →
in metadata → nhập lại SHA-256 → transaction import. Sai hoặc hủy checksum dừng trước
khi transaction ghi bắt đầu.

Không chạy command này trước khi suite mục tiêu của checkpoint được VERIFIED và backup
đã có size/checksum. Import thành công chưa tự publish runtime snapshot.

### Publish thử SKILL runtime snapshot

```text
java -jar target/nsocry-server.jar skill-runtime-publish <skill-archive-path>
```

Command chỉ đọc database, yêu cầu archive hợp lệ và schema V003 READY, sau đó atomic publish
snapshot trong tiến trình command. Output phải có `databaseChanged=false`,
`runtimeSnapshotPublished=true` và `serverStartupWired=false`. Đây chưa phải wiring startup
hoặc publish `ClientAssetSnapshot` đầy đủ cho session.

## MAP seed convert và dry-run

```powershell
java -jar target/nsocry-server-0.1.0-SNAPSHOT.jar map-seed-convert "source-reference\database.sql"
java -jar target/nsocry-server-0.1.0-SNAPSHOT.jar map-seed-dry-run "source-reference\database-map-seed-v7-candidate.zip"
```

Hai command chỉ đọc file offline. Convert tạo archive cạnh dump; dry-run decode và đối chiếu
manifest/count/length/SHA-256. Cả hai đều không mở database và không publish runtime.

Sau import, `item-seed-db-verify` dùng cùng archive làm nguồn sự thật để xác minh dữ
liệu database có tái tạo đúng payload. Kết quả thành công vẫn in
`databaseChanged=false` và `runtimeSnapshotPublished=false`.
