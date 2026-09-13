package com.nsocry.persistence;

import com.nsocry.configuration.DatabaseConfiguration;
import java.sql.SQLException;
import java.util.Objects;
import javax.sql.DataSource;
import org.mariadb.jdbc.MariaDbDataSource;

/** Tạo MariaDB DataSource chính thức từ cấu hình đã kiểm tra. */
public final class MariaDbDataSourceFactory {
    private MariaDbDataSourceFactory() {
    }

    /** Tạo DataSource nhưng chưa mở connection; lỗi cấu hình được bọc bằng exception đã làm sạch. */
    public static DataSource create(DatabaseConfiguration configuration) {
        Objects.requireNonNull(configuration, "configuration");
        try {
            MariaDbDataSource dataSource = new MariaDbDataSource();
            dataSource.setUrl(configuration.url());
            dataSource.setUser(configuration.user());
            dataSource.setPassword(configuration.password());
            return dataSource;
        } catch (SQLException exception) {
            throw new AccountPersistenceException("create-data-source", exception);
        }
    }
}
