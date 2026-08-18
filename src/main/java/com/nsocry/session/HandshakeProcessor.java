package com.nsocry.session;

import com.nsocry.protocol.compat.ProtocolFrame;
import java.io.IOException;
import java.util.Objects;

/** Điều phối thông điệp handshake theo trạng thái phiên mà không phụ thuộc cơ sở dữ liệu. */
public final class HandshakeProcessor {
    private final LegacySessionTransport transport;
    private ClientInfo clientInfo;

    public HandshakeProcessor(LegacySessionTransport transport) {
        this.transport = Objects.requireNonNull(transport, "transport");
    }

    public HandshakeEvent begin(byte[] key) throws IOException {
        transport.beginHandshake(key);
        return HandshakeEvent.KEY_ESTABLISHED;
    }

    public HandshakeEvent receiveNext(AuthenticationPort authentication) throws IOException {
        Objects.requireNonNull(authentication, "authentication");
        ProtocolFrame frame = transport.readClientFrame();
        return switch (transport.state().phase()) {
            case KEY_SENT -> acceptClientInfo(frame);
            case CLIENT_INFO_RECEIVED -> authenticate(frame, authentication);
            default -> throw new IOException(
                    "handshake message is not allowed in phase " + transport.state().phase());
        };
    }

    public ClientInfo clientInfo() {
        return clientInfo;
    }

    private HandshakeEvent acceptClientInfo(ProtocolFrame frame) throws IOException {
        clientInfo = HandshakePayloadDecoder.decodeClientInfo(frame);
        transport.state().clientInfoReceived();
        return HandshakeEvent.CLIENT_INFO_ACCEPTED;
    }

    private HandshakeEvent authenticate(
            ProtocolFrame frame, AuthenticationPort authentication) throws IOException {
        LoginRequest request = HandshakePayloadDecoder.decodeLogin(frame);
        transport.state().loginStarted();
        AuthenticationDecision decision;
        try {
            decision = Objects.requireNonNull(
                    authentication.authenticate(request, clientInfo),
                    "authentication decision");
        } catch (RuntimeException exception) {
            transport.state().loginRejected();
            throw exception;
        }
        if (decision == AuthenticationDecision.ACCEPTED) {
            transport.state().loginSucceeded();
            return HandshakeEvent.AUTHENTICATED;
        }
        transport.state().loginRejected();
        return HandshakeEvent.LOGIN_REJECTED;
    }
}
