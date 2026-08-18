# Chỉ mục package/module NSOCry

> Source NSOCry đã bắt đầu. Protocol, session và network hiện ACTIVE; các module nghiệp vụ còn lại vẫn PROPOSED.

## Chuẩn namespace

- Implementation mới dùng package root com.nsocry.
- Không tạo package/class/method mới chứa nsoz hoặc nsotien.
- Không sao chép cây package reference sang namespace mới.

| Module | Trách nhiệm | Dependency chính | Trạng thái |
|---|---|---|---|
| bootstrap | Startup, shutdown và lifecycle | Các contract ứng dụng | next |
| configuration | Đọc và kiểm tra config | Không phụ thuộc gameplay | next |
| persistence | DB, transaction và repository | MariaDB driver | proposed |
| com.nsocry.protocol.compat | Key, frame và stream compatibility | Java I/O | active |
| com.nsocry.session | Handshake state, payload decode và auth port | protocol.compat | active |
| com.nsocry.network | Socket accept, giới hạn và session ownership | protocol/session | active |
| authentication | Login và session binding | player repository | pending |
| player | Account và character lifecycle | persistence/domain | pending |
| game-data | Static data loading | persistence/resources | pending |
| world | Map, zone, mob và NPC | game-data/player | pending |
| scheduler | Periodic jobs | application contracts | pending |
| administration | Maintenance/admin use cases | application | pending |
| observability | Log, metrics và trace | cross-cutting | next |

## Tài liệu đang hoạt động

- [Protocol và session](protocol-session.md)
- [Tra cứu source protocol, session và network](../code-reference/protocol-session-network.md)
- [Chuẩn documentation](../development/documentation-standard.md)
