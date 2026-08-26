# Danh mục code production NSOCry

> File này được sinh cơ học từ `src/main/java` bởi `tools/generate-developer-catalog.sh`.

> Catalog giúp tìm symbol; mô tả hành vi chuẩn nằm trong manual module và STATUS.

## Cách dùng

- Tìm theo `package.Type`, tên method hoặc source path.
- `IMPLEMENTED` chỉ xác nhận code tồn tại; xem STATUS/manual để biết mức VERIFIED.
- Method package-private/private quan trọng có thể không xuất hiện trong danh sách API; xem source và manual module.
- Khi thêm/xóa source phải sinh lại catalog và chạy `DocumentationCoverageTest`.

## `com.nsocry.assets`

**Vai trò:** Read model, codec, manifest, validator và runtime asset snapshot.

**Trạng thái:** `IMPLEMENTED`; bằng chứng chi tiết xem [STATUS](../project/STATUS.md).

### `com.nsocry.assets.AppearanceAssetBundle`

- **Source:** `src/main/java/com/nsocry/assets/AppearanceAssetBundle.java`
- **Vai trò tóm tắt:** Read model đầy đủ cho phần appearance nối sau bốn byte version.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 7 — `public record AppearanceAssetBundle(`**: Read model đầy đủ cho phần appearance nối sau bốn byte version.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.AppearanceAssetCodec`

- **Source:** `src/main/java/com/nsocry/assets/AppearanceAssetCodec.java`
- **Vai trò tóm tắt:** Encoder và parser kiểm chứng phần appearance của UPDATE_VERSION.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 13 — `public final class AppearanceAssetCodec {`**: Encoder và parser kiểm chứng phần appearance của UPDATE_VERSION.
  - **Dòng 23 — `public static byte[] encode(AppearanceAssetBundle bundle) throws IOException {`**: Mã hóa ba head group, leg, ba body group và mount theo thứ tự client đọc.
  - **Dòng 60 — `public static AppearanceAssetBundle decode(byte[] payload) throws IOException {`**: Parse lại appearance payload và từ chối byte dư.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.AppearanceAssetSource`

- **Source:** `src/main/java/com/nsocry/assets/AppearanceAssetSource.java`
- **Vai trò tóm tắt:** Cổng đọc read model ngoại hình dùng trong payload thương lượng phiên bản.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 5 — `public interface AppearanceAssetSource {`**: Cổng đọc read model ngoại hình dùng trong payload thương lượng phiên bản.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.AppearanceLayerAsset`

- **Source:** `src/main/java/com/nsocry/assets/AppearanceLayerAsset.java`
- **Vai trò tóm tắt:** Một lớp ảnh con và độ lệch dùng để ghép part ngoại hình.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 4 — `public record AppearanceLayerAsset(short imageId, short dx, short dy) {`**: Một lớp ảnh con và độ lệch dùng để ghép part ngoại hình.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.AppearancePartAsset`

- **Source:** `src/main/java/com/nsocry/assets/AppearancePartAsset.java`
- **Vai trò tóm tắt:** Một part head/body gồm id, ảnh nhỏ và các lớp ảnh ghép.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 7 — `public record AppearancePartAsset(`**: Một part head/body gồm id, ảnh nhỏ và các lớp ảnh ghép.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.AtomicClientAssetSnapshotProvider`

- **Source:** `src/main/java/com/nsocry/assets/AtomicClientAssetSnapshotProvider.java`
- **Vai trò tóm tắt:** Provider cho phép thay toàn bộ asset snapshot nguyên tử mà session đang chạy không bị khóa.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 7 — `public final class AtomicClientAssetSnapshotProvider`**: Provider cho phép thay toàn bộ asset snapshot nguyên tử mà session đang chạy không bị khóa.
  - **Dòng 12 — `public AtomicClientAssetSnapshotProvider(ClientAssetSnapshot initialSnapshot) {`**: Khởi tạo provider bằng một snapshot hoàn chỉnh bắt buộc.
  - **Dòng 18 — `public ClientAssetSnapshot currentSnapshot() {`**: Trả đúng một snapshot nhất quán tại thời điểm đọc.
  - **Dòng 24 — `public void publish(ClientAssetSnapshot snapshot) {`**: Publish snapshot hoàn chỉnh mới trong một thao tác nguyên tử.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.AtomicMapAssetRuntimeSnapshotStore`

- **Source:** `src/main/java/com/nsocry/assets/AtomicMapAssetRuntimeSnapshotStore.java`
- **Vai trò tóm tắt:** Kho runtime thay toàn bộ MAP snapshot nguyên tử, không lộ trạng thái bán phần.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 8 — `public final class AtomicMapAssetRuntimeSnapshotStore {`**: Kho runtime thay toàn bộ MAP snapshot nguyên tử, không lộ trạng thái bán phần.
  - **Dòng 12 — `public Optional<MapAssetRuntimeSnapshot> currentSnapshot() {`**: Trả snapshot hiện hành hoặc rỗng khi chưa vượt gate.
  - **Dòng 17 — `public void publish(MapAssetRuntimeSnapshot snapshot) {`**: Publish một snapshot hoàn chỉnh bằng atomic swap.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.AtomicSkillAssetRuntimeSnapshotStore`

- **Source:** `src/main/java/com/nsocry/assets/AtomicSkillAssetRuntimeSnapshotStore.java`
- **Vai trò tóm tắt:** Kho runtime thay toàn bộ SKILL snapshot nguyên tử và không cho thấy trạng thái bán phần.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 8 — `public final class AtomicSkillAssetRuntimeSnapshotStore {`**: Kho runtime thay toàn bộ SKILL snapshot nguyên tử và không cho thấy trạng thái bán phần.
  - **Dòng 12 — `public Optional<SkillAssetRuntimeSnapshot> currentSnapshot() {`**: Trả snapshot đã publish, hoặc rỗng khi startup chưa hoàn tất gate.
  - **Dòng 17 — `public void publish(SkillAssetRuntimeSnapshot snapshot) {`**: Publish đúng một snapshot đã hoàn chỉnh bằng atomic swap.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.ClientAssetSnapshot`

- **Source:** `src/main/java/com/nsocry/assets/ClientAssetSnapshot.java`
- **Vai trò tóm tắt:** Ảnh chụp bất biến của toàn bộ asset client cần trong một lần thương lượng phiên bản.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 14 — `public final class ClientAssetSnapshot {`**: Ảnh chụp bất biến của toàn bộ asset client cần trong một lần thương lượng phiên bản.
  - **Dòng 20 — `public ClientAssetSnapshot(`**: Tạo snapshot và xác minh byte phiên bản đầu mỗi payload khớp manifest.
  - **Dòng 42 — `public ClientVersionManifest manifest() {`**: Trả manifest phiên bản gắn cố định với snapshot này.
  - **Dòng 47 — `public byte[] appearanceData() {`**: Trả bản sao dữ liệu ngoại hình nối sau header UPDATE_VERSION.
  - **Dòng 52 — `public byte[] payload(ClientDataSet dataSet) {`**: Trả bản sao payload của bộ dữ liệu được yêu cầu.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.ClientAssetSnapshotAssembler`

- **Source:** `src/main/java/com/nsocry/assets/ClientAssetSnapshotAssembler.java`
- **Vai trò tóm tắt:** Ghép các bundle đã kiểm tra thành một snapshot đồng bộ có thể publish cho session.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 10 — `public final class ClientAssetSnapshotAssembler {`**: Ghép các bundle đã kiểm tra thành một snapshot đồng bộ có thể publish cho session.
  - **Dòng 18 — `public static ClientAssetSnapshot assemble(`**: Mã hóa toàn bộ bundle trước, sau đó mới tạo snapshot; nếu một codec lỗi thì không có
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.ClientAssetSnapshotBuildService`

- **Source:** `src/main/java/com/nsocry/assets/ClientAssetSnapshotBuildService.java`
- **Vai trò tóm tắt:** Điều phối đọc năm nguồn, mã hóa và publish snapshot theo nguyên tắc tất cả hoặc không.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 7 — `public final class ClientAssetSnapshotBuildService {`**: Điều phối đọc năm nguồn, mã hóa và publish snapshot theo nguyên tắc tất cả hoặc không.
  - **Dòng 16 — `public ClientAssetSnapshotBuildService(`**: Khởi tạo dịch vụ bằng các cổng nguồn và cổng publish bắt buộc.
  - **Dòng 36 — `public ClientAssetSnapshot rebuild() throws ClientAssetSourceException, IOException {`**: Dựng rồi publish một snapshot mới. Snapshot cũ không đổi nếu đọc nguồn hoặc mã hóa thất bại.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.ClientAssetSnapshotProvider`

- **Source:** `src/main/java/com/nsocry/assets/ClientAssetSnapshotProvider.java`
- **Vai trò tóm tắt:** Cổng cung cấp snapshot asset hoàn chỉnh cho tầng session mà không lộ nguồn lưu trữ bên dưới.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 5 — `public interface ClientAssetSnapshotProvider {`**: Cổng cung cấp snapshot asset hoàn chỉnh cho tầng session mà không lộ nguồn lưu trữ bên dưới.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.ClientAssetSnapshotPublisher`

- **Source:** `src/main/java/com/nsocry/assets/ClientAssetSnapshotPublisher.java`
- **Vai trò tóm tắt:** Cổng publish nguyên tử một snapshot đã build và kiểm tra đầy đủ.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 5 — `public interface ClientAssetSnapshotPublisher {`**: Cổng publish nguyên tử một snapshot đã build và kiểm tra đầy đủ.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.ClientAssetSourceException`

- **Source:** `src/main/java/com/nsocry/assets/ClientAssetSourceException.java`
- **Vai trò tóm tắt:** Lỗi đọc dữ liệu asset từ nguồn lưu trữ, không làm lộ công nghệ lưu trữ cho tầng build.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 4 — `public final class ClientAssetSourceException extends Exception {`**: Lỗi đọc dữ liệu asset từ nguồn lưu trữ, không làm lộ công nghệ lưu trữ cho tầng build.
  - **Dòng 6 — `public ClientAssetSourceException(String message, Throwable cause) {`**: Tạo lỗi kèm nguyên nhân gốc để tầng vận hành có thể ghi nhận an toàn.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.ClientGraphicBlock`

- **Source:** `src/main/java/com/nsocry/assets/ClientGraphicBlock.java`
- **Vai trò tóm tắt:** Năm block graphics có length-prefix nằm đầu payload DATA.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 4 — `public enum ClientGraphicBlock {`**: Năm block graphics có length-prefix nằm đầu payload DATA.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.DataAssetBundle`

- **Source:** `src/main/java/com/nsocry/assets/DataAssetBundle.java`
- **Vai trò tóm tắt:** Read model bất biến của payload DATA tổng hợp.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 10 — `public final class DataAssetBundle {`**: Read model bất biến của payload DATA tổng hợp.
  - **Dòng 19 — `public DataAssetBundle(`**: Sao chép sâu mọi mảng và danh sách trước khi publish bundle.
  - **Dòng 38 — `public byte version() {`**: Trả version raw byte của payload DATA.
  - **Dòng 43 — `public byte[] graphic(ClientGraphicBlock block) {`**: Trả bản sao block graphics được yêu cầu.
  - **Dòng 50 — `public List<List<TaskRouteAsset>> taskRoutes() {`**: Trả các tuyến nhiệm vụ bất biến theo từng nhóm client.
  - **Dòng 55 — `public long[] experienceThresholds() {`**: Trả bản sao bảng ngưỡng kinh nghiệm để caller không sửa bundle.
  - **Dòng 60 — `public int[] progression(ProgressionTable table) {`**: Trả bản sao bảng progression được yêu cầu.
  - **Dòng 67 — `public byte[] effectTemplates() {`**: Trả bản sao payload effect template thuộc DATA.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.DataAssetCodec`

- **Source:** `src/main/java/com/nsocry/assets/DataAssetCodec.java`
- **Vai trò tóm tắt:** Encoder và parser cấp container cho payload DATA tổng hợp.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 15 — `public final class DataAssetCodec {`**: Encoder và parser cấp container cho payload DATA tổng hợp.
  - **Dòng 22 — `public static byte[] encode(DataAssetBundle bundle) throws IOException {`**: Mã hóa năm graphics block, task routes, EXP, progression và effect-template tail.
  - **Dòng 62 — `public static DataAssetBundle decode(byte[] payload) throws IOException {`**: Parse lại container; effect-template là block cuối nên nhận toàn bộ byte còn lại.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.DataAssetSource`

- **Source:** `src/main/java/com/nsocry/assets/DataAssetSource.java`
- **Vai trò tóm tắt:** Cổng đọc read model DATA; implementation có thể dùng JDBC, file hoặc fixture.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 5 — `public interface DataAssetSource {`**: Cổng đọc read model DATA; implementation có thể dùng JDBC, file hoặc fixture.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.ItemAssetBundle`

- **Source:** `src/main/java/com/nsocry/assets/ItemAssetBundle.java`
- **Vai trò tóm tắt:** Read model bất biến dùng để build payload ITEM, tách khỏi entity gameplay và persistence.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 7 — `public record ItemAssetBundle(`**: Read model bất biến dùng để build payload ITEM, tách khỏi entity gameplay và persistence.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.ItemAssetCodec`

- **Source:** `src/main/java/com/nsocry/assets/ItemAssetCodec.java`
- **Vai trò tóm tắt:** Mã hóa và parse kiểm chứng payload ITEM theo byte layout của client V7.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 13 — `public final class ItemAssetCodec {`**: Mã hóa và parse kiểm chứng payload ITEM theo byte layout của client V7.
  - **Dòng 21 — `public static byte[] encode(ItemAssetBundle bundle) throws IOException {`**: Mã hóa bundle thành payload bắt đầu bằng item version.
  - **Dòng 50 — `public static ItemAssetBundle decode(byte[] payload) throws IOException {`**: Parse lại payload đã build để validator có thể chứng minh schema và không còn byte dư.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.ItemAssetSeedArtifact`

- **Source:** `src/main/java/com/nsocry/assets/ItemAssetSeedArtifact.java`
- **Vai trò tóm tắt:** Artifact seed ITEM bất biến gồm payload codec và manifest văn bản xác định.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 7 — `public final class ItemAssetSeedArtifact {`**: Artifact seed ITEM bất biến gồm payload codec và manifest văn bản xác định.
  - **Dòng 26 — `public byte[] payload() {`**: Trả bản sao payload để bên gọi không thể sửa artifact đã kiểm định.
  - **Dòng 31 — `public ItemAssetSeedManifest manifest() {`**: Trả manifest có thể dùng để kiểm định lại trước khi import.
  - **Dòng 36 — `public ItemAssetValidationResult validation() {`**: Trả metadata validation của payload.
  - **Dòng 41 — `public String manifestText() {`**: Trả manifest dạng UTF-8 key=value có thứ tự dòng cố định.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.ItemAssetSeedArtifactGenerator`

- **Source:** `src/main/java/com/nsocry/assets/ItemAssetSeedArtifactGenerator.java`
- **Vai trò tóm tắt:** Sinh artifact seed ITEM xác định, không tạo SQL động và không truy cập database.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 10 — `public final class ItemAssetSeedArtifactGenerator {`**: Sinh artifact seed ITEM xác định, không tạo SQL động và không truy cập database.
  - **Dòng 17 — `public static ItemAssetSeedArtifact generate(ItemAssetBundle bundle)`**: Encode bundle, tạo manifest rồi tự kiểm định trước khi trả artifact.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.ItemAssetSeedManifest`

- **Source:** `src/main/java/com/nsocry/assets/ItemAssetSeedManifest.java`
- **Vai trò tóm tắt:** Kỳ vọng bất biến dùng để nhận diện chính xác một bộ seed ITEM đã được phê duyệt.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 8 — `public record ItemAssetSeedManifest(`**: Kỳ vọng bất biến dùng để nhận diện chính xác một bộ seed ITEM đã được phê duyệt.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.ItemAssetSeedManifestParser`

- **Source:** `src/main/java/com/nsocry/assets/ItemAssetSeedManifestParser.java`
- **Vai trò tóm tắt:** Parse manifest ITEM seed v1 theo định dạng canonical, từ chối khóa thừa hoặc đổi thứ tự.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 6 — `public final class ItemAssetSeedManifestParser {`**: Parse manifest ITEM seed v1 theo định dạng canonical, từ chối khóa thừa hoặc đổi thứ tự.
  - **Dòng 13 — `public static ItemAssetSeedManifest parse(String text) throws ItemAssetSeedValidationException {`**: Parse đúng sáu dòng key=value và yêu cầu newline LF kết thúc file.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.ItemAssetSeedValidationException`

- **Source:** `src/main/java/com/nsocry/assets/ItemAssetSeedValidationException.java`
- **Vai trò tóm tắt:** Báo bộ seed ITEM không khớp manifest đã phê duyệt hoặc không qua được codec.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 4 — `public final class ItemAssetSeedValidationException extends Exception {`**: Báo bộ seed ITEM không khớp manifest đã phê duyệt hoặc không qua được codec.
  - **Dòng 6 — `public ItemAssetSeedValidationException(String message) {`**: Tạo lỗi validation không chứa nội dung payload.
  - **Dòng 11 — `public ItemAssetSeedValidationException(String message, Throwable cause) {`**: Tạo lỗi validation và giữ nguyên nguyên nhân kỹ thuật.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.ItemAssetSeedValidator`

- **Source:** `src/main/java/com/nsocry/assets/ItemAssetSeedValidator.java`
- **Vai trò tóm tắt:** Xác minh count, version, round-trip codec và SHA-256 trước khi chấp nhận seed ITEM.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 10 — `public final class ItemAssetSeedValidator {`**: Xác minh count, version, round-trip codec và SHA-256 trước khi chấp nhận seed ITEM.
  - **Dòng 15 — `public static ItemAssetValidationResult validate(`**: Validate bundle theo manifest và trả metadata có thể ghi vào nhật ký vận hành.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.ItemAssetSource`

- **Source:** `src/main/java/com/nsocry/assets/ItemAssetSource.java`
- **Vai trò tóm tắt:** Cổng đọc read model ITEM độc lập với repository gameplay.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 5 — `public interface ItemAssetSource {`**: Cổng đọc read model ITEM độc lập với repository gameplay.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.ItemAssetValidationResult`

- **Source:** `src/main/java/com/nsocry/assets/ItemAssetValidationResult.java`
- **Vai trò tóm tắt:** Metadata của payload ITEM đã encode, parse lại và khớp manifest.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 4 — `public record ItemAssetValidationResult(`**: Metadata của payload ITEM đã encode, parse lại và khớp manifest.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.ItemOptionAsset`

- **Source:** `src/main/java/com/nsocry/assets/ItemOptionAsset.java`
- **Vai trò tóm tắt:** Metadata một loại tùy chọn vật phẩm mà client cần để hiển thị.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 6 — `public record ItemOptionAsset(String name, byte type) {`**: Metadata một loại tùy chọn vật phẩm mà client cần để hiển thị.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.ItemTemplateAsset`

- **Source:** `src/main/java/com/nsocry/assets/ItemTemplateAsset.java`
- **Vai trò tóm tắt:** Metadata hiển thị bất biến của một mẫu vật phẩm phía client.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 6 — `public record ItemTemplateAsset(`**: Metadata hiển thị bất biến của một mẫu vật phẩm phía client.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.LegAppearanceAsset`

- **Source:** `src/main/java/com/nsocry/assets/LegAppearanceAsset.java`
- **Vai trò tóm tắt:** Part chân chỉ cần id và ảnh nhỏ trong version payload.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 4 — `public record LegAppearanceAsset(short id, short smallImageId) {`**: Part chân chỉ cần id và ảnh nhỏ trong version payload.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.MapAssetBundle`

- **Source:** `src/main/java/com/nsocry/assets/MapAssetBundle.java`
- **Vai trò tóm tắt:** Read model gồm tên map, NPC template và mob template cho payload MAP.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 7 — `public record MapAssetBundle(`**: Read model gồm tên map, NPC template và mob template cho payload MAP.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.MapAssetCodec`

- **Source:** `src/main/java/com/nsocry/assets/MapAssetCodec.java`
- **Vai trò tóm tắt:** Encoder và parser kiểm chứng payload MAP dành riêng cho client V7 build 217.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 13 — `public final class MapAssetCodec {`**: Encoder và parser kiểm chứng payload MAP dành riêng cho client V7 build 217.
  - **Dòng 22 — `public static byte[] encode(MapAssetBundle bundle) throws IOException {`**: Mã hóa map/NPC/mob catalog và kiểm soát đúng giới hạn kiểu count trên wire.
  - **Dòng 51 — `public static MapAssetBundle decode(byte[] payload) throws IOException {`**: Parse lại payload MAP và từ chối count âm hoặc byte dư.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.MapAssetRuntimePublishService`

- **Source:** `src/main/java/com/nsocry/assets/MapAssetRuntimePublishService.java`
- **Vai trò tóm tắt:** Đọc, xác minh và publish MAP runtime theo nguyên tắc tất cả hoặc không.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 7 — `public final class MapAssetRuntimePublishService {`**: Đọc, xác minh và publish MAP runtime theo nguyên tắc tất cả hoặc không.
  - **Dòng 13 — `public MapAssetRuntimePublishService(`**: Tạo service từ source, manifest khóa và atomic store đích.
  - **Dòng 23 — `public MapAssetRuntimeSnapshot rebuildAndPublish()`**: Publish chỉ sau khi JDBC bundle khớp version/count/length/SHA-256.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.MapAssetRuntimeSnapshot`

- **Source:** `src/main/java/com/nsocry/assets/MapAssetRuntimeSnapshot.java`
- **Vai trò tóm tắt:** Snapshot MAP bất biến đã vượt version, count, length và checksum gate.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 7 — `public final class MapAssetRuntimeSnapshot {`**: Snapshot MAP bất biến đã vượt version, count, length và checksum gate.
  - **Dòng 40 — `public byte version() { return version; }`**: Trả version wire đã khóa.
  - **Dòng 42 — `public int mapCount() { return mapCount; }`**: Trả số map name trong payload.
  - **Dòng 44 — `public int npcCount() { return npcCount; }`**: Trả số NPC template trong payload.
  - **Dòng 46 — `public int mobCount() { return mobCount; }`**: Trả số mob template trong payload.
  - **Dòng 48 — `public int payloadLength() { return payloadLength; }`**: Trả độ dài payload đã khóa.
  - **Dòng 50 — `public String payloadSha256() { return payloadSha256; }`**: Trả SHA-256 đã đối chiếu với payload.
  - **Dòng 52 — `public byte[] payload() { return Arrays.copyOf(payload, payload.length); }`**: Trả defensive copy để session không sửa snapshot dùng chung.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.MapAssetSeedArtifact`

- **Source:** `src/main/java/com/nsocry/assets/MapAssetSeedArtifact.java`
- **Vai trò tóm tắt:** MAP seed artifact bất biến gồm payload codec, manifest text và validation result.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 7 — `public final class MapAssetSeedArtifact {`**: MAP seed artifact bất biến gồm payload codec, manifest text và validation result.
  - **Dòng 23 — `public byte[] payload() {`**: Trả defensive copy để caller không thể sửa candidate đã kiểm định.
  - **Dòng 28 — `public String manifestText() {`**: Trả manifest UTF-8 xác định dùng khi ghi archive.
  - **Dòng 33 — `public MapAssetSeedValidationResult validation() {`**: Trả metadata đã xác minh của đúng payload candidate.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.MapAssetSeedArtifactGenerator`

- **Source:** `src/main/java/com/nsocry/assets/MapAssetSeedArtifactGenerator.java`
- **Vai trò tóm tắt:** Sinh MAP seed artifact xác định và tự kiểm định trước khi trả kết quả.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 7 — `public final class MapAssetSeedArtifactGenerator {`**: Sinh MAP seed artifact xác định và tự kiểm định trước khi trả kết quả.
  - **Dòng 14 — `public static MapAssetSeedArtifact generate(MapAssetBundle bundle) {`**: Encode bundle, tạo manifest checksum rồi validate lại cùng candidate.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.MapAssetSeedManifest`

- **Source:** `src/main/java/com/nsocry/assets/MapAssetSeedManifest.java`
- **Vai trò tóm tắt:** Manifest xác định cho một MAP seed candidate đã encode.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 8 — `public record MapAssetSeedManifest(`**: Manifest xác định cho một MAP seed candidate đã encode.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.MapAssetSeedManifestParser`

- **Source:** `src/main/java/com/nsocry/assets/MapAssetSeedManifestParser.java`
- **Vai trò tóm tắt:** Parse manifest MAP key=value với schema đóng, không chấp nhận field lạ hoặc trùng.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 8 — `public final class MapAssetSeedManifestParser {`**: Parse manifest MAP key=value với schema đóng, không chấp nhận field lạ hoặc trùng.
  - **Dòng 15 — `public static MapAssetSeedManifest parse(String text) {`**: Parse toàn bộ manifest và fail closed nếu schema/field/value không hợp lệ.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.MapAssetSeedValidationResult`

- **Source:** `src/main/java/com/nsocry/assets/MapAssetSeedValidationResult.java`
- **Vai trò tóm tắt:** Kết quả kiểm định MAP candidate sau khi đối chiếu manifest với payload encode lại.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 6 — `public record MapAssetSeedValidationResult(`**: Kết quả kiểm định MAP candidate sau khi đối chiếu manifest với payload encode lại.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.MapAssetSeedValidator`

- **Source:** `src/main/java/com/nsocry/assets/MapAssetSeedValidator.java`
- **Vai trò tóm tắt:** Đối chiếu MAP bundle với manifest bằng count, payload length và SHA-256.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 10 — `public final class MapAssetSeedValidator {`**: Đối chiếu MAP bundle với manifest bằng count, payload length và SHA-256.
  - **Dòng 15 — `public static MapAssetSeedValidationResult validate(`**: Encode lại bundle và từ chối candidate nếu bất kỳ metadata nào lệch manifest.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.MapAssetSource`

- **Source:** `src/main/java/com/nsocry/assets/MapAssetSource.java`
- **Vai trò tóm tắt:** Cổng đọc read model MAP mà không để tầng build phụ thuộc trực tiếp vào JDBC.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 5 — `public interface MapAssetSource {`**: Cổng đọc read model MAP mà không để tầng build phụ thuộc trực tiếp vào JDBC.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.MobTemplateAsset`

- **Source:** `src/main/java/com/nsocry/assets/MobTemplateAsset.java`
- **Vai trò tóm tắt:** Metadata tĩnh của một loại quái mà client cần để khởi tạo template.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 6 — `public record MobTemplateAsset(`**: Metadata tĩnh của một loại quái mà client cần để khởi tạo template.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.MountAppearanceAsset`

- **Source:** `src/main/java/com/nsocry/assets/MountAppearanceAsset.java`
- **Vai trò tóm tắt:** Dữ liệu ngoại hình thú cưỡi gồm item id và đúng sáu dãy frame.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 7 — `public record MountAppearanceAsset(short itemId, List<List<Short>> frameGroups) {`**: Dữ liệu ngoại hình thú cưỡi gồm item id và đúng sáu dãy frame.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.NpcTemplateAsset`

- **Source:** `src/main/java/com/nsocry/assets/NpcTemplateAsset.java`
- **Vai trò tóm tắt:** Metadata hiển thị và menu tĩnh của một NPC phía client.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 7 — `public record NpcTemplateAsset(`**: Metadata hiển thị và menu tĩnh của một NPC phía client.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.ProgressionTable`

- **Source:** `src/main/java/com/nsocry/assets/ProgressionTable.java`
- **Vai trò tóm tắt:** Các bảng số nguyên progression được gửi cho client theo thứ tự cố định.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 4 — `public enum ProgressionTable {`**: Các bảng số nguyên progression được gửi cho client theo thứ tự cố định.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.SkillAssetBundle`

- **Source:** `src/main/java/com/nsocry/assets/SkillAssetBundle.java`
- **Vai trò tóm tắt:** Read model hoàn chỉnh dùng để build payload SKILL.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 7 — `public record SkillAssetBundle(`**: Read model hoàn chỉnh dùng để build payload SKILL.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.SkillAssetCodec`

- **Source:** `src/main/java/com/nsocry/assets/SkillAssetCodec.java`
- **Vai trò tóm tắt:** Encoder và parser kiểm chứng payload SKILL theo đúng thứ tự client V7 đọc.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 13 — `public final class SkillAssetCodec {`**: Encoder và parser kiểm chứng payload SKILL theo đúng thứ tự client V7 đọc.
  - **Dòng 21 — `public static byte[] encode(SkillAssetBundle bundle) throws IOException {`**: Mã hóa toàn bộ skill catalog, kiểm tra count trước khi ghi một byte.
  - **Dòng 46 — `public static SkillAssetBundle decode(byte[] payload) throws IOException {`**: Parse lại output để validator phát hiện sai layout hoặc byte dư.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.SkillAssetRuntimePublishService`

- **Source:** `src/main/java/com/nsocry/assets/SkillAssetRuntimePublishService.java`
- **Vai trò tóm tắt:** Đọc, xác minh và publish SKILL runtime theo nguyên tắc tất cả hoặc không.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 7 — `public final class SkillAssetRuntimePublishService {`**: Đọc, xác minh và publish SKILL runtime theo nguyên tắc tất cả hoặc không.
  - **Dòng 13 — `public SkillAssetRuntimePublishService(`**: Tạo service từ source, manifest khóa và atomic store đích; từ chối dependency null.
  - **Dòng 23 — `public SkillAssetRuntimeSnapshot rebuildAndPublish() throws ClientAssetSourceException, IOException {`**: Publish sau khi bundle database khớp version, count, payload length và SHA-256 đã khóa.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.SkillAssetRuntimeSnapshot`

- **Source:** `src/main/java/com/nsocry/assets/SkillAssetRuntimeSnapshot.java`
- **Vai trò tóm tắt:** Snapshot SKILL bất biến đã vượt gate version, cấu trúc và checksum trước khi publish.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 7 — `public final class SkillAssetRuntimeSnapshot {`**: Snapshot SKILL bất biến đã vượt gate version, cấu trúc và checksum trước khi publish.
  - **Dòng 38 — `public byte version() { return version; }`**: Trả version raw byte đã vượt manifest gate.
  - **Dòng 40 — `public SkillAssetValidationReport structure() { return structure; }`**: Trả báo cáo cấu trúc SKILL bất biến.
  - **Dòng 42 — `public int payloadLength() { return payloadLength; }`**: Trả độ dài payload đã khóa.
  - **Dòng 44 — `public String payloadSha256() { return payloadSha256; }`**: Trả SHA-256 chữ thường đã đối chiếu với chính payload.
  - **Dòng 47 — `public byte[] payload() { return Arrays.copyOf(payload, payload.length); }`**: Trả bản sao để session không thể sửa payload đang được dùng chung.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.SkillAssetSeedArtifact`

- **Source:** `src/main/java/com/nsocry/assets/SkillAssetSeedArtifact.java`
- **Vai trò tóm tắt:** Artifact SKILL bất biến gồm payload codec và manifest UTF-8 xác định.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 7 — `public final class SkillAssetSeedArtifact {`**: Artifact SKILL bất biến gồm payload codec và manifest UTF-8 xác định.
  - **Dòng 19 — `public byte[] payload() {`**: Trả defensive copy của payload seed.
  - **Dòng 24 — `public String manifestText() {`**: Trả manifest UTF-8 xác định đi kèm payload.
  - **Dòng 29 — `public SkillAssetSeedValidationResult validation() {`**: Trả metadata/checksum đã xác minh của candidate.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.SkillAssetSeedArtifactGenerator`

- **Source:** `src/main/java/com/nsocry/assets/SkillAssetSeedArtifactGenerator.java`
- **Vai trò tóm tắt:** Sinh artifact SKILL xác định và tự kiểm định trước khi trả kết quả.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 7 — `public final class SkillAssetSeedArtifactGenerator {`**: Sinh artifact SKILL xác định và tự kiểm định trước khi trả kết quả.
  - **Dòng 14 — `public static SkillAssetSeedArtifact generate(SkillAssetBundle bundle) {`**: Encode bundle, sinh manifest deterministic và validate lại trước khi trả artifact.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.SkillAssetSeedManifest`

- **Source:** `src/main/java/com/nsocry/assets/SkillAssetSeedManifest.java`
- **Vai trò tóm tắt:** Kỳ vọng bất biến để nhận diện chính xác một candidate SKILL đã kiểm định.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 8 — `public record SkillAssetSeedManifest(`**: Kỳ vọng bất biến để nhận diện chính xác một candidate SKILL đã kiểm định.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.SkillAssetSeedManifestParser`

- **Source:** `src/main/java/com/nsocry/assets/SkillAssetSeedManifestParser.java`
- **Vai trò tóm tắt:** Parse manifest SKILL key=value với schema đóng và không chấp nhận field lạ.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 8 — `public final class SkillAssetSeedManifestParser {`**: Parse manifest SKILL key=value với schema đóng và không chấp nhận field lạ.
  - **Dòng 15 — `public static SkillAssetSeedManifest parse(String text) {`**: Parse manifest schema đóng và từ chối field thiếu, lạ, trùng hoặc sai kiểu.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.SkillAssetSeedValidationResult`

- **Source:** `src/main/java/com/nsocry/assets/SkillAssetSeedValidationResult.java`
- **Vai trò tóm tắt:** Metadata SKILL đã xác minh từ read model, payload và manifest.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 8 — `public record SkillAssetSeedValidationResult(`**: Metadata SKILL đã xác minh từ read model, payload và manifest.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.SkillAssetSeedValidator`

- **Source:** `src/main/java/com/nsocry/assets/SkillAssetSeedValidator.java`
- **Vai trò tóm tắt:** Đối chiếu cấu trúc, checksum và raw-byte SKILL trước khi chấp nhận candidate.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 13 — `public final class SkillAssetSeedValidator {`**: Đối chiếu cấu trúc, checksum và raw-byte SKILL trước khi chấp nhận candidate.
  - **Dòng 18 — `public static SkillAssetSeedValidationResult validate(SkillAssetBundle bundle, SkillAssetSeedManifest manifest) {`**: Encode lại bundle rồi so toàn bộ metadata với manifest.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.SkillAssetSource`

- **Source:** `src/main/java/com/nsocry/assets/SkillAssetSource.java`
- **Vai trò tóm tắt:** Cổng đọc read model SKILL độc lập với schema và công nghệ lưu trữ.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 5 — `public interface SkillAssetSource {`**: Cổng đọc read model SKILL độc lập với schema và công nghệ lưu trữ.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.SkillAssetStructureValidator`

- **Source:** `src/main/java/com/nsocry/assets/SkillAssetStructureValidator.java`
- **Vai trò tóm tắt:** Kiểm tra quan hệ ID/count của SKILL read model trước seed/codec validation.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 8 — `public final class SkillAssetStructureValidator {`**: Kiểm tra quan hệ ID/count của SKILL read model trước seed/codec validation.
  - **Dòng 16 — `public static SkillAssetValidationReport validate(SkillAssetBundle bundle) {`**: Từ chối ID trùng, reference option sai và count vượt wire; trả count report.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.SkillAssetValidationReport`

- **Source:** `src/main/java/com/nsocry/assets/SkillAssetValidationReport.java`
- **Vai trò tóm tắt:** Count tổng hợp của SKILL bundle đã vượt structural validation.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 4 — `public record SkillAssetValidationReport(`**: Count tổng hợp của SKILL bundle đã vượt structural validation.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.SkillClassAsset`

- **Source:** `src/main/java/com/nsocry/assets/SkillClassAsset.java`
- **Vai trò tóm tắt:** Nhóm kỹ năng của một môn phái/lớp nhân vật phía client.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 7 — `public record SkillClassAsset(String name, List<SkillTemplateAsset> templates) {`**: Nhóm kỹ năng của một môn phái/lớp nhân vật phía client.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.SkillLevelAsset`

- **Source:** `src/main/java/com/nsocry/assets/SkillLevelAsset.java`
- **Vai trò tóm tắt:** Dữ liệu một cấp kỹ năng mà client dùng cho mana, cooldown, tầm đánh và option.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 7 — `public record SkillLevelAsset(`**: Dữ liệu một cấp kỹ năng mà client dùng cho mana, cooldown, tầm đánh và option.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.SkillLevelOptionAsset`

- **Source:** `src/main/java/com/nsocry/assets/SkillLevelOptionAsset.java`
- **Vai trò tóm tắt:** Một tham số option gắn với cấp kỹ năng, tham chiếu option template bằng byte id.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 4 — `public record SkillLevelOptionAsset(short parameter, byte optionTemplateId) {`**: Một tham số option gắn với cấp kỹ năng, tham chiếu option template bằng byte id.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.SkillTemplateAsset`

- **Source:** `src/main/java/com/nsocry/assets/SkillTemplateAsset.java`
- **Vai trò tóm tắt:** Metadata và các cấp độ của một mẫu kỹ năng thuộc một môn phái.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 7 — `public record SkillTemplateAsset(`**: Metadata và các cấp độ của một mẫu kỹ năng thuộc một môn phái.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.TaskRouteAsset`

- **Source:** `src/main/java/com/nsocry/assets/TaskRouteAsset.java`
- **Vai trò tóm tắt:** Một cặp NPC/map trong tuyến nhiệm vụ tĩnh mà client dùng để định tuyến.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 4 — `public record TaskRouteAsset(byte npcId, byte mapId) {`**: Một cặp NPC/map trong tuyến nhiệm vụ tĩnh mà client dùng để định tuyến.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.package-info`

- **Source:** `src/main/java/com/nsocry/assets/package-info.java`
- **Vai trò tóm tắt:** Pipeline dựng, xác minh và cung cấp asset tương thích client của NSOCry.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - Không có API public/protected một dòng; xem source/package contract.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

## `com.nsocry.assets.conversion`

**Vai trò:** Parse/chuyển nguồn reference offline sang immutable asset bundle.

**Trạng thái:** `IMPLEMENTED`; bằng chứng chi tiết xem [STATUS](../project/STATUS.md).

### `com.nsocry.assets.conversion.ItemAssetConversionReport`

- **Source:** `src/main/java/com/nsocry/assets/conversion/ItemAssetConversionReport.java`
- **Vai trò tóm tắt:** Báo cáo count/range/difference của một lần chuyển dữ liệu ITEM tham chiếu.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 4 — `public record ItemAssetConversionReport(`**: Báo cáo count/range/difference của một lần chuyển dữ liệu ITEM tham chiếu.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.conversion.ItemAssetConversionResult`

- **Source:** `src/main/java/com/nsocry/assets/conversion/ItemAssetConversionResult.java`
- **Vai trò tóm tắt:** Kết quả chuyển đổi gồm bundle có thể encode và báo cáo đối chiếu đi kèm.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 7 — `public record ItemAssetConversionResult(ItemAssetBundle bundle, ItemAssetConversionReport report) {`**: Kết quả chuyển đổi gồm bundle có thể encode và báo cáo đối chiếu đi kèm.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.conversion.MapAssetConversionResult`

- **Source:** `src/main/java/com/nsocry/assets/conversion/MapAssetConversionResult.java`
- **Vai trò tóm tắt:** Kết quả convert MAP gồm bundle candidate và inventory evidence từ dump tham chiếu.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 7 — `public record MapAssetConversionResult(MapAssetBundle bundle, MapDumpInventoryReport report) {`**: Kết quả convert MAP gồm bundle candidate và inventory evidence từ dump tham chiếu.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.conversion.MapDumpInventoryReport`

- **Source:** `src/main/java/com/nsocry/assets/conversion/MapDumpInventoryReport.java`
- **Vai trò tóm tắt:** Inventory đã kiểm tra của ba nguồn MAP trong dump tham chiếu.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 7 — `public record MapDumpInventoryReport(`**: Inventory đã kiểm tra của ba nguồn MAP trong dump tham chiếu.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.conversion.MapRawByteDifference`

- **Source:** `src/main/java/com/nsocry/assets/conversion/MapRawByteDifference.java`
- **Vai trò tóm tắt:** Ghi nhận giá trị MAP raw byte 128..255 để phân biệt wire byte với signed Java byte.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 6 — `public record MapRawByteDifference(String entityType, int entityId, String field, int value) {`**: Ghi nhận giá trị MAP raw byte 128..255 để phân biệt wire byte với signed Java byte.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.conversion.ReferenceItemAssetConverter`

- **Source:** `src/main/java/com/nsocry/assets/conversion/ReferenceItemAssetConverter.java`
- **Vai trò tóm tắt:** Chuyển row tham chiếu thành ITEM read model NSOCry mà không truy cập database.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 12 — `public final class ReferenceItemAssetConverter {`**: Chuyển row tham chiếu thành ITEM read model NSOCry mà không truy cập database.
  - **Dòng 17 — `public static ItemAssetConversionResult convert(`**: Sắp theo ID, kiểm tra contract wire và trả bundle cùng báo cáo khác biệt.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.conversion.ReferenceItemDumpRows`

- **Source:** `src/main/java/com/nsocry/assets/conversion/ReferenceItemDumpRows.java`
- **Vai trò tóm tắt:** Hai tập row ITEM được parse từ dump tham chiếu trước bước chuyển sang NSOCry.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 7 — `public record ReferenceItemDumpRows(`**: Hai tập row ITEM được parse từ dump tham chiếu trước bước chuyển sang NSOCry.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.conversion.ReferenceItemOptionRow`

- **Source:** `src/main/java/com/nsocry/assets/conversion/ReferenceItemOptionRow.java`
- **Vai trò tóm tắt:** Một dòng item option từ nguồn tham chiếu dùng riêng cho công cụ chuyển đổi offline.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 6 — `public record ReferenceItemOptionRow(int id, int type, String name) {`**: Một dòng item option từ nguồn tham chiếu dùng riêng cho công cụ chuyển đổi offline.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.conversion.ReferenceItemSqlDumpParser`

- **Source:** `src/main/java/com/nsocry/assets/conversion/ReferenceItemSqlDumpParser.java`
- **Vai trò tóm tắt:** Parser giới hạn cho đúng hai INSERT ITEM trong MariaDB dump tham chiếu.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 8 — `public final class ReferenceItemSqlDumpParser {`**: Parser giới hạn cho đúng hai INSERT ITEM trong MariaDB dump tham chiếu.
  - **Dòng 18 — `public static ReferenceItemDumpRows parse(String dump) {`**: Parse hai statement bắt buộc; không thực thi SQL và không đọc bảng khác.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.conversion.ReferenceItemTemplateRow`

- **Source:** `src/main/java/com/nsocry/assets/conversion/ReferenceItemTemplateRow.java`
- **Vai trò tóm tắt:** Một dòng item template tham chiếu; giữ cả fashion để báo cáo phần không có trên wire ITEM.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 6 — `public record ReferenceItemTemplateRow(`**: Một dòng item template tham chiếu; giữ cả fashion để báo cáo phần không có trên wire ITEM.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.conversion.ReferenceMapAssetConverter`

- **Source:** `src/main/java/com/nsocry/assets/conversion/ReferenceMapAssetConverter.java`
- **Vai trò tóm tắt:** Chuyển đúng ba catalog MAP trong dump thành read model theo wire client V7.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 11 — `public final class ReferenceMapAssetConverter {`**: Chuyển đúng ba catalog MAP trong dump thành read model theo wire client V7.
  - **Dòng 16 — `public static MapAssetConversionResult convert(byte version, String dump) {`**: Validate toàn dump trước, sau đó chỉ lấy các cột thực sự thuộc payload MAP client.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.conversion.ReferenceMapDumpInventoryParser`

- **Source:** `src/main/java/com/nsocry/assets/conversion/ReferenceMapDumpInventoryParser.java`
- **Vai trò tóm tắt:** Parse và kiểm kê đúng ba catalog MAP client từ MariaDB dump tham chiếu.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 8 — `public final class ReferenceMapDumpInventoryParser {`**: Parse và kiểm kê đúng ba catalog MAP client từ MariaDB dump tham chiếu.
  - **Dòng 17 — `public static MapDumpInventoryReport parse(String dump) {`**: Kiểm tra count, ID, menu NPC và mọi field thực sự đi lên wire MAP.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.conversion.ReferenceSkillAssetConverter`

- **Source:** `src/main/java/com/nsocry/assets/conversion/ReferenceSkillAssetConverter.java`
- **Vai trò tóm tắt:** Chuyển bốn nguồn SKILL trong dump thành read model đúng thứ tự wire client V7.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 15 — `public final class ReferenceSkillAssetConverter {`**: Chuyển bốn nguồn SKILL trong dump thành read model đúng thứ tự wire client V7.
  - **Dòng 26 — `public static SkillAssetConversionResult convert(byte version, String dump) {`**: Validate toàn dump trước, sau đó dựng cây class → template → level → option.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.conversion.ReferenceSkillDumpInventoryParser`

- **Source:** `src/main/java/com/nsocry/assets/conversion/ReferenceSkillDumpInventoryParser.java`
- **Vai trò tóm tắt:** Parse và đối chiếu inventory SKILL tham chiếu mà chưa tạo seed hoặc truy cập database.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 12 — `public final class ReferenceSkillDumpInventoryParser {`**: Parse và đối chiếu inventory SKILL tham chiếu mà chưa tạo seed hoặc truy cập database.
  - **Dòng 23 — `public static SkillDumpInventoryReport parse(String dump) {`**: Kiểm tra count, ID liên tục và mọi reference class/template/option trong dump.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.conversion.SkillAssetConversionResult`

- **Source:** `src/main/java/com/nsocry/assets/conversion/SkillAssetConversionResult.java`
- **Vai trò tóm tắt:** Kết quả chuyển đổi SKILL gồm read model wire-ready và inventory đối chiếu.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 7 — `public record SkillAssetConversionResult(`**: Kết quả chuyển đổi SKILL gồm read model wire-ready và inventory đối chiếu.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.conversion.SkillDumpInventoryReport`

- **Source:** `src/main/java/com/nsocry/assets/conversion/SkillDumpInventoryReport.java`
- **Vai trò tóm tắt:** Inventory đã kiểm tra của bốn nguồn SKILL trong dump tham chiếu.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 7 — `public record SkillDumpInventoryReport(`**: Inventory đã kiểm tra của bốn nguồn SKILL trong dump tham chiếu.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.assets.conversion.SkillRawByteDifference`

- **Source:** `src/main/java/com/nsocry/assets/conversion/SkillRawByteDifference.java`
- **Vai trò tóm tắt:** Một giá trị raw byte 128–255 cần quyết định compatibility trước khi cast sang byte Java.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 6 — `public record SkillRawByteDifference(String entityType, int entityId, String field, int value) {`**: Một giá trị raw byte 128–255 cần quyết định compatibility trước khi cast sang byte Java.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

## `com.nsocry.authentication`

**Vai trò:** Credential policy và application authentication contract.

**Trạng thái:** `IMPLEMENTED`; bằng chứng chi tiết xem [STATUS](../project/STATUS.md).

### `com.nsocry.authentication.AccountCredential`

- **Source:** `src/main/java/com/nsocry/authentication/AccountCredential.java`
- **Vai trò tóm tắt:** Dữ liệu xác thực tối thiểu được repository tải cho một tài khoản.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 7 — `public record AccountCredential(`**: Dữ liệu xác thực tối thiểu được repository tải cho một tài khoản.
  - **Dòng 26 — `public boolean isTemporarilyLockedAt(Instant instant) {`**: Cho biết tài khoản có bị khóa tạm tại thời điểm được cung cấp hay không.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.authentication.AccountProvisioningRepository`

- **Source:** `src/main/java/com/nsocry/authentication/AccountProvisioningRepository.java`
- **Vai trò tóm tắt:** Port tối thiểu dùng riêng cho quá trình tạo account ban đầu.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 4 — `public interface AccountProvisioningRepository {`**: Port tối thiểu dùng riêng cho quá trình tạo account ban đầu.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.authentication.AccountRepository`

- **Source:** `src/main/java/com/nsocry/authentication/AccountRepository.java`
- **Vai trò tóm tắt:** Port lưu trữ tối thiểu mà authentication service cần, chưa phụ thuộc JDBC.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 7 — `public interface AccountRepository {`**: Port lưu trữ tối thiểu mà authentication service cần, chưa phụ thuộc JDBC.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.authentication.AccountRole`

- **Source:** `src/main/java/com/nsocry/authentication/AccountRole.java`
- **Vai trò tóm tắt:** Role tài khoản được lưu đúng theo constraint của schema accounts.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 4 — `public enum AccountRole {`**: Role tài khoản được lưu đúng theo constraint của schema accounts.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.authentication.AccountStatus`

- **Source:** `src/main/java/com/nsocry/authentication/AccountStatus.java`
- **Vai trò tóm tắt:** Trạng thái nghiệp vụ tối thiểu quyết định tài khoản có được đăng nhập hay không.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 4 — `public enum AccountStatus {`**: Trạng thái nghiệp vụ tối thiểu quyết định tài khoản có được đăng nhập hay không.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.authentication.AuthenticationService`

- **Source:** `src/main/java/com/nsocry/authentication/AuthenticationService.java`
- **Vai trò tóm tắt:** Xác thực account bằng repository và password hasher mà không phân biệt lỗi trả cho client.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 14 — `public final class AuthenticationService implements AuthenticationPort {`**: Xác thực account bằng repository và password hasher mà không phân biệt lỗi trả cho client.
  - **Dòng 21 — `public AuthenticationService(`**: Tạo service cùng dummy hash dùng để cân bằng đường xử lý username không tồn tại.
  - **Dòng 34 — `public AuthenticationDecision authenticate(LoginRequest request, ClientInfo clientInfo) {`**: Xác minh password rồi kiểm tra activated, status và khóa tạm trước khi chấp nhận.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.authentication.FirstAdministratorService`

- **Source:** `src/main/java/com/nsocry/authentication/FirstAdministratorService.java`
- **Vai trò tóm tắt:** Tạo administrator đầu tiên theo quy tắc một lần và luôn xóa password đầu vào.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 8 — `public final class FirstAdministratorService {`**: Tạo administrator đầu tiên theo quy tắc một lần và luôn xóa password đầu vào.
  - **Dòng 17 — `public FirstAdministratorService(`**: Tạo service từ provisioning repository và password hasher.
  - **Dòng 28 — `public long provision(String username, char[] password) {`**: Tạo administrator đầu tiên và trả id mới.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.authentication.PasswordHashingPort`

- **Source:** `src/main/java/com/nsocry/authentication/PasswordHashingPort.java`
- **Vai trò tóm tắt:** Port tạo và xác minh password hash có version mà không làm lộ thuật toán cho service.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 4 — `public interface PasswordHashingPort {`**: Port tạo và xác minh password hash có version mà không làm lộ thuật toán cho service.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.authentication.Pbkdf2PasswordHasher`

- **Source:** `src/main/java/com/nsocry/authentication/Pbkdf2PasswordHasher.java`
- **Vai trò tóm tắt:** Password hasher thuần Java dùng PBKDF2-HMAC-SHA256, salt riêng và định dạng có version.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 12 — `public final class Pbkdf2PasswordHasher implements PasswordHashingPort {`**: Password hasher thuần Java dùng PBKDF2-HMAC-SHA256, salt riêng và định dạng có version.
  - **Dòng 25 — `public Pbkdf2PasswordHasher() {`**: Tạo hasher với SecureRandom mặc định và work factor 600.000 vòng.
  - **Dòng 40 — `public String hash(char[] password) {`**: Tạo salt mới, dẫn xuất hash và đóng gói version, work factor, salt cùng kết quả.
  - **Dòng 52 — `public boolean verify(char[] password, String encodedHash) {`**: Phân tích chuỗi đã lưu và so sánh kết quả bằng MessageDigest.isEqual.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.authentication.package-info`

- **Source:** `src/main/java/com/nsocry/authentication/package-info.java`
- **Vai trò tóm tắt:** Chứa domain và application service cho xác thực tài khoản NSOCry.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - Không có API public/protected một dòng; xem source/package contract.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

## `com.nsocry.bootstrap`

**Vai trò:** Composition root, launcher và command vận hành.

**Trạng thái:** `IMPLEMENTED`; bằng chứng chi tiết xem [STATUS](../project/STATUS.md).

### `com.nsocry.bootstrap.FirstAdministratorCommand`

- **Source:** `src/main/java/com/nsocry/bootstrap/FirstAdministratorCommand.java`
- **Vai trò tóm tắt:** Lệnh tương tác tạo administrator đầu tiên mà không nhận password qua argument.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 15 — `public final class FirstAdministratorCommand {`**: Lệnh tương tác tạo administrator đầu tiên mà không nhận password qua argument.
  - **Dòng 20 — `public static void main(String[] args) throws Exception {`**: Đọc username/password từ Console, xác nhận password rồi tạo đúng một administrator.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.bootstrap.ItemAssetDatabaseVerifyCommand`

- **Source:** `src/main/java/com/nsocry/bootstrap/ItemAssetDatabaseVerifyCommand.java`
- **Vai trò tóm tắt:** Xác minh dữ liệu ITEM trong database tái tạo đúng payload candidate mà không ghi dữ liệu.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 23 — `public final class ItemAssetDatabaseVerifyCommand {`**: Xác minh dữ liệu ITEM trong database tái tạo đúng payload candidate mà không ghi dữ liệu.
  - **Dòng 28 — `public static void main(String[] args) throws Exception {`**: Đọc archive, schema và JDBC source rồi so count/length/SHA-256 end-to-end.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.bootstrap.ItemAssetSchemaPreflightCommand`

- **Source:** `src/main/java/com/nsocry/bootstrap/ItemAssetSchemaPreflightCommand.java`
- **Vai trò tóm tắt:** Command chỉ đọc kiểm tra database hiện tại có khớp schema ITEM V002 hay không.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 14 — `public final class ItemAssetSchemaPreflightCommand {`**: Command chỉ đọc kiểm tra database hiện tại có khớp schema ITEM V002 hay không.
  - **Dòng 19 — `public static void main(String[] args) throws Exception {`**: Nạp config, mở DataSource chỉ đọc và dừng với lỗi nếu schema chưa sẵn sàng.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.bootstrap.ItemAssetSeedConvertCommand`

- **Source:** `src/main/java/com/nsocry/bootstrap/ItemAssetSeedConvertCommand.java`
- **Vai trò tóm tắt:** Command chuyển ITEM trong dump tham chiếu thành candidate archive, không dùng JDBC.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 19 — `public final class ItemAssetSeedConvertCommand {`**: Command chuyển ITEM trong dump tham chiếu thành candidate archive, không dùng JDBC.
  - **Dòng 27 — `public static void main(String[] args) throws Exception {`**: Chấp nhận đúng một dump path, xuất candidate cạnh file nguồn và in metadata.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.bootstrap.ItemAssetSeedDryRunCommand`

- **Source:** `src/main/java/com/nsocry/bootstrap/ItemAssetSeedDryRunCommand.java`
- **Vai trò tóm tắt:** Command kiểm định archive ITEM seed và chỉ in metadata, không mở database.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 8 — `public final class ItemAssetSeedDryRunCommand {`**: Command kiểm định archive ITEM seed và chỉ in metadata, không mở database.
  - **Dòng 13 — `public static void main(String[] args) throws Exception {`**: Chấp nhận đúng một đường dẫn archive và in báo cáo dry-run.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.bootstrap.ItemAssetSeedImportCommand`

- **Source:** `src/main/java/com/nsocry/bootstrap/ItemAssetSeedImportCommand.java`
- **Vai trò tóm tắt:** Command tương tác import ITEM seed đã duyệt; không chạy migration hoặc publish runtime.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 21 — `public final class ItemAssetSeedImportCommand {`**: Command tương tác import ITEM seed đã duyệt; không chạy migration hoặc publish runtime.
  - **Dòng 26 — `public static void main(String[] args) throws Exception {`**: Kiểm định archive/schema, yêu cầu nhập đúng SHA-256 rồi mới mở transaction import.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.bootstrap.MapAssetDatabaseVerifyCommand`

- **Source:** `src/main/java/com/nsocry/bootstrap/MapAssetDatabaseVerifyCommand.java`
- **Vai trò tóm tắt:** Xác minh dữ liệu MAP trong database tái tạo đúng payload candidate.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 13 — `public final class MapAssetDatabaseVerifyCommand {`**: Xác minh dữ liệu MAP trong database tái tạo đúng payload candidate.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.bootstrap.MapAssetRuntimePublishCommand`

- **Source:** `src/main/java/com/nsocry/bootstrap/MapAssetRuntimePublishCommand.java`
- **Vai trò tóm tắt:** Publish thử MAP snapshot từ JDBC sau toàn bộ gate; không ghi database.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 13 — `public final class MapAssetRuntimePublishCommand {`**: Publish thử MAP snapshot từ JDBC sau toàn bộ gate; không ghi database.
  - **Dòng 17 — `public static void main(String[] args) throws Exception {`**: Đọc archive, preflight schema và publish vào atomic store cục bộ của command.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.bootstrap.MapAssetSchemaPreflightCommand`

- **Source:** `src/main/java/com/nsocry/bootstrap/MapAssetSchemaPreflightCommand.java`
- **Vai trò tóm tắt:** Command chỉ đọc kiểm tra database hiện tại có khớp schema MAP V004 hay không.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 14 — `public final class MapAssetSchemaPreflightCommand {`**: Command chỉ đọc kiểm tra database hiện tại có khớp schema MAP V004 hay không.
  - **Dòng 19 — `public static void main(String[] args) throws Exception {`**: Nạp config, mở DataSource read-only và trả lỗi khi schema chưa sẵn sàng.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.bootstrap.MapAssetSeedConvertCommand`

- **Source:** `src/main/java/com/nsocry/bootstrap/MapAssetSeedConvertCommand.java`
- **Vai trò tóm tắt:** Tạo MAP seed candidate offline từ dump; không mở database hoặc publish runtime.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 16 — `public final class MapAssetSeedConvertCommand {`**: Tạo MAP seed candidate offline từ dump; không mở database hoặc publish runtime.
  - **Dòng 24 — `public static void main(String[] args) throws Exception {`**: Entry CLI yêu cầu đúng một dump path rồi tạo MAP candidate offline.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.bootstrap.MapAssetSeedDryRunCommand`

- **Source:** `src/main/java/com/nsocry/bootstrap/MapAssetSeedDryRunCommand.java`
- **Vai trò tóm tắt:** Xác minh MAP archive offline; không mở database hoặc publish runtime.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 8 — `public final class MapAssetSeedDryRunCommand {`**: Xác minh MAP archive offline; không mở database hoặc publish runtime.
  - **Dòng 13 — `public static void main(String[] args) throws Exception {`**: Entry CLI yêu cầu đúng một archive path rồi xác minh không mutation.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.bootstrap.MapAssetSeedImportCommand`

- **Source:** `src/main/java/com/nsocry/bootstrap/MapAssetSeedImportCommand.java`
- **Vai trò tóm tắt:** Command tương tác import MAP đã duyệt; không migration hoặc publish runtime.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 15 — `public final class MapAssetSeedImportCommand {`**: Command tương tác import MAP đã duyệt; không migration hoặc publish runtime.
  - **Dòng 18 — `public static void main(String[] args)throws Exception{`**: Archive + V004 READY + full SHA-256 là ba gate trước transaction.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.bootstrap.NsocryLauncher`

- **Source:** `src/main/java/com/nsocry/bootstrap/NsocryLauncher.java`
- **Vai trò tóm tắt:** Entry point duy nhất của executable JAR, phân luồng lệnh vận hành rõ ràng.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 6 — `public final class NsocryLauncher {`**: Entry point duy nhất của executable JAR, phân luồng lệnh vận hành rõ ràng.
  - **Dòng 11 — `public static void main(String[] args) throws Exception {`**: Phân tích argument rồi chạy server, tạo administrator hoặc in trợ giúp.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.bootstrap.NsocryServerApplication`

- **Source:** `src/main/java/com/nsocry/bootstrap/NsocryServerApplication.java`
- **Vai trò tóm tắt:** Điểm ghép và vòng đời tối thiểu để chạy TCP server NSOCry từ cấu hình.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 26 — `public final class NsocryServerApplication implements Closeable {`**: Điểm ghép và vòng đời tối thiểu để chạy TCP server NSOCry từ cấu hình.
  - **Dòng 30 — `public NsocryServerApplication(`**: Ghép cấu hình, xác thực và event sink thành server nhưng chưa tự động start.
  - **Dòng 43 — `public void start() throws IOException {`**: Khởi động TCP listener sau khi toàn bộ dependency đã được tạo thành công.
  - **Dòng 48 — `public TcpServer server() {`**: Trả server đang được application sở hữu để kiểm tra trạng thái và địa chỉ bind.
  - **Dòng 54 — `public void close() throws IOException {`**: Dừng toàn bộ tài nguyên runtime thuộc application.
  - **Dòng 62 — `public static void main(String[] args) throws Exception {`**: Chạy server từ file cấu hình được chỉ định ở argument đầu tiên hoặc config/nsocry.properties.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.bootstrap.SkillAssetDatabaseVerifyCommand`

- **Source:** `src/main/java/com/nsocry/bootstrap/SkillAssetDatabaseVerifyCommand.java`
- **Vai trò tóm tắt:** Xác minh dữ liệu SKILL trong database tái tạo đúng payload candidate.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 22 — `public final class SkillAssetDatabaseVerifyCommand {`**: Xác minh dữ liệu SKILL trong database tái tạo đúng payload candidate.
  - **Dòng 27 — `public static void main(String[] args) throws Exception {`**: Đọc archive/schema/JDBC source và so payload end-to-end, không ghi database.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.bootstrap.SkillAssetRuntimePublishCommand`

- **Source:** `src/main/java/com/nsocry/bootstrap/SkillAssetRuntimePublishCommand.java`
- **Vai trò tóm tắt:** Publish thử SKILL snapshot từ JDBC sau toàn bộ gate candidate; không ghi database.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 22 — `public final class SkillAssetRuntimePublishCommand {`**: Publish thử SKILL snapshot từ JDBC sau toàn bộ gate candidate; không ghi database.
  - **Dòng 27 — `public static void main(String[] args) throws Exception {`**: Đọc archive, preflight schema và publish vào atomic store cục bộ của command.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.bootstrap.SkillAssetSchemaPreflightCommand`

- **Source:** `src/main/java/com/nsocry/bootstrap/SkillAssetSchemaPreflightCommand.java`
- **Vai trò tóm tắt:** Command chỉ đọc kiểm tra database hiện tại có khớp schema SKILL V003 hay không.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 14 — `public final class SkillAssetSchemaPreflightCommand {`**: Command chỉ đọc kiểm tra database hiện tại có khớp schema SKILL V003 hay không.
  - **Dòng 19 — `public static void main(String[] args) throws Exception {`**: Nạp config, mở DataSource read-only và trả lỗi khi schema chưa sẵn sàng.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.bootstrap.SkillAssetSeedConvertCommand`

- **Source:** `src/main/java/com/nsocry/bootstrap/SkillAssetSeedConvertCommand.java`
- **Vai trò tóm tắt:** Command tạo SKILL candidate offline từ dump, không dùng JDBC.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 16 — `public final class SkillAssetSeedConvertCommand {`**: Command tạo SKILL candidate offline từ dump, không dùng JDBC.
  - **Dòng 24 — `public static void main(String[] args) throws Exception {`**: Entry CLI yêu cầu đúng một dump path rồi tạo SKILL candidate offline.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.bootstrap.SkillAssetSeedDryRunCommand`

- **Source:** `src/main/java/com/nsocry/bootstrap/SkillAssetSeedDryRunCommand.java`
- **Vai trò tóm tắt:** Command xác minh SKILL archive mà không mở database hoặc publish snapshot.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 8 — `public final class SkillAssetSeedDryRunCommand {`**: Command xác minh SKILL archive mà không mở database hoặc publish snapshot.
  - **Dòng 13 — `public static void main(String[] args) throws Exception {`**: Entry CLI yêu cầu đúng một archive path rồi dry-run không mutation.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.bootstrap.SkillAssetSeedImportCommand`

- **Source:** `src/main/java/com/nsocry/bootstrap/SkillAssetSeedImportCommand.java`
- **Vai trò tóm tắt:** Command tương tác import SKILL đã duyệt; không migration hoặc publish runtime.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 21 — `public final class SkillAssetSeedImportCommand {`**: Command tương tác import SKILL đã duyệt; không migration hoặc publish runtime.
  - **Dòng 26 — `public static void main(String[] args) throws Exception {`**: Archive + schema READY + full SHA-256 là ba gate bắt buộc trước transaction.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.bootstrap.package-info`

- **Source:** `src/main/java/com/nsocry/bootstrap/package-info.java`
- **Vai trò tóm tắt:** Ghép dependency và quản lý vòng đời tiến trình NSOCry.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - Không có API public/protected một dòng; xem source/package contract.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

## `com.nsocry.character`

**Vai trò:** Read model/payload danh sách nhân vật sau đăng nhập.

**Trạng thái:** `IMPLEMENTED`; bằng chứng chi tiết xem [STATUS](../project/STATUS.md).

### `com.nsocry.character.CharacterSelectionPayloadCodec`

- **Source:** `src/main/java/com/nsocry/character/CharacterSelectionPayloadCodec.java`
- **Vai trò tóm tắt:** Mã hóa và giải mã payload màn hình nhân vật tương thích luồng client V7 đã xác minh tĩnh.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 13 — `public final class CharacterSelectionPayloadCodec {`**: Mã hóa và giải mã payload màn hình nhân vật tương thích luồng client V7 đã xác minh tĩnh.
  - **Dòng 22 — `public static ProtocolFrame encodeCharacterList(List<CharacterSummary> characters) throws IOException {`**: Tạo frame danh sách nhân vật theo đúng thứ tự trường mà client cũ đọc.
  - **Dòng 48 — `public static String decodeSelectedCharacterName(ProtocolFrame frame) throws IOException {`**: Giải mã tên nhân vật client muốn chọn và từ chối envelope, command hoặc byte đuôi sai.
  - **Dòng 56 — `public static CreateCharacterRequest decodeCreateCharacterRequest(ProtocolFrame frame) throws IOException {`**: Giải mã đúng ba trường wire của yêu cầu tạo nhân vật, chưa trộn quy tắc nghiệp vụ vào codec.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.character.CharacterSummary`

- **Source:** `src/main/java/com/nsocry/character/CharacterSummary.java`
- **Vai trò tóm tắt:** Dữ liệu hiển thị tối thiểu của một nhân vật trong màn hình chọn nhân vật.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 6 — `public record CharacterSummary(`**: Dữ liệu hiển thị tối thiểu của một nhân vật trong màn hình chọn nhân vật.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.character.CreateCharacterRequest`

- **Source:** `src/main/java/com/nsocry/character/CreateCharacterRequest.java`
- **Vai trò tóm tắt:** Yêu cầu tạo nhân vật đã giải mã từ client; quy tắc nghiệp vụ sẽ được kiểm tra ở tầng dịch vụ.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 6 — `public record CreateCharacterRequest(String name, byte gender, byte head) {`**: Yêu cầu tạo nhân vật đã giải mã từ client; quy tắc nghiệp vụ sẽ được kiểm tra ở tầng dịch vụ.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.character.package-info`

- **Source:** `src/main/java/com/nsocry/character/package-info.java`
- **Vai trò tóm tắt:** Mô hình và codec phục vụ luồng danh sách, chọn và tạo nhân vật của NSOCry.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - Không có API public/protected một dòng; xem source/package contract.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

## `com.nsocry.configuration`

**Vai trò:** Load và validate cấu hình server/database.

**Trạng thái:** `IMPLEMENTED`; bằng chứng chi tiết xem [STATUS](../project/STATUS.md).

### `com.nsocry.configuration.DatabaseConfiguration`

- **Source:** `src/main/java/com/nsocry/configuration/DatabaseConfiguration.java`
- **Vai trò tóm tắt:** Cấu hình kết nối database bất biến; biểu diễn chuỗi luôn che password.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 8 — `public final class DatabaseConfiguration {`**: Cấu hình kết nối database bất biến; biểu diễn chuỗi luôn che password.
  - **Dòng 21 — `public DatabaseConfiguration(String url, String user, String password) {`**: Tạo cấu hình sau khi kiểm tra URL MariaDB, user và password bắt buộc.
  - **Dòng 31 — `public static DatabaseConfiguration from(Properties properties, Map<String, String> environment) {`**: Đọc cấu hình, ưu tiên biến môi trường rồi mới đến file properties.
  - **Dòng 41 — `public String url() {`**: Trả JDBC URL đã kiểm tra.
  - **Dòng 46 — `public String user() {`**: Trả database user cho DataSource factory.
  - **Dòng 51 — `public String password() {`**: Trả password cho DataSource factory; bên gọi không được log giá trị.
  - **Dòng 57 — `public String toString() {`**: Mô tả cấu hình mà không bao giờ đưa password vào chuỗi.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.configuration.DatabaseConfigurationLoader`

- **Source:** `src/main/java/com/nsocry/configuration/DatabaseConfigurationLoader.java`
- **Vai trò tóm tắt:** Đọc cấu hình database từ file cục bộ và biến môi trường mà không ghi log nội dung.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 12 — `public final class DatabaseConfigurationLoader {`**: Đọc cấu hình database từ file cục bộ và biến môi trường mà không ghi log nội dung.
  - **Dòng 14 — `public DatabaseConfiguration load(Path path, Map<String, String> environment) throws IOException {`**: Đọc file nếu tồn tại rồi áp dụng biến môi trường có độ ưu tiên cao hơn.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.configuration.ServerConfiguration`

- **Source:** `src/main/java/com/nsocry/configuration/ServerConfiguration.java`
- **Vai trò tóm tắt:** Cấu hình runtime bất biến dùng để tạo TCP server và nguồn khóa phiên.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 10 — `public record ServerConfiguration(TcpServerConfig tcp, int sessionKeyLength) {`**: Cấu hình runtime bất biến dùng để tạo TCP server và nguồn khóa phiên.
  - **Dòng 28 — `public static ServerConfiguration from(Properties properties) {`**: Tạo cấu hình từ Properties, dùng mặc định an toàn cho các khóa bị thiếu.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.configuration.ServerConfigurationLoader`

- **Source:** `src/main/java/com/nsocry/configuration/ServerConfigurationLoader.java`
- **Vai trò tóm tắt:** Đọc cấu hình NSOCry từ file properties mà không lưu hoặc ghi log giá trị cấu hình.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 11 — `public final class ServerConfigurationLoader {`**: Đọc cấu hình NSOCry từ file properties mà không lưu hoặc ghi log giá trị cấu hình.
  - **Dòng 13 — `public ServerConfiguration load(Path path) throws IOException {`**: Đọc file tồn tại; nếu đường dẫn không tồn tại thì dùng toàn bộ giá trị mặc định.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.configuration.package-info`

- **Source:** `src/main/java/com/nsocry/configuration/package-info.java`
- **Vai trò tóm tắt:** Đọc, kiểm tra và biểu diễn cấu hình runtime của NSOCry.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - Không có API public/protected một dòng; xem source/package contract.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

## `com.nsocry.network`

**Vai trò:** TCP acceptor, connection lifecycle và network event boundary.

**Trạng thái:** `IMPLEMENTED`; bằng chứng chi tiết xem [STATUS](../project/STATUS.md).

### `com.nsocry.network.LegacyHandshakeConnectionHandler`

- **Source:** `src/main/java/com/nsocry/network/LegacyHandshakeConnectionHandler.java`
- **Vai trò tóm tắt:** Điều phối một socket đã kết nối qua trao đổi khóa, đọc thông tin client và xác thực đăng nhập.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 13 — `public final class LegacyHandshakeConnectionHandler implements SessionConnectionHandler {`**: Điều phối một socket đã kết nối qua trao đổi khóa, đọc thông tin client và xác thực đăng nhập.
  - **Dòng 19 — `public LegacyHandshakeConnectionHandler(`**: Tạo handler bằng giới hạn protocol, nguồn khóa phiên và port xác thực bắt buộc.
  - **Dòng 30 — `public void handle(Socket socket) throws Exception {`**: Chạy trọn handshake cho một socket đến kết quả xác thực hoặc từ chối.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.network.NetworkEventSink`

- **Source:** `src/main/java/com/nsocry/network/NetworkEventSink.java`
- **Vai trò tóm tắt:** Nhận sự kiện lỗi đã được làm sạch từ listener và phiên mà không ràng buộc tầng mạng với công cụ log.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 7 — `public interface NetworkEventSink {`**: Nhận sự kiện lỗi đã được làm sạch từ listener và phiên mà không ràng buộc tầng mạng với công cụ log.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.network.SessionConnectionHandler`

- **Source:** `src/main/java/com/nsocry/network/SessionConnectionHandler.java`
- **Vai trò tóm tắt:** Ranh giới ứng dụng được TcpServer gọi một lần cho mỗi socket vừa chấp nhận.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 7 — `public interface SessionConnectionHandler {`**: Ranh giới ứng dụng được TcpServer gọi một lần cho mỗi socket vừa chấp nhận.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.network.TcpServer`

- **Source:** `src/main/java/com/nsocry/network/TcpServer.java`
- **Vai trò tóm tắt:** TCP acceptor có giới hạn, sở hữu listener, bộ thực thi phiên và vòng đời dừng an toàn.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 19 — `public final class TcpServer implements Closeable {`**: TCP acceptor có giới hạn, sở hữu listener, bộ thực thi phiên và vòng đời dừng an toàn.
  - **Dòng 29 — `public TcpServer(`**: Tạo TCP server cùng executor giới hạn theo cấu hình số phiên tối đa.
  - **Dòng 47 — `public synchronized void start() throws IOException {`**: Bind listener và khởi chạy accept thread; từ chối nếu server đã chạy.
  - **Dòng 64 — `public boolean isRunning() {`**: Trả trạng thái hoạt động hiện tại của listener.
  - **Dòng 69 — `public synchronized InetSocketAddress localAddress() {`**: Trả địa chỉ thực tế đã bind, bao gồm cổng tạm nếu cấu hình dùng cổng 0.
  - **Dòng 78 — `public void close() throws IOException {`**: Dừng listener, chờ phiên kết thúc trong timeout và bảo toàn interrupt của thread gọi.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.network.TcpServerConfig`

- **Source:** `src/main/java/com/nsocry/network/TcpServerConfig.java`
- **Vai trò tóm tắt:** Cấu hình bất biến đã kiểm tra cho địa chỉ lắng nghe, giới hạn và thời gian chờ TCP.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 8 — `public record TcpServerConfig(`**: Cấu hình bất biến đã kiểm tra cho địa chỉ lắng nghe, giới hạn và thời gian chờ TCP.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.network.package-info`

- **Source:** `src/main/java/com/nsocry/network/package-info.java`
- **Vai trò tóm tắt:** Quản lý vòng đời TCP listener, cấu hình socket, giới hạn số phiên đồng thời và
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - Không có API public/protected một dòng; xem source/package contract.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

## `com.nsocry.observability`

**Vai trò:** Log/sự kiện đã làm sạch dữ liệu nhạy cảm.

**Trạng thái:** `IMPLEMENTED`; bằng chứng chi tiết xem [STATUS](../project/STATUS.md).

### `com.nsocry.observability.SanitizedNetworkEventSink`

- **Source:** `src/main/java/com/nsocry/observability/SanitizedNetworkEventSink.java`
- **Vai trò tóm tắt:** Chuyển sự kiện mạng thành dòng log tối thiểu, không chứa message hoặc stack trace từ client.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 10 — `public final class SanitizedNetworkEventSink implements NetworkEventSink {`**: Chuyển sự kiện mạng thành dòng log tối thiểu, không chứa message hoặc stack trace từ client.
  - **Dòng 14 — `public SanitizedNetworkEventSink(Consumer<String> output) {`**: Tạo event sink ghi các dòng đã làm sạch đến đích được cung cấp.
  - **Dòng 20 — `public void sessionFailed(SocketAddress remoteAddress, Exception failure) {`**: Ghi loại lỗi phiên và địa chỉ từ xa, không ghi exception message.
  - **Dòng 27 — `public void sessionRejected(SocketAddress remoteAddress) {`**: Ghi sự kiện từ chối do đạt giới hạn phiên.
  - **Dòng 33 — `public void acceptFailed(IOException failure) {`**: Ghi loại lỗi accept, không ghi message hoặc dữ liệu nội bộ của exception.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.observability.package-info`

- **Source:** `src/main/java/com/nsocry/observability/package-info.java`
- **Vai trò tóm tắt:** Cung cấp các bộ điều hợp quan sát runtime đã làm sạch dữ liệu trước khi ghi log.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - Không có API public/protected một dòng; xem source/package contract.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

## `com.nsocry.operations`

**Vai trò:** Use-case vận hành archive/migration/import có safety gate.

**Trạng thái:** `IMPLEMENTED`; bằng chứng chi tiết xem [STATUS](../project/STATUS.md).

### `com.nsocry.operations.ItemAssetSeedArchiveService`

- **Source:** `src/main/java/com/nsocry/operations/ItemAssetSeedArchiveService.java`
- **Vai trò tóm tắt:** Xuất và dry-run archive ITEM seed mà không truy cập database.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 26 — `public final class ItemAssetSeedArchiveService {`**: Xuất và dry-run archive ITEM seed mà không truy cập database.
  - **Dòng 33 — `public void export(ItemAssetSeedArtifact artifact, Path target) throws IOException {`**: Ghi archive qua file tạm rồi atomic move; không ghi đè artifact đã tồn tại.
  - **Dòng 54 — `public ItemAssetValidationResult dryRun(Path archive)`**: Đọc, kiểm định archive và trả metadata; không mở JDBC connection.
  - **Dòng 60 — `public ValidatedItemAssetSeedArchive readValidated(Path archive)`**: Đọc archive đã kiểm định để command import nhận payload/manifest bất biến.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.operations.MapAssetSeedArchiveService`

- **Source:** `src/main/java/com/nsocry/operations/MapAssetSeedArchiveService.java`
- **Vai trò tóm tắt:** Xuất và dry-run MAP candidate mà không mở hoặc thay đổi database/runtime.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 25 — `public final class MapAssetSeedArchiveService {`**: Xuất và dry-run MAP candidate mà không mở hoặc thay đổi database/runtime.
  - **Dòng 32 — `public void export(MapAssetSeedArtifact artifact, Path target) throws IOException {`**: Ghi file tạm rồi atomic move; cấm ghi đè candidate đã tồn tại.
  - **Dòng 52 — `public MapAssetSeedValidationResult dryRun(Path archive) throws IOException {`**: Xác minh đầy đủ archive và chỉ trả metadata, không mở database/runtime.
  - **Dòng 57 — `public ValidatedMapAssetSeedArchive readValidated(Path archive) throws IOException {`**: Decode rồi đối chiếu lại manifest/count/length/SHA-256 trước khi trả dữ liệu.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.operations.SkillAssetSeedArchiveService`

- **Source:** `src/main/java/com/nsocry/operations/SkillAssetSeedArchiveService.java`
- **Vai trò tóm tắt:** Xuất và dry-run archive SKILL mà không mở database hoặc publish runtime.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 25 — `public final class SkillAssetSeedArchiveService {`**: Xuất và dry-run archive SKILL mà không mở database hoặc publish runtime.
  - **Dòng 32 — `public void export(SkillAssetSeedArtifact artifact, Path target) throws IOException {`**: Ghi qua file tạm và atomic move; không ghi đè candidate đã tồn tại.
  - **Dòng 51 — `public SkillAssetSeedValidationResult dryRun(Path archive) throws IOException {`**: Đọc đủ hai entry, decode và xác minh lại manifest/checksum/raw-byte.
  - **Dòng 56 — `public ValidatedSkillAssetSeedArchive readValidated(Path archive) throws IOException {`**: Trả payload/manifest bất biến sau full validation cho interactive importer.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.operations.ValidatedItemAssetSeedArchive`

- **Source:** `src/main/java/com/nsocry/operations/ValidatedItemAssetSeedArchive.java`
- **Vai trò tóm tắt:** Nội dung archive ITEM đã qua parse/codec/checksum validation.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 8 — `public final class ValidatedItemAssetSeedArchive {`**: Nội dung archive ITEM đã qua parse/codec/checksum validation.
  - **Dòng 24 — `public byte[] payload() {`**: Trả defensive copy của payload dùng cho transactional importer.
  - **Dòng 29 — `public String manifestText() {`**: Trả manifest canonical đã được kiểm định.
  - **Dòng 34 — `public ItemAssetValidationResult validation() {`**: Trả metadata đã khớp payload và manifest.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.operations.ValidatedMapAssetSeedArchive`

- **Source:** `src/main/java/com/nsocry/operations/ValidatedMapAssetSeedArchive.java`
- **Vai trò tóm tắt:** Nội dung MAP archive đã vượt codec, manifest, count và checksum validation.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 8 — `public final class ValidatedMapAssetSeedArchive {`**: Nội dung MAP archive đã vượt codec, manifest, count và checksum validation.
  - **Dòng 23 — `public byte[] payload() {`**: Trả defensive copy cho importer tương lai.
  - **Dòng 28 — `public String manifestText() {`**: Trả manifest đã được dùng để xác minh payload.
  - **Dòng 33 — `public MapAssetSeedValidationResult validation() {`**: Trả count/length/checksum đã xác minh.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.operations.ValidatedSkillAssetSeedArchive`

- **Source:** `src/main/java/com/nsocry/operations/ValidatedSkillAssetSeedArchive.java`
- **Vai trò tóm tắt:** Nội dung SKILL archive đã qua codec, manifest, checksum và raw-byte validation.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 8 — `public final class ValidatedSkillAssetSeedArchive {`**: Nội dung SKILL archive đã qua codec, manifest, checksum và raw-byte validation.
  - **Dòng 23 — `public byte[] payload() {`**: Trả defensive copy dùng cho transactional importer.
  - **Dòng 28 — `public String manifestText() {`**: Trả manifest SKILL đã được dùng để xác minh payload.
  - **Dòng 33 — `public SkillAssetSeedValidationResult validation() {`**: Trả cấu trúc/raw-byte/length/checksum đã xác minh.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

## `com.nsocry.persistence`

**Vai trò:** JDBC adapter, schema inspector, repository/source/importer.

**Trạng thái:** `IMPLEMENTED`; bằng chứng chi tiết xem [STATUS](../project/STATUS.md).

### `com.nsocry.persistence.AccountPersistenceException`

- **Source:** `src/main/java/com/nsocry/persistence/AccountPersistenceException.java`
- **Vai trò tóm tắt:** Lỗi truy cập account đã làm sạch, không đưa SQL hoặc credential vào message công khai.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 4 — `public final class AccountPersistenceException extends RuntimeException {`**: Lỗi truy cập account đã làm sạch, không đưa SQL hoặc credential vào message công khai.
  - **Dòng 6 — `public AccountPersistenceException(String operation, Throwable cause) {`**: Bọc nguyên nhân JDBC bằng mã thao tác cố định để tầng trên có thể phân loại an toàn.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.persistence.ItemAssetSchemaColumn`

- **Source:** `src/main/java/com/nsocry/persistence/ItemAssetSchemaColumn.java`
- **Vai trò tóm tắt:** Mô tả tối thiểu một cột ITEM asset lấy từ information_schema.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 6 — `public record ItemAssetSchemaColumn(`**: Mô tả tối thiểu một cột ITEM asset lấy từ information_schema.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.persistence.ItemAssetSchemaContract`

- **Source:** `src/main/java/com/nsocry/persistence/ItemAssetSchemaContract.java`
- **Vai trò tóm tắt:** Đối chiếu metadata database với contract V002 mà không thực hiện DDL.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 12 — `public final class ItemAssetSchemaContract {`**: Đối chiếu metadata database với contract V002 mà không thực hiện DDL.
  - **Dòng 19 — `public static ItemAssetSchemaPreflightReport evaluate(List<ItemAssetSchemaColumn> actualColumns) {`**: Trả report đầy đủ về cột thiếu, thừa hoặc sai type/nullability.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.persistence.ItemAssetSchemaPreflightReport`

- **Source:** `src/main/java/com/nsocry/persistence/ItemAssetSchemaPreflightReport.java`
- **Vai trò tóm tắt:** Báo cáo chỉ đọc cho biết schema ITEM đã đúng V002 hay còn chênh lệch.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 7 — `public record ItemAssetSchemaPreflightReport(boolean ready, List<String> differences) {`**: Báo cáo chỉ đọc cho biết schema ITEM đã đúng V002 hay còn chênh lệch.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.persistence.ItemAssetSeedImportException`

- **Source:** `src/main/java/com/nsocry/persistence/ItemAssetSeedImportException.java`
- **Vai trò tóm tắt:** Lỗi kiểm định hoặc ghi seed ITEM; transaction phải được rollback trước khi lỗi thoát ra.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 4 — `public final class ItemAssetSeedImportException extends Exception {`**: Lỗi kiểm định hoặc ghi seed ITEM; transaction phải được rollback trước khi lỗi thoát ra.
  - **Dòng 6 — `public ItemAssetSeedImportException(String message, Throwable cause) {`**: Tạo lỗi import và giữ nguyên nguyên nhân kỹ thuật.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.persistence.JdbcAccountProvisioningRepository`

- **Source:** `src/main/java/com/nsocry/persistence/JdbcAccountProvisioningRepository.java`
- **Vai trò tóm tắt:** Adapter JDBC chỉ dành cho đếm và tạo account bootstrap.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 14 — `public final class JdbcAccountProvisioningRepository implements AccountProvisioningRepository {`**: Adapter JDBC chỉ dành cho đếm và tạo account bootstrap.
  - **Dòng 24 — `public JdbcAccountProvisioningRepository(DataSource dataSource) {`**: Tạo adapter từ DataSource thuộc database nsocry.
  - **Dòng 30 — `public long countAccounts() {`**: Đếm account bằng truy vấn không nhận dữ liệu từ người dùng.
  - **Dòng 45 — `public long create(String username, String passwordHash, AccountRole role, boolean activated) {`**: Insert account bằng prepared statement và trả generated id.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.persistence.JdbcAccountRepository`

- **Source:** `src/main/java/com/nsocry/persistence/JdbcAccountRepository.java`
- **Vai trò tóm tắt:** Adapter JDBC cho bảng accounts của NSOCry, chỉ sử dụng prepared statement.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 17 — `public final class JdbcAccountRepository implements AccountRepository {`**: Adapter JDBC cho bảng accounts của NSOCry, chỉ sử dụng prepared statement.
  - **Dòng 38 — `public JdbcAccountRepository(DataSource dataSource) {`**: Tạo repository từ DataSource do composition root quản lý.
  - **Dòng 44 — `public Optional<AccountCredential> findByUsername(String username) {`**: Tải đúng một credential theo username phân biệt hoa thường.
  - **Dòng 59 — `public void recordSuccessfulLogin(long accountId, Instant occurredAt) {`**: Đặt lại bộ đếm lỗi, bỏ khóa tạm và ghi thời điểm đăng nhập thành công.
  - **Dòng 69 — `public void recordFailedLogin(long accountId, Instant occurredAt) {`**: Tăng nguyên tử bộ đếm đăng nhập sai; chính sách đặt locked_until thuộc checkpoint kế tiếp.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.persistence.JdbcItemAssetSchemaInspector`

- **Source:** `src/main/java/com/nsocry/persistence/JdbcItemAssetSchemaInspector.java`
- **Vai trò tóm tắt:** Đọc information_schema để kiểm tra V002 mà không thay đổi database.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 14 — `public final class JdbcItemAssetSchemaInspector {`**: Đọc information_schema để kiểm tra V002 mà không thay đổi database.
  - **Dòng 26 — `public JdbcItemAssetSchemaInspector(DataSource dataSource) {`**: Tạo inspector chỉ đọc từ DataSource NSOCry.
  - **Dòng 31 — `public ItemAssetSchemaPreflightReport inspect() throws ClientAssetSourceException {`**: Đọc metadata rồi đối chiếu contract V002, không thực hiện DDL/DML.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.persistence.JdbcItemAssetSeedImporter`

- **Source:** `src/main/java/com/nsocry/persistence/JdbcItemAssetSeedImporter.java`
- **Vai trò tóm tắt:** Import seed ITEM đã kiểm định bằng một transaction và prepared batch.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 19 — `public final class JdbcItemAssetSeedImporter {`**: Import seed ITEM đã kiểm định bằng một transaction và prepared batch.
  - **Dòng 35 — `public JdbcItemAssetSeedImporter(DataSource dataSource) {`**: Tạo importer với DataSource của database NSOCry.
  - **Dòng 44 — `public ItemAssetValidationResult importSeed(byte[] payload, String manifestText)`**: Kiểm định artifact trước khi mở connection, sau đó thay hai bảng trong một transaction.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.persistence.JdbcItemAssetSource`

- **Source:** `src/main/java/com/nsocry/persistence/JdbcItemAssetSource.java`
- **Vai trò tóm tắt:** Adapter JDBC đọc read model ITEM tĩnh của NSOCry trong một transaction nhất quán.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 18 — `public final class JdbcItemAssetSource implements ItemAssetSource {`**: Adapter JDBC đọc read model ITEM tĩnh của NSOCry trong một transaction nhất quán.
  - **Dòng 34 — `public JdbcItemAssetSource(DataSource dataSource, byte version) {`**: Tạo source với DataSource ứng dụng và version sẽ ghi vào payload ITEM.
  - **Dòng 41 — `public ItemAssetBundle load() throws ClientAssetSourceException {`**: Đọc option và template trên cùng một repeatable-read transaction.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.persistence.JdbcMapAssetSchemaInspector`

- **Source:** `src/main/java/com/nsocry/persistence/JdbcMapAssetSchemaInspector.java`
- **Vai trò tóm tắt:** Đọc information_schema để kiểm tra MAP V004 mà không thay đổi database.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 14 — `public final class JdbcMapAssetSchemaInspector {`**: Đọc information_schema để kiểm tra MAP V004 mà không thay đổi database.
  - **Dòng 27 — `public JdbcMapAssetSchemaInspector(DataSource dataSource) {`**: Tạo inspector read-only cho đúng DataSource NSOCry; từ chối null.
  - **Dòng 32 — `public MapAssetSchemaPreflightReport inspect() throws ClientAssetSourceException {`**: Dùng connection read-only, chỉ đọc metadata và không thực hiện DDL/DML.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.persistence.JdbcMapAssetSeedImporter`

- **Source:** `src/main/java/com/nsocry/persistence/JdbcMapAssetSeedImporter.java`
- **Vai trò tóm tắt:** Thay toàn bộ MAP seed đã kiểm định trong một transaction SERIALIZABLE.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 9 — `public final class JdbcMapAssetSeedImporter {`**: Thay toàn bộ MAP seed đã kiểm định trong một transaction SERIALIZABLE.
  - **Dòng 23 — `public MapAssetSeedValidationResult importSeed(byte[] payload, String manifestText)`**: Validate trước connection; commit đủ bốn bảng hoặc rollback toàn bộ.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.persistence.JdbcMapAssetSource`

- **Source:** `src/main/java/com/nsocry/persistence/JdbcMapAssetSource.java`
- **Vai trò tóm tắt:** Adapter JDBC tái dựng MAP catalog trong một repeatable-read snapshot.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 9 — `public final class JdbcMapAssetSource implements MapAssetSource {`**: Adapter JDBC tái dựng MAP catalog trong một repeatable-read snapshot.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.persistence.JdbcSkillAssetSchemaInspector`

- **Source:** `src/main/java/com/nsocry/persistence/JdbcSkillAssetSchemaInspector.java`
- **Vai trò tóm tắt:** Đọc information_schema để kiểm tra V003 mà không thay đổi database.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 14 — `public final class JdbcSkillAssetSchemaInspector {`**: Đọc information_schema để kiểm tra V003 mà không thay đổi database.
  - **Dòng 27 — `public JdbcSkillAssetSchemaInspector(DataSource dataSource) {`**: Tạo inspector read-only cho đúng DataSource NSOCry; từ chối null.
  - **Dòng 32 — `public SkillAssetSchemaPreflightReport inspect() throws ClientAssetSourceException {`**: Dùng connection read-only, không thực hiện DDL/DML.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.persistence.JdbcSkillAssetSeedImporter`

- **Source:** `src/main/java/com/nsocry/persistence/JdbcSkillAssetSeedImporter.java`
- **Vai trò tóm tắt:** Thay toàn bộ SKILL seed đã kiểm định trong một transaction SERIALIZABLE.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 21 — `public final class JdbcSkillAssetSeedImporter {`**: Thay toàn bộ SKILL seed đã kiểm định trong một transaction SERIALIZABLE.
  - **Dòng 47 — `public JdbcSkillAssetSeedImporter(DataSource dataSource) {`**: Tạo importer transaction cho DataSource đích; chưa mở connection ở constructor.
  - **Dòng 52 — `public SkillAssetSeedValidationResult importSeed(byte[] payload, String manifestText)`**: Validate trước khi mở connection; commit đủ năm bảng hoặc rollback toàn bộ.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.persistence.JdbcSkillAssetSource`

- **Source:** `src/main/java/com/nsocry/persistence/JdbcSkillAssetSource.java`
- **Vai trò tóm tắt:** Adapter JDBC tái dựng toàn bộ SKILL read model trong một snapshot nhất quán.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 22 — `public final class JdbcSkillAssetSource implements SkillAssetSource {`**: Adapter JDBC tái dựng toàn bộ SKILL read model trong một snapshot nhất quán.
  - **Dòng 42 — `public JdbcSkillAssetSource(DataSource dataSource, byte version) {`**: Tạo source JDBC với version payload explicit; chưa đọc database ở constructor.
  - **Dòng 49 — `public SkillAssetBundle load() throws ClientAssetSourceException {`**: Đọc năm bảng trên cùng repeatable-read transaction và commit snapshot chỉ đọc.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.persistence.MapAssetSchemaColumn`

- **Source:** `src/main/java/com/nsocry/persistence/MapAssetSchemaColumn.java`
- **Vai trò tóm tắt:** Metadata một cột MAP đọc từ information_schema, không chứa dữ liệu game.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 6 — `public record MapAssetSchemaColumn(`**: Metadata một cột MAP đọc từ information_schema, không chứa dữ liệu game.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.persistence.MapAssetSchemaContract`

- **Source:** `src/main/java/com/nsocry/persistence/MapAssetSchemaContract.java`
- **Vai trò tóm tắt:** Đối chiếu metadata database với đúng 18 cột MAP V004 mà không thực hiện DDL.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 12 — `public final class MapAssetSchemaContract {`**: Đối chiếu metadata database với đúng 18 cột MAP V004 mà không thực hiện DDL.
  - **Dòng 19 — `public static MapAssetSchemaPreflightReport evaluate(List<MapAssetSchemaColumn> actualColumns) {`**: Báo đầy đủ cột thiếu, thừa, trùng hoặc sai type/unsigned/nullability.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.persistence.MapAssetSchemaPreflightReport`

- **Source:** `src/main/java/com/nsocry/persistence/MapAssetSchemaPreflightReport.java`
- **Vai trò tóm tắt:** Kết quả đối chiếu schema MAP V004, gồm trạng thái và toàn bộ sai khác.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 7 — `public record MapAssetSchemaPreflightReport(boolean ready, List<String> differences) {`**: Kết quả đối chiếu schema MAP V004, gồm trạng thái và toàn bộ sai khác.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.persistence.MapAssetSeedImportException`

- **Source:** `src/main/java/com/nsocry/persistence/MapAssetSeedImportException.java`
- **Vai trò tóm tắt:** Lỗi validation hoặc transaction khi thay thế MAP seed.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 4 — `public final class MapAssetSeedImportException extends Exception {`**: Lỗi validation hoặc transaction khi thay thế MAP seed.
  - **Dòng 6 — `public MapAssetSeedImportException(String message, Throwable cause) { super(message, cause); }`**: Giữ thông báo nghiệp vụ và nguyên nhân gốc.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.persistence.MariaDbDataSourceFactory`

- **Source:** `src/main/java/com/nsocry/persistence/MariaDbDataSourceFactory.java`
- **Vai trò tóm tắt:** Tạo MariaDB DataSource chính thức từ cấu hình đã kiểm tra.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 10 — `public final class MariaDbDataSourceFactory {`**: Tạo MariaDB DataSource chính thức từ cấu hình đã kiểm tra.
  - **Dòng 15 — `public static DataSource create(DatabaseConfiguration configuration) {`**: Tạo DataSource nhưng chưa mở connection; lỗi cấu hình được bọc bằng exception đã làm sạch.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.persistence.SkillAssetSchemaColumn`

- **Source:** `src/main/java/com/nsocry/persistence/SkillAssetSchemaColumn.java`
- **Vai trò tóm tắt:** Metadata tối thiểu của một cột SKILL đọc từ information_schema.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 6 — `public record SkillAssetSchemaColumn(`**: Metadata tối thiểu của một cột SKILL đọc từ information_schema.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.persistence.SkillAssetSchemaContract`

- **Source:** `src/main/java/com/nsocry/persistence/SkillAssetSchemaContract.java`
- **Vai trò tóm tắt:** Đối chiếu metadata database với đúng 26 cột V003 mà không thực hiện DDL.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 12 — `public final class SkillAssetSchemaContract {`**: Đối chiếu metadata database với đúng 26 cột V003 mà không thực hiện DDL.
  - **Dòng 19 — `public static SkillAssetSchemaPreflightReport evaluate(List<SkillAssetSchemaColumn> actualColumns) {`**: Báo đầy đủ cột thiếu, thừa, trùng hoặc sai type/unsigned/nullability.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.persistence.SkillAssetSchemaPreflightReport`

- **Source:** `src/main/java/com/nsocry/persistence/SkillAssetSchemaPreflightReport.java`
- **Vai trò tóm tắt:** Báo cáo chỉ đọc cho biết schema SKILL đã đúng V003 hay chưa.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 7 — `public record SkillAssetSchemaPreflightReport(boolean ready, List<String> differences) {`**: Báo cáo chỉ đọc cho biết schema SKILL đã đúng V003 hay chưa.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.persistence.SkillAssetSeedImportException`

- **Source:** `src/main/java/com/nsocry/persistence/SkillAssetSeedImportException.java`
- **Vai trò tóm tắt:** Lỗi validation hoặc transaction khi thay thế SKILL seed.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 4 — `public final class SkillAssetSeedImportException extends Exception {`**: Lỗi validation hoặc transaction khi thay thế SKILL seed.
  - **Dòng 6 — `public SkillAssetSeedImportException(String message, Throwable cause) {`**: Giữ thông báo nghiệp vụ và nguyên nhân gốc của validation/transaction failure.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.persistence.package-info`

- **Source:** `src/main/java/com/nsocry/persistence/package-info.java`
- **Vai trò tóm tắt:** Chứa adapter lưu trữ của NSOCry và chuyển lỗi hạ tầng thành lỗi ứng dụng đã làm sạch.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - Không có API public/protected một dòng; xem source/package contract.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

## `com.nsocry.protocol.compat`

**Vai trò:** Wire frame, key transform, payload reader/writer và client compatibility.

**Trạng thái:** `IMPLEMENTED`; bằng chứng chi tiết xem [STATUS](../project/STATUS.md).

### `com.nsocry.protocol.compat.ClientDataSet`

- **Source:** `src/main/java/com/nsocry/protocol/compat/ClientDataSet.java`
- **Vai trò tóm tắt:** Bộ dữ liệu tương thích mà client V7 có thể yêu cầu cập nhật sau khi đăng nhập.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 4 — `public enum ClientDataSet {`**: Bộ dữ liệu tương thích mà client V7 có thể yêu cầu cập nhật sau khi đăng nhập.
  - **Dòng 17 — `public byte requestCommand() {`**: Trả command con nằm trong envelope NOT_MAP của yêu cầu cập nhật.
  - **Dòng 22 — `public static ClientDataSet fromRequestCommand(byte command) {`**: Ánh xạ command wire sang bộ dữ liệu, từ chối command không thuộc bước đồng bộ.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.protocol.compat.ClientVersionManifest`

- **Source:** `src/main/java/com/nsocry/protocol/compat/ClientVersionManifest.java`
- **Vai trò tóm tắt:** Bốn phiên bản dữ liệu mà client V7 so sánh trước khi xác nhận CLIENT_OK.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 4 — `public record ClientVersionManifest(`**: Bốn phiên bản dữ liệu mà client V7 so sánh trước khi xác nhận CLIENT_OK.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.protocol.compat.LegacyFrameCodec`

- **Source:** `src/main/java/com/nsocry/protocol/compat/LegacyFrameCodec.java`
- **Vai trò tóm tắt:** Mã hóa và giải mã toàn bộ frame của client cũ, phục vụ fixture và kiểm thử tương thích.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 7 — `public final class LegacyFrameCodec {`**: Mã hóa và giải mã toàn bộ frame của client cũ, phục vụ fixture và kiểm thử tương thích.
  - **Dòng 15 — `public static byte[] encodeShortFrame(byte command, byte[] payload, RollingXorCipher cipher) {`**: Tạo frame ngắn, tùy chọn áp dụng cipher và từ chối payload vượt giới hạn unsigned-short.
  - **Dòng 26 — `public static byte[] encodeFullSizeFrame(byte[] payload, RollingXorCipher cipher) {`**: Tạo frame kích thước đầy đủ dùng command đặc biệt và trường độ dài int.
  - **Dòng 34 — `public static ProtocolFrame decodeFrame(byte[] wire, RollingXorCipher cipher) {`**: Giải mã một frame hoàn chỉnh trong bộ nhớ và kiểm tra độ dài payload khớp header.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.protocol.compat.LegacyFrameReader`

- **Source:** `src/main/java/com/nsocry/protocol/compat/LegacyFrameReader.java`
- **Vai trò tóm tắt:** Đọc một frame có giới hạn từ stream và duy trì liên tục con trỏ mã hóa chiều vào.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 10 — `public final class LegacyFrameReader {`**: Đọc một frame có giới hạn từ stream và duy trì liên tục con trỏ mã hóa chiều vào.
  - **Dòng 15 — `public LegacyFrameReader(InputStream input, ProtocolLimits limits) {`**: Khởi tạo bộ đọc từ stream đầu vào với giới hạn cấp phát bắt buộc.
  - **Dòng 21 — `public ProtocolFrame readUnencryptedShortFrame() throws IOException {`**: Đọc frame ngắn chưa mã hóa, hiện dùng cho trigger trao đổi khóa đầu phiên.
  - **Dòng 29 — `public ProtocolFrame readEncryptedFrame(RollingXorCipher cipher, boolean allowFullSize) throws IOException {`**: Đọc frame đã mã hóa, duy trì con trỏ cipher và có thể cấm frame full-size từ client.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.protocol.compat.LegacyFrameWriter`

- **Source:** `src/main/java/com/nsocry/protocol/compat/LegacyFrameWriter.java`
- **Vai trò tóm tắt:** Ghi, flush frame có giới hạn và duy trì liên tục con trỏ mã hóa chiều ra.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 8 — `public final class LegacyFrameWriter {`**: Ghi, flush frame có giới hạn và duy trì liên tục con trỏ mã hóa chiều ra.
  - **Dòng 13 — `public LegacyFrameWriter(OutputStream output, ProtocolLimits limits) {`**: Khởi tạo bộ ghi ra stream với giới hạn payload bắt buộc.
  - **Dòng 19 — `public synchronized void writeUnencryptedShortFrame(ProtocolFrame frame) throws IOException {`**: Ghi và flush một frame ngắn chưa mã hóa.
  - **Dòng 25 — `public synchronized void writeEncryptedShortFrame(`**: Ghi và flush một frame ngắn đã mã hóa bằng cipher chiều ra.
  - **Dòng 33 — `public synchronized void writeEncryptedFullSizeFrame(`**: Ghi và flush payload full-size đã mã hóa sau khi kiểm tra giới hạn.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.protocol.compat.LegacyKeyCodec`

- **Source:** `src/main/java/com/nsocry/protocol/compat/LegacyKeyCodec.java`
- **Vai trò tóm tắt:** Chuyển đổi khóa phiên sang hoặc từ payload dạng sai phân mà client V7 yêu cầu.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 6 — `public final class LegacyKeyCodec {`**: Chuyển đổi khóa phiên sang hoặc từ payload dạng sai phân mà client V7 yêu cầu.
  - **Dòng 11 — `public static byte[] encodePayload(byte[] key) {`**: Mã hóa khóa phiên thành payload sai phân mà client V7 có thể khôi phục.
  - **Dòng 23 — `public static byte[] decodePayload(byte[] payload) {`**: Khôi phục khóa phiên từ payload sai phân và xác minh độ dài khai báo.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.protocol.compat.PostLoginVersionPayloadCodec`

- **Source:** `src/main/java/com/nsocry/protocol/compat/PostLoginVersionPayloadCodec.java`
- **Vai trò tóm tắt:** Codec cho bước thương lượng phiên bản dữ liệu ngay sau khi xác thực thành công.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 10 — `public final class PostLoginVersionPayloadCodec {`**: Codec cho bước thương lượng phiên bản dữ liệu ngay sau khi xác thực thành công.
  - **Dòng 21 — `public static ProtocolFrame encodeVersion(`**: Tạo frame UPDATE_VERSION gồm bốn byte phiên bản và phần dữ liệu ngoại hình nối tiếp.
  - **Dòng 40 — `public static ClientDataSet decodeDataRequest(ProtocolFrame frame) throws IOException {`**: Giải mã yêu cầu cập nhật rỗng của client và từ chối mọi payload phụ không xác định.
  - **Dòng 59 — `public static ProtocolFrame encodeDataResponse(ClientDataSet dataSet, byte[] dataPayload) {`**: Tạo response cho một bộ dữ liệu; payload đã gồm byte phiên bản ở đầu.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.protocol.compat.ProtocolFrame`

- **Source:** `src/main/java/com/nsocry/protocol/compat/ProtocolFrame.java`
- **Vai trò tóm tắt:** Giá trị bất biến gồm command và payload; dữ liệu payload luôn được sao chép phòng vệ.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 6 — `public record ProtocolFrame(byte command, byte[] payload) {`**: Giá trị bất biến gồm command và payload; dữ liệu payload luôn được sao chép phòng vệ.
  - **Dòng 14 — `public byte[] payload() {`**: Trả bản sao payload nhằm giữ tính bất biến của frame.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.protocol.compat.ProtocolLimits`

- **Source:** `src/main/java/com/nsocry/protocol/compat/ProtocolLimits.java`
- **Vai trò tóm tắt:** Giới hạn cấp phát đã kiểm tra cho payload dạng ngắn và dạng đầy đủ.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 4 — `public record ProtocolLimits(int maxShortPayload, int maxFullPayload) {`**: Giới hạn cấp phát đã kiểm tra cho payload dạng ngắn và dạng đầy đủ.
  - **Dòng 18 — `public void requireAllowed(int length, boolean fullSize) {`**: Từ chối độ dài âm hoặc vượt giới hạn tương ứng với loại frame.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.protocol.compat.RollingXorCipher`

- **Source:** `src/main/java/com/nsocry/protocol/compat/RollingXorCipher.java`
- **Vai trò tóm tắt:** Phép biến đổi rolling XOR có trạng thái và con trỏ độc lập cho một chiều truyền.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 6 — `public final class RollingXorCipher {`**: Phép biến đổi rolling XOR có trạng thái và con trỏ độc lập cho một chiều truyền.
  - **Dòng 11 — `public RollingXorCipher(byte[] key) {`**: Tạo cipher với bản sao khóa riêng; khóa rỗng không hợp lệ.
  - **Dòng 19 — `public byte transform(byte value) {`**: Biến đổi một byte và tăng con trỏ tuần hoàn.
  - **Dòng 26 — `public byte[] transform(byte[] values) {`**: Biến đổi một bản sao của mảng byte, không sửa mảng do bên gọi cung cấp.
  - **Dòng 35 — `public int cursor() {`**: Trả vị trí con trỏ hiện tại để chẩn đoán và kiểm thử.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.protocol.compat.package-info`

- **Source:** `src/main/java/com/nsocry/protocol/compat/package-info.java`
- **Vai trò tóm tắt:** Cô lập phần tương thích định dạng dữ liệu bắt buộc cho client V7: đóng khung,
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - Không có API public/protected một dòng; xem source/package contract.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

## `com.nsocry.session`

**Vai trò:** Handshake/login phase, processor và session transport.

**Trạng thái:** `IMPLEMENTED`; bằng chứng chi tiết xem [STATUS](../project/STATUS.md).

### `com.nsocry.session.AuthenticationDecision`

- **Source:** `src/main/java/com/nsocry/session/AuthenticationDecision.java`
- **Vai trò tóm tắt:** Kết quả do ranh giới xác thực trả về trong giai đoạn đăng nhập.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 4 — `public enum AuthenticationDecision {`**: Kết quả do ranh giới xác thực trả về trong giai đoạn đăng nhập.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.session.AuthenticationPort`

- **Source:** `src/main/java/com/nsocry/session/AuthenticationPort.java`
- **Vai trò tóm tắt:** Ranh giới không phụ thuộc lưu trữ dùng để xác thực yêu cầu đăng nhập đã giải mã.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 5 — `public interface AuthenticationPort {`**: Ranh giới không phụ thuộc lưu trữ dùng để xác thực yêu cầu đăng nhập đã giải mã.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.session.ClientInfo`

- **Source:** `src/main/java/com/nsocry/session/ClientInfo.java`
- **Vai trò tóm tắt:** Thông tin khả năng và tương thích của client đã được giải mã trước khi đăng nhập.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 4 — `public record ClientInfo(`**: Thông tin khả năng và tương thích của client đã được giải mã trước khi đăng nhập.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.session.HandshakeEvent`

- **Source:** `src/main/java/com/nsocry/session/HandshakeEvent.java`
- **Vai trò tóm tắt:** Kết quả quan sát được sau một bước handshake được xử lý thành công.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 4 — `public enum HandshakeEvent {`**: Kết quả quan sát được sau một bước handshake được xử lý thành công.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.session.HandshakePayloadDecoder`

- **Source:** `src/main/java/com/nsocry/session/HandshakePayloadDecoder.java`
- **Vai trò tóm tắt:** Giải mã nghiêm ngặt payload CLIENT_INFO và LOGIN theo đúng thứ tự byte đã xác minh.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 9 — `public final class HandshakePayloadDecoder {`**: Giải mã nghiêm ngặt payload CLIENT_INFO và LOGIN theo đúng thứ tự byte đã xác minh.
  - **Dòng 18 — `public static ClientInfo decodeClientInfo(ProtocolFrame frame) throws IOException {`**: Giải mã CLIENT_INFO theo thứ tự wire và từ chối mọi byte dư.
  - **Dòng 39 — `public static LoginRequest decodeLogin(ProtocolFrame frame) throws IOException {`**: Giải mã LOGIN theo thứ tự wire; dữ liệu bí mật chỉ được giữ trong LoginRequest.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.session.HandshakeProcessor`

- **Source:** `src/main/java/com/nsocry/session/HandshakeProcessor.java`
- **Vai trò tóm tắt:** Điều phối thông điệp handshake theo trạng thái phiên mà không phụ thuộc cơ sở dữ liệu.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 8 — `public final class HandshakeProcessor {`**: Điều phối thông điệp handshake theo trạng thái phiên mà không phụ thuộc cơ sở dữ liệu.
  - **Dòng 13 — `public HandshakeProcessor(LegacySessionTransport transport) {`**: Tạo processor điều phối trên một transport duy nhất.
  - **Dòng 18 — `public HandshakeEvent begin(byte[] key) throws IOException {`**: Bắt đầu trao đổi khóa và trả sự kiện KEY_ESTABLISHED khi thành công.
  - **Dòng 24 — `public HandshakeEvent receiveNext(AuthenticationPort authentication) throws IOException {`**: Đọc thông điệp tiếp theo và xử lý theo phase hiện tại của phiên.
  - **Dòng 36 — `public ClientInfo clientInfo() {`**: Trả CLIENT_INFO đã chấp nhận; có thể null trước bước CLIENT_INFO.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.session.HandshakeStateMachine`

- **Source:** `src/main/java/com/nsocry/session/HandshakeStateMachine.java`
- **Vai trò tóm tắt:** Máy trạng thái an toàn luồng, từ chối chuyển trạng thái sai hoặc không đúng thứ tự.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 6 — `public final class HandshakeStateMachine {`**: Máy trạng thái an toàn luồng, từ chối chuyển trạng thái sai hoặc không đúng thứ tự.
  - **Dòng 10 — `public SessionPhase phase() {`**: Trả phase hiện tại bằng phép đọc nguyên tử.
  - **Dòng 15 — `public void keySent() {`**: Chuyển CONNECTED sang KEY_SENT sau khi gửi khóa.
  - **Dòng 20 — `public void clientInfoReceived() {`**: Chuyển KEY_SENT sang CLIENT_INFO_RECEIVED.
  - **Dòng 25 — `public void loginStarted() {`**: Chuyển CLIENT_INFO_RECEIVED sang LOGIN_PENDING.
  - **Dòng 30 — `public void loginSucceeded() {`**: Chuyển LOGIN_PENDING sang AUTHENTICATED.
  - **Dòng 35 — `public void loginRejected() {`**: Đưa LOGIN_PENDING về CLIENT_INFO_RECEIVED để chính sách ngoài có thể xử lý thử lại.
  - **Dòng 40 — `public boolean close() {`**: Đóng state machine theo cách idempotent; trả true nếu lời gọi này thực hiện chuyển trạng thái.
  - **Dòng 45 — `public boolean isAuthenticated() {`**: Kiểm tra phiên đã xác thực thành công hay chưa.
  - **Dòng 50 — `public boolean isClosed() {`**: Kiểm tra phiên đã ở trạng thái CLOSED hay chưa.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.session.LegacySessionTransport`

- **Source:** `src/main/java/com/nsocry/session/LegacySessionTransport.java`
- **Vai trò tóm tắt:** Sở hữu I/O frame, trạng thái mã hóa hai chiều và thao tác đóng của một client.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 19 — `public final class LegacySessionTransport implements Closeable {`**: Sở hữu I/O frame, trạng thái mã hóa hai chiều và thao tác đóng của một client.
  - **Dòng 29 — `public LegacySessionTransport(`**: Tạo transport từ hai stream, giới hạn protocol và tài nguyên cần đóng cuối phiên.
  - **Dòng 40 — `public void beginHandshake(byte[] key) throws IOException {`**: Xác minh trigger -27 rỗng, gửi khóa và kích hoạt cipher độc lập hai chiều.
  - **Dòng 59 — `public ProtocolFrame readClientFrame() throws IOException {`**: Đọc frame mã hóa tiếp theo từ client sau khi trao đổi khóa hoàn tất.
  - **Dòng 68 — `public void sendShortFrame(ProtocolFrame frame) throws IOException {`**: Gửi frame ngắn mã hóa cho client bằng con trỏ chiều ra.
  - **Dòng 77 — `public void sendFullSizePayload(byte[] payload) throws IOException {`**: Gửi payload full-size mã hóa cho client bằng con trỏ chiều ra.
  - **Dòng 86 — `public HandshakeStateMachine state() {`**: Trả state machine thuộc transport để processor điều phối phase.
  - **Dòng 92 — `public void close() throws IOException {`**: Đóng phase và tài nguyên nền đúng một lần.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.session.LoginRequest`

- **Source:** `src/main/java/com/nsocry/session/LoginRequest.java`
- **Vai trò tóm tắt:** Dữ liệu đăng nhập đã giải mã; biểu diễn chuỗi luôn che mật khẩu và client token.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 6 — `public final class LoginRequest {`**: Dữ liệu đăng nhập đã giải mã; biểu diễn chuỗi luôn che mật khẩu và client token.
  - **Dòng 16 — `public LoginRequest(`**: Tạo yêu cầu đăng nhập; mọi trường bắt buộc phải khác null.
  - **Dòng 34 — `public String username() {`**: Trả tên đăng nhập cho port xác thực.
  - **Dòng 39 — `public String password() {`**: Trả mật khẩu cho port xác thực; không được ghi log giá trị này.
  - **Dòng 44 — `public String version() {`**: Trả phiên bản client đã khai báo.
  - **Dòng 49 — `public String reservedUtf1() {`**: Trả trường UTF dự phòng thứ nhất để giữ tương thích wire.
  - **Dòng 54 — `public String reservedUtf2() {`**: Trả trường UTF dự phòng thứ hai để giữ tương thích wire.
  - **Dòng 59 — `public String clientToken() {`**: Trả token client cho port xác thực; không được ghi log giá trị này.
  - **Dòng 64 — `public byte serverId() {`**: Trả mã server mà client yêu cầu.
  - **Dòng 70 — `public String toString() {`**: Trả mô tả an toàn, luôn che mật khẩu và token.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.session.ProtocolStateException`

- **Source:** `src/main/java/com/nsocry/session/ProtocolStateException.java`
- **Vai trò tóm tắt:** Mô tả yêu cầu chuyển trạng thái handshake không hợp lệ tại giai đoạn hiện tại.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 4 — `public final class ProtocolStateException extends IllegalStateException {`**: Mô tả yêu cầu chuyển trạng thái handshake không hợp lệ tại giai đoạn hiện tại.
  - **Dòng 6 — `public ProtocolStateException(SessionPhase actual, SessionPhase expected, SessionPhase requested) {`**: Tạo lỗi chứa phase thực tế, phase mong đợi và phase được yêu cầu.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.session.SecureRandomSessionKeyProvider`

- **Source:** `src/main/java/com/nsocry/session/SecureRandomSessionKeyProvider.java`
- **Vai trò tóm tắt:** Tạo khóa tương thích ngẫu nhiên bằng SecureRandom cho từng phiên kết nối.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 7 — `public final class SecureRandomSessionKeyProvider implements SessionKeyProvider {`**: Tạo khóa tương thích ngẫu nhiên bằng SecureRandom cho từng phiên kết nối.
  - **Dòng 14 — `public SecureRandomSessionKeyProvider(int keyLength) {`**: Tạo provider dùng SecureRandom mặc định với độ dài khóa đã kiểm tra.
  - **Dòng 29 — `public byte[] createKey() {`**: Sinh một mảng khóa mới cho phiên hiện tại.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.session.SessionKeyProvider`

- **Source:** `src/main/java/com/nsocry/session/SessionKeyProvider.java`
- **Vai trò tóm tắt:** Cấp khóa mới cho từng phiên mà không ràng buộc handshake với chính sách sinh khóa.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 5 — `public interface SessionKeyProvider {`**: Cấp khóa mới cho từng phiên mà không ràng buộc handshake với chính sách sinh khóa.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.session.SessionPhase`

- **Source:** `src/main/java/com/nsocry/session/SessionPhase.java`
- **Vai trò tóm tắt:** Các giai đoạn theo thứ tự của vòng đời kết nối và khởi tạo đăng nhập.
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - **Dòng 4 — `public enum SessionPhase {`**: Các giai đoạn theo thứ tự của vòng đời kết nối và khởi tạo đăng nhập.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

### `com.nsocry.session.package-info`

- **Source:** `src/main/java/com/nsocry/session/package-info.java`
- **Vai trò tóm tắt:** Định nghĩa trạng thái khởi tạo kết nối, giải mã handshake của client và các
- **Trạng thái:** `IMPLEMENTED`
- **API public/protected phát hiện được:**
  - Không có API public/protected một dòng; xem source/package contract.
- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.

## Phạm vi chưa có source

Các package gameplay RESERVED/TRACE_REQUIRED không được tạo stub chỉ để xuất hiện trong catalog. Tra cứu [planned-contracts.tsv](../architecture/planned-contracts.tsv) và [trace-register](trace-register.md).
