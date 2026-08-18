package com.nsocry.session;

@FunctionalInterface
public interface SessionKeyProvider {
    byte[] createKey();
}
