package com.nsocry.protocol.compat;

import java.util.Arrays;

public final class RollingXorCipher {
    private final byte[] key;
    private int cursor;

    public RollingXorCipher(byte[] key) {
        if (key == null || key.length == 0) {
            throw new IllegalArgumentException("key must not be empty");
        }
        this.key = Arrays.copyOf(key, key.length);
    }

    public byte transform(byte value) {
        byte transformed = (byte) (value ^ key[cursor]);
        cursor = (cursor + 1) % key.length;
        return transformed;
    }

    public byte[] transform(byte[] values) {
        byte[] result = Arrays.copyOf(values, values.length);
        for (int index = 0; index < result.length; index++) {
            result[index] = transform(result[index]);
        }
        return result;
    }

    public int cursor() {
        return cursor;
    }
}
