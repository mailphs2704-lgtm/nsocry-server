package com.nsocry.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;

/** Đọc cấu hình NSOCry từ file properties mà không lưu hoặc ghi log giá trị cấu hình. */
public final class ServerConfigurationLoader {
    /** Đọc file tồn tại; nếu đường dẫn không tồn tại thì dùng toàn bộ giá trị mặc định. */
    public ServerConfiguration load(Path path) throws IOException {
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
        return ServerConfiguration.from(properties);
    }
}
