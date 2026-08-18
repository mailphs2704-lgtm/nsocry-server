package com.nsocry.session;

import com.nsocry.protocol.compat.ProtocolFrame;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

/** Giải mã nghiêm ngặt payload CLIENT_INFO và LOGIN theo đúng thứ tự byte đã xác minh. */
public final class HandshakePayloadDecoder {
    public static final byte NOT_LOGIN_ENVELOPE = -29;
    public static final byte CLIENT_INFO_COMMAND = -125;
    public static final byte LOGIN_COMMAND = -127;

    private HandshakePayloadDecoder() {
    }

    /** Giải mã CLIENT_INFO theo thứ tự wire và từ chối mọi byte dư. */
    public static ClientInfo decodeClientInfo(ProtocolFrame frame) throws IOException {
        DataInputStream input = nestedPayload(frame, CLIENT_INFO_COMMAND);
        ClientInfo info = new ClientInfo(
                input.readByte(),
                input.readByte(),
                input.readBoolean(),
                input.readInt(),
                input.readInt(),
                input.readBoolean(),
                input.readBoolean(),
                input.readUTF(),
                input.readByte(),
                input.readInt(),
                input.readByte(),
                input.readInt(),
                input.readUTF());
        requireFullyConsumed(input);
        return info;
    }

    /** Giải mã LOGIN theo thứ tự wire; dữ liệu bí mật chỉ được giữ trong LoginRequest. */
    public static LoginRequest decodeLogin(ProtocolFrame frame) throws IOException {
        DataInputStream input = nestedPayload(frame, LOGIN_COMMAND);
        LoginRequest request = new LoginRequest(
                input.readUTF().trim(),
                input.readUTF().trim(),
                input.readUTF().trim(),
                input.readUTF(),
                input.readUTF(),
                input.readUTF().trim(),
                input.readByte());
        requireFullyConsumed(input);
        return request;
    }

    /** Mở payload lồng và xác minh envelope cùng command con được mong đợi. */
    private static DataInputStream nestedPayload(ProtocolFrame frame, byte expectedNested) throws IOException {
        if (frame.command() != NOT_LOGIN_ENVELOPE) {
            throw new IOException("unexpected handshake envelope");
        }
        DataInputStream input = new DataInputStream(new ByteArrayInputStream(frame.payload()));
        byte nested = input.readByte();
        if (nested != expectedNested) {
            throw new IOException("unexpected nested handshake command");
        }
        return input;
    }

    /** Bảo đảm decoder đã tiêu thụ toàn bộ payload, ngăn dữ liệu đuôi không xác định. */
    private static void requireFullyConsumed(DataInputStream input) throws IOException {
        if (input.available() != 0) {
            throw new IOException("unexpected trailing handshake payload bytes");
        }
    }
}
