# Trạng thái hiện tại của NSOCry

**Cập nhật:** 2026-09-01 UTC

**Trạng thái:** IN_PROGRESS

**Tiến độ đến gameplay cơ bản:** 18%

**Nguồn lịch sử:** `docs/project/WORKLOG.md`

STATUS chỉ mô tả checkpoint hiện tại; lịch sử chi tiết nằm trong WORKLOG.

## Git và quyền merge

- Repository: `mailphs2704-lgtm/nsocry-server`.
- Nhánh phát triển: `agent/document-nsokiss-runtime`.
- Draft PR: [#1](https://github.com/mailphs2704-lgtm/nsocry-server/pull/1).
- Chưa merge `main`; chỉ merge sau review độc lập, gate VERIFIED và chủ dự án xác nhận rõ
  `ĐỒNG Ý MERGE VÀO MAIN`.

## VERIFIED gần nhất

- Full Maven suite Windows: **308/308**, không failure/error/skipped.
- ITEM pipeline: version 26, 161 option, 1213 item, payload 66811 byte, JDBC VERIFIED.
- SKILL pipeline: version 26, 72 option, 7 class, 91 template, 967 level,
  3883 level-option, payload 42402 byte, JDBC VERIFIED.
- MAP pipeline: version 7, 177 map, 44 NPC, 258 mob, payload 14401 byte, JDBC VERIFIED.
- MAP runtime publish command đã xác minh count/length/SHA-256 trên JDBC thật; snapshot chỉ
  sống trong tiến trình command và startup vẫn chưa nối.

## Checkpoint đang xây

- Full Maven suite Windows: **308/308 VERIFIED**.
- DATA authoritative candidate: **VERIFIED**.
- Version 7; 43 task group; 131 EXP; payload length 85154 byte.
- SHA-256: `242a3551cc110c4eda9f8e40f06fcd0f0b0b2d32bcab6f1b07669dbd0c9b148b`.
- `databaseChanged=false`, `runtimeSnapshotPublished=false`,
  `serverStartupWired=false`.
- Handoff, Developer Manual, trace register, package index và DATA inventory đã đồng bộ tại
  checkpoint tạm dừng; code catalog đã tái sinh đủ 199 source production.
- DATA manifest parser canonical, archive service và command `data-seed-convert` /
  `data-seed-archive-dry-run`: **IMPLEMENTED_PENDING_VERIFY**.
- Môi trường Work có Java runtime nhưng thiếu Maven/JDK compiler; chưa có output test mới và
  không được cộng vào mốc 308/308 cũ.

## Tác động và giới hạn

- `databaseChanged=false`.
- `runtimeSnapshotPublished=false` trong checkpoint này.
- `serverStartupWired=false`.
- DATA candidate authoritative đã VERIFIED; archive/persistence/runtime DATA và appearance
  production vẫn `TRACE_REQUIRED`. Archive read-back hiện PENDING_VERIFY; không được dùng bundle
  rỗng/số liệu giả để bật startup.
- Client thật chưa vào gameplay.

## Next exact action

Chạy full Maven suite cho DATA archive checkpoint, sửa failure nếu có, rồi tạo/dry-run archive
thật từ config authoritative để xác nhận giữ nguyên version 7, 43 task group, 131 EXP, payload
85154 byte và SHA-256 đã khóa; chưa ghi DATA vào database, chưa nối startup và chưa merge `main`.
