package com.nsocry.session;

import com.nsocry.protocol.compat.ProtocolFrame;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

/** Strictly decodes CLIENT_INFO and LOGIN nested payloads in their verified wire order. */
public final class HandshakePayloadDecoder {
    public static final byte NOT_LOGIN_ENVELOPE = -29;
    public static final byte CLIENT_INFO_COMMAND = -125;
    public static final byte LOGIN_COMMAND = -127;

    private HandshakePayloadDecoder() {
    }

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

    private static void requireFullyConsumed(DataInputStream input) throws IOException {
        if (input.available() != 0) {
            throw new IOException("unexpected trailing handshake payload bytes");
        }
    }
}
