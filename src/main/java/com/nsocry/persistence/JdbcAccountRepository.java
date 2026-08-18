package com.nsocry.persistence;

import com.nsocry.authentication.AccountCredential;
import com.nsocry.authentication.AccountRepository;
import com.nsocry.authentication.AccountStatus;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import javax.sql.DataSource;

/** Adapter JDBC cho bảng accounts của NSOCry, chỉ sử dụng prepared statement. */
public final class JdbcAccountRepository implements AccountRepository {
    private static final String FIND_BY_USERNAME = """
            SELECT id, username, password_hash, status, activated, locked_until
            FROM accounts
            WHERE username = ?
            LIMIT 1
            """;
    private static final String RECORD_SUCCESS = """
            UPDATE accounts
            SET failed_login_count = 0, locked_until = NULL, last_login_at = ?
            WHERE id = ?
            """;
    private static final String RECORD_FAILURE = """
            UPDATE accounts
            SET failed_login_count = LEAST(failed_login_count + 1, 65535)
            WHERE id = ?
            """;

    private final DataSource dataSource;

    /** Tạo repository từ DataSource do composition root quản lý. */
    public JdbcAccountRepository(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource, "dataSource");
    }

    /** Tải đúng một credential theo username phân biệt hoa thường. */
    @Override
    public Optional<AccountCredential> findByUsername(String username) {
        Objects.requireNonNull(username, "username");
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(FIND_BY_USERNAME)) {
            statement.setString(1, username);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? Optional.of(map(result)) : Optional.empty();
            }
        } catch (SQLException exception) {
            throw new AccountPersistenceException("find-by-username", exception);
        }
    }

    /** Đặt lại bộ đếm lỗi, bỏ khóa tạm và ghi thời điểm đăng nhập thành công. */
    @Override
    public void recordSuccessfulLogin(long accountId, Instant occurredAt) {
        Objects.requireNonNull(occurredAt, "occurredAt");
        update("record-success", RECORD_SUCCESS, statement -> {
            statement.setTimestamp(1, Timestamp.from(occurredAt));
            statement.setLong(2, accountId);
        });
    }

    /** Tăng nguyên tử bộ đếm đăng nhập sai; chính sách đặt locked_until thuộc checkpoint kế tiếp. */
    @Override
    public void recordFailedLogin(long accountId, Instant occurredAt) {
        Objects.requireNonNull(occurredAt, "occurredAt");
        update("record-failure", RECORD_FAILURE, statement -> statement.setLong(1, accountId));
    }

    /** Chuyển một row JDBC thành credential domain và ánh xạ status có kiểm tra. */
    private static AccountCredential map(ResultSet result) throws SQLException {
        Timestamp lockedUntil = result.getTimestamp("locked_until");
        return new AccountCredential(
                result.getLong("id"),
                result.getString("username"),
                result.getString("password_hash"),
                status(result.getInt("status")),
                result.getBoolean("activated"),
                lockedUntil == null ? null : lockedUntil.toInstant());
    }

    /** Ánh xạ mã database sang enum và từ chối dữ liệu ngoài schema contract. */
    private static AccountStatus status(int value) throws SQLException {
        return switch (value) {
            case 0 -> AccountStatus.ACTIVE;
            case 1 -> AccountStatus.LOCKED;
            case 2 -> AccountStatus.BANNED;
            default -> throw new SQLException("unsupported account status");
        };
    }

    /** Thực hiện một update đơn lẻ và yêu cầu chính xác một account được tác động. */
    private void update(String operation, String sql, StatementBinder binder) {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            binder.bind(statement);
            if (statement.executeUpdate() != 1) {
                throw new AccountPersistenceException(operation, null);
            }
        } catch (SQLException exception) {
            throw new AccountPersistenceException(operation, exception);
        }
    }

    /** Callback nội bộ gắn tham số mà không lặp mã quản lý Connection/PreparedStatement. */
    @FunctionalInterface
    private interface StatementBinder {
        /** Gắn toàn bộ tham số cho prepared statement hiện tại. */
        void bind(PreparedStatement statement) throws SQLException;
    }
}
