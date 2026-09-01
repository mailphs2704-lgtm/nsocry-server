# Worklog checkpoint DATA V005

**Ngày:** 2026-09-01 UTC  
**Branch:** `agent/document-nsokiss-runtime`  
**Trạng thái:** `PAUSED_BY_OWNER`

## Mục tiêu đã thực hiện

Đưa DATA authoritative từ candidate trong bộ nhớ sang archive offline có thể kiểm định độc lập,
sau đó chuẩn bị và chạy schema persistence V005 an toàn trên database NSOCry riêng.

## Kết quả VERIFIED

- DATA candidate: version 7, 43 task group, 131 EXP, payload 85154 byte.
- SHA-256 payload:
  `242a3551cc110c4eda9f8e40f06fcd0f0b0b2d32bcab6f1b07669dbd0c9b148b`.
- Archive `data.bin` + `data.manifest`: convert/read-back độc lập VERIFIED;
  `archiveRoundTripVerified=true`.
- Full suite archive checkpoint: 314/314 PASS.
- V005 migration draft, schema contract, JDBC inspector và preflight command: 321/321 PASS.
- Preflight database trước V005: NOT_READY, đúng bảy cột còn thiếu,
  `databaseChanged=false`.
- Backup trước migration: `nsocry-before-v005-20260901-174146.sql`, 234839 byte,
  SHA-256 `9cea61d3482ec08a727b71f11c4400dd2c6144cc55b9450baf27bd6dd71983c6`.
- Chủ dự án xác nhận rõ: `ĐỒNG Ý CHẠY MIGRATION V005 TRÊN DATABASE NSOCRY`.
- Migration V005: VERIFIED; preflight sau migration READY, `databaseChanged=true`.
- Full regression sau migration: 321/321 PASS.

## Tác động và giới hạn

- Database schema đã thêm bảng `client_data_assets` và constraint V005.
- Chưa import DATA candidate; bảng chưa được dùng làm nguồn runtime.
- `dataImported=false`.
- `runtimeSnapshotPublished=false`.
- `serverStartupWired=false`.
- Không sửa/chạy source NSOKISS và không dùng database NSOKISS làm database đích.
- Không merge `main`.

## Sự cố đã xử lý

- Fixture test DATA task route truyền sai kiểu byte/list; đã sửa và full suite đạt.
- MariaDB ban đầu chưa lắng nghe; sau khi Laragon chạy, preflight kết nối đúng database `nsocry`.
- Dump thủ công dùng password prompt sinh file 0 byte; các file đó bị loại khỏi bằng chứng.
- Script backup fail-closed đọc credential từ properties, khóa database đích, size và checksum.
- PowerShell ban đầu coi stderr NOT_READY là terminating error; đã sửa capture có kiểm soát.

## Điểm dừng và bước tiếp theo

Dự án tạm dừng theo yêu cầu chủ dự án. Khi tiếp tục, bắt đầu từ DATA transactional importer nhận
`ValidatedDataAssetSeedArchive`, kèm overwrite gate, transaction rollback test và database
checksum verifier. Không chạy import database thật nếu chưa có preflight, backup/checksum và xác
nhận riêng mới.
