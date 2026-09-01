package com.nsocry.protocol.compat;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/** Mã hóa và giải mã toàn bộ frame của client cũ, phục vụ fixture và kiểm thử tương thích. */
public final class LegacyFrameCodec {
    public static final byte KEY_EXCHANGE_COMMAND = -27;
    public static final byte FULL_SIZE_COMMAND = -32;

    private LegacyFrameCodec() {
    }

    /** Tạo frame ngắn, tùy chọn áp dụng cipher và từ chối payload vượt giới hạn unsigned-short. */
    public static byte[] encodeShortFrame(byte command, byte[] payload, RollingXorCipher cipher) {
        if (payload.length > 0xFFFF) {
            throw new IllegalArgumentException("short frame payload exceeds 65535 bytes");
        }
        ByteBuffer buffer = ByteBuffer.allocate(3 + payload.length).order(ByteOrder.BIG_ENDIAN);
        buffer.put(command).putShort((short) payload.length).put(payload);
        byte[] frame = buffer.array();
        return cipher == null ? frame : cipher.transform(frame);
    }

    /** Tạo frame kích thước đầy đủ dùng command đặc biệt và trường độ dài int. */
    public static byte[] encodeFullSizeFrame(byte[] payload, RollingXorCipher cipher) {
        ByteBuffer buffer = ByteBuffer.allocate(5 + payload.length).order(ByteOrder.BIG_ENDIAN);
        buffer.put(FULL_SIZE_COMMAND).putInt(payload.length).put(payload);
        byte[] frame = buffer.array();
        return cipher == null ? frame : cipher.transform(frame);
    }

    /** Giải mã một frame hoàn chỉnh trong bộ nhớ và kiểm tra độ dài payload khớp header. */
    public static ProtocolFrame decodeFrame(byte[] wire, RollingXorCipher cipher) {
        if (wire == null || wire.length < 3) {
            throw new IllegalArgumentException("frame is too short");
        }
        byte[] plain = cipher == null ? wire.clone() : cipher.transform(wire);
        ByteBuffer buffer = ByteBuffer.wrap(plain).order(ByteOrder.BIG_ENDIAN);
        byte command = buffer.get();
        int payloadLength;
        if (command == FULL_SIZE_COMMAND) {
            if (plain.length < 5) {
                throw new IllegalArgumentException("full-size frame is too short");
            }
            payloadLength = buffer.getInt();
        } else {
            payloadLength = Short.toUnsignedInt(buffer.getShort());
        }
        if (payloadLength < 0 || buffer.remaining() != payloadLength) {
            throw new IllegalArgumentException("frame payload length mismatch");
        }
        byte[] payload = new byte[payloadLength];
        buffer.get(payload);
        return new ProtocolFrame(command, payload);
    }
}
