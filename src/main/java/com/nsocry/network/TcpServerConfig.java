package com.nsocry.network;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Objects;

/** Immutable validated TCP listener limits and timeout configuration. */
public record TcpServerConfig(
        InetSocketAddress bindAddress,
        int backlog,
        int maxSessions,
        int readTimeoutMillis,
        Duration shutdownTimeout) {

    public TcpServerConfig {
        Objects.requireNonNull(bindAddress, "bindAddress");
        Objects.requireNonNull(shutdownTimeout, "shutdownTimeout");
        if (backlog < 1) {
            throw new IllegalArgumentException("backlog must be positive");
        }
        if (maxSessions < 1) {
            throw new IllegalArgumentException("maxSessions must be positive");
        }
        if (readTimeoutMillis < 1) {
            throw new IllegalArgumentException("readTimeoutMillis must be positive");
        }
        if (shutdownTimeout.isNegative() || shutdownTimeout.isZero()) {
            throw new IllegalArgumentException("shutdownTimeout must be positive");
        }
    }
}
