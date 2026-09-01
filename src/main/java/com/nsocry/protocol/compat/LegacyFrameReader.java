package com.nsocry.protocol.compat;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/** Đọc một frame có giới hạn từ stream và duy trì liên tục con trỏ mã hóa chiều vào. */
public final class LegacyFrameReader {
    private final DataInputStream input;
    private final ProtocolLimits limits;

    /** Khởi tạo bộ đọc từ stream đầu vào với giới hạn cấp phát bắt buộc. */
    public LegacyFrameReader(InputStream input, ProtocolLimits limits) {
        this.input = new DataInputStream(Objects.requireNonNull(input, "input"));
        this.limits = Objects.requireNonNull(limits, "limits");
    }

    /** Đọc frame ngắn chưa mã hóa, hiện dùng cho trigger trao đổi khóa đầu phiên. */
    public ProtocolFrame readUnencryptedShortFrame() throws IOException {
        byte command = input.readByte();
        int length = input.readUnsignedShort();
        limits.requireAllowed(length, false);
        return new ProtocolFrame(command, readPayload(length, null));
    }

    /** Đọc frame đã mã hóa, duy trì con trỏ cipher và có thể cấm frame full-size từ client. */
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

    /** Đọc trường độ dài unsigned-short qua cipher theo thứ tự big-endian. */
    private int readEncryptedUnsignedShort(RollingXorCipher cipher) throws IOException {
        int high = Byte.toUnsignedInt(cipher.transform(input.readByte()));
        int low = Byte.toUnsignedInt(cipher.transform(input.readByte()));
        return (high << 8) | low;
    }

    /** Đọc trường độ dài int qua cipher theo thứ tự big-endian. */
    private int readEncryptedInt(RollingXorCipher cipher) throws IOException {
        int value = 0;
        for (int index = 0; index < Integer.BYTES; index++) {
            value = (value << 8) | Byte.toUnsignedInt(cipher.transform(input.readByte()));
        }
        return value;
    }

    /** Đọc đủ payload, sau đó giải mã tại chỗ khi cipher đã được kích hoạt. */
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
