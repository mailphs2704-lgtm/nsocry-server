package com.nsocry.session;

@FunctionalInterface
public interface AuthenticationPort {
    AuthenticationDecision authenticate(LoginRequest request, ClientInfo clientInfo);
}
