# Ma trận bao phủ logic NSOKISS → NSOCry

Ma trận này chứng minh mọi package tham chiếu đã có đích kiến trúc. Đây không phải chỉ
định sao chép class; NSOCry giữ behavior cần thiết nhưng viết lại model và contract.

| Package tham chiếu | Đích NSOCry đã khóa | Quyết định |
|---|---|---|
| ability | game.ability, game.combat | Strategy đóng góp stat, không mutable global ability |
| admin | administration | Command có actor/audit, không gọi DAO tùy ý |
| api, db.jdbc, db.mongodb | application.port, persistence | Port trong core; JDBC/Mongo chỉ là adapter |
| bot, bot.attack, bot.move | game.bot | Composition policy, không subclass theo tên NPC/bot |
| clan | game.clan | Aggregate + repository + application service |
| constants | protocol.command, assets, game.* | Constant về đúng owner; cấm một constants package hỗn hợp |
| convert, fake, fake.model_read | assets.conversion, operations | Công cụ offline; không nằm trong runtime gameplay |
| effect | game.effect, assets | Definition tĩnh tách khỏi effect runtime |
| event, event.eventpoint | game.event | Definition/handler plugin, không class riêng cho mỗi lễ hội |
| fashion | game.fashion | Resolver appearance/ability theo strategy |
| item, option | game.item, assets | Template client tách khỏi item instance/inventory runtime |
| lib | common hoặc adapter sở hữu trực tiếp | Không tạo utility dump; từng capability có owner |
| map | game.map | Definition/registry/travel service |
| map.item | game.map.item | Ground item lifecycle và pickup policy |
| map.world | game.map.instance, game.event | World/instance rule thay inheritance sâu |
| map.zones | game.map.instance | Zone đặc biệt là data + MapInstanceRule |
| mob | game.mob, assets | Template tĩnh tách mob runtime/behavior |
| model | game.player và domain tương ứng | Giải thể god package/model; type về đúng bounded context |
| model.phancung | operations/reference only | Không đưa tool ngoài gameplay vào domain |
| network, socket | network, session, protocol.*, application | Transport decode typed command rồi gọi use-case |
| npc | game.npc, assets | NPC template tĩnh + typed interaction handler |
| party | game.party | Party aggregate và service |
| server | bootstrap, scheduler, administration, game.* | Giải thể ServerManager/GameData singleton |
| skill | game.skill, assets | Skill template tĩnh tách learned skill/runtime execution |
| stall, store | game.store | Catalog/purchase/player stall với transaction port |
| task | game.task, assets | Task definition/progress/service/repository |
| thiendia | game.ranking | Một ranking subsystem có type và snapshot |
| util | common hoặc package owner | Chỉ giữ utility thuần có owner rõ ràng |

## Logic không được port máy móc

- UI Swing quản trị (`JFrameSendItem`) không thuộc server core; thay bằng admin command,
  sau này `run.bat` hoặc website chỉ là client của cùng use-case.
- Class lễ hội/zone/bot đặt theo tên riêng không trở thành public skeleton; dùng registry
  và definition data để thêm nội dung mà không đổi kiến trúc.
- SQL string, global singleton, static mutable manager và session truy cập trực tiếp domain
  đều bị loại khỏi khung mới.
- MongoDB không là bắt buộc; chỉ thêm adapter khi có use-case và ADR thực tế.
