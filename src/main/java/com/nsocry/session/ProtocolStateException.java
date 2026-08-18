package com.nsocry.session;

/** Reports an attempted handshake transition that is invalid for the current phase. */
public final class ProtocolStateException extends IllegalStateException {
    public ProtocolStateException(SessionPhase actual, SessionPhase expected, SessionPhase requested) {
        super("cannot transition from " + actual + " to " + requested + "; expected " + expected);
    }
}
