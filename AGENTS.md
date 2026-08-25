# Luật làm việc bắt buộc của NSOCry

Mọi AI hoặc lập trình viên phải đọc theo thứ tự: `docs/project/STATUS.md`,
`docs/project/WORKLOG.md`, `docs/architecture/architecture-lock.md` và
`docs/architecture/planned-contracts.tsv`, sau đó
`docs/handoff/OWNER-COLLABORATION.md` trước khi sửa source.

## Giao tiếp với chủ dự án

- Tuân thủ toàn bộ `docs/handoff/OWNER-COLLABORATION.md`.
- Trả lời bằng tiếng Việt, ngắn gọn, chỉ tập trung phần mới; không kể lại phần VERIFIED.
- Mỗi bước phải nói đang xây gì, kiểm chứng, tác động database/runtime, commit, tiến độ phần
  trăm đến gameplay cơ bản và một bước tiếp theo chính xác.
- Không tăng tiến độ cho stub/tài liệu/code chưa chạy và không gọi PENDING là VERIFIED.
- Chỉ đưa lệnh Windows khi người dùng thật sự phải thao tác; lệnh phải copy được và có output
  kỳ vọng rõ ràng.

## Bất biến

- Tên chuẩn duy nhất: NSOCry/nsocry/Cry/cry. Cấm namespace hoặc type mới mang tên dự án cũ.
- NSOKISS chỉ là nguồn tham chiếu logic tĩnh; không chạy và không sao chép source.
- Không thêm top-level package ngoài architecture lock.
- Không đảo chiều dependency đã khóa hoặc tạo god class thay cho application service nhỏ.
- Không đổi/xóa public contract `LOCKED` nếu chưa có Architecture Decision Record (ADR),
  migration plan, test tương thích và xác nhận của chủ dự án.
- Contract `RESERVED` là tên/trách nhiệm đã giữ chỗ; được triển khai nhưng không tự ý đổi nghĩa.
- Database migration/import cần backup, preflight, checksum, test và xác nhận riêng.
- Runtime snapshot chỉ publish khi đủ nguồn và validation; cấm dữ liệu giả vượt gate.

## Quản trị nhánh Git

- Nhánh phát triển chung hiện tại là `agent/document-nsokiss-runtime`; mọi AI tiếp quản phải
  pull và push checkpoint vào đúng nhánh này nếu chủ dự án chưa chỉ định nhánh khác.
- Cấm push trực tiếp hoặc tự ý merge vào `main`.
- Sau mỗi khoảng 5–10 commit, checkpoint phải được phiên Codex rà soát diff, Architecture
  Lock, test, tài liệu, database/runtime impact và rủi ro hồi quy.
- Chỉ được merge vào `main` khi review đạt, toàn bộ gate liên quan VERIFIED và chủ dự án
  xác nhận rõ nguyên văn hoặc tương đương: `ĐỒNG Ý MERGE VÀO MAIN`.

## Hoàn tất một checkpoint

1. Cập nhật tài liệu chức năng liên quan bằng tiếng Việt.
2. Cập nhật `STATUS.md` và `WORKLOG.md`: VERIFIED/PENDING, test, count/checksum,
   tác động database/runtime, tiến độ tổng thể và `Next exact action`.
3. Chạy test, `git diff --check` và architecture lock test.
4. Stage đúng file thuộc nhiệm vụ, commit và push vào `agent/document-nsokiss-runtime`;
   không merge `main` nếu chưa đủ review và xác nhận bắt buộc.
