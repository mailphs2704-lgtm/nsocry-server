# Tài liệu dự án NSOCry

Thư mục `docs/` là nguồn sự thật chính thức về yêu cầu, quyết định, tiến độ và kiến trúc NSOCry. Không được dựa riêng vào lịch sử trò chuyện để tiếp tục dự án.

## Đọc theo thứ tự

1. [START-HERE.md](START-HERE.md) — điểm vào bắt buộc cho người hoặc AI mới.
2. [project/REQUIREMENTS.md](project/REQUIREMENTS.md) — yêu cầu và nguyên tắc bất biến.
3. [project/STATUS.md](project/STATUS.md) — trạng thái mới nhất và bước tiếp theo chính xác.
4. [developer-manual/README.md](developer-manual/README.md) — sổ tay quản trị, tra cứu và sửa source.
5. [developer-manual/code-catalog.md](developer-manual/code-catalog.md) — catalog 199 source production.
6. [developer-manual/trace-register.md](developer-manual/trace-register.md) — phần chưa đủ dữ liệu và điều kiện đóng.
7. [project/ROADMAP.md](project/ROADMAP.md) — lộ trình tổng thể và tiêu chí hoàn thành.
8. [project/WORKLOG.md](project/WORKLOG.md) — nhật ký theo thời gian.
9. [architecture/architecture-lock.md](architecture/architecture-lock.md) — khung kiến trúc bắt buộc và quy trình thay đổi.
10. [architecture/planned-contracts.tsv](architecture/planned-contracts.tsv) — danh mục package/type/method máy có thể kiểm tra.
11. [architecture/reference-coverage.md](architecture/reference-coverage.md) — ánh xạ phạm vi NSOKISS sang NSOCry.
12. [architecture/current-source-inventory.md](architecture/current-source-inventory.md) — ảnh chụp lịch sử tại Architecture Lock v1.
13. [architecture/current-public-api.md](architecture/current-public-api.md) — ảnh chụp API tại Architecture Lock v1.
14. [architecture/overview.md](architecture/overview.md) — định hướng kiến trúc NSOCry.
15. [architecture/nsokiss-runtime.md](architecture/nsokiss-runtime.md) — hành vi runtime NSOKISS đã kiểm chứng.
16. [development/workflow.md](development/workflow.md) — quy trình làm việc và Git.
17. [development/documentation-standard.md](development/documentation-standard.md) — chuẩn tài liệu bắt buộc.
18. [handoff/AI-HANDOFF.md](handoff/AI-HANDOFF.md) — cách tiếp tục khi đổi Chat/Work/AI.
19. [handoff/OWNER-COLLABORATION.md](handoff/OWNER-COLLABORATION.md) — cách giao tiếp,
    thực hiện và trả kết quả bắt buộc khi làm việc với chủ dự án.

## Phân loại độ tin cậy

Mỗi tài liệu phải phân biệt:

- **CONFIRMED**: người dùng đã xác nhận yêu cầu/quyết định.
- **VERIFIED**: đã đối chiếu source, database, client, log hoặc kết quả chạy.
- **PROPOSED**: đề xuất chưa được chốt.
- **UNKNOWN**: chưa đủ dữ liệu, không được tự suy đoán.
- **SUPERSEDED**: quyết định cũ đã được thay thế, phải liên kết quyết định mới.

## Quy tắc cốt lõi

- NSOKISS chỉ là reference và phải giữ nguyên.
- NSOCry được viết lại có kiểm soát, không copy nguyên source.
- Database NSOCry tên `nsocry` và độc lập với database NSOKISS.
- Code và documentation phải phát triển cùng nhau.
- Không lặp lại bước đã được ghi **VERIFIED** trừ khi có bằng chứng trạng thái đã thay đổi.
- Không đánh dấu hoàn thành nếu chưa có tiêu chí kiểm chứng tương ứng.
