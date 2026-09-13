# Phục hồi NSOCry trên máy Windows mới — 2026-09-13

## Phạm vi

Tài liệu này ghi lại việc nối lại repository tại `D:\nsocry-server`, dựng database `nsocry` sạch trên MySQL 8.0.30, import lại client assets và kiểm chứng startup. Không phục hồi được tài khoản/người chơi từ máy cũ vì không có bản SQL chứa dữ liệu đó; database mới bắt đầu với bảng `accounts` rỗng.

## Môi trường đã xác minh

- Repository: `mailphs2704-lgtm/nsocry-server`.
- Nhánh: `agent/document-nsokiss-runtime`.
- Java: Eclipse Temurin 17.0.20.1.
- Maven: Apache Maven 3.9.16 tại `D:\tools\apache-maven-3.9.16`.
- Database: MySQL 8.0.30 của Laragon, `127.0.0.1:3306/nsocry`.
- Server TCP: `0.0.0.0:14444`.

Credential thật chỉ nằm trong `config/nsocry.properties` cục bộ và không được commit. Mật khẩu không xuất hiện trong báo cáo hoặc tài liệu.

## Dựng schema

Database mới ban đầu không tồn tại. Đã chạy:

1. `database/00-create-database.sql`;
2. V001 account authentication;
3. V002 ITEM assets;
4. V003 SKILL assets;
5. V004 MAP assets;
6. V005 DATA assets.

Kết quả có 13 bảng. DATA, ITEM, SKILL và MAP schema preflight đều `READY`.

## Backup nền trước import

- File cục bộ: `backups/nsocry-clean-schema-20260912-224141.sql`.
- Size: 15.691 byte.
- SHA-256: `57da0355c176e6b632435fb928204baede35022e8910727eeb29742037c2bf1a`.
- Backup được tạo sau migration và trước import asset.

File backup chứa credential hoặc dữ liệu vận hành không được commit lên repository.

## Archive authoritative và kết quả import

| Nhóm | Version | Nội dung | Payload SHA-256 |
|---|---:|---|---|
| ITEM | 26 | 161 option, 1.213 item | `abb320fb8a940fc28c49c6d0c5b84e09e83d28248130884881845b9dd5bea6f8` |
| SKILL | 26 | 72 option, 7 class, 91 template, 967 level, 3.883 level-option | `4f13faa5d95653ff9d04945d0fe8a5146030526383944d22de1786c497155cf5` |
| MAP | 7 | 177 map, 44 NPC, 258 mob | `1d97991f932960340e4097b86b39ffd6b67bccdca158d025a018ff4af344a8de` |
| DATA | 7 | 43 task group, 131 EXP, 85.154 byte | `242a3551cc110c4eda9f8e40f06fcd0f0b0b2d32bcab6f1b07669dbd0c9b148b` |

ITEM, SKILL và MAP import transaction thành công. DATA dùng quyền `REJECT_EXISTING`, trả `IMPORTED_AND_VERIFIED` và `overwritten=false`. Database read-back của cả bốn nhóm khớp archive và payload checksum.

Bốn chênh lệch raw byte của SKILL ở level 957, 958, 962 và 966 đã được validator ghi nhận; archive vẫn VERIFIED và không được âm thầm sửa giá trị.

## Runtime và startup

Các command publish cô lập của SKILL, MAP và DATA đều thành công, database read-only. Startup smoke trên Windows:

- report commit: `95ca540d`;
- tested source commit: `d1cccdaf`;
- Maven suite: 386/386 PASS;
- trạng thái: `STARTED_READY_AND_STOPPED`;
- output có `NSOCry server started on /0.0.0.0:14444`;
- output có `DATA runtime snapshot READY version=7`;
- stderr rỗng;
- runner dừng đúng process;
- database không đổi trong smoke.

Báo cáo nguồn: `reports/windows/latest-data-startup-smoke.md`.

## Sự cố và biện pháp phòng ngừa

Maven đã được cài ở ổ D nhưng CMD mở BAT không có Maven trong PATH. Runner ban đầu tiếp tục sau lỗi thiếu `mvn` và gặp lỗi phụ khi stdout là null. Từ commit `d1cccdaf`, mọi menu cần build dùng chung Maven auto-discovery qua PATH, MAVEN_HOME hoặc `tools/apache-maven-*` trên ổ repository; startup dừng fail-closed nếu thiếu Java/Maven và xử lý stdout rỗng an toàn.

Commit báo cáo smoke ban đầu bị push reject vì remote có commit tài liệu mới. Commit report-only được rebase an toàn rồi push thành `95ca540d`.

## Trạng thái sau phục hồi

- Repository và môi trường build: VERIFIED.
- Schema và bốn nhóm client asset: VERIFIED.
- DATA startup ownership/TCP bind: VERIFIED.
- Account/player cũ: không được phục hồi vì không có SQL máy cũ.
- Gameplay runtime ngoài phạm vi asset/startup vẫn tiếp tục theo roadmap; không suy diễn rằng game đã hoàn chỉnh.
