# Trạng thái hiện tại của NSOCry

**Cập nhật:** 2026-09-10 UTC

**Trạng thái:** IN_PROGRESS

**Tiến độ đến gameplay cơ bản:** 20%

**Nguồn lịch sử:** `docs/project/WORKLOG.md` và các worklog checkpoint bổ sung.

## Git và quyền merge

- Repository: `mailphs2704-lgtm/nsocry-server`.
- Nhánh phát triển: `agent/document-nsokiss-runtime`.
- Draft PR: [#1](https://github.com/mailphs2704-lgtm/nsocry-server/pull/1).
- Chưa merge `main`; chỉ merge sau review độc lập, gate VERIFIED và chủ dự án xác nhận rõ `ĐỒNG Ý MERGE VÀO MAIN`.

## VERIFIED gần nhất

- Full Maven suite Windows mới nhất: **365/365**, không failure/error/skipped, đúng commit `8458c327`.
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
- Full suite Windows: **330/330 PASS**; DATA persistence/read-back VERIFIED.
- Import safety gate offline đã thêm: backup file/size/SHA-256, candidate constant-time confirmation và explicit overwrite mode.
- Bốn safety-gate tests đang chờ Windows full suite; chưa có command DML/launcher route.
- BAT đã đổi để tác vụ tương tác quay lại menu đầu sau khi nhấn phím.

## Checkpoint mới nhất

- Windows full suite: **334/334 PASS** tại commit `631bc9e7`.
- Import safety gate backup/checksum/overwrite: VERIFIED_BY_FULL_SUITE.
- Git auto-gc suppression: VERIFIED trong đúng lần chạy này.
- Exact operator confirmation parser và bốn tests: VERIFIED_BY_FULL_SUITE 338/338.
- Import workflow orchestration và bốn tests: VERIFIED_BY_FULL_SUITE 342/342.
- `data-seed-import-plan` offline, launcher route và tests: VERIFIED_BY_FULL_SUITE 344/344.
- BAT option 5 và auto-published offline plan report: VERIFIED_END_TO_END trên Windows.
- Plan đúng commit `6dfedd5a`, DATA v7, candidate SHA-256 authoritative, backup 234839 byte/checksum checkpoint và `REJECT_EXISTING`.
- `databaseConnectionOpened=false`, `databaseChanged=false`, `dataImported=false`, runtime/startup false.
- Plan command không tạo DataSource và không gọi importer.
- Workflow chưa route launcher; database không thay đổi.
- Chưa có import launcher route; database không thay đổi.

## Sự cố vận hành đã xử lý

Lựa chọn 1 đã fast-forward đến checkpoint safety gate nhưng Git auto-maintenance bị Windows khóa
file pack index và hỏi retry. Build 334 chưa bắt đầu; không phải lỗi source/database. Runner đã
được sửa để tắt auto-gc/maintenance trong các lệnh pull/commit/push.

## Quyền mới đã nhận

Chủ dự án đã cho phép import DATA v7 vào database NSOCry bằng `REJECT_EXISTING`. Không cho
phép overwrite, runtime publish, startup wiring hoặc merge main. Command thật đã triển khai và
khóa cứng phạm vi này; chưa chạy database.

## Gate trước import

- Local divergence đã recovery bằng rebase; báo cáo GitHub đúng commit `a04c6f9c`.
- Full suite Windows: **347/347 PASS**.
- Command DATA import REJECT_EXISTING: VERIFIED_BY_FULL_SUITE.
- BAT option 6 đã thêm: pull, full build lại, import, read-back và auto-publish report.
- Database chưa import tại thời điểm cập nhật này.

## DATA v7 database import

- Windows pre-import full suite: 347/347 PASS.
- Import report đúng commit `0b956467`, exit code 0, `IMPORTED_AND_VERIFIED`.
- Version 7, 43 task group, 131 EXP, payload 85154 byte.
- SHA-256 `242a3551cc110c4eda9f8e40f06fcd0f0b0b2d32bcab6f1b07669dbd0c9b148b`.
- `overwritten=false`, `databaseChanged=true`, `dataImported=true`.
- Transaction commit và database read-back checksum đều đạt.
- `runtimeSnapshotPublished=false`, `serverStartupWired=false`.
- DATA runtime tranche: đã thêm JDBC source, immutable snapshot, atomic store, publish service và 6 unit tests; đang chờ full suite.
- DataAssetSource contract đã tồn tại và được tái sử dụng, không tạo contract trùng.

## DATA runtime pipeline

- Full suite Windows: **353/353 PASS** tại commit `bbb62e16`.
- Runtime snapshot, defensive copy, atomic store và publish service: VERIFIED_BY_FULL_SUITE.
- JDBC DATA source và ba tests: VERIFIED_BY_FULL_SUITE 356/356.
- DATA runtime publish command cô lập, bốn command tests và launcher route: **VERIFIED_BY_FULL_SUITE 361/361**.
- BAT option 7: **VERIFIED_END_TO_END** trên Windows; report exit 0, DATA v7/checksum authoritative, database read-only và `PUBLISHED_ISOLATED`.
- Startup readiness gate trong atomic store: **VERIFIED_BY_FULL_SUITE 365/365**.
- TCP readiness hook: **VERIFIED_BY_FULL_SUITE 368/368**.
- DATA startup composition: **VERIFIED_BY_FULL_SUITE 370/370**.
- Production DATA startup wiring: **VERIFIED_END_TO_END_WINDOWS**; 372/372 PASS và smoke `STARTED_READY_AND_STOPPED` tại commit `a9ebc146`.
- Application DATA ownership: **VERIFIED_BY_FULL_SUITE 374/374**.
- `PostLoginDataSyncService`: **VERIFIED_BY_FULL_SUITE 378/378**.
- Full post-login asset sync: **VERIFIED_BY_FULL_SUITE 382/382**.
- Đã thêm response sizing plan: short ≤65535, full-size trong configured limit; ba test đang chờ full suite.
- Layout wire bên trong command `-32` vẫn TRACE_REQUIRED; chưa nối transport.
- Đã thêm BAT option 8: full build, bounded startup smoke, xác nhận DATA READY/TCP bind, cleanup đúng PID và push report.
- Smoke test server thật chưa chạy; chưa tuyên bố runtime production READY.

## Next exact action

Chủ dự án mở `NSOCRY_WORK.bat`, chọn `1` để kiểm chứng full-size codec theo fixture
authoritative. Mục tiêu dự kiến: **386/386 PASS**. Chưa nối production session loop.
