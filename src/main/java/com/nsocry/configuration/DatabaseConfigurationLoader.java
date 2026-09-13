package com.nsocry.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/** Đọc cấu hình database từ file cục bộ và biến môi trường mà không ghi log nội dung. */
public final class DatabaseConfigurationLoader {
    /** Đọc file nếu tồn tại rồi áp dụng biến môi trường có độ ưu tiên cao hơn. */
    public DatabaseConfiguration load(Path path, Map<String, String> environment) throws IOException {
        Objects.requireNonNull(path, "path");
        Properties properties = new Properties();
        if (Files.exists(path)) {
            if (!Files.isRegularFile(path)) {
                throw new IOException("configuration path is not a regular file");
            }
            try (InputStream input = Files.newInputStream(path)) {
                properties.load(input);
            }
        }
        return DatabaseConfiguration.from(properties, environment);
    }
}
