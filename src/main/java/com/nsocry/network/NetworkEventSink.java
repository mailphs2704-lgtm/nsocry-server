package com.nsocry.network;

import java.io.IOException;
import java.net.SocketAddress;

/** Receives sanitized listener and session lifecycle failures without coupling networking to a logger. */
public interface NetworkEventSink {
    void sessionFailed(SocketAddress remoteAddress, Exception failure);

    void sessionRejected(SocketAddress remoteAddress);

    void acceptFailed(IOException failure);
}
