package com.nsocry.authentication;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** Password hasher thuần Java dùng PBKDF2-HMAC-SHA256, salt riêng và định dạng có version. */
public final class Pbkdf2PasswordHasher implements PasswordHashingPort {
    public static final int DEFAULT_ITERATIONS = 600_000;
    private static final String VERSION = "pbkdf2-sha256";
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int SALT_BYTES = 16;
    private static final int HASH_BITS = 256;
    private static final int MAX_PASSWORD_CHARS = 256;
    private static final int MAX_STORED_ITERATIONS = 2_000_000;

    private final SecureRandom random;
    private final int iterations;

    /** Tạo hasher với SecureRandom mặc định và work factor 600.000 vòng. */
    public Pbkdf2PasswordHasher() {
        this(new SecureRandom(), DEFAULT_ITERATIONS);
    }

    /** Constructor nội bộ cho phép test dùng work factor nhỏ mà không đổi mặc định production. */
    Pbkdf2PasswordHasher(SecureRandom random, int iterations) {
        this.random = Objects.requireNonNull(random, "random");
        if (iterations < 1) {
            throw new IllegalArgumentException("iterations must be positive");
        }
        this.iterations = iterations;
    }

    /** Tạo salt mới, dẫn xuất hash và đóng gói version, work factor, salt cùng kết quả. */
    @Override
    public String hash(char[] password) {
        requirePassword(password);
        byte[] salt = new byte[SALT_BYTES];
        random.nextBytes(salt);
        byte[] derived = derive(password, salt, iterations);
        return VERSION + "$" + iterations + "$"
                + Base64.getEncoder().withoutPadding().encodeToString(salt) + "$"
                + Base64.getEncoder().withoutPadding().encodeToString(derived);
    }

    /** Phân tích chuỗi đã lưu và so sánh kết quả bằng MessageDigest.isEqual. */
    @Override
    public boolean verify(char[] password, String encodedHash) {
        if (!validPassword(password) || encodedHash == null) {
            return false;
        }
        try {
            String[] parts = encodedHash.split("\\$", -1);
            if (parts.length != 4 || !VERSION.equals(parts[0])) {
                return false;
            }
            int encodedIterations = Integer.parseInt(parts[1]);
            if (encodedIterations < 1 || encodedIterations > MAX_STORED_ITERATIONS) {
                return false;
            }
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expected = Base64.getDecoder().decode(parts[3]);
            if (salt.length < SALT_BYTES || expected.length != HASH_BITS / Byte.SIZE) {
                return false;
            }
            byte[] actual = derive(password, salt, encodedIterations);
            return MessageDigest.isEqual(expected, actual);
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    /** Dẫn xuất khóa bằng JCA và luôn xóa password khỏi PBEKeySpec sau khi dùng. */
    private static byte[] derive(char[] password, byte[] salt, int iterations) {
        PBEKeySpec specification = new PBEKeySpec(password, salt, iterations, HASH_BITS);
        try {
            return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(specification).getEncoded();
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("PBKDF2-HMAC-SHA256 is unavailable", exception);
        } finally {
            specification.clearPassword();
        }
    }

    /** Từ chối password rỗng hoặc quá dài trước khi thực hiện phép tính tốn CPU. */
    private static void requirePassword(char[] password) {
        if (!validPassword(password)) {
            throw new IllegalArgumentException("password length must be between 1 and 256 characters");
        }
    }

    /** Kiểm tra phạm vi password chung cho cả hash và verify. */
    private static boolean validPassword(char[] password) {
        return password != null && password.length >= 1 && password.length <= MAX_PASSWORD_CHARS;
    }
}
