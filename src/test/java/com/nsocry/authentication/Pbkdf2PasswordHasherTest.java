package com.nsocry.authentication;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.SecureRandom;
import org.junit.jupiter.api.Test;

class Pbkdf2PasswordHasherTest {
    @Test
    void hashesWithUniqueSaltAndVerifiesCorrectPassword() {
        Pbkdf2PasswordHasher hasher = new Pbkdf2PasswordHasher(new SecureRandom(), 2_000);
        char[] password = "MậtKhẩu-Cry-123".toCharArray();
        String first = hasher.hash(password);
        String second = hasher.hash(password);
        assertNotEquals(first, second);
        assertTrue(hasher.verify(password, first));
        assertFalse(hasher.verify("sai".toCharArray(), first));
    }

    @Test
    void rejectsMalformedStoredHashWithoutThrowing() {
        Pbkdf2PasswordHasher hasher = new Pbkdf2PasswordHasher(new SecureRandom(), 2_000);
        assertFalse(hasher.verify("password".toCharArray(), "invalid"));
        assertFalse(hasher.verify("password".toCharArray(), "pbkdf2-sha256$wrong$salt$hash"));
    }
}
