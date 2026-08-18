# Chỉ mục package/module NSOCry

> Chưa có source NSOCry. Danh sách dưới đây là PROPOSED và sẽ được chốt sau Phase 1.

| Module | Trách nhiệm | Dependency chính | Trạng thái tài liệu |
|---|---|---|---|
| bootstrap | startup/shutdown/lifecycle | tất cả qua contract | proposed |
| configuration | đọc/validate config | không phụ thuộc gameplay | proposed |
| persistence | DB, transaction, repository | MariaDB driver | proposed |
| network.transport | socket/session bytes | protocol codec | proposed |
| protocol | frame/command/payload | không phụ thuộc DB | discovery |
| authentication | login/session binding | player repository | pending |
| player | account/character lifecycle | persistence, domain | pending |
| game-data | static data loading | persistence/resources | pending |
| world | map/zone/mob/NPC | game-data/player | pending |
| scheduler | periodic jobs | application contracts | pending |
| administration | maintenance/admin use cases | application | pending |
| observability | log/metrics/trace | cross-cutting | pending |

Khi tạo package thật, thêm tài liệu riêng cho từng module theo [documentation-standard.md](../development/documentation-standard.md). Không đổi tên/ràng buộc module chỉ để giống package NSOKISS.
