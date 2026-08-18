# Tra cứu mã nguồn: protocol, session và network

## Quy ước tài liệu

Đây là mục lục tra cứu đến cấp method cho checkpoint runtime đầu tiên của NSOCry. Mã nguồn là căn cứ cuối cùng. Mọi thay đổi source phải cập nhật Javadoc và dòng tương ứng trong tài liệu này. Package mới bắt buộc có package-info.java.

## Bản đồ package

| Package | Trách nhiệm | Không được chứa |
|---|---|---|
| com.nsocry.protocol.compat | Tương thích dữ liệu V7 và I/O có giới hạn | Xác thực, lưu trữ, gameplay |
| com.nsocry.session | Thứ tự handshake, dữ liệu đã giải mã và các port ứng dụng | Vòng lặp nhận socket, triển khai database |
| com.nsocry.network | Vòng đời listener, giới hạn và nối socket với session | Giải mã trường protocol, gameplay |

## Tra cứu protocol tương thích

| Class/method | Mục đích | Hợp đồng và lỗi |
|---|---|---|
| ProtocolFrame | Giá trị command/payload bất biến | Luôn sao chép phòng vệ payload |
| ProtocolLimits / requireAllowed | Định nghĩa và kiểm tra giới hạn cấp phát | Từ chối khoảng sai, độ dài âm hoặc quá lớn |
| RollingXorCipher constructor | Tạo con trỏ mã hóa cho một chiều | Từ chối khóa rỗng; không dùng chung hai chiều |
| transform(byte/byte[]) | Biến đổi dữ liệu và tăng con trỏ | Sao chép mảng; con trỏ liên tục qua nhiều frame |
| cursor | Trả vị trí hiện tại để kiểm thử | Chỉ dùng chẩn đoán |
| LegacyKeyCodec.encodePayload | Mã hóa sai phân khóa cho command -27 | Khóa dài 1–255 byte |
| LegacyKeyCodec.decodePayload | Khôi phục khóa đã truyền | Từ chối độ dài khai báo sai |
| LegacyFrameCodec.encodeShortFrame | Tạo command + độ dài unsigned-short + payload | Payload tối đa 65535 byte |
| encodeFullSizeFrame | Tạo -32 + độ dài int + payload | Luồng tương thích server gửi client |
| decodeFrame | Giải mã fixture hoàn chỉnh trong bộ nhớ | Từ chối frame thiếu hoặc sai độ dài |
| LegacyFrameReader constructor | Bọc stream vào và giới hạn | Duy trì con trỏ chiều vào |
| readUnencryptedShortFrame | Đọc trigger trao đổi khóa | Chỉ dùng trước khi bật cipher |
| readEncryptedFrame | Đọc frame đã mã hóa | Có thể cấm full-size từ client; kiểm tra trước cấp phát |
| LegacyFrameWriter constructor | Bọc stream ra và giới hạn | Ghi đồng bộ và flush |
| writeUnencryptedShortFrame | Gửi phản hồi khóa dạng rõ | Chỉ dùng trước cipher |
| writeEncryptedShortFrame | Gửi frame mã hóa thông thường | Bắt buộc có cipher chiều ra |
| writeEncryptedFullSizeFrame | Gửi payload server kích thước lớn | Áp dụng giới hạn full payload |

## Tra cứu session

| Class/method | Mục đích | Hợp đồng hoặc tác động trạng thái |
|---|---|---|
| SessionPhase | Các giai đoạn kết nối/đăng nhập | CLOSED là trạng thái cuối |
| HandshakeEvent | Kết quả của một bước xử lý | Tách biệt với phase có trạng thái |
| AuthenticationDecision | Kết quả từ port xác thực | ACCEPTED hoặc REJECTED |
| ClientInfo | Khả năng client đã giải mã | Thứ tự trường theo wire đã xác minh |
| LoginRequest | Dữ liệu đăng nhập đã giải mã | toString che mật khẩu và token |
| Các accessor LoginRequest | Cấp dữ liệu cho tầng xác thực | Tuyệt đối không log bí mật |
| ProtocolStateException | Báo chuyển phase không hợp lệ | Chỉ chứa phase, không chứa thông tin đăng nhập |
| HandshakeStateMachine.phase | Đọc phase nguyên tử | Không thay đổi trạng thái |
| keySent | CONNECTED sang KEY_SENT | Sai thứ tự sẽ ném exception |
| clientInfoReceived | KEY_SENT sang CLIENT_INFO_RECEIVED | Bắt buộc đúng thứ tự |
| loginStarted | CLIENT_INFO_RECEIVED sang LOGIN_PENDING | Bắt buộc đúng thứ tự |
| loginSucceeded | LOGIN_PENDING sang AUTHENTICATED | Ghi nhận đăng nhập thành công |
| loginRejected | LOGIN_PENDING về CLIENT_INFO_RECEIVED | Chuẩn bị cho chính sách thử lại có giới hạn |
| close | Phase đang hoạt động sang CLOSED | Gọi nhiều lần an toàn |
| isAuthenticated / isClosed | Truy vấn trạng thái | Không thay đổi dữ liệu |
| HandshakePayloadDecoder.decodeClientInfo | Giải mã nghiêm ngặt payload lồng -125 | Từ chối envelope, command, thiếu hoặc dư byte |
| decodeLogin | Giải mã nghiêm ngặt payload lồng -127 | Bí mật chỉ chuyển cho port xác thực |
| AuthenticationPort.authenticate | Ranh giới module tài khoản | Test hiện dùng adapter giả |
| SessionKeyProvider.createKey | Cấp một khóa cho mỗi kết nối | Chính sách nằm ngoài transport |
| SecureRandomSessionKeyProvider constructor | Cấu hình độ dài khóa ngẫu nhiên | Chấp nhận 1–255 |
| createKey | Sinh byte SecureRandom mới | Gọi một lần mỗi handshake |
| LegacySessionTransport constructor | Sở hữu frame I/O, cipher và đối tượng cần đóng | Một instance cho một client |
| beginHandshake | Kiểm tra trigger -27, gửi khóa, bật hai cipher | Chuyển sang KEY_SENT |
| readClientFrame | Đọc frame mã hóa tiếp theo từ client | Yêu cầu đã trao đổi khóa |
| sendShortFrame / sendFullSizePayload | Gửi dữ liệu mã hóa cho client | Yêu cầu đã trao đổi khóa |
| state | Cho processor truy cập state machine | Transport vẫn sở hữu vòng đời |
| close | Đóng trạng thái và tài nguyên đúng một lần | Gọi nhiều lần an toàn |
| HandshakeProcessor constructor | Nối transport với giải mã đúng thứ tự | Không phụ thuộc database |
| begin | Bắt đầu trao đổi khóa | Trả KEY_ESTABLISHED |
| receiveNext | Phân luồng CLIENT_INFO hoặc LOGIN theo phase | Từ chối sai thứ tự |
| clientInfo | Trả metadata đã chấp nhận | Null trước CLIENT_INFO |

## Tra cứu network

| Class/method | Mục đích | Hợp đồng hoặc tác động trạng thái |
|---|---|---|
| TcpServerConfig | Kiểm tra bind, backlog, giới hạn phiên và timeout | Từ chối cấu hình không an toàn |
| SessionConnectionHandler.handle | Ranh giới xử lý một socket | TcpServer chịu trách nhiệm đóng socket cuối cùng |
| Các callback NetworkEventSink | Báo lỗi accept/session/từ chối | Implementation phải làm sạch log |
| TcpServer constructor | Tạo bộ thực thi phiên có giới hạn | Không tạo hàng đợi ẩn |
| start | Bind và chạy accept thread | Từ chối start hai lần; lỗi bind sẽ hoàn tác |
| isRunning | Đọc trạng thái listener | Nguyên tử |
| localAddress | Trả địa chỉ thực tế đã bind | Yêu cầu server đã bind |
| close | Dừng listener, session và accept thread | Gọi nhiều lần an toàn, có timeout |
| LegacyHandshakeConnectionHandler constructor | Ghép giới hạn, key port và auth port | Không dependency nào được null |
| handle | Chạy key, CLIENT_INFO và LOGIN cho một socket | Chỉ AUTHENTICATED/LOGIN_REJECTED là kết quả cuối hợp lệ |

## Test bảo vệ

| Test | Hành vi được bảo vệ |
|---|---|
| ProtocolFixtureTest | Fixture khóa, cipher và frame |
| LegacyFrameStreamTest | Giới hạn stream và quy tắc full-size theo chiều |
| HandshakeStateMachineTest | Chuyển trạng thái hợp lệ, bị từ chối và trạng thái cuối |
| LegacySessionTransportTest | Trigger, khóa, input mã hóa và đóng |
| HandshakePayloadDecoderTest | Đúng thứ tự trường và che dữ liệu nhạy cảm |
| HandshakeProcessorTest | Luồng đúng thứ tự với xác thực giả |
| TcpServerTest | Nhận kết nối loopback và dừng an toàn |
| LegacyHandshakeLoopbackTest | Socket thật: trigger, key, CLIENT_INFO và LOGIN |
