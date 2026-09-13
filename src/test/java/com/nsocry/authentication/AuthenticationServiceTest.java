package com.nsocry.authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nsocry.session.AuthenticationDecision;
import com.nsocry.session.ClientInfo;
import com.nsocry.session.LoginRequest;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class AuthenticationServiceTest {
    private static final Instant NOW = Instant.parse("2026-08-18T10:00:00Z");
    private static final ClientInfo CLIENT = new ClientInfo(
            (byte) 1, (byte) 2, false, 240, 320, false, false,
            "J2ME", (byte) 0, 0, (byte) 0, 0, "test");

    @Test
    void acceptsActiveAccountAndRecordsSuccess() {
        FakeRepository repository = new FakeRepository(activeAccount("valid"));
        AuthenticationService service = service(repository);
        assertEquals(AuthenticationDecision.ACCEPTED, service.authenticate(login("valid"), CLIENT));
        assertEquals(1, repository.successes);
        assertEquals(0, repository.failures);
    }

    @Test
    void rejectsWrongPasswordAndRecordsFailure() {
        FakeRepository repository = new FakeRepository(activeAccount("valid"));
        AuthenticationService service = service(repository);
        assertEquals(AuthenticationDecision.REJECTED, service.authenticate(login("wrong"), CLIENT));
        assertEquals(0, repository.successes);
        assertEquals(1, repository.failures);
    }

    @Test
    void rejectsMissingAccountButStillRunsPasswordVerification() {
        FakeRepository repository = new FakeRepository(null);
        CountingPasswords passwords = new CountingPasswords();
        AuthenticationService service = new AuthenticationService(
                repository, passwords, Clock.fixed(NOW, ZoneOffset.UTC), "dummy");
        assertEquals(AuthenticationDecision.REJECTED, service.authenticate(login("wrong"), CLIENT));
        assertEquals(1, passwords.verifications);
    }

    @Test
    void rejectsLockedOrInactiveAccountEvenWithCorrectPassword() {
        AccountCredential locked = new AccountCredential(
                1, "cry", "valid", AccountStatus.ACTIVE, true, NOW.plusSeconds(60));
        FakeRepository repository = new FakeRepository(locked);
        assertEquals(AuthenticationDecision.REJECTED, service(repository).authenticate(login("valid"), CLIENT));
        assertEquals(0, repository.successes);
    }

    private static AuthenticationService service(FakeRepository repository) {
        return new AuthenticationService(
                repository, new CountingPasswords(), Clock.fixed(NOW, ZoneOffset.UTC), "dummy");
    }

    private static AccountCredential activeAccount(String hash) {
        return new AccountCredential(1, "cry", hash, AccountStatus.ACTIVE, true, null);
    }

    private static LoginRequest login(String password) {
        return new LoginRequest("cry", password, "2.17.0", "", "", "token", (byte) 0);
    }

    private static final class CountingPasswords implements PasswordHashingPort {
        int verifications;
        public String hash(char[] password) { return new String(password); }
        public boolean verify(char[] password, String encodedHash) {
            verifications++;
            return encodedHash.equals(new String(password));
        }
    }

    private static final class FakeRepository implements AccountRepository {
        private final AccountCredential account;
        int successes;
        int failures;
        FakeRepository(AccountCredential account) { this.account = account; }
        public Optional<AccountCredential> findByUsername(String username) {
            return Optional.ofNullable(account);
        }
        public void recordSuccessfulLogin(long accountId, Instant occurredAt) { successes++; }
        public void recordFailedLogin(long accountId, Instant occurredAt) { failures++; }
    }
}
