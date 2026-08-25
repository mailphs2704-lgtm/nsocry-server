# Sổ truy vết phần chưa đủ dữ liệu

Nhãn `TRACE_REQUIRED` có nghĩa là chưa được phép viết tài liệu như một hành vi chuẩn hoặc
đánh dấu hoàn thành. Khi đủ evidence phải cập nhật dòng tương ứng, tài liệu module, STATUS và
WORKLOG.

| ID | Phạm vi | Thiếu dữ liệu gì | Điều kiện đóng truy vết |
| --- | --- | --- | --- |
| TR-001 | DATA client asset | nguồn authoritative, codec/command đầy đủ | fixture + converter + checksum + Windows test |
| TR-002 | Appearance client asset | ranh giới item/fashion/effect paint | client read path + immutable model + codec test |
| TR-003 | MAP database | bảng catalog đích, index/FK, migration version | schema contract + backup + preflight test |
| TR-004 | MAP runtime | ownership map/zone và snapshot composition | full asset sources + startup integration test |
| TR-005 | Player load/save | schema character đầy đủ và transaction boundary | field mapping + repository contract + integration test |
| TR-006 | Select player | command payload và lifecycle sau auth | client/server fixture + session transition test |
| TR-007 | World/map entry | zone allocation, spawn, leave/reconnect | static call graph + concurrency decision + loopback test |
| TR-008 | Combat | damage formula, target validation, cooldown | reference evidence + gameplay contract + deterministic tests |
| TR-009 | Skill runtime | template lookup và mutable cooldown/state | typed runtime design + startup snapshot composition |
| TR-010 | Inventory/equipment | slot rules, persistence, item options | schema mapping + invariants + transactional tests |
| TR-011 | Mob/NPC | spawn AI, menu action routing | command mapping + world ownership + fixtures |
| TR-012 | Task | progression conditions/rewards | data source + state transition matrix + tests |
| TR-013 | Party/clan | membership/concurrency/persistence | use-case contracts + schema + race tests |
| TR-014 | Trade/stall/store | atomic economy mutation, rollback | transaction model + exploit/threat tests |
| TR-015 | Event/bot/ranking | schedule/config/strategy boundaries | data-driven design + clock abstraction + tests |
| TR-016 | Giftcode/admin | authorization, audit, idempotency | admin identity + audit schema + security tests |
| TR-017 | Real client gameplay | PC/phone compatibility after login | server startup + client connection evidence |

## Quy tắc đóng một truy vết

1. Ghi evidence chính xác và source location/fixture.
2. Cập nhật contract từ RESERVED sang trạng thái thích hợp nếu cần.
3. Viết module manual theo tám câu hỏi trong README.
4. Thêm test tự động và output Windows.
5. Ghi database/runtime impact.
6. Không xóa ID truy vết; chuyển sang CLOSED và liên kết commit/checkpoint.
