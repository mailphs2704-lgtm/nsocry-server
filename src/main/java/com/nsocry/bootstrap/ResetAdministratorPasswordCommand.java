package com.nsocry.bootstrap;

import com.nsocry.authentication.Pbkdf2PasswordHasher;
import com.nsocry.configuration.DatabaseConfiguration;
import com.nsocry.configuration.DatabaseConfigurationLoader;
import com.nsocry.persistence.MariaDbDataSourceFactory;
import java.io.Console;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Arrays;
import javax.sql.DataSource;

/** Lệnh tương tác đổi password administrator mà không nhận password qua argument. */
public final class ResetAdministratorPasswordCommand {
    private static final int MIN_PASSWORD_CHARS = 8;
    private static final int MAX_PASSWORD_CHARS = 256;
    private static final String UPDATE = """
            UPDATE accounts
            SET password_hash = ?, failed_login_count = 0, locked_until = NULL
            WHERE username = ? AND role = 'ADMINISTRATOR'
            """;

    private ResetAdministratorPasswordCommand() {
    }

    /** Đọc password kín, tạo PBKDF2 hash mới và chỉ cập nhật đúng một administrator. */
    public static void main(String[] args) throws Exception {
        Console console = System.console();
        if (console == null) {
            throw new IllegalStateException("interactive console is required");
        }
        Path path = args.length == 0 ? Path.of("config", "nsocry.properties") : Path.of(args[0]);
        String username = console.readLine("Tên administrator cần reset: ").trim();
        char[] password = console.readPassword("Password mới: ");
        char[] confirmation = console.readPassword("Nhập lại password mới: ");
        if (password == null || confirmation == null) {
            clear(password);
            clear(confirmation);
            throw new IllegalStateException("password input was cancelled");
        }
        try {
            if (!Arrays.equals(password, confirmation)) {
                throw new IllegalArgumentException("password confirmation does not match");
            }
            if (password.length < MIN_PASSWORD_CHARS || password.length > MAX_PASSWORD_CHARS) {
                throw new IllegalArgumentException("password length must be between 8 and 256 characters");
            }

            DatabaseConfiguration configuration = new DatabaseConfigurationLoader()
                    .load(path, System.getenv());
            DataSource dataSource = MariaDbDataSourceFactory.create(configuration);
            String passwordHash = new Pbkdf2PasswordHasher().hash(password);
            try (Connection connection = dataSource.getConnection();
                    PreparedStatement statement = connection.prepareStatement(UPDATE)) {
                statement.setString(1, passwordHash);
                statement.setString(2, username);
                if (statement.executeUpdate() != 1) {
                    throw new IllegalStateException(
                            "expected exactly one administrator account to be updated");
                }
            }
            console.printf("Đã reset password administrator NSOCry: %s%n", username);
        } finally {
            clear(password);
            clear(confirmation);
        }
    }

    private static void clear(char[] value) {
        if (value != null) {
            Arrays.fill(value, '\0');
        }
    }
}
