package com.nsocry.authentication;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;

/** Tạo administrator đầu tiên theo quy tắc một lần và luôn xóa password đầu vào. */
public final class FirstAdministratorService {
    private static final Pattern USERNAME = Pattern.compile("[A-Za-z0-9_]{3,32}");
    private static final int MIN_PASSWORD_CHARS = 8;
    private static final int MAX_PASSWORD_CHARS = 256;

    private final AccountProvisioningRepository accounts;
    private final PasswordHashingPort passwords;

    /** Tạo service từ provisioning repository và password hasher. */
    public FirstAdministratorService(
            AccountProvisioningRepository accounts,
            PasswordHashingPort passwords) {
        this.accounts = Objects.requireNonNull(accounts, "accounts");
        this.passwords = Objects.requireNonNull(passwords, "passwords");
    }

    /**
     * Tạo administrator đầu tiên và trả id mới.
     * Mảng password được xóa trong mọi trường hợp, kể cả validation hoặc persistence thất bại.
     */
    public long provision(String username, char[] password) {
        Objects.requireNonNull(password, "password");
        try {
            if (accounts.countAccounts() != 0) {
                throw new IllegalStateException("first administrator already exists");
            }
            if (username == null || !USERNAME.matcher(username).matches()) {
                throw new IllegalArgumentException(
                        "username must contain 3-32 ASCII letters, digits or underscore");
            }
            if (password.length < MIN_PASSWORD_CHARS || password.length > MAX_PASSWORD_CHARS) {
                throw new IllegalArgumentException("password length must be between 8 and 256 characters");
            }
            String passwordHash = passwords.hash(password);
            return accounts.create(username, passwordHash, AccountRole.ADMINISTRATOR, true);
        } finally {
            Arrays.fill(password, '\0');
        }
    }
}
