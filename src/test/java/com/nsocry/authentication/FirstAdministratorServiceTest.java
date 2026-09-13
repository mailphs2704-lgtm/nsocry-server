package com.nsocry.authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

class FirstAdministratorServiceTest {
    @Test
    void createsActivatedAdministratorAndClearsPassword() {
        FakeRepository repository = new FakeRepository();
        char[] password = "Cry-Admin-123".toCharArray();
        long id = new FirstAdministratorService(repository, new FakePasswords())
                .provision("Cry_Admin", password);
        assertEquals(7, id);
        assertEquals(AccountRole.ADMINISTRATOR, repository.role);
        assertEquals(true, repository.activated);
        assertEquals("hashed", repository.hash);
        assertEquals(true, allCleared(password));
    }

    @Test
    void refusesToCreateSecondBootstrapAdministrator() {
        FakeRepository repository = new FakeRepository();
        repository.count = 1;
        char[] password = "Cry-Admin-123".toCharArray();
        assertThrows(IllegalStateException.class,
                () -> new FirstAdministratorService(repository, new FakePasswords())
                        .provision("Cry_Admin", password));
        assertEquals(true, allCleared(password));
    }

    @Test
    void rejectsInvalidUsername() {
        char[] password = "Cry-Admin-123".toCharArray();
        assertThrows(IllegalArgumentException.class,
                () -> new FirstAdministratorService(new FakeRepository(), new FakePasswords())
                        .provision("ký tự lạ", password));
        assertEquals(true, allCleared(password));
    }

    @Test
    void rejectsShortPasswordAndClearsIt() {
        char[] password = "short".toCharArray();
        assertThrows(IllegalArgumentException.class,
                () -> new FirstAdministratorService(new FakeRepository(), new FakePasswords())
                        .provision("Cry_Admin", password));
        assertEquals(true, allCleared(password));
    }

    private static boolean allCleared(char[] password) {
        char[] cleared = new char[password.length];
        return Arrays.equals(cleared, password);
    }

    private static final class FakePasswords implements PasswordHashingPort {
        public String hash(char[] password) { return "hashed"; }
        public boolean verify(char[] password, String encodedHash) { return false; }
    }

    private static final class FakeRepository implements AccountProvisioningRepository {
        long count;
        String hash;
        AccountRole role;
        boolean activated;
        public long countAccounts() { return count; }
        public long create(String username, String passwordHash, AccountRole role, boolean activated) {
            this.hash = passwordHash;
            this.role = role;
            this.activated = activated;
            return 7;
        }
    }
}
