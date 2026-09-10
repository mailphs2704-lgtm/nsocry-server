# Menu làm việc Windows NSOCry

## Mục đích

`NSOCRY_WORK.bat` tạo một điểm chạy duy nhất cho chủ dự án. Khi AI yêu cầu kiểm chứng trên Windows, người dùng mở file BAT và chọn một số; script sẽ tự pull đúng nhánh, build/test và gửi báo cáo gọn lên GitHub.

Công cụ này không chạy migration, không import DATA, không publish runtime snapshot và không khởi động server.

## Cách chạy

Mở CMD:

```bat
cd /d "C:\Users\15130\Desktop\Project NSOCRY\nsocry-server"
NSOCRY_WORK.bat
```

Sau đó chọn:

| Số | Công việc |
|---:|---|
| 1 | Pull fast-forward, chạy `mvn clean package`, tạo và push báo cáo. Đây là lựa chọn mặc định khi AI yêu cầu chạy kiểm chứng. |
| 2 | Chỉ pull đúng nhánh làm việc. |
| 3 | Build/test và push báo cáo mà không pull. Chỉ dùng khi AI yêu cầu rõ. |
| 4 | Xem báo cáo gần nhất đã lưu trên máy. |
| 5 | Chạy DATA import plan offline bằng JAR và file example; không mở database. |
| 6 | Sau quyền riêng và gate 347/347, build lại rồi import DATA v7 bằng REJECT_EXISTING và push báo cáo. |
| 0 | Thoát. |

Có thể chạy trực tiếp `NSOCRY_WORK.bat 1`, nhưng chế độ menu dễ dùng hơn.

## Luồng của lựa chọn 1

1. Xác nhận đây là Git repository và đang ở nhánh `agent/document-nsokiss-runtime`.
2. Dừng an toàn nếu tracked source đang có thay đổi chưa commit hoặc có file chưa theo dõi trong `src/`, `tools/`.
3. Chạy `git pull --ff-only`; không merge tự động và không đẩy vào `main`.
4. Ghi nhận SHA commit thực sự được kiểm tra.
5. Chạy `mvn clean package` bằng Java/Maven trên máy Windows.
6. Tạo `reports/windows/latest.md` và một bản lịch sử theo thời gian.
7. Chỉ stage đúng hai file báo cáo; không dùng `git add .`, không tự commit source.
8. Commit và push báo cáo lên nhánh làm việc, kể cả khi Maven báo FAILURE.

## Đọc kết quả

Cuối màn hình có các khóa:

- `NSOCRY_WORKFLOW_RESULT=REPORT_PUBLISHED`: GitHub đã nhận báo cáo.
- `BUILD_STATUS=SUCCESS|FAILURE`: kết quả Maven.
- `TEST_SUMMARY=...`: tổng hợp test tìm thấy trong log.
- `TESTED_COMMIT=...`: commit source được kiểm tra.
- `REPORT_COMMIT=...`: commit chỉ chứa báo cáo.
- Các cờ database/DATA/runtime/startup luôn `false` trong workflow này.

Sau khi người dùng báo “xong”, AI phải đọc `reports/windows/latest.md` trên GitHub, đối chiếu `TESTED_COMMIT`, giải thích kết quả và tiếp tục đúng công đoạn kế tiếp. Nếu FAILURE, AI đọc phần cuối log trong báo cáo, sửa code/tài liệu, push fix rồi yêu cầu chạy lại lựa chọn 1.

## Nhật ký và an toàn

Log Maven đầy đủ nằm tại `.nsocry-work/maven-latest.log` và bị Git bỏ qua. GitHub chỉ nhận báo cáo cùng 80 dòng cuối log, tránh làm repository phình lớn.

Nếu push thất bại, script giữ commit báo cáo ở local và in `REPORT_COMMITTED_PUSH_FAILED`. Không chạy lại build ngay; kiểm tra mạng/quyền Git rồi dùng `git push origin agent/document-nsokiss-runtime`, hoặc báo AI để xử lý.

Nếu báo sai nhánh hoặc working tree bẩn, không xóa file và không reset. Hãy báo nguyên văn lỗi cho AI.

## Tác động dự án

Đây là công cụ vận hành/kiểm chứng, không làm tăng trực tiếp phần trăm gameplay. Nó rút ngắn vòng lặp test Windows và cung cấp bằng chứng có commit SHA, từ đó giảm việc chép log thủ công và tránh kết luận PASS từ log cũ.


## Quay lại menu sau tác vụ

Khi chạy bằng menu tương tác, sau khi tác vụ hoàn tất hoặc báo lỗi, nhấn phím tại màn hình
`pause`; BAT tự quay về trang menu đầu. Chỉ lựa chọn `0` mới đóng BAT. Chế độ gọi trực tiếp
có argument, ví dụ `NSOCRY_WORK.bat 1`, vẫn kết thúc và trả exit code cho automation.


## Git hỏi unlink pack.idx

Nếu Windows đang giữ file `.git/objects/pack/*.idx`, auto-gc của Git có thể hỏi
`Should I try again? (y/n)`. Trả lời `n`; đây là lỗi dọn object sau pull, không phải lỗi
source hoặc Maven. Runner từ checkpoint này truyền `gc.auto=0` và
`maintenance.auto=false` cho pull/commit/push để công việc tương tác không bị chặn. Việc
repack thủ công chỉ thực hiện sau khi đóng IDE/Git process và không thuộc build gate.


## Lựa chọn 5 — DATA plan offline

Lựa chọn 5 yêu cầu JAR đã được build và dùng
`config/data-import-plan.properties.example`. Script chỉ validate archive/backup/confirmation.
Output thành công phải có `DATA_IMPORT_PLAN_REPORT_PUBLISHED`,
`DATABASE_CONNECTION_OPENED=false`, `DATABASE_CHANGED=false`, `DATA_IMPORTED=false`.


Kết quả lựa chọn 5 được commit/push vào `reports/windows/latest-data-import-plan.md` để AI kiểm tra sau khi chủ dự án chỉ cần báo “xong”. Báo cáo không chứa database password hoặc payload.


## Tránh report push bị fetch-first

Lựa chọn 5 từ checkpoint này luôn chạy pull fast-forward trước offline plan. Điều này tránh tạo
commit báo cáo trên branch local cũ khi AI vừa push code mới. Nếu một report-only commit cũ đã
bị reject, dùng pull --rebase để đặt commit báo cáo lên remote mới; không reset hoặc xóa report.


## Lựa chọn 6 — DATA v7 import được cấp quyền

Lựa chọn 6 chỉ được thêm sau xác nhận riêng của chủ dự án và full suite 347/347 PASS. Script tự
pull, chạy lại `mvn clean package`, sau đó mới gọi `data-seed-import`. Command khóa
`REJECT_EXISTING`; nếu version 7 đã tồn tại sẽ fail thay vì overwrite. Thành công bắt buộc có
`DATA seed IMPORTED_AND_VERIFIED` và `overwritten=false`.

Báo cáo được push tại `reports/windows/latest-data-import.md`. Nếu exit/output không chứng minh
cả import và read-back, trạng thái là `FAILED_OR_UNCERTAIN`; không publish runtime/startup và
phải dừng điều tra.
