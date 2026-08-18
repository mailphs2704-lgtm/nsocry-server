package com.nsocry.session;

/** Observable result of one successfully processed handshake step. */
public enum HandshakeEvent {
    KEY_ESTABLISHED,
    CLIENT_INFO_ACCEPTED,
    AUTHENTICATED,
    LOGIN_REJECTED
}
