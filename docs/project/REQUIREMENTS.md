# Yêu cầu và nguyên tắc dự án NSOCry

## 1. Yêu cầu đã xác nhận (CONFIRMED)

### 1.1 Mục tiêu sản phẩm

- Xây dựng một server mới tên **NSOCry**.
- NSOKISS là nguồn tham khảo hành vi và tài nguyên, không phải codebase để tiếp tục sửa.
- NSOCry phải có cấu trúc dễ hiểu, dễ bảo trì và có documentation chi tiết.
- Ưu tiên khả năng tương thích với client thực tế hơn việc giữ nguyên tên/class/package của NSOKISS.

### 1.2 Chuẩn đặt tên NSOCry/Cry

- Tên sản phẩm chuẩn: `NSOCry`.
- Dạng kỹ thuật chữ thường: `nsocry`.
- Tên rút gọn được phép khi phù hợp ngữ cảnh: `Cry` hoặc `cry`.
- Source NSOCry phải được viết mới dựa trên **logic/hành vi đã hiểu** từ NSOKISS, không kế thừa tên định danh của source cũ.
- Package mới không được chứa `nsoz` hoặc `nsotien`; package gốc định hướng là `com.nsocry`.
- Class, interface, method, field, constant, cấu hình, environment variable, artifact, module, database object và tài liệu thuộc NSOCry không được mang tên `nsoz` hoặc `nsotien`.
- Không đổi tên máy móc. Tên mới phải mô tả đúng trách nhiệm trong kiến trúc NSOCry; dùng `nsocry`/`cry` cho nhận diện dự án và tên miền phù hợp cho nghiệp vụ.
- Tên legacy chỉ được xuất hiện khi trích dẫn hoặc mô tả reference NSOKISS, và phải được đánh dấu rõ là reference.
- Trước khi merge code NSOCry, phải chạy kiểm tra tìm `nsoz|nsotien` ngoài `source-reference/` và các đoạn tài liệu reference được cho phép.

### 1.3 Tách biệt hệ thống

- Giữ NSOKISS đang chạy nguyên trạng để đối chiếu.
- NSOCry sử dụng database riêng tên `nsocry`.
- Không thao tác phá hủy hoặc migration trực tiếp trên database NSOKISS.
- Reference phải nằm tách biệt khỏi source NSOCry.

### 1.4 Documentation bắt buộc

Người dùng phải có thể truy từ:

```text
package
  -> class
    -> method
      -> đoạn/dòng code liên quan
      -> nhiệm vụ
      -> dữ liệu vào
      -> dữ liệu ra
      -> luồng xử lý
      -> class/bảng/command liên quan
      -> lý do thiết kế
      -> cách kiểm chứng
```

Tài liệu không chỉ mô tả “code làm gì” mà phải giải thích “vì sao thiết kế như vậy” và tác động khi sửa.

### 1.5 Cách cộng tác

- Người dùng không phải lập trình viên; hướng dẫn phải rõ, tuần tự và không giả định kiến thức nền.
- Không yêu cầu người dùng dán hàng nghìn dòng code khi repository/reference đã truy cập được.
- Tránh chia một thao tác đơn giản thành quá nhiều lượt hỏi–đáp.
- Lưu checkpoint lên GitHub để ChatGPT Work, Chat thường hoặc phiên mới có thể tiếp tục.

## 2. Yêu cầu suy ra cần xác nhận dần (UNKNOWN)

Các mục sau chưa được coi là quyết định cuối:

- Phiên bản Java/Maven chính thức cho NSOCry.
- Mức tương thích tuyệt đối với tất cả phiên bản client.
- Bộ tính năng tối thiểu của bản phát hành đầu tiên.
- Quy tắc gameplay nào giữ nguyên, sửa hoặc loại bỏ.
- Kiến trúc concurrency cuối cùng.
- Cách version/migration database.
- Môi trường triển khai production.
- Chính sách bảo mật tài khoản, mật khẩu và vận hành public server.

AI không được tự chốt các mục UNKNOWN. Phải tạo đề xuất, nêu trade-off và xin quyết định khi nó ảnh hưởng hướng phát triển.

## 3. Định nghĩa “viết lại”

Được phép:

- Quan sát hành vi/reference.
- Lập bảng protocol, schema intent và dependency.
- Viết implementation mới có tên, ranh giới và test rõ ràng.
- Tái sử dụng tài nguyên/dữ liệu khi quyền sử dụng cho phép và người dùng yêu cầu.

Không được coi là viết lại:

- Copy class rồi đổi package.
- Giữ nguyên singleton/static global state mà không đánh giá.
- Copy schema nguyên trạng trước khi hiểu ý nghĩa bảng/cột.
- Copy bug hoặc hành vi bất thường chỉ vì NSOKISS đang có.

## 4. Ưu tiên chất lượng

Theo thứ tự:

1. Không làm mất/ảnh hưởng NSOKISS đang chạy.
2. Tương thích protocol cần thiết với client.
3. Đúng dữ liệu và vòng đời session/player.
4. Có thể kiểm thử và quan sát lỗi.
5. Kiến trúc rõ ràng.
6. Hiệu năng và tối ưu sau khi có số liệu.
7. Tính năng mở rộng.

## 5. Tiêu chí chấp nhận chung

Một module chỉ được DONE khi:

- Có yêu cầu/phạm vi rõ ràng.
- Có implementation hoặc tài liệu reverse-engineering tương ứng.
- Có kiểm chứng (test, build, log, trace hoặc đối chiếu).
- Có documentation.
- STATUS và WORKLOG đã cập nhật.

## Khóa khung phát triển toàn dự án

- **CONFIRMED:** AI/nhà phát triển tiếp quản phải xây tiếp trong khung package, type và
  method đã công bố tại `docs/architecture/planned-contracts.tsv`.
- Contract `LOCKED` chỉ được thay đổi sau ADR, cập nhật test/tài liệu và xác nhận rõ của
  chủ dự án; không được tự tiện đổi tên hoặc tổ chức lại theo sở thích phiên làm việc.
- Contract `RESERVED` là thiết kế đích để hiện thực dần, không phải code đã hoàn thành và
  không được tạo hàng loạt class rỗng để nâng tiến độ giả.
- NSOKISS là nguồn tham khảo hành vi tĩnh; không chạy, không sửa và không sao chép cơ học
  các god class/singleton vào NSOCry.
- Mỗi bước hoàn thành phải cập nhật tài liệu chi tiết bằng tiếng Việt, STATUS và WORKLOG.
- **CONFIRMED:** mọi AI phải tuân thủ giao thức giao tiếp, thực hiện và trả kết quả tại
  `docs/handoff/OWNER-COLLABORATION.md`; ưu tiên ngắn gọn, không lặp phần VERIFIED và luôn
  báo tiến độ tổng thể cùng bước tiếp theo chính xác.
- Không làm hỏng module đã hoàn thành.


## Console quản trị cục bộ

- Trước khi có website, NSOCry phải có run.bat với trải nghiệm lệnh bài Admin bằng tiếng Việt.
- Batch file chỉ là launcher; logic quản trị phải nằm trong Java và đi qua application/domain service.
- Administrator có thể quản lý hệ thống, account, thông báo, sự kiện, vật phẩm và giftcode khi module nền tương ứng đã tồn tại.
- Bắt buộc xác thực role ADMINISTRATOR, xác nhận thao tác nguy hiểm và audit mọi thay đổi.
- Cấm hard-code master password, commit secret hoặc cung cấp SQL console tùy ý.
- Thiết kế command/service phải tái sử dụng được cho website/API quản trị trong tương lai.


### Điều chỉnh tiến độ Admin

- Không xây Admin Console như một phase riêng trước gameplay.
- Core module luôn được ưu tiên; command quản trị chỉ bổ sung khi service tương ứng đã hoàn thành.
- Backlog Admin vẫn giữ đầy đủ nhưng không được trở thành blocker của login, character, map hoặc gameplay.
