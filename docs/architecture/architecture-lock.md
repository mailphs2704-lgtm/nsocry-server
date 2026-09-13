# Architecture Lock NSOCry v1

## Mục đích

Tài liệu này khóa khung xương để nhiều phiên AI phát triển cùng một dự án mà không đổi
tên, dựng trùng domain hoặc tạo dependency vòng. Lock bảo vệ ranh giới và public contract;
không khóa thuật toán gameplay chưa được kiểm chứng.

## Các tầng và chiều phụ thuộc

1. **Foundation**: `common`, `configuration`, `observability`; không phụ thuộc gameplay.
2. **Protocol/transport**: `protocol.*`, `network`, `session`; chỉ biết port/application DTO,
   không truy cập JDBC hoặc chứa luật gameplay.
3. **Domain**: `game.*`, `character`, `authentication`; model và luật thuần Java, không JDBC,
   socket, file system hoặc launcher.
4. **Application**: `application.*`; điều phối use-case qua port, transaction boundary do adapter cấp.
5. **Assets**: `assets.*`; read-model client tĩnh, codec và validator; không chứa trạng thái người chơi.
6. **Adapters**: `persistence`, `operations`; triển khai port JDBC/file/archive, không quyết định gameplay.
7. **Composition**: `bootstrap`, `administration`, `scheduler`; nối dependency và vận hành.

Chiều hợp lệ: composition/adapters/transport → application/domain/foundation. Domain không
được import adapter, bootstrap, network hoặc JDBC. Mọi giao tiếp chéo domain đi qua service/port,
không gọi repository của domain khác trực tiếp.

## Package đã khóa

| Nhóm | Package |
|---|---|
| Hiện có | `assets`, `assets.conversion`, `authentication`, `bootstrap`, `character`, `configuration`, `network`, `observability`, `operations`, `persistence`, `protocol.compat`, `session` |
| Application | `application`, `application.port` |
| Protocol | `protocol.command`, `protocol.message` |
| Gameplay | `game.ability`, `game.bot`, `game.chat`, `game.clan`, `game.combat`, `game.effect`, `game.event`, `game.fashion`, `game.giftcode`, `game.item`, `game.map`, `game.map.instance`, `game.map.item`, `game.mob`, `game.npc`, `game.party`, `game.player`, `game.ranking`, `game.skill`, `game.store`, `game.task`, `game.trade` |
| Vận hành | `administration`, `scheduler`, `common` |

Subpackage mới chỉ được phép khi thuộc đúng một package gameplay đã khóa và có ADR. Không
dùng lại cách chia package theo event cụ thể hoặc từng zone cụ thể như nguồn tham chiếu;
event/zone là data + strategy/plugin, không phải một package/class cứng cho mỗi tên sự kiện.

## Quy tắc thiết kế thay cho NSOKISS

- `Char` được tách thành `Player`, component/state và các application service.
- `Service`/`Controller` khổng lồ được thay bằng command handler theo use-case.
- `ServerManager`/singleton manager được thay bằng registry hoặc repository được inject.
- Mỗi map đặc biệt dùng `MapInstanceRule`; mỗi event dùng `GameEventDefinition/Handler`.
- Bot dùng composition của sensor/decision/movement/combat strategy, không subclass theo tên bot.
- Item/skill asset template tách khỏi item/skill runtime của nhân vật.
- Constant command nằm ở protocol catalog; business code không dùng magic number.

## Trạng thái contract

- `LOCKED`: đã có source/test hoặc public signature được chốt; thay đổi cần ADR.
- `RESERVED`: tên, package, trách nhiệm và method shape đã giữ chỗ; triển khai theo lộ trình.
- `REFERENCE_ONLY`: chỉ là khả năng quan sát từ NSOKISS, chưa được phép đưa vào runtime.

Danh mục máy đọc được nằm ở `planned-contracts.tsv`. Architecture test chặn package ngoài
lock, namespace cũ, dòng contract lỗi/trùng và dependency domain → adapter.

## Quy trình thay đổi khung

1. Tạo `docs/architecture/decisions/ADR-xxxx-*.md` nêu vấn đề, lựa chọn và migration.
2. Cập nhật TSV và tài liệu module.
3. Thêm/đổi architecture test trước source.
4. Giữ adapter tương thích hoặc có migration rõ ràng.
5. Nhận xác nhận của chủ dự án rồi mới merge/push.
