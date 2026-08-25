# Giao thức làm việc với chủ dự án NSOCry

Tài liệu này là **CONFIRMED** và bắt buộc với mọi AI/lập trình viên tiếp quản dự án.

## 1. Cách giao tiếp

- Dùng tiếng Việt, rõ ràng, trực tiếp; người dùng không cần biết sâu về lập trình.
- Không lặp lại kiến thức, lệnh hoặc giải thích đã VERIFIED nếu đầu vào không thay đổi.
- Mỗi phản hồi chỉ tập trung vào bước mới; ưu tiên kết quả trước, giải thích kỹ thuật sau.
- Khi bắt đầu một khối việc, nói ngắn gọn: đang xây phần nào, mục đích trong game và tỷ lệ
  tiến độ tổng thể đến mốc gameplay cơ bản.
- Không tăng phần trăm vì tài liệu, stub hoặc code chưa chạy. Nếu chỉ giảm rủi ro kiến trúc,
  phải nói rõ phần trăm giữ nguyên.
- Không hỏi lại điều đã có trong REQUIREMENTS/STATUS/WORKLOG/GitHub. Chỉ hỏi khi thiếu một
  quyết định có thể làm thay đổi kết quả hoặc trước thao tác ghi database/destructive.
- Khi cần người dùng thao tác Windows, đưa đúng một nhóm lệnh PowerShell có thể copy, nói
  rõ phải chờ lệnh nào xong và output kỳ vọng. Không yêu cầu dán log dài nếu có thể lọc.

## 2. Cách thực hiện công việc

1. Đọc bộ handoff và kiểm tra branch/diff trước khi sửa.
2. Tiếp tục `Next exact action`; không tự đổi ưu tiên hoặc phân tích lại phần VERIFIED.
3. Thay đổi theo lát cắt nhỏ nhưng hoàn chỉnh: contract → code → test → tài liệu.
4. Dựa trên logic tĩnh NSOKISS nhưng viết mới bằng namespace NSOCry; không chạy/copy source.
5. Không vượt Architecture Lock. Thay đổi khung cần ADR và xác nhận chủ dự án.
6. Mọi thao tác database phải có preflight, backup/checksum, confirmation, post-check và
   ghi rõ `databaseChanged`; runtime snapshot ghi riêng `runtimeSnapshotPublished`.
7. Sau mỗi checkpoint phải cập nhật tài liệu module, STATUS và append WORKLOG bằng tiếng Việt.
8. Chạy kiểm chứng phù hợp, review diff, commit/push đúng branch rồi mới bàn giao.

## 3. Cách trả kết quả trong chat

Khi hoàn thành một checkpoint, phản hồi theo mẫu ngắn sau:

```text
Đã hoàn thành: <chức năng/khối đang xây>.
Kiểm chứng: <x/y test hoặc PENDING; checksum/count nếu có>.
Tác động: database=<có/không>, runtime snapshot=<đã/chưa>.
GitHub: <commit/branch>.
Tiến độ đến gameplay cơ bản: <n%>; <lý do tăng/giữ nguyên>.
Bước tiếp theo: <một hành động chính xác>.
Bạn cần chạy: <chỉ ghi khi thật sự cần>.
```

- Phân biệt chính xác `CONFIRMED`, `VERIFIED`, `PENDING`, `PROPOSED`, `UNKNOWN`.
- Không nói “đã xong”, “đã sửa” hoặc “pass” nếu chưa có bằng chứng tương ứng.
- Kết quả Work/Linux không được gọi là Windows VERIFIED trước khi người dùng xác nhận.
- Nếu thất bại, nêu nguyên nhân đã chứng minh, phạm vi ảnh hưởng và một bước khôi phục; không
  đổ cho môi trường khi chưa kiểm tra.
- Nếu người dùng chỉ báo số test pass, ghi nhận ngắn, cập nhật GitHub rồi tiếp tục checkpoint;
  không kể lại toàn bộ lịch sử.

## 4. Quy tắc tiết kiệm token và bàn giao dài hạn

- GitHub là bộ nhớ chính; chat chỉ là giao diện điều phối.
- Lưu quyết định, checksum, count, test, commit, file/method đang làm và next action trong repo.
- AI tiếp quản phải đọc diff và tài liệu trước; không yêu cầu người dùng kể lại dự án.
- Review định kỳ nên dùng khoảng commit/PR rõ ràng, kiểm tra theo rủi ro và architecture gate,
  không đọc lại vô điều kiện toàn bộ 250 class reference.
- Khi nhiều AI cùng làm, mỗi AI dùng nhánh/commit có ranh giới nhỏ; không để thay đổi chưa ghi
  nhật ký kéo dài qua nhiều module.

## 5. Điều kiện dừng

- Thiếu xác nhận cho migration/import/destructive action.
- Code hoặc tài liệu mâu thuẫn với contract `LOCKED`.
- Test thất bại chưa xác định nguyên nhân.
- STATUS, source và GitHub không cùng checkpoint.

Khi dừng phải ghi blocker và `Next exact action`; không âm thầm chuyển sang chức năng khác.
