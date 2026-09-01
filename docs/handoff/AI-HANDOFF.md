# Hướng dẫn bàn giao giữa ChatGPT Work, Chat thường và AI mới

## 1. Mục tiêu

Ngăn mất tiến độ, lặp kiểm tra và thay đổi hướng không có căn cứ khi phiên làm việc bị giới hạn hoặc chuyển công cụ.

## 2. Context bắt buộc phải đọc

Theo thứ tự:

1. `docs/START-HERE.md`
2. `docs/project/REQUIREMENTS.md`
3. `docs/project/STATUS.md`
4. Entry mới nhất trong `docs/project/WORKLOG.md`
5. `docs/architecture/architecture-lock.md`
6. `docs/architecture/planned-contracts.tsv`
7. `docs/architecture/reference-coverage.md`
8. `docs/handoff/OWNER-COLLABORATION.md`
9. ADR đang hiệu lực
10. Tài liệu architecture/module liên quan
11. Diff/PR hiện tại

## 3. Quy tắc cho AI tiếp quản

- Treat CONFIRMED as requirement.
- Treat VERIFIED as completed evidence; không lặp lại nếu trạng thái đầu vào chưa đổi.
- Treat PROPOSED as chưa được người dùng duyệt.
- Treat UNKNOWN as câu hỏi/rủi ro, không tự lấp bằng phỏng đoán.
- Không lấy lời cũ trong chat ghi đè tài liệu mới hơn trên GitHub.
- Nếu STATUS và code mâu thuẫn, báo mâu thuẫn và kiểm tra commit/diff; không âm thầm chọn một bên.
- Không chuyển task nếu “Next exact action” chưa xong hoặc chưa ghi lý do thay đổi ưu tiên.
- Không tạo package ngoài danh mục khóa và không đổi contract `LOCKED` nếu chưa có ADR,
  cập nhật manifest/test và xác nhận của chủ dự án.
- Contract `RESERVED` là ranh giới đã dành trước, không phải bằng chứng chức năng đã hoàn thành.
- Phản hồi và cách làm việc phải tuân thủ `OWNER-COLLABORATION.md`; không bắt người dùng
  kể lại tiến độ đã có trên GitHub.
- Nếu STATUS là `PAUSED_BY_OWNER`, không tự triển khai Next exact action. Chỉ tiếp tục khi chủ
  dự án yêu cầu rõ; điểm tiếp tục hiện tại là DATA archive service + manifest parser/dry-run.
- Không làm lại full suite 314/314 hoặc DATA candidate version 7/SHA-256 đã VERIFIED nếu input
  source và converter chưa thay đổi.

## 4. Prompt tiếp tục dành cho Chat thường

> Hãy đọc repository public `mailphs2704-lgtm/nsocry-server`, bắt đầu từ `docs/START-HERE.md`, sau đó đọc REQUIREMENTS, STATUS, entry WORKLOG mới nhất, `docs/architecture/architecture-lock.md`, `docs/architecture/planned-contracts.tsv` và tài liệu liên quan. Tóm tắt đúng trạng thái, liệt kê phần VERIFIED không được làm lại. Nếu STATUS là `PAUSED_BY_OWNER`, dừng ở báo cáo checkpoint; chỉ tiếp tục “Next exact action” khi chủ dự án yêu cầu rõ. Không tự sửa requirement PROPOSED/UNKNOWN và không đổi khung kiến trúc nếu chưa qua ADR cùng xác nhận của chủ dự án.

Nếu Chat thường không truy cập được GitHub, đính kèm bốn file trên. Không cần tải lại toàn bộ source chỉ để định hướng.

## 5. Prompt tiếp tục dành cho Work/Codex

> Mở repo `mailphs2704-lgtm/nsocry-server`. Đọc bộ handoff bắt buộc, Architecture Lock, contract manifest và diff/PR hiện tại. Thực hiện “Next exact action” trên nhánh riêng, chỉ xây trong package/contract đã khóa. Kiểm chứng, cập nhật documentation tiếng Việt, STATUS, WORKLOG, commit và mở/cập nhật Draft PR. Mọi thay đổi khung phải có ADR và xác nhận chủ dự án. Không sửa hoặc chạy NSOKISS reference.

## 6. Mẫu checkpoint khi dừng giữa task

```markdown
Status: IN_PROGRESS
Branch:
Commit/PR:
Goal:
Completed:
Verified by:
Currently editing:
Stopped at symbol/step:
Not yet verified:
Do not repeat:
Blocker:
Next exact command/action:
```

## 7. Trách nhiệm của phiên kết thúc

Phiên kết thúc không được chỉ nói “sẽ tiếp tục sau”. Phải ghi checkpoint vào GitHub nếu có quyền ghi. Nếu không có quyền, xuất nguyên nội dung checkpoint để người dùng lưu.
