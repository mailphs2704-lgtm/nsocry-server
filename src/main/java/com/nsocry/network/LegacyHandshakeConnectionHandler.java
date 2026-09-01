package com.nsocry.network;

import com.nsocry.protocol.compat.ProtocolLimits;
import com.nsocry.session.AuthenticationPort;
import com.nsocry.session.HandshakeEvent;
import com.nsocry.session.HandshakeProcessor;
import com.nsocry.session.LegacySessionTransport;
import com.nsocry.session.SessionKeyProvider;
import java.net.Socket;
import java.util.Objects;

/** Điều phối một socket đã kết nối qua trao đổi khóa, đọc thông tin client và xác thực đăng nhập. */
public final class LegacyHandshakeConnectionHandler implements SessionConnectionHandler {
    private final ProtocolLimits limits;
    private final SessionKeyProvider keys;
    private final AuthenticationPort authentication;

    /** Tạo handler bằng giới hạn protocol, nguồn khóa phiên và port xác thực bắt buộc. */
    public LegacyHandshakeConnectionHandler(
            ProtocolLimits limits,
            SessionKeyProvider keys,
            AuthenticationPort authentication) {
        this.limits = Objects.requireNonNull(limits, "limits");
        this.keys = Objects.requireNonNull(keys, "keys");
        this.authentication = Objects.requireNonNull(authentication, "authentication");
    }

    @Override
    /** Chạy trọn handshake cho một socket đến kết quả xác thực hoặc từ chối. */
    public void handle(Socket socket) throws Exception {
        Objects.requireNonNull(socket, "socket");
        LegacySessionTransport transport = new LegacySessionTransport(
                socket.getInputStream(), socket.getOutputStream(), limits, socket);
        HandshakeProcessor processor = new HandshakeProcessor(transport);

        processor.begin(keys.createKey());
        require(HandshakeEvent.CLIENT_INFO_ACCEPTED, processor.receiveNext(authentication));
        HandshakeEvent login = processor.receiveNext(authentication);
        if (login != HandshakeEvent.AUTHENTICATED && login != HandshakeEvent.LOGIN_REJECTED) {
            throw new IllegalStateException("unexpected terminal handshake event " + login);
        }
    }

    /** Xác minh sự kiện thực tế đúng với bước handshake bắt buộc. */
    private static void require(HandshakeEvent expected, HandshakeEvent actual) {
        if (actual != expected) {
            throw new IllegalStateException(
                    "expected handshake event " + expected + " but received " + actual);
        }
    }
}
