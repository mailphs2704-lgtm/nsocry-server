# Kiến trúc runtime NSOKISS (bản đồ tham khảo)

> Trạng thái: đã kiểm chứng từ source trong `source-reference/NSOKISS-inspection`.
>
> Mục đích: ghi lại hành vi của NSOKISS để thiết kế lại NSOCry. Tài liệu này không yêu cầu và không cho phép sửa source NSOKISS.

## 1. Phạm vi và nguyên tắc

- NSOKISS là hệ thống tham khảo đang chạy được, phải được giữ nguyên.
- NSOCry sẽ được viết lại, không sao chép nguyên cấu trúc hoặc nguyên class của NSOKISS.
- NSOCry sử dụng database `nsocry` riêng.
- Mọi thành phần NSOCry sau này phải có tài liệu về nhiệm vụ, đầu vào, đầu ra, luồng xử lý, quan hệ phụ thuộc và lý do thiết kế.
- Những điểm dưới đây mô tả **hành vi quan sát được của NSOKISS**, chưa phải kiến trúc cuối cùng của NSOCry.

## 2. Điểm khởi động thực tế

Entry point của server là:

`com.nsoz.server.NinjaSchool.main(String[] args)`

Chuỗi khởi động đã kiểm chứng:

```text
NinjaSchool.main()
  -> Config.getInstance().load()
  -> DbManager.getInstance().start()
  -> NinjaUtils.availablePort(configuredPort)
  -> new NinjaSchool()            [cửa sổ quản trị AWT]
  -> Server.init()
  -> Server.start()
```

Nếu cấu hình không hợp lệ, database không khởi động được, port đã bị sử dụng hoặc `Server.init()` thất bại, server dừng trước khi mở socket.

## 3. Khởi tạo dữ liệu và dịch vụ

Trong `Server.start()`, NSOKISS lần lượt khởi động nhiều subsystem dùng chung:

- đặt trạng thái người chơi offline;
- vòng xoay và gian hàng;
- `GameData`;
- world/map;
- socket/action phụ trợ;
- lịch sinh boss;
- clan;
- chiến trường theo lịch;
- autosave;
- bảng xếp hạng.

Sau khi dữ liệu và tác vụ nền được tạo, server lấy port từ `Config`, tạo `ServerSocket`, đặt trạng thái chạy và bắt đầu vòng lặp nhận kết nối.

Lưu ý kỹ thuật cần xử lý khi thiết kế NSOCry:

- `Server.start()` đang gánh quá nhiều trách nhiệm;
- khởi tạo dữ liệu, lập lịch, socket listener và vòng đời server bị trộn trong một method;
- một số lời gọi có dấu hiệu lặp (ví dụ khởi tạo `SpawnBossManager`);
- nhiều singleton và trạng thái static khiến thứ tự khởi động khó kiểm soát và khó kiểm thử.

## 4. Luồng nhận kết nối

Luồng socket đã kiểm chứng:

```text
ServerSocket.accept()
  -> kiểm tra chế độ bảo trì
  -> lấy IP client
  -> ServerManager.frequency(ip)
  -> kiểm tra giới hạn kết nối/IP
  -> new Session(socket, ++id)
  -> lưu IP vào Session
  -> ServerManager.add(ip)
```

Kết nối bị đóng ngay khi server đang chặn đăng nhập hoặc IP đã đạt giới hạn cấu hình.

## 5. Vòng đời Session

Constructor `com.nsoz.network.Session` thực hiện:

1. lưu socket và session ID;
2. bật TCP keep-alive;
3. tạo `DataInputStream` và `DataOutputStream`;
4. gắn `Controller` làm message handler;
5. gắn `Service` làm lớp gửi phản hồi game;
6. tạo sender queue/thread;
7. tạo message collector/thread;
8. khởi động luồng đọc message.

Mỗi session vì vậy kết hợp bốn vai trò:

- trạng thái kết nối;
- trạng thái client/người dùng;
- giải mã/đóng gói protocol;
- quản lý hai luồng đọc và ghi.

Đây là điểm cần tách trong NSOCry để việc đóng session, chống race condition và kiểm thử protocol rõ ràng hơn.

## 6. Message protocol

Phần ghi message trong `Session.doSendMessage(Message)` cho thấy frame cơ bản gồm:

```text
command
length
payload
```

Đặc điểm:

- payload thông thường dùng độ dài 2 byte;
- message lớn hơn `Short.MAX_VALUE` chuyển sang `CMD.FULL_SIZE` và dùng độ dài 4 byte;
- sau khi hoàn tất trao đổi key, command, length và payload được biến đổi bằng key;
- `Message` mang command và vùng dữ liệu đọc/ghi;
- sender queue tách việc tạo message khỏi thao tác ghi socket trực tiếp.

Cần lập bảng protocol riêng trước khi viết network layer NSOCry để bảo đảm client `V7_217_X1.jar` tương thích.

## 7. Điều phối message

Quan hệ runtime hiện tại:

```text
Session.MessageCollector
  -> Message
  -> Controller
  -> Service / game logic
  -> Session.sendMessage()
  -> Sender queue
  -> socket output
```

Phân biệt vai trò:

- `Controller`: nhận command từ client và điều phối xử lý.
- `Service`: tạo message phản hồi hoặc cập nhật trạng thái cho client.
- `AbsService`: nền tảng dùng chung của service.
- `Session`: giữ kết nối, trạng thái handshake/client và vận chuyển message.

Cần phân tích toàn bộ bảng command trong `Controller` ở bước kế tiếp.

## 8. Quan hệ với database

Bộ SQL tham khảo có 44 bảng. Các nhóm chính đã nhận diện:

- tài khoản/người chơi: `users`, `players`, `clone_char`, `user_logs`;
- thế giới: `map`, `monster`, `npc`;
- vật phẩm/kỹ năng: `item`, `item_option`, `skill`, `skill_option`, `skill_template`;
- nhiệm vụ: `task`, `task_template`;
- gia tộc: `clan`, `clan_member`;
- cửa hàng: `stores`, `store_data`, `weapon_store`;
- dữ liệu client/render: `nj_image`, `nj_part`, `nj_skill`, `nj_effect`, `nj_arrow`;
- vận hành/sự kiện/nạp: gift code, event point, ranking, transaction và log.

Chưa được phép sao chép schema này sang `nsocry` nguyên trạng. Mỗi bảng phải được truy ngược đến code đọc/ghi và mục đích thật trước khi thiết kế schema mới.

## 9. Ranh giới module dự kiến cho NSOCry

Bản đồ này gợi ý các ranh giới ban đầu, chưa phải quyết định cuối:

```text
bootstrap       cấu hình và điều phối vòng đời
persistence     kết nối và repository database
network         socket, frame codec, session transport
protocol        command và message contract
authentication  đăng nhập và bảo vệ phiên
game-data       nạp dữ liệu tĩnh
world           map, zone, mob, npc
player          user, character và trạng thái online
scheduler       boss, war, autosave, ranking
administration  bảo trì và thao tác quản trị
```

Mục tiêu là để bootstrap chỉ điều phối, không chứa logic của từng subsystem.

## 10. Công việc tiếp theo

1. Lập command inventory từ `CMD.java` và các nhánh xử lý trong `Controller.java`.
2. Mô tả handshake, trao đổi key, thông tin client và login.
3. Lập call graph `Controller -> Service -> game logic -> database`.
4. Ghép từng DAO/SQL statement với 44 bảng.
5. Kiểm tra client JAR để xác nhận frame protocol hai chiều.
6. Từ kết quả đó mới chốt cấu trúc package và skeleton đầu tiên của NSOCry.

## 11. Source đã dùng để kiểm chứng

- `src/main/java/com/nsoz/server/NinjaSchool.java`
- `src/main/java/com/nsoz/server/Server.java`
- `src/main/java/com/nsoz/network/Session.java`
- `src/main/java/com/nsoz/network/Message.java`
- `src/main/java/com/nsoz/network/Controller.java`
- `src/main/java/com/nsoz/network/Service.java`
- `database.sql`
