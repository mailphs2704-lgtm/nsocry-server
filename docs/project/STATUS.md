# Trạng thái hiện tại của NSOCry

**Cập nhật:** 2026-08-26 UTC

**Trạng thái:** DATA_JSON_SIMPLE_COMPAT_PENDING_WINDOWS

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

- Full Maven suite Windows: **303/303**, không failure/error/skipped.
- ITEM pipeline: version 26, 161 option, 1213 item, payload 66811 byte, JDBC VERIFIED.
- SKILL pipeline: version 26, 72 option, 7 class, 91 template, 967 level,
  3883 level-option, payload 42402 byte, JDBC VERIFIED.
- MAP pipeline: version 7, 177 map, 44 NPC, 258 mob, payload 14401 byte, JDBC VERIFIED.
- MAP runtime publish command đã xác minh count/length/SHA-256 trên JDBC thật; snapshot chỉ
  sống trong tiến trình command và startup vẫn chưa nối.

## Checkpoint đang xây

- Effect image legacy short narrowing: **303/303 VERIFIED**.
- Dump và database live `nsotien_0.nj_part.id=295` đều giữ object thiếu comma tại
  `{"dx":-5"dy":-9,"id":7665}`.
- Reference dùng `json-simple 1.1`; compatibility parser cho phép thiếu comma giữa hai member
  object chỉ khi token kế tiếp bắt đầu bằng key string. Array thiếu comma và cú pháp khác vẫn fail.
- Ba test mới khóa acceptance, exact PART wire và array strictness; full suite Windows mục tiêu
  **306/306** đang PENDING.

## Tác động và giới hạn

- `databaseChanged=false`.
- `runtimeSnapshotPublished=false` trong checkpoint này.
- `serverStartupWired=false`.
- DATA và appearance production vẫn `TRACE_REQUIRED`; không được dùng bundle rỗng/số liệu giả
  để bật startup.
- Client thật chưa vào gameplay.

## Next exact action

Pull checkpoint, chạy full Maven suite Windows 306/306, package lại JAR rồi chạy lại
`data-seed-dry-run data-dry-run.properties`. Chưa ghi database, chưa nối startup và chưa merge
`main`.
