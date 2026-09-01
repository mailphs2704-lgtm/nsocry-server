package com.nsocry.session;

/** Kết quả quan sát được sau một bước handshake được xử lý thành công. */
public enum HandshakeEvent {
    KEY_ESTABLISHED,
    CLIENT_INFO_ACCEPTED,
    AUTHENTICATED,
    LOGIN_REJECTED
}
