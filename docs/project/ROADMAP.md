# Roadmap tổng thể NSOCry

Roadmap dùng stage gate: không chuyển pha chỉ vì đã viết nhiều code; phải đạt tiêu chí đầu ra và kiểm chứng.

## Phase 0 — Bảo toàn và nền tảng dự án

**Mục tiêu:** lưu reference an toàn và thiết lập cách cộng tác không mất tiến độ.

Đầu ra:

- Repository/Git hoạt động.
- Reference được tách khỏi source mới.
- Documentation skeleton.
- STATUS/WORKLOG/ADR/handoff.
- NSOKISS đang chạy không bị ảnh hưởng.

**Trạng thái:** gần hoàn thành; chờ duyệt/merge bộ tài liệu nền tảng.

## Phase 1 — Reverse engineering có kiểm soát

**Mục tiêu:** hiểu contract cần tương thích, không cố hiểu mọi feature cùng lúc.

Workstream:

1. Runtime/bootstrap.
2. Message framing và key transform.
3. Command inventory.
4. Handshake/client metadata.
5. Authentication/login.
6. Player load và enter game.
7. Game-data loading.
8. Database access map.
9. Client JAR cross-check.

Đầu ra bắt buộc:

- Protocol glossary.
- Command inventory có trạng thái VERIFIED/UNKNOWN.
- Sequence diagram login.
- Call graph login.
- Table-to-code matrix.
- Danh sách hành vi cần giữ và bug không nên sao chép.

**Gate:** có thể mô tả byte-level flow từ client connect đến login/character selection mà không suy đoán.

## Phase 2 — Kiến trúc và nền tảng NSOCry

**Mục tiêu:** tạo server skeleton độc lập, chưa triển khai gameplay lớn.

Module đề xuất (PROPOSED):

- `bootstrap`
- `configuration`
- `persistence`
- `network.transport`
- `protocol`
- `authentication`
- `player`
- `game-data`
- `world`
- `scheduler`
- `administration`
- `observability`

Đầu ra:

- Build xanh.
- Config có validation.
- Database `nsocry` và migration đầu tiên.
- Server lifecycle start/stop.
- Frame codec có unit test.
- Session lifecycle có test.
- Structured logging.
- Không phụ thuộc runtime vào package `com.nsoz`.

**Gate:** server khởi động/dừng sạch và codec vượt test fixture đã lấy từ protocol reference.

## Phase 3 — Vertical slice đầu tiên

**Mục tiêu:** một đường đi hoàn chỉnh, nhỏ nhưng chạy thật.

Luồng mục tiêu:

```text
client connect
-> handshake
-> login
-> load account/character
-> enter một map thử nghiệm
-> disconnect/save sạch
```

Đầu ra:

- Schema tối thiểu.
- Authentication.
- Character load.
- Một map/zone.
- Session cleanup.
- Integration test và log kiểm chứng.
- Test trên PC; sau đó test thiết bị cùng mạng.

**Gate:** client thật hoàn thành luồng trên lặp lại được mà không dùng database NSOKISS.

## Phase 4 — Gameplay nền tảng

Theo dependency, không theo độ hấp dẫn:

1. Player stats/state.
2. Map/zone movement.
3. Mob.
4. Item/inventory/equipment.
5. Skill/combat/effect.
6. NPC/menu.
7. Task.
8. Store/trade.

Mỗi module cần spec, mapping reference, code, test, documentation và migration nếu có.

## Phase 5 — Hệ thống xã hội và nội dung mở rộng

- Party.
- Clan.
- Friend.
- Chat/global.
- Stall/trade.
- Ranking.
- Arena/war.
- Event.
- Boss scheduler.
- Gift code/admin.

Thứ tự cuối cùng phụ thuộc yêu cầu gameplay của người dùng.

## Phase 6 — Ổn định và vận hành

- Load/concurrency test.
- Backup/restore.
- Migration rehearsal.
- Metrics/health checks.
- Rate limiting và abuse protection.
- Security review.
- Deployment/runbook.
- Release checklist.
- Test PC/mobile/network.

## Mốc phát hành đề xuất

- **M0:** documentation và discovery foundation.
- **M1:** protocol/login specification.
- **M2:** NSOCry skeleton build xanh.
- **M3:** client login + enter test map.
- **M4:** gameplay core.
- **M5:** feature-complete theo scope đã chốt.
- **M6:** release candidate ổn định.

Không gắn ngày cố định trước khi hoàn thành Phase 1 và đo được tốc độ triển khai/kiểm thử.
