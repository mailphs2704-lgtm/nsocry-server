package com.nsocry.bootstrap;

import com.nsocry.authentication.FirstAdministratorService;
import com.nsocry.authentication.Pbkdf2PasswordHasher;
import com.nsocry.configuration.DatabaseConfiguration;
import com.nsocry.configuration.DatabaseConfigurationLoader;
import com.nsocry.persistence.JdbcAccountProvisioningRepository;
import com.nsocry.persistence.MariaDbDataSourceFactory;
import java.io.Console;
import java.nio.file.Path;
import java.util.Arrays;
import javax.sql.DataSource;

/** Lệnh tương tác tạo administrator đầu tiên mà không nhận password qua argument. */
public final class FirstAdministratorCommand {
    private FirstAdministratorCommand() {
    }

    /** Đọc username/password từ Console, xác nhận password rồi tạo đúng một administrator. */
    public static void main(String[] args) throws Exception {
        Console console = System.console();
        if (console == null) {
            throw new IllegalStateException("interactive console is required");
        }
        Path path = args.length == 0 ? Path.of("config", "nsocry.properties") : Path.of(args[0]);
        String username = console.readLine("Tên administrator đầu tiên: ").trim();
        char[] password = console.readPassword("Password: ");
        char[] confirmation = console.readPassword("Nhập lại password: ");
        if (password == null || confirmation == null) {
            if (password != null) {
                Arrays.fill(password, '\0');
            }
            if (confirmation != null) {
                Arrays.fill(confirmation, '\0');
            }
            throw new IllegalStateException("password input was cancelled");
        }
        try {
            if (!Arrays.equals(password, confirmation)) {
                throw new IllegalArgumentException("password confirmation does not match");
            }
            DatabaseConfiguration configuration = new DatabaseConfigurationLoader()
                    .load(path, System.getenv());
            DataSource dataSource = MariaDbDataSourceFactory.create(configuration);
            FirstAdministratorService service = new FirstAdministratorService(
                    new JdbcAccountProvisioningRepository(dataSource),
                    new Pbkdf2PasswordHasher());
            long accountId = service.provision(username, password);
            console.printf("Đã tạo administrator NSOCry, id=%d%n", accountId);
        } finally {
            Arrays.fill(password, '\0');
            Arrays.fill(confirmation, '\0');
        }
    }
}
