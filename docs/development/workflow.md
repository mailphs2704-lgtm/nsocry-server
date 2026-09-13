# Quy trình phát triển NSOCry

## 1. Nguyên tắc nhánh

- `main`: checkpoint ổn định, không thử nghiệm trực tiếp.
- **CONFIRMED:** nhánh phát triển chung hiện tại là `agent/document-nsokiss-runtime`.
- Mọi AI tiếp quản phải tiếp tục/push vào nhánh trên; không tự tạo nhánh khác nếu chủ dự án
  chưa yêu cầu, tránh phân tán checkpoint.
- Mở Draft PR sớm để giữ checkpoint và cho phép kiểm tra.
- Cấm push trực tiếp hoặc tự merge vào `main`.
- Sau mỗi khoảng 5–10 commit phải có một lượt rà soát diff, Architecture Lock, test, tài
  liệu và tác động database/runtime bởi phiên Codex được chủ dự án giao review.
- Chỉ merge vào `main` khi review đạt, tiêu chí hoàn thành/documentation đủ và chủ dự án
  xác nhận rõ `ĐỒNG Ý MERGE VÀO MAIN` hoặc câu có ý nghĩa tương đương.
- Tác giả/AI tạo commit không được tự đánh dấu commit của mình `REVIEWED`; reviewer độc lập
  phải ghi base SHA, head SHA, findings, test evidence và kết luận rõ.

## 2. Chu trình một task

1. Đọc START-HERE, REQUIREMENTS, STATUS, WORKLOG mới nhất, Architecture Lock,
   contract manifest và ADR liên quan.
2. Xác định task có nằm đúng “Next exact action” không.
3. Đọc source/reference tối thiểu cần thiết.
4. Ghi rõ facts VERIFIED, giả thuyết PROPOSED và UNKNOWN.
5. Thực hiện thay đổi nhỏ, có ranh giới.
6. Chạy kiểm chứng phù hợp.
7. Review diff để tránh thay đổi ngoài phạm vi.
8. Cập nhật tài liệu module.
9. Cập nhật STATUS và append WORKLOG.
10. Commit/push và cập nhật PR.
11. Ghi next exact action.

`STATUS.md` là snapshot ngắn của hiện tại, chỉ có một `Next exact action`. Mọi lịch sử theo
thời gian phải append vào `WORKLOG.md`; không dùng STATUS làm nhật ký cộng dồn.

## 2.1. Kiểm soát thay đổi kiến trúc

- Package/type/method trong `planned-contracts.tsv` là đường biên phát triển chính thức.
- `LOCKED` không được đổi/xóa/di chuyển nếu chưa có ADR, test kiến trúc, cập nhật toàn bộ
  tài liệu liên quan và xác nhận rõ của chủ dự án.
- `RESERVED` được phép hiện thực dần nhưng phải giữ đúng package, trách nhiệm và hướng phụ
  thuộc; không được tạo class rỗng chỉ để đánh dấu hoàn thành.
- Package mới hoặc phụ thuộc ngược tầng phải được đề xuất bằng ADR trước khi viết code.
- Mỗi lần thay đổi manifest phải chạy `ArchitectureLockTest` và ghi lý do vào WORKLOG.

## 3. Quy tắc reverse engineering

- Bắt đầu từ entry point/command/call graph, không đọc ngẫu nhiên 250 class.
- Không suy luận payload chỉ từ tên command.
- Với mỗi fact protocol, lưu source location hoặc capture/test fixture.
- Phân biệt client→server, server→client và hai chiều.
- Nếu cùng command có payload theo trạng thái/version khác nhau, ghi từng variant.
- Không chuyển hành vi nghi là bug thành requirement nếu chưa xác minh.

## 4. Quy tắc code NSOCry

- Không import runtime từ `com.nsoz`.
- Không copy class hàng loạt.
- Ưu tiên dependency rõ ràng thay vì singleton/global state.
- Lifecycle start/stop phải có ownership.
- Không nuốt exception im lặng.
- Database access qua lớp có contract rõ.
- Protocol codec phải test được không cần socket thật.
- Module mới phải có tài liệu và tiêu chí kiểm chứng.

## 5. Kiểm chứng

Tùy task:

- Build/test tự động.
- Unit test.
- Integration test với database `nsocry`.
- Protocol fixture.
- Log runtime.
- Client PC.
- Client điện thoại cùng mạng.
- Regression check NSOKISS chỉ ở chế độ quan sát, không sửa.

Ghi chính xác lệnh, kết quả và giới hạn kiểm chứng. “Không thấy lỗi” không đồng nghĩa test pass.

## 6. Làm việc với người dùng

- Người dùng không phải lập trình viên: giải thích kết quả trước, lệnh sau.
- Không yêu cầu họ chạy lệnh nếu công cụ có thể đọc repository trực tiếp.
- Nếu cần thao tác máy Windows, cung cấp một nhóm lệnh an toàn và điểm dừng rõ.
- Không để họ dán output khổng lồ; lọc/tóm tắt bằng lệnh.
- Mọi quyết định gameplay chưa có dữ liệu phải hỏi, không tự chọn.

## 7. Khi bị giới hạn hoặc gián đoạn

Trước khi dừng:

- Commit checkpoint an toàn nếu có thay đổi hợp lệ.
- Không commit code chưa rõ trạng thái mà không ghi cảnh báo.
- STATUS ghi `IN_PROGRESS` và điểm dừng.
- WORKLOG ghi file/method đang làm.
- Ghi phần đã kiểm chứng và chưa kiểm chứng.
- Ghi next exact action đủ cụ thể cho phiên tiếp theo.
