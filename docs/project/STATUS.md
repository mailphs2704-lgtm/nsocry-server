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

- Full Maven suite Windows: **321/321**, không failure/error/skipped.
- ITEM pipeline: version 26, 161 option, 1213 item, payload 66811 byte, JDBC VERIFIED.
- SKILL pipeline: version 26, 72 option, 7 class, 91 template, 967 level,
  3883 level-option, payload 42402 byte, JDBC VERIFIED.
- MAP pipeline: version 7, 177 map, 44 NPC, 258 mob, payload 14401 byte, JDBC VERIFIED.
- MAP runtime publish command đã xác minh count/length/SHA-256 trên JDBC thật; snapshot chỉ
  sống trong tiến trình command và startup vẫn chưa nối.

## Checkpoint đang xây

- Full Maven suite Windows: **321/321 VERIFIED**.
- DATA authoritative candidate: **VERIFIED**.
- Version 7; 43 task group; 131 EXP; payload length 85154 byte.
- SHA-256: `242a3551cc110c4eda9f8e40f06fcd0f0b0b2d32bcab6f1b07669dbd0c9b148b`.
- `databaseChanged=false`, `runtimeSnapshotPublished=false`,
  `serverStartupWired=false`.
- Handoff, Developer Manual, trace register, package index và DATA inventory đã đồng bộ tại
  checkpoint tạm dừng; code catalog đã tái sinh đủ 199 source production.
- DATA manifest parser canonical, archive service và command `data-seed-convert` /
  `data-seed-archive-dry-run`: **VERIFIED_END_TO_END_OFFLINE**.
- Windows `mvn clean package`: 205 production source, 74 test source, **314/314 PASS**.
- Candidate archive authoritative đã tạo và dry-run độc lập: version 7, 43 task group,
  131 EXP, 85154 byte, SHA-256
  `242a3551cc110c4eda9f8e40f06fcd0f0b0b2d32bcab6f1b07669dbd0c9b148b`.
- `archiveRoundTripVerified=true`; database/runtime/startup đều false.
- DATA persistence V005 draft, schema contract, JDBC inspector và preflight command:
  **VERIFIED_BY_FULL_SUITE 321/321**; chưa chạy database thật.
- Build đã compile 210 production source và 76 test source trên Java 17.
- Database NSOCry `127.0.0.1:3306/nsocry` đã kết nối được; preflight thật báo
  **NOT_READY VERIFIED**, đúng bảy cột V005 còn thiếu và `databaseChanged=false`.
- Ba lần dump thủ công dùng password prompt thất bại với MariaDB 1045 và sinh file 0 byte;
  các file này không phải backup. Script backup fail-closed đã thêm, đang PENDING chạy Windows.
- Backup hợp lệ trước V005: `nsocry-before-v005-20260901-174146.sql`, 234839 byte,
  SHA-256 `9cea61d3482ec08a727b71f11c4400dd2c6144cc55b9450baf27bd6dd71983c6`,
  `databaseChanged=false`, `v005Executed=false` — **VERIFIED**.
- Chủ dự án đã xác nhận `ĐỒNG Ý CHẠY MIGRATION V005 TRÊN DATABASE NSOCRY`.
- Script migration fail-closed khóa backup/target/pre-post preflight đã triển khai,
  `IMPLEMENTED_PENDING_RUN_WINDOWS`; V005 vẫn chưa chạy tại thời điểm commit.
- Lần chạy đầu dừng ở stderr NOT_READY do PowerShell ErrorActionPreference; migration command
  chưa được gọi, `databaseChanged=false`. Bản sửa chỉ cho phép capture stderr có kiểm soát.

## Tác động và giới hạn

- `databaseChanged=false`.
- `runtimeSnapshotPublished=false` trong checkpoint này.
- `serverStartupWired=false`.
- DATA candidate authoritative đã VERIFIED; archive/persistence/runtime DATA và appearance
  production vẫn `TRACE_REQUIRED`. Archive offline đã VERIFIED; persistence/runtime DATA chưa
  thiết kế hoặc triển khai. Không được dùng bundle rỗng/số liệu giả để bật startup.
- Client thật chưa vào gameplay.

## Next exact action

Chạy script V005 fail-closed trên Windows, xác nhận preflight READY và các cờ tác động; sau đó
chạy full regression. Chưa import DATA, chưa publish runtime, chưa nối startup và chưa merge
`main`.
