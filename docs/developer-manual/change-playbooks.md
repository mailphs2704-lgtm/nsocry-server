# Playbook thay đổi và mở rộng source

## 1. Thêm hoặc đổi field client asset

Áp dụng cho ITEM/SKILL/MAP/DATA/appearance.

1. Xác minh field có thực sự đi lên wire; lưu fixture/reference location.
2. Sửa immutable asset record/model.
3. Sửa encoder và decoder cùng lúc.
4. Thêm range/count/null guard tại model/validator.
5. Sửa converter và JDBC source nếu có.
6. Tạo candidate mới; version/checksum cũ không được tái sử dụng.
7. Thêm round-trip, boundary, malformed và reference-dump test.
8. Nếu DB đổi: tạo migration mới, không sửa migration đã áp dụng.
9. Cập nhật module manual, code catalog, STATUS và WORKLOG.

Không lấy cột runtime/cache JSON đưa vào payload chỉ vì nó tồn tại trong dump.

## 2. Đổi kiểu truyền hoặc thứ tự payload

- Đây là thay đổi protocol, mặc định có rủi ro phá client.
- Ghi chính xác kiểu đọc phía client và kiểu ghi phía server.
- Kiểm tra signed/unsigned, byte order, modified UTF và count width.
- Tạo fixture byte trước/sau và compatibility test.
- Nếu cần hỗ trợ nhiều client version, thêm variant rõ ràng; không dùng `if` rải rác.
- Cần ADR và xác nhận chủ dự án nếu thay contract `LOCKED`.

## 3. Đổi schema database

- Không sửa trực tiếp file migration cũ đã chạy.
- Tạo migration version mới với forward SQL và rollback/restore plan.
- Giữ domain/application độc lập schema bằng repository/source port.
- Backup trước migration, ghi size/SHA-256 vào WORKLOG.
- Preflight phải mô tả chính xác bảng/cột/index/FK thiếu hoặc lệch.
- Import phải transaction, validate trước connection và rollback khi lỗi.
- Sau import phải so count/range/reference và checksum payload từ JDBC.

## 4. Thêm command executable JAR

Các file tối thiểu:

- command class trong `com.nsocry.bootstrap`;
- `NsocryLauncher` enum, parse, dispatch và usage;
- command/launcher tests;
- `docs/operations/runnable-jar.md`;
- module manual, STATUS và WORKLOG.

Command ghi dữ liệu phải interactive hoặc có confirmation gate; credential không được in log.

## 5. Thêm package/class/method

1. Kiểm tra `planned-contracts.tsv`.
2. Nếu RESERVED: giữ đúng package/trách nhiệm/dependency.
3. Nếu chưa có contract: viết ADR và xin xác nhận trước.
4. Viết class nhỏ, dependency explicit, tránh singleton/god class.
5. Thêm test và hồ sơ trong code catalog/manual.
6. Chạy `ArchitectureLockTest`.

## 6. Sửa checksum/version

- Không sửa checksum bằng tay để làm test pass.
- Checksum phải sinh từ payload deterministic sau validation.
- Version là contract wire/artifact; ghi rõ unsigned representation.
- Candidate mới phải có filename/manifest/report mới và dry-run độc lập.
- Database import chỉ dùng đúng candidate đã khóa bằng full SHA-256.

## 7. Sửa parser dump/archive

- Parser phải fail closed với field/entry lạ, trùng, thiếu và trailing data.
- Có hard limit kích thước trước khi cấp phát lớn.
- Không thực thi SQL dump.
- ZIP không được chấp nhận directory/path traversal/entry tùy ý.
- Thêm malformed/boundary/tamper test trước khi dùng nguồn thật.

## 8. Thêm gameplay module

Gameplay hiện chủ yếu `TRACE_REQUIRED`. Trước khi code:

1. Truy reference call graph tĩnh và client command liên quan.
2. Chốt aggregate/use-case/port trong contract manifest.
3. Tách template asset khỏi mutable runtime state.
4. Tách application service khỏi persistence/network.
5. Chốt concurrency ownership và transaction boundary.
6. Viết vertical slice nhỏ có test; không port nguyên god class NSOKISS.
