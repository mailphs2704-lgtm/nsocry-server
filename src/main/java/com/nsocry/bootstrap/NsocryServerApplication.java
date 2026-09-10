package com.nsocry.bootstrap;

import com.nsocry.assets.AtomicDataAssetRuntimeSnapshotStore;
import com.nsocry.configuration.ServerConfiguration;
import com.nsocry.configuration.ServerConfigurationLoader;
import com.nsocry.configuration.DatabaseConfiguration;
import com.nsocry.configuration.DatabaseConfigurationLoader;
import com.nsocry.authentication.AuthenticationService;
import com.nsocry.authentication.Pbkdf2PasswordHasher;
import com.nsocry.network.LegacyHandshakeConnectionHandler;
import com.nsocry.network.NetworkEventSink;
import com.nsocry.network.TcpServer;
import com.nsocry.observability.SanitizedNetworkEventSink;
import com.nsocry.persistence.JdbcAccountRepository;
import com.nsocry.persistence.MariaDbDataSourceFactory;
import com.nsocry.protocol.compat.ProtocolLimits;
import com.nsocry.session.AuthenticationPort;
import com.nsocry.session.SecureRandomSessionKeyProvider;
import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Arrays;
import javax.sql.DataSource;

/** Điểm ghép và vòng đời tối thiểu để chạy TCP server NSOCry từ cấu hình. */
public final class NsocryServerApplication implements Closeable {
    private final TcpServer server;
    private final StartupReadiness startupReadiness;

    /** Ghép cấu hình, xác thực và event sink thành server nhưng chưa tự động start. */
    public NsocryServerApplication(
            ServerConfiguration configuration,
            AuthenticationPort authentication,
            NetworkEventSink events) {
        this(configuration, authentication, events, () -> { });
    }

    /**
     * Ghép thêm readiness gate production; gate chạy ngay trước khi listener bind.
     * Constructor này chưa tự chạy gate hoặc mở socket.
     */
    public NsocryServerApplication(
            ServerConfiguration configuration,
            AuthenticationPort authentication,
            NetworkEventSink events,
            StartupReadiness startupReadiness) {
        Objects.requireNonNull(configuration, "configuration");
        LegacyHandshakeConnectionHandler handler = new LegacyHandshakeConnectionHandler(
                ProtocolLimits.DEFAULT,
                new SecureRandomSessionKeyProvider(configuration.sessionKeyLength()),
                Objects.requireNonNull(authentication, "authentication"));
        server = new TcpServer(configuration.tcp(), handler, Objects.requireNonNull(events, "events"));
        this.startupReadiness = Objects.requireNonNull(startupReadiness, "startupReadiness");
    }

    /** Khởi động TCP listener chỉ sau khi readiness gate hoàn tất không lỗi. */
    public void start() throws IOException {
        startupReadiness.verify();
        server.start();
    }

    /** Trả server đang được application sở hữu để kiểm tra trạng thái và địa chỉ bind. */
    public TcpServer server() {
        return server;
    }

    /** Dừng toàn bộ tài nguyên runtime thuộc application. */
    @Override
    public void close() throws IOException {
        server.close();
    }

    /**
     * Chạy server từ file cấu hình được chỉ định ở argument đầu tiên hoặc config/nsocry.properties.
     * Ghép MariaDB account repository và authentication service trước khi mở TCP listener.
     */
    public static void main(String[] args) throws Exception {
        Path path = args.length == 0 ? Path.of("config", "nsocry.properties") : Path.of(args[0]);
        ServerConfiguration configuration = new ServerConfigurationLoader().load(path);
        DatabaseConfiguration database = new DatabaseConfigurationLoader().load(path, System.getenv());
        DataSource dataSource = MariaDbDataSourceFactory.create(database);
        Pbkdf2PasswordHasher passwords = new Pbkdf2PasswordHasher();
        char[] dummyPassword = "nsocry-missing-account".toCharArray();
        String missingAccountHash;
        try {
            missingAccountHash = passwords.hash(dummyPassword);
        } finally {
            Arrays.fill(dummyPassword, '\0');
        }
        AuthenticationPort authentication = new AuthenticationService(
                new JdbcAccountRepository(dataSource),
                passwords,
                java.time.Clock.systemUTC(),
                missingAccountHash);
        SanitizedNetworkEventSink events = new SanitizedNetworkEventSink(System.err::println);
        AtomicDataAssetRuntimeSnapshotStore dataStore =
                new AtomicDataAssetRuntimeSnapshotStore();
        DataAssetServerStartupReadiness dataReadiness =
                DataAssetServerStartupReadiness.authoritativeV7(dataSource, dataStore);
        NsocryServerApplication application = new NsocryServerApplication(
                configuration, authentication, events, dataReadiness);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> closeQuietly(application), "nsocry-shutdown"));
        application.start();
        System.out.println("NSOCry server started on " + application.server().localAddress());
        System.out.println("DATA runtime snapshot READY version="
                + Byte.toUnsignedInt(dataStore.requireCurrent(
                        DataAssetServerStartupReadiness.AUTHORITATIVE_VERSION,
                        DataAssetServerStartupReadiness.AUTHORITATIVE_PAYLOAD_SHA256).version()));
    }

    /** Gate đồng bộ, fail-closed, không được mở listener khi verify ném lỗi. */
    @FunctionalInterface
    public interface StartupReadiness {
        void verify();
    }

    /** Đóng application trong shutdown hook mà không che khuất quá trình JVM đang dừng. */
    private static void closeQuietly(NsocryServerApplication application) {
        try {
            application.close();
        } catch (IOException ignored) {
            // JVM đang dừng; lỗi chi tiết không được ghi để tránh log dữ liệu nội bộ ngoài ý muốn.
        }
    }
}
