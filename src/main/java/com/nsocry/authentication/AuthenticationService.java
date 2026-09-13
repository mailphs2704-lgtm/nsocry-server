package com.nsocry.authentication;

import com.nsocry.session.AuthenticationDecision;
import com.nsocry.session.AuthenticationPort;
import com.nsocry.session.ClientInfo;
import com.nsocry.session.LoginRequest;
import java.time.Clock;
import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/** Xác thực account bằng repository và password hasher mà không phân biệt lỗi trả cho client. */
public final class AuthenticationService implements AuthenticationPort {
    private final AccountRepository accounts;
    private final PasswordHashingPort passwords;
    private final Clock clock;
    private final String missingAccountHash;

    /** Tạo service cùng dummy hash dùng để cân bằng đường xử lý username không tồn tại. */
    public AuthenticationService(
            AccountRepository accounts,
            PasswordHashingPort passwords,
            Clock clock,
            String missingAccountHash) {
        this.accounts = Objects.requireNonNull(accounts, "accounts");
        this.passwords = Objects.requireNonNull(passwords, "passwords");
        this.clock = Objects.requireNonNull(clock, "clock");
        this.missingAccountHash = Objects.requireNonNull(missingAccountHash, "missingAccountHash");
    }

    /** Xác minh password rồi kiểm tra activated, status và khóa tạm trước khi chấp nhận. */
    @Override
    public AuthenticationDecision authenticate(LoginRequest request, ClientInfo clientInfo) {
        Objects.requireNonNull(request, "request");
        Objects.requireNonNull(clientInfo, "clientInfo");
        Optional<AccountCredential> found = accounts.findByUsername(request.username());
        AccountCredential account = found.orElse(null);
        char[] suppliedPassword = request.password().toCharArray();
        boolean passwordMatches;
        try {
            passwordMatches = passwords.verify(
                    suppliedPassword,
                    account == null ? missingAccountHash : account.passwordHash());
        } finally {
            Arrays.fill(suppliedPassword, '\0');
        }

        if (account == null) {
            return AuthenticationDecision.REJECTED;
        }
        Instant now = clock.instant();
        if (!passwordMatches) {
            accounts.recordFailedLogin(account.id(), now);
            return AuthenticationDecision.REJECTED;
        }
        if (!account.activated()
                || account.status() != AccountStatus.ACTIVE
                || account.isTemporarilyLockedAt(now)) {
            return AuthenticationDecision.REJECTED;
        }
        accounts.recordSuccessfulLogin(account.id(), now);
        return AuthenticationDecision.ACCEPTED;
    }
}
