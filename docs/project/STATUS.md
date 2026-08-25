# Trạng thái hiện tại của NSOCry

**Cập nhật:** 2026-08-25 UTC

**Trạng thái:** DEVELOPER_MANUAL_VERIFIED

**Tiến độ đến gameplay cơ bản:** 17%

**Nguồn lịch sử:** `docs/project/WORKLOG.md`

STATUS chỉ mô tả checkpoint hiện tại. Không append lịch sử hoặc tạo thêm mục
`Next exact action` trong file này.

## Git và quyền merge

- Repository: `mailphs2704-lgtm/nsocry-server`.
- Nhánh phát triển: `agent/document-nsokiss-runtime`.
- Draft PR: [#1](https://github.com/mailphs2704-lgtm/nsocry-server/pull/1).
- Mốc review: base `747958b754f0b3c265dc35b6734d71a9f8522a54`, head đã rà soát
  `399646a5a3310f77753f3032c5fcd5af112ef51a`.
- Chưa được merge `main`. Chỉ merge sau review độc lập đạt, gate VERIFIED và chủ dự án xác
  nhận rõ `ĐỒNG Ý MERGE VÀO MAIN`.

## Bất biến đang hiệu lực

- Chỉ dùng NSOCry/nsocry/Cry/cry trong source mới; cấm namespace `com.nsoz`, `com.nsotien`.
- NSOKISS chỉ được phân tích tĩnh; không chạy, sửa hoặc sao chép source.
- Tuân thủ `AGENTS.md`, Architecture Lock v1 và `planned-contracts.tsv`.
- Contract `LOCKED` chỉ đổi sau ADR, compatibility test và xác nhận chủ dự án.
- Migration/import phải có preflight, backup/checksum, confirmation và post-check.
- Tác giả không tự review commit của mình; review phải ghi base/head và findings.
- Documentation, STATUS và WORKLOG phải viết bằng tiếng Việt ở mỗi checkpoint.

## VERIFIED

### Nền tảng

- Architecture Lock v1 và năm architecture gate: 200/200 VERIFIED trên Windows tại
  checkpoint tương ứng.
- Protocol/frame/key, handshake/session transport, authentication foundation, configuration,
  observability, TCP lifecycle và executable JAR đã có regression test.
- Namespace legacy và chiều dependency gameplay được architecture test bảo vệ.

### ITEM

- ITEM pipeline `VERIFIED_END_TO_END`.
- Candidate version 26: 161 option, 1213 item, 431 item nâng cấp.
- Payload 66811 byte; SHA-256
  `abb320fb8a940fc28c49c6d0c5b84e09e83d28248130884881845b9dd5bea6f8`.
- Schema V002 READY, database đã import và JDBC payload verification khớp.

### SKILL

- SKILL seed/database pipeline `VERIFIED_END_TO_END`.
- Candidate version 26: 72 option, 7 class, 91 template, 967 level,
  3883 level-option.
- Payload 42402 byte; SHA-256
  `4f13faa5d95653ff9d04945d0fe8a5146030526383944d22de1786c497155cf5`.
- Raw point được bảo toàn: level 957=150, 958=150, 962=140, 966=140.
- Schema V003 READY; database đã import và JDBC payload verification khớp.
- Runtime publish thử đã được tách khỏi startup; database không bị ghi.
- Windows full suite gần nhất do người dùng xác nhận: **235/235**, không failure/error/skipped.

### MAP đã xác nhận theo nhóm

- Codec MAP có bốn regression test hiện hữu.
- Người dùng đã xác nhận nhóm 13 test MAP codec/inventory/parser/converter PASS.
- Converter chỉ dựng catalog client offline; không nhập placement, zone hoặc runtime state.

## PENDING

- MAP chưa có schema/migration/importer và chưa publish runtime.
- Server startup chưa nối runtime snapshot client đầy đủ DATA/MAP/SKILL/ITEM/appearance.
- Client thật chưa vào gameplay.

## Kết quả review base 747958b → head 399646a5

### Đã đạt

- Không vi phạm namespace hoặc chiều dependency Architecture Lock.
- Không có migration/database write mới.
- SKILL command không tự nối startup; MAP vẫn là candidate offline.
- Atomic swap giữ snapshot cũ khi source/validation thất bại.

### Khắc phục trong checkpoint hiện tại

- Đóng đường bypass checksum: factory `SkillAssetRuntimeSnapshot.verified(...)` tính lại
  SHA-256 của payload trước khi tạo snapshot.
- JSON menu NPC từ chối control character `U+0000..U+001F` chưa escape.
- Loại bỏ hơn 1.000 dòng STATUS trùng/lệch thời gian; lịch sử vẫn nằm trong WORKLOG.
- Architecture gate buộc STATUS có đúng một next action và cấm AI tự công bố review.
- Quy tắc bắt buộc đã chuẩn hóa trong AGENTS, workflow, requirements và handoff.
- Toàn bộ năm MAP artifact test và ba regression test sửa review đã vượt full suite Windows
  **227/227**.

## Tác động hiện tại

- `databaseChanged=false`.
- `serverStartupWired=false`.
- Không publish runtime snapshot server trong checkpoint sửa review.
- Tiến độ giữ nguyên 17% vì đây là sửa integrity/documentation, chưa thêm gameplay.

## MAP archive/command đang xây

- `map-seed-convert <dump-path>` tạo candidate v7 cạnh dump, không JDBC.
- `map-seed-dry-run <archive-path>` decode và xác minh lại manifest/checksum.
- ZIP schema đóng: `map.bin` + `map.manifest`; cấm entry lạ/trùng/thiếu và ghi đè.
- Payload/manifest có hard size limit; export qua temporary file + atomic move.
- Validated archive trả defensive payload copy cho importer tương lai.
- Sáu regression test archive/command đã vượt full suite Windows **233/233**.

## Developer Manual đang xây

- Điểm vào quản trị tại `docs/developer-manual/README.md`.
- Catalog bao phủ 173 file production/12 package và API public/protected phát hiện được.
- Có kiến trúc/luồng, change playbook, troubleshooting, maintenance standard và 17 mục
  TRACE_REQUIRED cho gameplay/runtime chưa đủ dữ liệu.
- Generator catalog nằm tại `tools/generate-developer-catalog.sh`.
- Hai documentation gate bảo vệ source coverage và các trường quản trị bắt buộc.
- Hai documentation gate và toàn bộ Developer Manual đã vượt full suite Windows **235/235**.

## Next exact action

Tiếp tục MAP schema contract, migration draft và read-only schema preflight trên
`agent/document-nsokiss-runtime`. Chưa chạy migration/import và chưa merge `main`.
