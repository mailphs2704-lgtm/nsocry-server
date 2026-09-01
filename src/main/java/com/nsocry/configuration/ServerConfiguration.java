package com.nsocry.configuration;

import com.nsocry.network.TcpServerConfig;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Objects;
import java.util.Properties;

/** Cấu hình runtime bất biến dùng để tạo TCP server và nguồn khóa phiên. */
public record ServerConfiguration(TcpServerConfig tcp, int sessionKeyLength) {
    public static final String HOST = "nsocry.server.host";
    public static final String PORT = "nsocry.server.port";
    public static final String BACKLOG = "nsocry.server.backlog";
    public static final String MAX_SESSIONS = "nsocry.server.max-sessions";
    public static final String READ_TIMEOUT_MILLIS = "nsocry.server.read-timeout-millis";
    public static final String SHUTDOWN_TIMEOUT_MILLIS = "nsocry.server.shutdown-timeout-millis";
    public static final String SESSION_KEY_LENGTH = "nsocry.session.key-length";

    /** Kiểm tra dependency và phạm vi độ dài khóa ngay khi tạo cấu hình. */
    public ServerConfiguration {
        Objects.requireNonNull(tcp, "tcp");
        if (sessionKeyLength < 1 || sessionKeyLength > 255) {
            throw new IllegalArgumentException("sessionKeyLength must be between 1 and 255");
        }
    }

    /** Tạo cấu hình từ Properties, dùng mặc định an toàn cho các khóa bị thiếu. */
    public static ServerConfiguration from(Properties properties) {
        Objects.requireNonNull(properties, "properties");
        String host = properties.getProperty(HOST, "0.0.0.0").trim();
        if (host.isEmpty()) {
            throw new IllegalArgumentException(HOST + " must not be blank");
        }
        int port = number(properties, PORT, 14_444, 0, 65_535);
        int backlog = number(properties, BACKLOG, 128, 1, 65_535);
        int maxSessions = number(properties, MAX_SESSIONS, 500, 1, 100_000);
        int readTimeout = number(properties, READ_TIMEOUT_MILLIS, 15_000, 1, Integer.MAX_VALUE);
        int shutdownTimeout = number(properties, SHUTDOWN_TIMEOUT_MILLIS, 5_000, 1, Integer.MAX_VALUE);
        int keyLength = number(properties, SESSION_KEY_LENGTH, 32, 1, 255);
        return new ServerConfiguration(
                new TcpServerConfig(
                        new InetSocketAddress(host, port),
                        backlog,
                        maxSessions,
                        readTimeout,
                        Duration.ofMillis(shutdownTimeout)),
                keyLength);
    }

    /** Đọc và kiểm tra một số nguyên, đồng thời gắn tên khóa vào thông báo lỗi. */
    private static int number(Properties properties, String key, int fallback, int min, int max) {
        String raw = properties.getProperty(key);
        if (raw == null) {
            return fallback;
        }
        final int value;
        try {
            value = Integer.parseInt(raw.trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(key + " must be an integer", exception);
        }
        if (value < min || value > max) {
            throw new IllegalArgumentException(key + " must be between " + min + " and " + max);
        }
        return value;
    }
}
