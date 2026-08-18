# Tài liệu dự án NSOCry

Thư mục `docs/` là nguồn sự thật chính thức về yêu cầu, quyết định, tiến độ và kiến trúc NSOCry. Không được dựa riêng vào lịch sử trò chuyện để tiếp tục dự án.

## Đọc theo thứ tự

1. [START-HERE.md](START-HERE.md) — điểm vào bắt buộc cho người hoặc AI mới.
2. [project/REQUIREMENTS.md](project/REQUIREMENTS.md) — yêu cầu và nguyên tắc bất biến.
3. [project/STATUS.md](project/STATUS.md) — trạng thái mới nhất và bước tiếp theo chính xác.
4. [project/ROADMAP.md](project/ROADMAP.md) — lộ trình tổng thể và tiêu chí hoàn thành.
5. [project/WORKLOG.md](project/WORKLOG.md) — nhật ký theo thời gian.
6. [architecture/overview.md](architecture/overview.md) — định hướng kiến trúc NSOCry.
7. [architecture/nsokiss-runtime.md](architecture/nsokiss-runtime.md) — hành vi runtime NSOKISS đã kiểm chứng.
8. [development/workflow.md](development/workflow.md) — quy trình làm việc và Git.
9. [development/documentation-standard.md](development/documentation-standard.md) — chuẩn tài liệu bắt buộc.
10. [handoff/AI-HANDOFF.md](handoff/AI-HANDOFF.md) — cách tiếp tục khi đổi Chat/Work/AI.

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
