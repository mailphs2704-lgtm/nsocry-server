package com.nsocry.protocol.compat;

import java.util.Arrays;

/** Phép biến đổi rolling XOR có trạng thái và con trỏ độc lập cho một chiều truyền. */
public final class RollingXorCipher {
    private final byte[] key;
    private int cursor;

    /** Tạo cipher với bản sao khóa riêng; khóa rỗng không hợp lệ. */
    public RollingXorCipher(byte[] key) {
        if (key == null || key.length == 0) {
            throw new IllegalArgumentException("key must not be empty");
        }
        this.key = Arrays.copyOf(key, key.length);
    }

    /** Biến đổi một byte và tăng con trỏ tuần hoàn. */
    public byte transform(byte value) {
        byte transformed = (byte) (value ^ key[cursor]);
        cursor = (cursor + 1) % key.length;
        return transformed;
    }

    /** Biến đổi một bản sao của mảng byte, không sửa mảng do bên gọi cung cấp. */
    public byte[] transform(byte[] values) {
        byte[] result = Arrays.copyOf(values, values.length);
        for (int index = 0; index < result.length; index++) {
            result[index] = transform(result[index]);
        }
        return result;
    }

    /** Trả vị trí con trỏ hiện tại để chẩn đoán và kiểm thử. */
    public int cursor() {
        return cursor;
    }
}
