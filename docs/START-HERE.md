# START HERE — Bàn giao nhanh dự án NSOCry

> Đây là file đầu tiên phải đọc khi bắt đầu phiên làm việc mới.

## Mục tiêu

Xây dựng server game **NSOCry** mới, tương thích với client được cung cấp, dựa trên việc hiểu hành vi của NSOKISS nhưng không sao chép nguyên kiến trúc/source. Người dùng không phải lập trình viên, vì vậy source và tài liệu phải đủ rõ để họ có thể lần theo và quản lý dự án.

## Tài sản hiện có

- Repository: `mailphs2704-lgtm/nsocry-server`.
- NSOKISS reference: `source-reference/NSOKISS-inspection/`.
- Client tham khảo: `V7_217_X1.jar`.
- Database NSOKISS đang chạy không phải database đích của NSOCry.

## Trạng thái hiện tại

- Nhánh phát triển: `agent/document-nsokiss-runtime`; Draft PR #1; chưa merge `main`.
- Architecture Lock v1 đang hiệu lực.
- Trạng thái quản trị: `IN_PROGRESS` theo yêu cầu tiếp tục ngày 2026-09-09.
- Windows full suite gần nhất trước runner: **321/321 VERIFIED**.
- DATA V005 migration: READY VERIFIED; database schema đã đổi, DATA chưa import, runtime/startup chưa nối.
- Windows one-number runner đã triển khai, đang chờ lần chạy thật đầu tiên trên máy chủ dự án.
- Tiến độ đến gameplay cơ bản: 18%.

Đọc chi tiết tại [project/STATUS.md](project/STATUS.md).
Hướng dẫn menu tại [operations/windows-work-menu.md](operations/windows-work-menu.md).
Khi cần hiểu hoặc sửa code, bắt đầu tại [developer-manual/README.md](developer-manual/README.md).

## Ràng buộc không được vi phạm

1. Không sửa, build hoặc chạy source NSOKISS.
2. Không dùng database NSOKISS làm database phát triển NSOCry.
3. Không copy hàng loạt class NSOKISS sang namespace mới.
4. Không bắt người dùng làm lại checkpoint VERIFIED nếu input không đổi.
5. Mỗi thay đổi code phải cập nhật tài liệu và worklog tiếng Việt.
6. Không tự quyết gameplay/business rule chưa được người dùng mô tả.
7. Không chạy migration/import database nếu thiếu backup, preflight và xác nhận riêng mới.
8. Không merge `main` nếu chưa có review độc lập, gate VERIFIED và xác nhận rõ của chủ dự án.

## Cách làm việc mới trên Windows

Khi AI yêu cầu chạy kiểm chứng:

```bat
cd /d "C:\Users\15130\Desktop\Project NSOCRY\nsocry-server"
NSOCRY_WORK.bat
```

Chọn `1`. BAT tự pull, build/test và push báo cáo lên GitHub. Khi thấy `NSOCRY_WORKFLOW_RESULT=REPORT_PUBLISHED`, báo “xong”; AI đọc báo cáo, giải thích và tiếp tục hoặc sửa lỗi.

## Trước khi kết thúc mỗi phiên

- Cập nhật STATUS.
- Thêm entry WORKLOG.
- Ghi commit/branch và kết quả kiểm chứng.
- Ghi đúng một “Next exact action”.
