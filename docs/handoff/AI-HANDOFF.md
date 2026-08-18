# Hướng dẫn bàn giao giữa ChatGPT Work, Chat thường và AI mới

## 1. Mục tiêu

Ngăn mất tiến độ, lặp kiểm tra và thay đổi hướng không có căn cứ khi phiên làm việc bị giới hạn hoặc chuyển công cụ.

## 2. Context bắt buộc phải đọc

Theo thứ tự:

1. `docs/START-HERE.md`
2. `docs/project/REQUIREMENTS.md`
3. `docs/project/STATUS.md`
4. Entry mới nhất trong `docs/project/WORKLOG.md`
5. ADR đang hiệu lực
6. Tài liệu architecture/module liên quan
7. Diff/PR hiện tại

## 3. Quy tắc cho AI tiếp quản

- Treat CONFIRMED as requirement.
- Treat VERIFIED as completed evidence; không lặp lại nếu trạng thái đầu vào chưa đổi.
- Treat PROPOSED as chưa được người dùng duyệt.
- Treat UNKNOWN as câu hỏi/rủi ro, không tự lấp bằng phỏng đoán.
- Không lấy lời cũ trong chat ghi đè tài liệu mới hơn trên GitHub.
- Nếu STATUS và code mâu thuẫn, báo mâu thuẫn và kiểm tra commit/diff; không âm thầm chọn một bên.
- Không chuyển task nếu “Next exact action” chưa xong hoặc chưa ghi lý do thay đổi ưu tiên.

## 4. Prompt tiếp tục dành cho Chat thường

> Hãy đọc repository public `mailphs2704-lgtm/nsocry-server`, bắt đầu từ `docs/START-HERE.md`, sau đó đọc REQUIREMENTS, STATUS, entry WORKLOG mới nhất và tài liệu liên quan. Tóm tắt đúng trạng thái, liệt kê phần VERIFIED không được làm lại, rồi tiếp tục “Next exact action”. Không tự sửa requirement PROPOSED/UNKNOWN.

Nếu Chat thường không truy cập được GitHub, đính kèm bốn file trên. Không cần tải lại toàn bộ source chỉ để định hướng.

## 5. Prompt tiếp tục dành cho Work/Codex

> Mở repo `mailphs2704-lgtm/nsocry-server`. Đọc bộ handoff bắt buộc và diff/PR hiện tại. Thực hiện “Next exact action” trên nhánh riêng. Kiểm chứng, cập nhật documentation, STATUS, WORKLOG, commit và mở/cập nhật Draft PR. Không sửa NSOKISS reference.

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
