package com.nsocry.network;

import java.net.Socket;

@FunctionalInterface
/** Ranh giới ứng dụng được TcpServer gọi một lần cho mỗi socket vừa chấp nhận. */
public interface SessionConnectionHandler {
    /** Xử lý một socket đã cấu hình; TcpServer sẽ đóng socket sau khi method kết thúc. */
    void handle(Socket socket) throws Exception;
}
