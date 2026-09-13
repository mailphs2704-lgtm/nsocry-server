package com.nsocry.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class HandshakeStateMachineTest {
    @Test
    void acceptsVerifiedHandshakeOrder() {
        HandshakeStateMachine state = new HandshakeStateMachine();

        state.keySent();
        state.clientInfoReceived();
        state.loginStarted();
        state.loginSucceeded();

        assertEquals(SessionPhase.AUTHENTICATED, state.phase());
        assertTrue(state.isAuthenticated());
    }

    @Test
    void rejectedLoginCanRetryWithoutRepeatingClientInfo() {
        HandshakeStateMachine state = readyToLogin();

        state.loginStarted();
        state.loginRejected();
        state.loginStarted();
        state.loginSucceeded();

        assertTrue(state.isAuthenticated());
    }

    @Test
    void rejectsOutOfOrderMessages() {
        HandshakeStateMachine state = new HandshakeStateMachine();

        assertThrows(ProtocolStateException.class, state::clientInfoReceived);
        assertEquals(SessionPhase.CONNECTED, state.phase());
    }

    @Test
    void closeIsIdempotentAndTerminal() {
        HandshakeStateMachine state = readyToLogin();

        assertTrue(state.close());
        assertFalse(state.close());
        assertTrue(state.isClosed());
        assertThrows(ProtocolStateException.class, state::loginStarted);
    }

    private static HandshakeStateMachine readyToLogin() {
        HandshakeStateMachine state = new HandshakeStateMachine();
        state.keySent();
        state.clientInfoReceived();
        return state;
    }
}
