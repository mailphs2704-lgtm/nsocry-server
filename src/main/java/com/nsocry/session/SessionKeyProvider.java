package com.nsocry.session;

@FunctionalInterface
/** Supplies a new per-session key without coupling the handshake to a generation policy. */
public interface SessionKeyProvider {
    byte[] createKey();
}
