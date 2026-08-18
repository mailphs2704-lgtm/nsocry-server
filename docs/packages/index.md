# Chỉ mục package/module NSOCry

> Source NSOCry đã bắt đầu. Protocol, session và network hiện ACTIVE; các module nghiệp vụ còn lại vẫn PROPOSED.

## Chuẩn namespace

- Implementation mới dùng package root com.nsocry.
- Không tạo package/class/method mới chứa nsoz hoặc nsotien.
- Không sao chép cây package reference sang namespace mới.

| Module | Trách nhiệm | Dependency chính | Trạng thái |
|---|---|---|---|
| com.nsocry.bootstrap | Startup, shutdown và composition root | configuration/network/session | active |
| com.nsocry.configuration | Đọc và kiểm tra config | network config | active |
| persistence | DB, transaction và repository | MariaDB driver | proposed |
| com.nsocry.protocol.compat | Key, frame và stream compatibility | Java I/O | active |
| com.nsocry.session | Handshake state, payload decode và auth port | protocol.compat | active |
| com.nsocry.network | Socket accept, giới hạn và session ownership | protocol/session | active |
| com.nsocry.authentication | Password hash và quyết định đăng nhập | account repository port | active |
| player | Account và character lifecycle | persistence/domain | pending |
| game-data | Static data loading | persistence/resources | pending |
| world | Map, zone, mob và NPC | game-data/player | pending |
| scheduler | Periodic jobs | application contracts | pending |
| administration | Maintenance/admin use cases | application | pending |
| com.nsocry.observability | Sự kiện runtime đã làm sạch | network event port | active |

## Tài liệu đang hoạt động

- [Protocol và session](protocol-session.md)
- [Tra cứu source protocol, session và network](../code-reference/protocol-session-network.md)
- [Chuẩn documentation](../development/documentation-standard.md)
- [Bootstrap, configuration và observability](bootstrap-configuration-observability.md)
- [Thiết kế tài khoản và xác thực](../database/account-authentication.md)
- [Package authentication](authentication.md)
