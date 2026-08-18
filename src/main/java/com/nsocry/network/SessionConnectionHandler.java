package com.nsocry.network;

import java.net.Socket;

@FunctionalInterface
/** Application boundary invoked once for every socket accepted by {@link TcpServer}. */
public interface SessionConnectionHandler {
    void handle(Socket socket) throws Exception;
}
