package com.nsocry.configuration;

import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/** Cấu hình kết nối database bất biến; biểu diễn chuỗi luôn che password. */
public final class DatabaseConfiguration {
    public static final String URL = "nsocry.database.url";
    public static final String USER = "nsocry.database.user";
    public static final String PASSWORD = "nsocry.database.password";
    public static final String ENV_URL = "NSOCRY_DB_URL";
    public static final String ENV_USER = "NSOCRY_DB_USER";
    public static final String ENV_PASSWORD = "NSOCRY_DB_PASSWORD";

    private final String url;
    private final String user;
    private final String password;

    /** Tạo cấu hình sau khi kiểm tra URL MariaDB, user và password bắt buộc. */
    public DatabaseConfiguration(String url, String user, String password) {
        this.url = required(url, "database url");
        this.user = required(user, "database user");
        this.password = required(password, "database password");
        if (!this.url.startsWith("jdbc:mariadb://")) {
            throw new IllegalArgumentException("database url must use jdbc:mariadb://");
        }
    }

    /** Đọc cấu hình, ưu tiên biến môi trường rồi mới đến file properties. */
    public static DatabaseConfiguration from(Properties properties, Map<String, String> environment) {
        Objects.requireNonNull(properties, "properties");
        Objects.requireNonNull(environment, "environment");
        return new DatabaseConfiguration(
                value(environment, ENV_URL, properties, URL),
                value(environment, ENV_USER, properties, USER),
                value(environment, ENV_PASSWORD, properties, PASSWORD));
    }

    /** Trả JDBC URL đã kiểm tra. */
    public String url() {
        return url;
    }

    /** Trả database user cho DataSource factory. */
    public String user() {
        return user;
    }

    /** Trả password cho DataSource factory; bên gọi không được log giá trị. */
    public String password() {
        return password;
    }

    /** Mô tả cấu hình mà không bao giờ đưa password vào chuỗi. */
    @Override
    public String toString() {
        return "DatabaseConfiguration[url=" + url + ", user=" + user + ", password=<redacted>]";
    }

    /** Lấy giá trị environment nếu có, ngược lại lấy property tương ứng. */
    private static String value(
            Map<String, String> environment, String environmentKey,
            Properties properties, String propertyKey) {
        String environmentValue = environment.get(environmentKey);
        return environmentValue == null || environmentValue.isBlank()
                ? properties.getProperty(propertyKey)
                : environmentValue;
    }

    /** Chuẩn hóa giá trị bắt buộc và từ chối chuỗi rỗng. */
    private static String required(String value, String label) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(label + " is required");
        }
        return value.trim();
    }
}
