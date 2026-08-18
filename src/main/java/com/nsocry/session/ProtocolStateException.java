package com.nsocry.session;

public final class ProtocolStateException extends IllegalStateException {
    public ProtocolStateException(SessionPhase actual, SessionPhase expected, SessionPhase requested) {
        super("cannot transition from " + actual + " to " + requested + "; expected " + expected);
    }
}
