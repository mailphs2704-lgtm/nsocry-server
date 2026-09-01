package com.nsocry.network;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nsocry.protocol.compat.LegacyFrameCodec;
import com.nsocry.protocol.compat.LegacyKeyCodec;
import com.nsocry.protocol.compat.ProtocolFrame;
import com.nsocry.protocol.compat.ProtocolLimits;
import com.nsocry.protocol.compat.RollingXorCipher;
import com.nsocry.session.AuthenticationDecision;
import com.nsocry.session.HandshakePayloadDecoder;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class LegacyHandshakeLoopbackTest {
    private static final byte[] KEY = new byte[] {12, 34, 56, 78, 90, 21, 43, 65};

    @Test
    void completesKeyClientInfoAndLoginOverLoopback() throws Exception {
        CountDownLatch authenticated = new CountDownLatch(1);
        LegacyHandshakeConnectionHandler handler = new LegacyHandshakeConnectionHandler(
                ProtocolLimits.DEFAULT,
                () -> KEY.clone(),
                (login, client) -> {
                    assertEquals("cry-user", login.username());
                    assertEquals("2.17.0", login.version());
                    assertEquals("J2ME", client.platform());
                    authenticated.countDown();
                    return AuthenticationDecision.ACCEPTED;
                });
        TcpServerConfig config = new TcpServerConfig(
                new InetSocketAddress(InetAddress.getLoopbackAddress(), 0),
                8, 2, 2_000, Duration.ofSeconds(2));

        try (TcpServer server = new TcpServer(config, handler, new FailingEvents())) {
            server.start();
            try (Socket client = new Socket()) {
                client.connect(server.localAddress(), 1_000);
                client.setSoTimeout(2_000);
                client.getOutputStream().write(LegacyFrameCodec.encodeShortFrame(
                        LegacyFrameCodec.KEY_EXCHANGE_COMMAND, new byte[0], null));
                client.getOutputStream().flush();

                ProtocolFrame keyFrame = readPlainShortFrame(client.getInputStream());
                byte[] receivedKey = LegacyKeyCodec.decodePayload(keyFrame.payload());
                RollingXorCipher outbound = new RollingXorCipher(receivedKey);
                client.getOutputStream().write(LegacyFrameCodec.encodeShortFrame(
                        HandshakePayloadDecoder.NOT_LOGIN_ENVELOPE, clientInfoPayload(), outbound));
                client.getOutputStream().write(LegacyFrameCodec.encodeShortFrame(
                        HandshakePayloadDecoder.NOT_LOGIN_ENVELOPE, loginPayload(), outbound));
                client.getOutputStream().flush();

                assertTrue(authenticated.await(2, TimeUnit.SECONDS));
            }
        }
    }

    private static ProtocolFrame readPlainShortFrame(InputStream input) throws Exception {
        DataInputStream data = new DataInputStream(input);
        byte command = data.readByte();
        byte[] payload = data.readNBytes(data.readUnsignedShort());
        return new ProtocolFrame(command, payload);
    }

    private static byte[] clientInfoPayload() throws Exception {
        java.io.ByteArrayOutputStream bytes = new java.io.ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(bytes);
        output.writeByte(HandshakePayloadDecoder.CLIENT_INFO_COMMAND);
        output.writeByte(1);
        output.writeByte(2);
        output.writeBoolean(false);
        output.writeInt(240);
        output.writeInt(320);
        output.writeBoolean(false);
        output.writeBoolean(false);
        output.writeUTF("J2ME");
        output.writeByte(0);
        output.writeInt(0);
        output.writeByte(0);
        output.writeInt(0);
        output.writeUTF("cry-loopback");
        return bytes.toByteArray();
    }

    private static byte[] loginPayload() throws Exception {
        java.io.ByteArrayOutputStream bytes = new java.io.ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(bytes);
        output.writeByte(HandshakePayloadDecoder.LOGIN_COMMAND);
        output.writeUTF("cry-user");
        output.writeUTF("secret-not-logged");
        output.writeUTF("2.17.0");
        output.writeUTF("");
        output.writeUTF("");
        output.writeUTF("loopback-token");
        output.writeByte(0);
        return bytes.toByteArray();
    }

    private static final class FailingEvents implements NetworkEventSink {
        @Override
        public void sessionFailed(java.net.SocketAddress remoteAddress, Exception failure) {
            throw new AssertionError(failure);
        }

        @Override
        public void sessionRejected(java.net.SocketAddress remoteAddress) {
            throw new AssertionError("session unexpectedly rejected");
        }

        @Override
        public void acceptFailed(java.io.IOException failure) {
            throw new AssertionError(failure);
        }
    }
}
