package com.nsocry.session;

/** Ordered lifecycle phases for the connection and login bootstrap. */
public enum SessionPhase {
    CONNECTED,
    KEY_SENT,
    CLIENT_INFO_RECEIVED,
    LOGIN_PENDING,
    AUTHENTICATED,
    CLOSED
}
