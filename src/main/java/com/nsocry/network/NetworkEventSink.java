package com.nsocry.network;

import java.io.IOException;
import java.net.SocketAddress;

/** Nhận sự kiện lỗi đã được làm sạch từ listener và phiên mà không ràng buộc tầng mạng với công cụ log. */
public interface NetworkEventSink {
    void sessionFailed(SocketAddress remoteAddress, Exception failure);

    void sessionRejected(SocketAddress remoteAddress);

    void acceptFailed(IOException failure);
}
