# Trạng thái hiện tại của NSOCry

**Cập nhật:** 2026-09-09 UTC

**Trạng thái:** IN_PROGRESS

**Tiến độ đến gameplay cơ bản:** 18%

**Nguồn lịch sử:** `docs/project/WORKLOG.md` và các worklog checkpoint bổ sung.

## Git và quyền merge

- Repository: `mailphs2704-lgtm/nsocry-server`.
- Nhánh phát triển: `agent/document-nsokiss-runtime`.
- Draft PR: [#1](https://github.com/mailphs2704-lgtm/nsocry-server/pull/1).
- Chưa merge `main`; chỉ merge sau review độc lập, gate VERIFIED và chủ dự án xác nhận rõ `ĐỒNG Ý MERGE VÀO MAIN`.

## VERIFIED gần nhất

- Full Maven suite Windows: **321/321**, không failure/error/skipped.
- ITEM pipeline: JDBC VERIFIED.
- SKILL pipeline: JDBC VERIFIED.
- MAP pipeline và runtime publish command: VERIFIED; startup ownership chưa nối.
- DATA authoritative candidate version 7: 43 task group, 131 EXP, payload 85154 byte, SHA-256 `242a3551cc110c4eda9f8e40f06fcd0f0b0b2d32bcab6f1b07669dbd0c9b148b`.
- DATA archive convert/read-back: VERIFIED_END_TO_END_OFFLINE.
- Backup trước V005: 234839 byte, SHA-256 `9cea61d3482ec08a727b71f11c4400dd2c6144cc55b9450baf27bd6dd71983c6`.
- V005 migration và preflight sau migration: READY VERIFIED; full regression 321/321 PASS.

## Checkpoint đang xây

- Đã thêm `NSOCRY_WORK.bat` và `tools/nsocry-work.ps1`.
- Menu số `1` thực hiện pull fast-forward, Maven clean package, tạo và push báo cáo PASS/FAIL.
- Báo cáo gắn `TESTED_COMMIT`, lưu latest và history trên GitHub.
- Guard khóa đúng nhánh và không tự commit source.
- Windows runner: `VERIFIED_END_TO_END`; báo cáo mới xác nhận đúng commit `3279ce2e`, BUILD SUCCESS và 321/321 PASS.
- Tài liệu: `docs/operations/windows-work-menu.md`.
- Worklog: `docs/project/WORKLOG-WINDOWS-RUNNER.md`.

## Tác động và giới hạn

- Runner không chạy migration/import/startup.
- `databaseChanged=false` đối với thay đổi runner này.
- Trạng thái database toàn dự án vẫn là V005 đã tạo schema; `dataImported=false`.
- `runtimeSnapshotPublished=false`; `serverStartupWired=false`.
- Client thật chưa vào gameplay; tiến độ gameplay giữ nguyên 18%.

## DATA importer đang xây

- Đã thêm importer transaction SERIALIZABLE, row lock và explicit overwrite policy.
- Đã thêm database read-back/checksum verifier.
- Chưa có launcher command; không import database thật.
- Full suite Windows mới nhất: **324/324 PASS** tại commit `dcb9401a`.
- Transactional importer và ba test rollback/overwrite: VERIFIED.
- DATA database verifier command, launcher route và hai command tests: VERIFIED_BY_FULL_SUITE 327/327.
- Lần chạy đầu của ba JDBC verifier tests dừng ở testCompile: test gọi nhầm accessor `artifact.validation()` không tồn tại; production verifier không lỗi.
- Test đã sửa để decode payload và gọi `DataAssetSeedValidator.validate(..., artifact.manifest())` qua API hiện có; đang chờ chạy lại.
- BAT đã đổi để tác vụ tương tác quay lại menu đầu sau khi nhấn phím.

## Next exact action

Chủ dự án mở `NSOCRY_WORK.bat`, chọn `1` để xác minh bản sửa testCompile. Mục tiêu:
330/330 PASS. Database/import/runtime/startup không thay đổi.