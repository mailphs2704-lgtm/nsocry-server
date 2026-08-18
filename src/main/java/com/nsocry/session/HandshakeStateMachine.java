package com.nsocry.session;

import java.util.concurrent.atomic.AtomicReference;

/** Thread-safe state machine that rejects illegal or out-of-order session transitions. */
public final class HandshakeStateMachine {
    private final AtomicReference<SessionPhase> phase = new AtomicReference<>(SessionPhase.CONNECTED);

    public SessionPhase phase() {
        return phase.get();
    }

    public void keySent() {
        transition(SessionPhase.CONNECTED, SessionPhase.KEY_SENT);
    }

    public void clientInfoReceived() {
        transition(SessionPhase.KEY_SENT, SessionPhase.CLIENT_INFO_RECEIVED);
    }

    public void loginStarted() {
        transition(SessionPhase.CLIENT_INFO_RECEIVED, SessionPhase.LOGIN_PENDING);
    }

    public void loginSucceeded() {
        transition(SessionPhase.LOGIN_PENDING, SessionPhase.AUTHENTICATED);
    }

    public void loginRejected() {
        transition(SessionPhase.LOGIN_PENDING, SessionPhase.CLIENT_INFO_RECEIVED);
    }

    public boolean close() {
        return phase.getAndSet(SessionPhase.CLOSED) != SessionPhase.CLOSED;
    }

    public boolean isAuthenticated() {
        return phase.get() == SessionPhase.AUTHENTICATED;
    }

    public boolean isClosed() {
        return phase.get() == SessionPhase.CLOSED;
    }

    private void transition(SessionPhase expected, SessionPhase requested) {
        SessionPhase actual = phase.get();
        if (actual == SessionPhase.CLOSED || !phase.compareAndSet(expected, requested)) {
            throw new ProtocolStateException(actual, expected, requested);
        }
    }
}
