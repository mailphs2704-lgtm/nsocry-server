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
