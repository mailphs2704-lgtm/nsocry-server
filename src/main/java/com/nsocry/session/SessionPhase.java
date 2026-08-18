package com.nsocry.session;

public enum SessionPhase {
    CONNECTED,
    KEY_SENT,
    CLIENT_INFO_RECEIVED,
    LOGIN_PENDING,
    AUTHENTICATED,
    CLOSED
}
