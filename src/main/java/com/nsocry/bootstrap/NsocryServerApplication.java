package com.nsocry.bootstrap;

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

    /** Ghép cấu hình, xác thực và event sink thành server nhưng chưa tự động start. */
    public NsocryServerApplication(
            ServerConfiguration configuration,
            AuthenticationPort authentication,
            NetworkEventSink events) {
        Objects.requireNonNull(configuration, "configuration");
        LegacyHandshakeConnectionHandler handler = new LegacyHandshakeConnectionHandler(
                ProtocolLimits.DEFAULT,
                new SecureRandomSessionKeyProvider(configuration.sessionKeyLength()),
                Objects.requireNonNull(authentication, "authentication"));
        server = new TcpServer(configuration.tcp(), handler, Objects.requireNonNull(events, "events"));
    }

    /** Khởi động TCP listener sau khi toàn bộ dependency đã được tạo thành công. */
    public void start() throws IOException {
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
        NsocryServerApplication application = new NsocryServerApplication(
                configuration, authentication, events);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> closeQuietly(application), "nsocry-shutdown"));
        application.start();
        System.out.println("NSOCry server started on " + application.server().localAddress());
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
