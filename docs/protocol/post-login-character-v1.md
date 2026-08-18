# Giao thức sau đăng nhập và chọn nhân vật V1

## Mục đích

Tài liệu này ghi lại bằng chứng phân tích tĩnh từ mã tham chiếu đang hoạt động. NSOCry chỉ dùng bằng chứng để tái tạo hành vi tương thích; không sao chép kiến trúc, tên package, class hoặc method của dự án cũ.

## Trình tự đã quan sát

1. Tài khoản xác thực thành công.
2. Server gửi envelope `NOT_MAP (-28)`, command con `UPDATE_VERSION (-123)` và một blob tài nguyên phiên bản.
3. Client xử lý dữ liệu rồi gửi envelope `NOT_MAP (-28)`, command con `CLIENT_OK (-101)`.
4. Server tải dữ liệu hiển thị nhân vật và gửi `SELECT_CHARACTER (-126)`.
5. Client gửi lại `SELECT_CHARACTER (-126)` kèm tên, hoặc `CREATE_CHARACTER (-125)` kèm thông tin tạo mới.

Không được gửi danh sách nhân vật ngay sau xác thực: client cũ còn chờ bước cập nhật phiên bản và `CLIENT_OK`.

## Bố cục byte đã xác minh tĩnh

### Server gửi danh sách nhân vật

| Thứ tự | Kiểu Java DataOutput | Nội dung |
|---:|---|---|
| 1 | `byte` | Envelope ngoài `-28` (nằm ở header frame) |
| 2 | `byte` | Command con `-126` |
| 3 | `byte` | Số nhân vật |
| 4 | lặp | Các trường bên dưới cho từng nhân vật |
| 4.1 | `byte` | Giới tính |
| 4.2 | `UTF` | Tên nhân vật |
| 4.3 | `UTF` | Môn phái/trường phái |
| 4.4 | `byte` | Cấp độ |
| 4.5 | `short` | Ngoại hình đầu |
| 4.6 | `short` | Ngoại hình vũ khí |
| 4.7 | `short` | Ngoại hình thân |
| 4.8 | `short` | Ngoại hình chân |

### Client yêu cầu chọn nhân vật

Payload trong envelope `-28`: `byte -126`, sau đó `UTF tên nhân vật`.

### Client yêu cầu tạo nhân vật

Payload trong envelope `-28`: `byte -125`, `UTF tên`, `byte giới tính`, `byte đầu`.

Codec chỉ giải mã cấu trúc wire. Giới hạn tên, số lượng nhân vật, đầu hợp lệ và các quy tắc nghiệp vụ phải do dịch vụ nhân vật quyết định sau khi có yêu cầu chính thức.

## Mức độ tin cậy và phần chưa biết

| Hạng mục | Trạng thái | Quyết định |
|---|---|---|
| Envelope và command con | Đã xác minh tĩnh | Có thể triển khai codec |
| Thứ tự trường danh sách/chọn/tạo | Đã xác minh tĩnh | Có thể viết fixture và test |
| Blob `UPDATE_VERSION` | Chưa đủ bằng chứng | Chưa tích hợp runtime |
| Tối đa 1 hay 3 nhân vật | Mã tham chiếu không nhất quán | Không sao chép quy tắc |
| Regex và độ dài tên | Chỉ là hành vi tham chiếu | Chờ đặc tả nghiệp vụ NSOCry |

## Yêu cầu an toàn

- Không ghi mật khẩu, token hoặc toàn bộ payload đăng nhập vào log.
- Tên nhân vật nhận từ client chưa phải dữ liệu đáng tin; tầng dịch vụ phải kiểm tra quyền sở hữu.
- Decoder từ chối envelope sai, command con sai và byte dư.
- Chưa nối codec vào session runtime cho đến khi hoàn tất trình tự `UPDATE_VERSION → CLIENT_OK`.
