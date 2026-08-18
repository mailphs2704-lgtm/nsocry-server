package com.nsocry.session;

import com.nsocry.protocol.compat.ProtocolFrame;
import java.io.IOException;
import java.util.Objects;

/** Điều phối thông điệp handshake theo trạng thái phiên mà không phụ thuộc cơ sở dữ liệu. */
public final class HandshakeProcessor {
    private final LegacySessionTransport transport;
    private ClientInfo clientInfo;

    /** Tạo processor điều phối trên một transport duy nhất. */
    public HandshakeProcessor(LegacySessionTransport transport) {
        this.transport = Objects.requireNonNull(transport, "transport");
    }

    /** Bắt đầu trao đổi khóa và trả sự kiện KEY_ESTABLISHED khi thành công. */
    public HandshakeEvent begin(byte[] key) throws IOException {
        transport.beginHandshake(key);
        return HandshakeEvent.KEY_ESTABLISHED;
    }

    /** Đọc thông điệp tiếp theo và xử lý theo phase hiện tại của phiên. */
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

    /** Trả CLIENT_INFO đã chấp nhận; có thể null trước bước CLIENT_INFO. */
    public ClientInfo clientInfo() {
        return clientInfo;
    }

    /** Giải mã CLIENT_INFO hợp lệ và chuyển state machine sang phase tương ứng. */
    private HandshakeEvent acceptClientInfo(ProtocolFrame frame) throws IOException {
        clientInfo = HandshakePayloadDecoder.decodeClientInfo(frame);
        transport.state().clientInfoReceived();
        return HandshakeEvent.CLIENT_INFO_ACCEPTED;
    }

    /** Giải mã LOGIN, gọi port xác thực và ghi nhận kết quả vào state machine. */
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
