package com.nsocry.session;

import java.security.SecureRandom;
import java.util.Objects;

public final class SecureRandomSessionKeyProvider implements SessionKeyProvider {
    private static final int MAX_KEY_LENGTH = 255;

    private final SecureRandom random;
    private final int keyLength;

    public SecureRandomSessionKeyProvider(int keyLength) {
        this(new SecureRandom(), keyLength);
    }

    SecureRandomSessionKeyProvider(SecureRandom random, int keyLength) {
        if (keyLength < 1 || keyLength > MAX_KEY_LENGTH) {
            throw new IllegalArgumentException("keyLength must be between 1 and 255");
        }
        this.random = Objects.requireNonNull(random, "random");
        this.keyLength = keyLength;
    }

    @Override
    public byte[] createKey() {
        byte[] key = new byte[keyLength];
        random.nextBytes(key);
        return key;
    }
}
