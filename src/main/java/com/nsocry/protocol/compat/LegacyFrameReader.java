package com.nsocry.protocol.compat;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public final class LegacyFrameReader {
    private final DataInputStream input;
    private final ProtocolLimits limits;

    public LegacyFrameReader(InputStream input, ProtocolLimits limits) {
        this.input = new DataInputStream(Objects.requireNonNull(input, "input"));
        this.limits = Objects.requireNonNull(limits, "limits");
    }

    public ProtocolFrame readUnencryptedShortFrame() throws IOException {
        byte command = input.readByte();
        int length = input.readUnsignedShort();
        limits.requireAllowed(length, false);
        return new ProtocolFrame(command, readPayload(length, null));
    }

    public ProtocolFrame readEncryptedFrame(RollingXorCipher cipher, boolean allowFullSize) throws IOException {
        Objects.requireNonNull(cipher, "cipher");
        byte command = cipher.transform(input.readByte());
        boolean fullSize = command == LegacyFrameCodec.FULL_SIZE_COMMAND;
        if (fullSize && !allowFullSize) {
            throw new IOException("full-size frame is not allowed in this direction");
        }
        int length = fullSize ? readEncryptedInt(cipher) : readEncryptedUnsignedShort(cipher);
        try {
            limits.requireAllowed(length, fullSize);
        } catch (IllegalArgumentException exception) {
            throw new IOException("invalid frame length", exception);
        }
        return new ProtocolFrame(command, readPayload(length, cipher));
    }

    private int readEncryptedUnsignedShort(RollingXorCipher cipher) throws IOException {
        int high = Byte.toUnsignedInt(cipher.transform(input.readByte()));
        int low = Byte.toUnsignedInt(cipher.transform(input.readByte()));
        return (high << 8) | low;
    }

    private int readEncryptedInt(RollingXorCipher cipher) throws IOException {
        int value = 0;
        for (int index = 0; index < Integer.BYTES; index++) {
            value = (value << 8) | Byte.toUnsignedInt(cipher.transform(input.readByte()));
        }
        return value;
    }

    private byte[] readPayload(int length, RollingXorCipher cipher) throws IOException {
        byte[] payload = new byte[length];
        input.readFully(payload);
        if (cipher != null) {
            for (int index = 0; index < payload.length; index++) {
                payload[index] = cipher.transform(payload[index]);
            }
        }
        return payload;
    }
}
