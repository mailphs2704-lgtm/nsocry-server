package com.nsocry.network;

import java.net.Socket;

@FunctionalInterface
/** Ranh giới ứng dụng được TcpServer gọi một lần cho mỗi socket vừa chấp nhận. */
public interface SessionConnectionHandler {
    void handle(Socket socket) throws Exception;
}
