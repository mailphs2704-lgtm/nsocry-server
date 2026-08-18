package com.nsocry.network;

import java.io.IOException;
import java.net.SocketAddress;

public interface NetworkEventSink {
    void sessionFailed(SocketAddress remoteAddress, Exception failure);

    void sessionRejected(SocketAddress remoteAddress);

    void acceptFailed(IOException failure);
}
