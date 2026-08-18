package com.nsocry.protocol.compat;

import java.util.Arrays;

public final class LegacyKeyCodec {
    private LegacyKeyCodec() {
    }

    public static byte[] encodePayload(byte[] key) {
        requireValidKey(key);
        byte[] payload = new byte[key.length + 1];
        payload[0] = (byte) key.length;
        payload[1] = key[0];
        for (int index = 1; index < key.length; index++) {
            payload[index + 1] = (byte) (key[index] ^ key[index - 1]);
        }
        return payload;
    }

    public static byte[] decodePayload(byte[] payload) {
        if (payload == null || payload.length < 2) {
            throw new IllegalArgumentException("key payload is too short");
        }
        int keyLength = Byte.toUnsignedInt(payload[0]);
        if (keyLength == 0 || payload.length != keyLength + 1) {
            throw new IllegalArgumentException("key payload length mismatch");
        }
        byte[] key = Arrays.copyOfRange(payload, 1, payload.length);
        for (int index = 1; index < key.length; index++) {
            key[index] = (byte) (key[index] ^ key[index - 1]);
        }
        return key;
    }

    private static void requireValidKey(byte[] key) {
        if (key == null || key.length == 0 || key.length > 255) {
            throw new IllegalArgumentException("key length must be between 1 and 255 bytes");
        }
    }
}
