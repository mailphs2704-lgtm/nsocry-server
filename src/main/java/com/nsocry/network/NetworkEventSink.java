package com.nsocry.network;

import java.io.IOException;
import java.net.SocketAddress;

/** Nhận sự kiện lỗi đã được làm sạch từ listener và phiên mà không ràng buộc tầng mạng với công cụ log. */
public interface NetworkEventSink {
    /** Nhận lỗi của một phiên cùng địa chỉ từ xa; implementation phải tránh log bí mật. */
    void sessionFailed(SocketAddress remoteAddress, Exception failure);

    /** Nhận sự kiện socket bị từ chối khi máy chủ đã đạt giới hạn phiên. */
    void sessionRejected(SocketAddress remoteAddress);

    /** Nhận lỗi nghiêm trọng từ vòng lặp accept khi listener vẫn được kỳ vọng hoạt động. */
    void acceptFailed(IOException failure);
}
