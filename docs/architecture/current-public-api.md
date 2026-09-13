# Public API hiện có tại Architecture Lock v1

Danh sách này là snapshot chữ ký public kèm dòng nguồn để AI sau không vô tình tạo trùng hoặc đổi contract. Với chữ ký nhiều dòng, dòng được ghi là điểm bắt đầu declaration; Javadoc ngay phía trên là mô tả chuẩn.

## src/main/java/com/nsocry/operations/ValidatedItemAssetSeedArchive.java

- Dòng 8: `public final class ValidatedItemAssetSeedArchive {`
- Dòng 24: `public byte[] payload() {`
- Dòng 29: `public String manifestText() {`
- Dòng 34: `public ItemAssetValidationResult validation() {`
## src/main/java/com/nsocry/operations/SkillAssetSeedArchiveService.java

- Dòng 25: `public final class SkillAssetSeedArchiveService {`
- Dòng 32: `public void export(SkillAssetSeedArtifact artifact, Path target) throws IOException {`
- Dòng 51: `public SkillAssetSeedValidationResult dryRun(Path archive) throws IOException {`
- Dòng 56: `public ValidatedSkillAssetSeedArchive readValidated(Path archive) throws IOException {`
## src/main/java/com/nsocry/operations/ValidatedSkillAssetSeedArchive.java

- Dòng 8: `public final class ValidatedSkillAssetSeedArchive {`
- Dòng 23: `public byte[] payload() {`
- Dòng 27: `public String manifestText() {`
- Dòng 31: `public SkillAssetSeedValidationResult validation() {`
## src/main/java/com/nsocry/operations/ItemAssetSeedArchiveService.java

- Dòng 26: `public final class ItemAssetSeedArchiveService {`
- Dòng 33: `public void export(ItemAssetSeedArtifact artifact, Path target) throws IOException {`
- Dòng 54: `public ItemAssetValidationResult dryRun(Path archive)`
- Dòng 60: `public ValidatedItemAssetSeedArchive readValidated(Path archive)`
## src/main/java/com/nsocry/observability/SanitizedNetworkEventSink.java

- Dòng 10: `public final class SanitizedNetworkEventSink implements NetworkEventSink {`
- Dòng 14: `public SanitizedNetworkEventSink(Consumer<String> output) {`
- Dòng 20: `public void sessionFailed(SocketAddress remoteAddress, Exception failure) {`
- Dòng 27: `public void sessionRejected(SocketAddress remoteAddress) {`
- Dòng 33: `public void acceptFailed(IOException failure) {`
## src/main/java/com/nsocry/bootstrap/SkillAssetSchemaPreflightCommand.java

- Dòng 14: `public final class SkillAssetSchemaPreflightCommand {`
- Dòng 19: `public static void main(String[] args) throws Exception {`
## src/main/java/com/nsocry/bootstrap/FirstAdministratorCommand.java

- Dòng 15: `public final class FirstAdministratorCommand {`
- Dòng 20: `public static void main(String[] args) throws Exception {`
## src/main/java/com/nsocry/bootstrap/SkillAssetSeedConvertCommand.java

- Dòng 16: `public final class SkillAssetSeedConvertCommand {`
- Dòng 23: `public static void main(String[] args) throws Exception {`
## src/main/java/com/nsocry/bootstrap/NsocryServerApplication.java

- Dòng 26: `public final class NsocryServerApplication implements Closeable {`
- Dòng 30: `public NsocryServerApplication(`
- Dòng 43: `public void start() throws IOException {`
- Dòng 48: `public TcpServer server() {`
- Dòng 54: `public void close() throws IOException {`
- Dòng 62: `public static void main(String[] args) throws Exception {`
## src/main/java/com/nsocry/bootstrap/SkillAssetSeedImportCommand.java

- Dòng 21: `public final class SkillAssetSeedImportCommand {`
- Dòng 26: `public static void main(String[] args) throws Exception {`
## src/main/java/com/nsocry/bootstrap/ItemAssetSchemaPreflightCommand.java

- Dòng 14: `public final class ItemAssetSchemaPreflightCommand {`
- Dòng 19: `public static void main(String[] args) throws Exception {`
## src/main/java/com/nsocry/bootstrap/ItemAssetDatabaseVerifyCommand.java

- Dòng 23: `public final class ItemAssetDatabaseVerifyCommand {`
- Dòng 28: `public static void main(String[] args) throws Exception {`
## src/main/java/com/nsocry/bootstrap/SkillAssetDatabaseVerifyCommand.java

- Dòng 22: `public final class SkillAssetDatabaseVerifyCommand {`
- Dòng 27: `public static void main(String[] args) throws Exception {`
## src/main/java/com/nsocry/bootstrap/ItemAssetSeedDryRunCommand.java

- Dòng 8: `public final class ItemAssetSeedDryRunCommand {`
- Dòng 13: `public static void main(String[] args) throws Exception {`
## src/main/java/com/nsocry/bootstrap/NsocryLauncher.java

- Dòng 6: `public final class NsocryLauncher {`
- Dòng 11: `public static void main(String[] args) throws Exception {`
## src/main/java/com/nsocry/bootstrap/ItemAssetSeedConvertCommand.java

- Dòng 19: `public final class ItemAssetSeedConvertCommand {`
- Dòng 27: `public static void main(String[] args) throws Exception {`
## src/main/java/com/nsocry/bootstrap/ItemAssetSeedImportCommand.java

- Dòng 21: `public final class ItemAssetSeedImportCommand {`
- Dòng 26: `public static void main(String[] args) throws Exception {`
## src/main/java/com/nsocry/bootstrap/SkillAssetSeedDryRunCommand.java

- Dòng 8: `public final class SkillAssetSeedDryRunCommand {`
- Dòng 12: `public static void main(String[] args) throws Exception {`
## src/main/java/com/nsocry/session/HandshakeProcessor.java

- Dòng 8: `public final class HandshakeProcessor {`
- Dòng 13: `public HandshakeProcessor(LegacySessionTransport transport) {`
- Dòng 18: `public HandshakeEvent begin(byte[] key) throws IOException {`
- Dòng 24: `public HandshakeEvent receiveNext(AuthenticationPort authentication) throws IOException {`
- Dòng 36: `public ClientInfo clientInfo() {`
## src/main/java/com/nsocry/session/LegacySessionTransport.java

- Dòng 19: `public final class LegacySessionTransport implements Closeable {`
- Dòng 29: `public LegacySessionTransport(`
- Dòng 40: `public void beginHandshake(byte[] key) throws IOException {`
- Dòng 59: `public ProtocolFrame readClientFrame() throws IOException {`
- Dòng 68: `public void sendShortFrame(ProtocolFrame frame) throws IOException {`
- Dòng 77: `public void sendFullSizePayload(byte[] payload) throws IOException {`
- Dòng 86: `public HandshakeStateMachine state() {`
- Dòng 92: `public void close() throws IOException {`
## src/main/java/com/nsocry/session/HandshakeStateMachine.java

- Dòng 6: `public final class HandshakeStateMachine {`
- Dòng 10: `public SessionPhase phase() {`
- Dòng 15: `public void keySent() {`
- Dòng 20: `public void clientInfoReceived() {`
- Dòng 25: `public void loginStarted() {`
- Dòng 30: `public void loginSucceeded() {`
- Dòng 35: `public void loginRejected() {`
- Dòng 40: `public boolean close() {`
- Dòng 45: `public boolean isAuthenticated() {`
- Dòng 50: `public boolean isClosed() {`
## src/main/java/com/nsocry/session/ClientInfo.java

- Dòng 4: `public record ClientInfo(`
## src/main/java/com/nsocry/network/SessionConnectionHandler.java

- Dòng 7: `public interface SessionConnectionHandler {`
## src/main/java/com/nsocry/network/TcpServer.java

- Dòng 19: `public final class TcpServer implements Closeable {`
- Dòng 29: `public TcpServer(`
- Dòng 47: `public synchronized void start() throws IOException {`
- Dòng 64: `public boolean isRunning() {`
- Dòng 69: `public synchronized InetSocketAddress localAddress() {`
- Dòng 78: `public void close() throws IOException {`
## src/main/java/com/nsocry/session/LoginRequest.java

- Dòng 6: `public final class LoginRequest {`
- Dòng 16: `public LoginRequest(`
- Dòng 34: `public String username() {`
- Dòng 39: `public String password() {`
- Dòng 44: `public String version() {`
- Dòng 49: `public String reservedUtf1() {`
- Dòng 54: `public String reservedUtf2() {`
- Dòng 59: `public String clientToken() {`
- Dòng 64: `public byte serverId() {`
- Dòng 70: `public String toString() {`
## src/main/java/com/nsocry/network/LegacyHandshakeConnectionHandler.java

- Dòng 13: `public final class LegacyHandshakeConnectionHandler implements SessionConnectionHandler {`
- Dòng 19: `public LegacyHandshakeConnectionHandler(`
- Dòng 30: `public void handle(Socket socket) throws Exception {`
## src/main/java/com/nsocry/network/NetworkEventSink.java

- Dòng 7: `public interface NetworkEventSink {`
## src/main/java/com/nsocry/session/SessionPhase.java

- Dòng 4: `public enum SessionPhase {`
## src/main/java/com/nsocry/session/ProtocolStateException.java

- Dòng 4: `public final class ProtocolStateException extends IllegalStateException {`
- Dòng 6: `public ProtocolStateException(SessionPhase actual, SessionPhase expected, SessionPhase requested) {`
## src/main/java/com/nsocry/session/AuthenticationDecision.java

- Dòng 4: `public enum AuthenticationDecision {`
## src/main/java/com/nsocry/session/AuthenticationPort.java

- Dòng 5: `public interface AuthenticationPort {`
## src/main/java/com/nsocry/session/HandshakePayloadDecoder.java

- Dòng 9: `public final class HandshakePayloadDecoder {`
- Dòng 10: `public static final byte NOT_LOGIN_ENVELOPE = -29;`
- Dòng 11: `public static final byte CLIENT_INFO_COMMAND = -125;`
- Dòng 12: `public static final byte LOGIN_COMMAND = -127;`
- Dòng 18: `public static ClientInfo decodeClientInfo(ProtocolFrame frame) throws IOException {`
- Dòng 39: `public static LoginRequest decodeLogin(ProtocolFrame frame) throws IOException {`
## src/main/java/com/nsocry/session/SecureRandomSessionKeyProvider.java

- Dòng 7: `public final class SecureRandomSessionKeyProvider implements SessionKeyProvider {`
- Dòng 14: `public SecureRandomSessionKeyProvider(int keyLength) {`
- Dòng 29: `public byte[] createKey() {`
## src/main/java/com/nsocry/session/HandshakeEvent.java

- Dòng 4: `public enum HandshakeEvent {`
## src/main/java/com/nsocry/session/SessionKeyProvider.java

- Dòng 5: `public interface SessionKeyProvider {`
## src/main/java/com/nsocry/persistence/ItemAssetSchemaPreflightReport.java

- Dòng 7: `public record ItemAssetSchemaPreflightReport(boolean ready, List<String> differences) {`
- Dòng 9: `public ItemAssetSchemaPreflightReport {`
## src/main/java/com/nsocry/persistence/ItemAssetSchemaColumn.java

- Dòng 6: `public record ItemAssetSchemaColumn(`
- Dòng 14: `public ItemAssetSchemaColumn {`
## src/main/java/com/nsocry/persistence/AccountPersistenceException.java

- Dòng 4: `public final class AccountPersistenceException extends RuntimeException {`
- Dòng 6: `public AccountPersistenceException(String operation, Throwable cause) {`
## src/main/java/com/nsocry/persistence/JdbcAccountProvisioningRepository.java

- Dòng 14: `public final class JdbcAccountProvisioningRepository implements AccountProvisioningRepository {`
- Dòng 24: `public JdbcAccountProvisioningRepository(DataSource dataSource) {`
- Dòng 30: `public long countAccounts() {`
- Dòng 45: `public long create(String username, String passwordHash, AccountRole role, boolean activated) {`
## src/main/java/com/nsocry/persistence/JdbcSkillAssetSource.java

- Dòng 22: `public final class JdbcSkillAssetSource implements SkillAssetSource {`
- Dòng 41: `public JdbcSkillAssetSource(DataSource dataSource, byte version) {`
- Dòng 48: `public SkillAssetBundle load() throws ClientAssetSourceException {`
## src/main/java/com/nsocry/persistence/JdbcSkillAssetSeedImporter.java

- Dòng 21: `public final class JdbcSkillAssetSeedImporter {`
- Dòng 46: `public JdbcSkillAssetSeedImporter(DataSource dataSource) {`
- Dòng 51: `public SkillAssetSeedValidationResult importSeed(byte[] payload, String manifestText)`
## src/main/java/com/nsocry/persistence/JdbcAccountRepository.java

- Dòng 17: `public final class JdbcAccountRepository implements AccountRepository {`
- Dòng 38: `public JdbcAccountRepository(DataSource dataSource) {`
- Dòng 44: `public Optional<AccountCredential> findByUsername(String username) {`
- Dòng 59: `public void recordSuccessfulLogin(long accountId, Instant occurredAt) {`
- Dòng 69: `public void recordFailedLogin(long accountId, Instant occurredAt) {`
## src/main/java/com/nsocry/persistence/MariaDbDataSourceFactory.java

- Dòng 10: `public final class MariaDbDataSourceFactory {`
- Dòng 15: `public static DataSource create(DatabaseConfiguration configuration) {`
## src/main/java/com/nsocry/persistence/SkillAssetSchemaColumn.java

- Dòng 6: `public record SkillAssetSchemaColumn(`
- Dòng 14: `public SkillAssetSchemaColumn {`
## src/main/java/com/nsocry/persistence/ItemAssetSeedImportException.java

- Dòng 4: `public final class ItemAssetSeedImportException extends Exception {`
- Dòng 6: `public ItemAssetSeedImportException(String message, Throwable cause) {`
## src/main/java/com/nsocry/persistence/JdbcItemAssetSeedImporter.java

- Dòng 19: `public final class JdbcItemAssetSeedImporter {`
- Dòng 35: `public JdbcItemAssetSeedImporter(DataSource dataSource) {`
- Dòng 44: `public ItemAssetValidationResult importSeed(byte[] payload, String manifestText)`
## src/main/java/com/nsocry/persistence/SkillAssetSchemaPreflightReport.java

- Dòng 7: `public record SkillAssetSchemaPreflightReport(boolean ready, List<String> differences) {`
- Dòng 9: `public SkillAssetSchemaPreflightReport {`
## src/main/java/com/nsocry/persistence/ItemAssetSchemaContract.java

- Dòng 12: `public final class ItemAssetSchemaContract {`
- Dòng 19: `public static ItemAssetSchemaPreflightReport evaluate(List<ItemAssetSchemaColumn> actualColumns) {`
## src/main/java/com/nsocry/persistence/SkillAssetSchemaContract.java

- Dòng 12: `public final class SkillAssetSchemaContract {`
- Dòng 19: `public static SkillAssetSchemaPreflightReport evaluate(List<SkillAssetSchemaColumn> actualColumns) {`
## src/main/java/com/nsocry/persistence/JdbcItemAssetSource.java

- Dòng 18: `public final class JdbcItemAssetSource implements ItemAssetSource {`
- Dòng 34: `public JdbcItemAssetSource(DataSource dataSource, byte version) {`
- Dòng 41: `public ItemAssetBundle load() throws ClientAssetSourceException {`
## src/main/java/com/nsocry/persistence/JdbcItemAssetSchemaInspector.java

- Dòng 14: `public final class JdbcItemAssetSchemaInspector {`
- Dòng 26: `public JdbcItemAssetSchemaInspector(DataSource dataSource) {`
- Dòng 31: `public ItemAssetSchemaPreflightReport inspect() throws ClientAssetSourceException {`
## src/main/java/com/nsocry/persistence/SkillAssetSeedImportException.java

- Dòng 4: `public final class SkillAssetSeedImportException extends Exception {`
- Dòng 5: `public SkillAssetSeedImportException(String message, Throwable cause) {`
## src/main/java/com/nsocry/persistence/JdbcSkillAssetSchemaInspector.java

- Dòng 14: `public final class JdbcSkillAssetSchemaInspector {`
- Dòng 26: `public JdbcSkillAssetSchemaInspector(DataSource dataSource) {`
- Dòng 31: `public SkillAssetSchemaPreflightReport inspect() throws ClientAssetSourceException {`
## src/main/java/com/nsocry/character/CharacterSelectionPayloadCodec.java

- Dòng 13: `public final class CharacterSelectionPayloadCodec {`
- Dòng 14: `public static final byte NOT_MAP_ENVELOPE = -28;`
- Dòng 15: `public static final byte SELECT_CHARACTER_COMMAND = -126;`
- Dòng 16: `public static final byte CREATE_CHARACTER_COMMAND = -125;`
- Dòng 22: `public static ProtocolFrame encodeCharacterList(List<CharacterSummary> characters) throws IOException {`
- Dòng 48: `public static String decodeSelectedCharacterName(ProtocolFrame frame) throws IOException {`
- Dòng 56: `public static CreateCharacterRequest decodeCreateCharacterRequest(ProtocolFrame frame) throws IOException {`
## src/main/java/com/nsocry/character/CharacterSummary.java

- Dòng 6: `public record CharacterSummary(`
- Dòng 17: `public CharacterSummary {`
## src/main/java/com/nsocry/character/CreateCharacterRequest.java

- Dòng 6: `public record CreateCharacterRequest(String name, byte gender, byte head) {`
- Dòng 8: `public CreateCharacterRequest {`
## src/main/java/com/nsocry/configuration/DatabaseConfigurationLoader.java

- Dòng 12: `public final class DatabaseConfigurationLoader {`
- Dòng 14: `public DatabaseConfiguration load(Path path, Map<String, String> environment) throws IOException {`
## src/main/java/com/nsocry/protocol/compat/ProtocolFrame.java

- Dòng 6: `public record ProtocolFrame(byte command, byte[] payload) {`
- Dòng 8: `public ProtocolFrame {`
- Dòng 14: `public byte[] payload() {`
## src/main/java/com/nsocry/protocol/compat/ProtocolLimits.java

- Dòng 4: `public record ProtocolLimits(int maxShortPayload, int maxFullPayload) {`
- Dòng 5: `public static final ProtocolLimits DEFAULT = new ProtocolLimits(65_535, 1_048_576);`
- Dòng 8: `public ProtocolLimits {`
- Dòng 18: `public void requireAllowed(int length, boolean fullSize) {`
## src/main/java/com/nsocry/protocol/compat/LegacyFrameWriter.java

- Dòng 8: `public final class LegacyFrameWriter {`
- Dòng 13: `public LegacyFrameWriter(OutputStream output, ProtocolLimits limits) {`
- Dòng 19: `public synchronized void writeUnencryptedShortFrame(ProtocolFrame frame) throws IOException {`
- Dòng 25: `public synchronized void writeEncryptedShortFrame(`
- Dòng 33: `public synchronized void writeEncryptedFullSizeFrame(`
## src/main/java/com/nsocry/protocol/compat/RollingXorCipher.java

- Dòng 6: `public final class RollingXorCipher {`
- Dòng 11: `public RollingXorCipher(byte[] key) {`
- Dòng 19: `public byte transform(byte value) {`
- Dòng 26: `public byte[] transform(byte[] values) {`
- Dòng 35: `public int cursor() {`
## src/main/java/com/nsocry/configuration/DatabaseConfiguration.java

- Dòng 8: `public final class DatabaseConfiguration {`
- Dòng 9: `public static final String URL = "nsocry.database.url";`
- Dòng 10: `public static final String USER = "nsocry.database.user";`
- Dòng 11: `public static final String PASSWORD = "nsocry.database.password";`
- Dòng 12: `public static final String ENV_URL = "NSOCRY_DB_URL";`
- Dòng 13: `public static final String ENV_USER = "NSOCRY_DB_USER";`
- Dòng 14: `public static final String ENV_PASSWORD = "NSOCRY_DB_PASSWORD";`
- Dòng 21: `public DatabaseConfiguration(String url, String user, String password) {`
- Dòng 31: `public static DatabaseConfiguration from(Properties properties, Map<String, String> environment) {`
- Dòng 41: `public String url() {`
- Dòng 46: `public String user() {`
- Dòng 51: `public String password() {`
- Dòng 57: `public String toString() {`
## src/main/java/com/nsocry/configuration/ServerConfigurationLoader.java

- Dòng 11: `public final class ServerConfigurationLoader {`
- Dòng 13: `public ServerConfiguration load(Path path) throws IOException {`
## src/main/java/com/nsocry/configuration/ServerConfiguration.java

- Dòng 10: `public record ServerConfiguration(TcpServerConfig tcp, int sessionKeyLength) {`
- Dòng 11: `public static final String HOST = "nsocry.server.host";`
- Dòng 12: `public static final String PORT = "nsocry.server.port";`
- Dòng 13: `public static final String BACKLOG = "nsocry.server.backlog";`
- Dòng 14: `public static final String MAX_SESSIONS = "nsocry.server.max-sessions";`
- Dòng 15: `public static final String READ_TIMEOUT_MILLIS = "nsocry.server.read-timeout-millis";`
- Dòng 16: `public static final String SHUTDOWN_TIMEOUT_MILLIS = "nsocry.server.shutdown-timeout-millis";`
- Dòng 17: `public static final String SESSION_KEY_LENGTH = "nsocry.session.key-length";`
- Dòng 20: `public ServerConfiguration {`
- Dòng 28: `public static ServerConfiguration from(Properties properties) {`
## src/main/java/com/nsocry/assets/ItemAssetCodec.java

- Dòng 13: `public final class ItemAssetCodec {`
- Dòng 21: `public static byte[] encode(ItemAssetBundle bundle) throws IOException {`
- Dòng 50: `public static ItemAssetBundle decode(byte[] payload) throws IOException {`
## src/main/java/com/nsocry/assets/ItemAssetSeedArtifact.java

- Dòng 7: `public final class ItemAssetSeedArtifact {`
- Dòng 26: `public byte[] payload() {`
- Dòng 31: `public ItemAssetSeedManifest manifest() {`
- Dòng 36: `public ItemAssetValidationResult validation() {`
- Dòng 41: `public String manifestText() {`
## src/main/java/com/nsocry/assets/MapAssetBundle.java

- Dòng 7: `public record MapAssetBundle(`
- Dòng 14: `public MapAssetBundle {`
## src/main/java/com/nsocry/assets/MountAppearanceAsset.java

- Dòng 7: `public record MountAppearanceAsset(short itemId, List<List<Short>> frameGroups) {`
- Dòng 11: `public MountAppearanceAsset {`
## src/main/java/com/nsocry/assets/ItemAssetSeedManifest.java

- Dòng 8: `public record ItemAssetSeedManifest(`
- Dòng 16: `public ItemAssetSeedManifest {`
## src/main/java/com/nsocry/assets/SkillAssetSource.java

- Dòng 5: `public interface SkillAssetSource {`
## src/main/java/com/nsocry/assets/ClientAssetSnapshotAssembler.java

- Dòng 10: `public final class ClientAssetSnapshotAssembler {`
- Dòng 18: `public static ClientAssetSnapshot assemble(`
## src/main/java/com/nsocry/assets/AppearanceAssetCodec.java

- Dòng 13: `public final class AppearanceAssetCodec {`
- Dòng 23: `public static byte[] encode(AppearanceAssetBundle bundle) throws IOException {`
- Dòng 60: `public static AppearanceAssetBundle decode(byte[] payload) throws IOException {`
## src/main/java/com/nsocry/assets/SkillTemplateAsset.java

- Dòng 7: `public record SkillTemplateAsset(`
- Dòng 17: `public SkillTemplateAsset {`
## src/main/java/com/nsocry/assets/ItemAssetSource.java

- Dòng 5: `public interface ItemAssetSource {`
## src/main/java/com/nsocry/assets/ClientAssetSnapshotProvider.java

- Dòng 5: `public interface ClientAssetSnapshotProvider {`
## src/main/java/com/nsocry/assets/ItemAssetValidationResult.java

- Dòng 4: `public record ItemAssetValidationResult(`
## src/main/java/com/nsocry/assets/ItemTemplateAsset.java

- Dòng 6: `public record ItemTemplateAsset(`
- Dòng 17: `public ItemTemplateAsset {`
## src/main/java/com/nsocry/protocol/compat/ClientDataSet.java

- Dòng 4: `public enum ClientDataSet {`
- Dòng 17: `public byte requestCommand() {`
- Dòng 22: `public static ClientDataSet fromRequestCommand(byte command) {`
## src/main/java/com/nsocry/assets/SkillAssetSeedArtifactGenerator.java

- Dòng 7: `public final class SkillAssetSeedArtifactGenerator {`
- Dòng 13: `public static SkillAssetSeedArtifact generate(SkillAssetBundle bundle) {`
## src/main/java/com/nsocry/assets/ClientAssetSnapshotPublisher.java

- Dòng 5: `public interface ClientAssetSnapshotPublisher {`
## src/main/java/com/nsocry/assets/ProgressionTable.java

- Dòng 4: `public enum ProgressionTable {`
## src/main/java/com/nsocry/assets/SkillAssetSeedValidationResult.java

- Dòng 8: `public record SkillAssetSeedValidationResult(`
- Dòng 16: `public SkillAssetSeedValidationResult {`
## src/main/java/com/nsocry/assets/MobTemplateAsset.java

- Dòng 6: `public record MobTemplateAsset(`
- Dòng 14: `public MobTemplateAsset {`
## src/main/java/com/nsocry/assets/SkillLevelAsset.java

- Dòng 7: `public record SkillLevelAsset(`
- Dòng 19: `public SkillLevelAsset {`
## src/main/java/com/nsocry/assets/NpcTemplateAsset.java

- Dòng 7: `public record NpcTemplateAsset(`
- Dòng 15: `public NpcTemplateAsset {`
## src/main/java/com/nsocry/protocol/compat/LegacyFrameReader.java

- Dòng 10: `public final class LegacyFrameReader {`
- Dòng 15: `public LegacyFrameReader(InputStream input, ProtocolLimits limits) {`
- Dòng 21: `public ProtocolFrame readUnencryptedShortFrame() throws IOException {`
- Dòng 29: `public ProtocolFrame readEncryptedFrame(RollingXorCipher cipher, boolean allowFullSize) throws IOException {`
## src/main/java/com/nsocry/network/TcpServerConfig.java

- Dòng 8: `public record TcpServerConfig(`
- Dòng 16: `public TcpServerConfig {`
## src/main/java/com/nsocry/assets/SkillAssetCodec.java

- Dòng 13: `public final class SkillAssetCodec {`
- Dòng 21: `public static byte[] encode(SkillAssetBundle bundle) throws IOException {`
- Dòng 46: `public static SkillAssetBundle decode(byte[] payload) throws IOException {`
## src/main/java/com/nsocry/assets/conversion/SkillDumpInventoryReport.java

- Dòng 7: `public record SkillDumpInventoryReport(`
- Dòng 22: `public SkillDumpInventoryReport {`
## src/main/java/com/nsocry/assets/MapAssetCodec.java

- Dòng 13: `public final class MapAssetCodec {`
- Dòng 22: `public static byte[] encode(MapAssetBundle bundle) throws IOException {`
- Dòng 51: `public static MapAssetBundle decode(byte[] payload) throws IOException {`
## src/main/java/com/nsocry/assets/SkillAssetStructureValidator.java

- Dòng 8: `public final class SkillAssetStructureValidator {`
- Dòng 16: `public static SkillAssetValidationReport validate(SkillAssetBundle bundle) {`
## src/main/java/com/nsocry/assets/conversion/ReferenceSkillAssetConverter.java

- Dòng 15: `public final class ReferenceSkillAssetConverter {`
- Dòng 26: `public static SkillAssetConversionResult convert(byte version, String dump) {`
## src/main/java/com/nsocry/assets/SkillAssetSeedValidator.java

- Dòng 13: `public final class SkillAssetSeedValidator {`
- Dòng 18: `public static SkillAssetSeedValidationResult validate(SkillAssetBundle bundle, SkillAssetSeedManifest manifest) {`
## src/main/java/com/nsocry/protocol/compat/LegacyKeyCodec.java

- Dòng 6: `public final class LegacyKeyCodec {`
- Dòng 11: `public static byte[] encodePayload(byte[] key) {`
- Dòng 23: `public static byte[] decodePayload(byte[] payload) {`
## src/main/java/com/nsocry/assets/conversion/ItemAssetConversionReport.java

- Dòng 4: `public record ItemAssetConversionReport(`
## src/main/java/com/nsocry/assets/ClientAssetSnapshot.java

- Dòng 14: `public final class ClientAssetSnapshot {`
- Dòng 20: `public ClientAssetSnapshot(`
- Dòng 42: `public ClientVersionManifest manifest() {`
- Dòng 47: `public byte[] appearanceData() {`
- Dòng 52: `public byte[] payload(ClientDataSet dataSet) {`
## src/main/java/com/nsocry/assets/AppearanceAssetSource.java

- Dòng 5: `public interface AppearanceAssetSource {`
## src/main/java/com/nsocry/assets/conversion/SkillRawByteDifference.java

- Dòng 6: `public record SkillRawByteDifference(String entityType, int entityId, String field, int value) {`
- Dòng 8: `public SkillRawByteDifference {`
## src/main/java/com/nsocry/assets/AtomicClientAssetSnapshotProvider.java

- Dòng 7: `public final class AtomicClientAssetSnapshotProvider`
- Dòng 12: `public AtomicClientAssetSnapshotProvider(ClientAssetSnapshot initialSnapshot) {`
- Dòng 18: `public ClientAssetSnapshot currentSnapshot() {`
- Dòng 24: `public void publish(ClientAssetSnapshot snapshot) {`
## src/main/java/com/nsocry/protocol/compat/PostLoginVersionPayloadCodec.java

- Dòng 10: `public final class PostLoginVersionPayloadCodec {`
- Dòng 11: `public static final byte NOT_MAP_ENVELOPE = -28;`
- Dòng 12: `public static final byte UPDATE_VERSION_COMMAND = -123;`
- Dòng 21: `public static ProtocolFrame encodeVersion(`
- Dòng 40: `public static ClientDataSet decodeDataRequest(ProtocolFrame frame) throws IOException {`
- Dòng 59: `public static ProtocolFrame encodeDataResponse(ClientDataSet dataSet, byte[] dataPayload) {`
## src/main/java/com/nsocry/assets/AppearanceAssetBundle.java

- Dòng 7: `public record AppearanceAssetBundle(`
- Dòng 18: `public AppearanceAssetBundle {`
## src/main/java/com/nsocry/assets/conversion/ReferenceItemSqlDumpParser.java

- Dòng 8: `public final class ReferenceItemSqlDumpParser {`
- Dòng 18: `public static ReferenceItemDumpRows parse(String dump) {`
## src/main/java/com/nsocry/assets/ClientGraphicBlock.java

- Dòng 4: `public enum ClientGraphicBlock {`
## src/main/java/com/nsocry/assets/SkillAssetValidationReport.java

- Dòng 4: `public record SkillAssetValidationReport(`
## src/main/java/com/nsocry/assets/conversion/ReferenceItemDumpRows.java

- Dòng 7: `public record ReferenceItemDumpRows(`
- Dòng 12: `public ReferenceItemDumpRows {`
## src/main/java/com/nsocry/protocol/compat/LegacyFrameCodec.java

- Dòng 7: `public final class LegacyFrameCodec {`
- Dòng 8: `public static final byte KEY_EXCHANGE_COMMAND = -27;`
- Dòng 9: `public static final byte FULL_SIZE_COMMAND = -32;`
- Dòng 15: `public static byte[] encodeShortFrame(byte command, byte[] payload, RollingXorCipher cipher) {`
- Dòng 26: `public static byte[] encodeFullSizeFrame(byte[] payload, RollingXorCipher cipher) {`
- Dòng 34: `public static ProtocolFrame decodeFrame(byte[] wire, RollingXorCipher cipher) {`
## src/main/java/com/nsocry/assets/TaskRouteAsset.java

- Dòng 4: `public record TaskRouteAsset(byte npcId, byte mapId) {`
## src/main/java/com/nsocry/assets/SkillAssetSeedArtifact.java

- Dòng 7: `public final class SkillAssetSeedArtifact {`
- Dòng 18: `public byte[] payload() {`
- Dòng 22: `public String manifestText() {`
- Dòng 26: `public SkillAssetSeedValidationResult validation() {`
## src/main/java/com/nsocry/assets/conversion/ReferenceItemAssetConverter.java

- Dòng 12: `public final class ReferenceItemAssetConverter {`
- Dòng 17: `public static ItemAssetConversionResult convert(`
## src/main/java/com/nsocry/assets/ItemAssetSeedValidationException.java

- Dòng 4: `public final class ItemAssetSeedValidationException extends Exception {`
- Dòng 6: `public ItemAssetSeedValidationException(String message) {`
- Dòng 11: `public ItemAssetSeedValidationException(String message, Throwable cause) {`
## src/main/java/com/nsocry/assets/ItemAssetSeedManifestParser.java

- Dòng 6: `public final class ItemAssetSeedManifestParser {`
- Dòng 13: `public static ItemAssetSeedManifest parse(String text) throws ItemAssetSeedValidationException {`
## src/main/java/com/nsocry/assets/conversion/ItemAssetConversionResult.java

- Dòng 7: `public record ItemAssetConversionResult(ItemAssetBundle bundle, ItemAssetConversionReport report) {`
- Dòng 9: `public ItemAssetConversionResult {`
## src/main/java/com/nsocry/protocol/compat/ClientVersionManifest.java

- Dòng 4: `public record ClientVersionManifest(`
## src/main/java/com/nsocry/assets/SkillAssetBundle.java

- Dòng 7: `public record SkillAssetBundle(`
- Dòng 13: `public SkillAssetBundle {`
## src/main/java/com/nsocry/assets/DataAssetSource.java

- Dòng 5: `public interface DataAssetSource {`
## src/main/java/com/nsocry/assets/conversion/ReferenceItemTemplateRow.java

- Dòng 6: `public record ReferenceItemTemplateRow(`
- Dòng 19: `public ReferenceItemTemplateRow {`
## src/main/java/com/nsocry/assets/AppearancePartAsset.java

- Dòng 7: `public record AppearancePartAsset(`
- Dòng 13: `public AppearancePartAsset {`
## src/main/java/com/nsocry/assets/conversion/SkillAssetConversionResult.java

- Dòng 7: `public record SkillAssetConversionResult(`
- Dòng 12: `public SkillAssetConversionResult {`
## src/main/java/com/nsocry/assets/ClientAssetSourceException.java

- Dòng 4: `public final class ClientAssetSourceException extends Exception {`
- Dòng 6: `public ClientAssetSourceException(String message, Throwable cause) {`
## src/main/java/com/nsocry/assets/SkillClassAsset.java

- Dòng 7: `public record SkillClassAsset(String name, List<SkillTemplateAsset> templates) {`
- Dòng 9: `public SkillClassAsset {`
## src/main/java/com/nsocry/assets/conversion/ReferenceSkillDumpInventoryParser.java

- Dòng 12: `public final class ReferenceSkillDumpInventoryParser {`
- Dòng 23: `public static SkillDumpInventoryReport parse(String dump) {`
## src/main/java/com/nsocry/assets/ItemAssetSeedArtifactGenerator.java

- Dòng 10: `public final class ItemAssetSeedArtifactGenerator {`
- Dòng 17: `public static ItemAssetSeedArtifact generate(ItemAssetBundle bundle)`
## src/main/java/com/nsocry/assets/SkillAssetSeedManifest.java

- Dòng 8: `public record SkillAssetSeedManifest(`
- Dòng 20: `public SkillAssetSeedManifest {`
## src/main/java/com/nsocry/assets/conversion/ReferenceItemOptionRow.java

- Dòng 6: `public record ReferenceItemOptionRow(int id, int type, String name) {`
- Dòng 8: `public ReferenceItemOptionRow {`
## src/main/java/com/nsocry/assets/ClientAssetSnapshotBuildService.java

- Dòng 7: `public final class ClientAssetSnapshotBuildService {`
- Dòng 16: `public ClientAssetSnapshotBuildService(`
- Dòng 36: `public ClientAssetSnapshot rebuild() throws ClientAssetSourceException, IOException {`
## src/main/java/com/nsocry/authentication/AccountRepository.java

- Dòng 7: `public interface AccountRepository {`
## src/main/java/com/nsocry/assets/MapAssetSource.java

- Dòng 5: `public interface MapAssetSource {`
## src/main/java/com/nsocry/assets/ItemOptionAsset.java

- Dòng 6: `public record ItemOptionAsset(String name, byte type) {`
- Dòng 8: `public ItemOptionAsset {`
## src/main/java/com/nsocry/assets/AppearanceLayerAsset.java

- Dòng 4: `public record AppearanceLayerAsset(short imageId, short dx, short dy) {`
## src/main/java/com/nsocry/assets/LegAppearanceAsset.java

- Dòng 4: `public record LegAppearanceAsset(short id, short smallImageId) {`
## src/main/java/com/nsocry/assets/SkillAssetSeedManifestParser.java

- Dòng 8: `public final class SkillAssetSeedManifestParser {`
- Dòng 14: `public static SkillAssetSeedManifest parse(String text) {`
## src/main/java/com/nsocry/assets/ItemAssetSeedValidator.java

- Dòng 10: `public final class ItemAssetSeedValidator {`
- Dòng 15: `public static ItemAssetValidationResult validate(`
## src/main/java/com/nsocry/assets/ItemAssetBundle.java

- Dòng 7: `public record ItemAssetBundle(`
- Dòng 13: `public ItemAssetBundle {`
## src/main/java/com/nsocry/assets/DataAssetBundle.java

- Dòng 10: `public final class DataAssetBundle {`
- Dòng 19: `public DataAssetBundle(`
- Dòng 37: `public byte version() {`
- Dòng 42: `public byte[] graphic(ClientGraphicBlock block) {`
- Dòng 48: `public List<List<TaskRouteAsset>> taskRoutes() {`
- Dòng 52: `public long[] experienceThresholds() {`
- Dòng 57: `public int[] progression(ProgressionTable table) {`
- Dòng 63: `public byte[] effectTemplates() {`
## src/main/java/com/nsocry/assets/DataAssetCodec.java

- Dòng 15: `public final class DataAssetCodec {`
- Dòng 22: `public static byte[] encode(DataAssetBundle bundle) throws IOException {`
- Dòng 62: `public static DataAssetBundle decode(byte[] payload) throws IOException {`
## src/main/java/com/nsocry/authentication/AccountProvisioningRepository.java

- Dòng 4: `public interface AccountProvisioningRepository {`
## src/main/java/com/nsocry/authentication/Pbkdf2PasswordHasher.java

- Dòng 12: `public final class Pbkdf2PasswordHasher implements PasswordHashingPort {`
- Dòng 13: `public static final int DEFAULT_ITERATIONS = 600_000;`
- Dòng 25: `public Pbkdf2PasswordHasher() {`
- Dòng 40: `public String hash(char[] password) {`
- Dòng 52: `public boolean verify(char[] password, String encodedHash) {`
## src/main/java/com/nsocry/authentication/AuthenticationService.java

- Dòng 14: `public final class AuthenticationService implements AuthenticationPort {`
- Dòng 21: `public AuthenticationService(`
- Dòng 34: `public AuthenticationDecision authenticate(LoginRequest request, ClientInfo clientInfo) {`
## src/main/java/com/nsocry/authentication/FirstAdministratorService.java

- Dòng 8: `public final class FirstAdministratorService {`
- Dòng 17: `public FirstAdministratorService(`
- Dòng 28: `public long provision(String username, char[] password) {`
## src/main/java/com/nsocry/authentication/AccountCredential.java

- Dòng 7: `public record AccountCredential(`
- Dòng 16: `public AccountCredential {`
- Dòng 26: `public boolean isTemporarilyLockedAt(Instant instant) {`
## src/main/java/com/nsocry/authentication/PasswordHashingPort.java

- Dòng 4: `public interface PasswordHashingPort {`
## src/main/java/com/nsocry/authentication/AccountRole.java

- Dòng 4: `public enum AccountRole {`
## src/main/java/com/nsocry/authentication/AccountStatus.java

- Dòng 4: `public enum AccountStatus {`
## src/main/java/com/nsocry/assets/SkillLevelOptionAsset.java

- Dòng 4: `public record SkillLevelOptionAsset(short parameter, byte optionTemplateId) {`

Tổng declaration public được chụp: **393**.
