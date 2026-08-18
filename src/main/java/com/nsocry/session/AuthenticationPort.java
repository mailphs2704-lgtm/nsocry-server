package com.nsocry.session;

@FunctionalInterface
/** Persistence-independent boundary for validating a decoded login request. */
public interface AuthenticationPort {
    AuthenticationDecision authenticate(LoginRequest request, ClientInfo clientInfo);
}
