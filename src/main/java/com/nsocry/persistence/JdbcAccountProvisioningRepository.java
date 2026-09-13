package com.nsocry.persistence;

import com.nsocry.authentication.AccountProvisioningRepository;
import com.nsocry.authentication.AccountRole;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import javax.sql.DataSource;

/** Adapter JDBC chỉ dành cho đếm và tạo account bootstrap. */
public final class JdbcAccountProvisioningRepository implements AccountProvisioningRepository {
    private static final String COUNT = "SELECT COUNT(*) FROM accounts";
    private static final String INSERT = """
            INSERT INTO accounts (username, password_hash, status, activated, role)
            VALUES (?, ?, 0, ?, ?)
            """;

    private final DataSource dataSource;

    /** Tạo adapter từ DataSource thuộc database nsocry. */
    public JdbcAccountProvisioningRepository(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource, "dataSource");
    }

    /** Đếm account bằng truy vấn không nhận dữ liệu từ người dùng. */
    @Override
    public long countAccounts() {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(COUNT);
                ResultSet result = statement.executeQuery()) {
            if (!result.next()) {
                throw new AccountPersistenceException("count-accounts", null);
            }
            return result.getLong(1);
        } catch (SQLException exception) {
            throw new AccountPersistenceException("count-accounts", exception);
        }
    }

    /** Insert account bằng prepared statement và trả generated id. */
    @Override
    public long create(String username, String passwordHash, AccountRole role, boolean activated) {
        Objects.requireNonNull(username, "username");
        Objects.requireNonNull(passwordHash, "passwordHash");
        Objects.requireNonNull(role, "role");
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        INSERT, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, username);
            statement.setString(2, passwordHash);
            statement.setBoolean(3, activated);
            statement.setString(4, role.name());
            if (statement.executeUpdate() != 1) {
                throw new AccountPersistenceException("create-account", null);
            }
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new AccountPersistenceException("create-account-key", null);
                }
                return keys.getLong(1);
            }
        } catch (SQLException exception) {
            throw new AccountPersistenceException("create-account", exception);
        }
    }
}
