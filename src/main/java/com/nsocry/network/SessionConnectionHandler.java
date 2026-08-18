package com.nsocry.network;

import java.net.Socket;

@FunctionalInterface
public interface SessionConnectionHandler {
    void handle(Socket socket) throws Exception;
}
