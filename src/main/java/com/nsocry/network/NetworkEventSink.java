package com.nsocry.network;

import java.io.IOException;
import java.net.SocketAddress;
import com.nsocry.session.HandshakeEvent;

/** Nhận sự kiện lỗi đã được làm sạch từ listener và phiên mà không ràng buộc tầng mạng với công cụ log. */
public interface NetworkEventSink {
    /** Nhận lỗi của một phiên cùng địa chỉ từ xa; implementation phải tránh log bí mật. */
    void sessionFailed(SocketAddress remoteAddress, Exception failure);

    /** Ghi kết quả terminal của handshake mà không chứa username hoặc payload. */
    default void handshakeCompleted(SocketAddress remoteAddress, HandshakeEvent outcome) {
        // Optional observability hook; implementations cũ không bắt buộc ghi log.
    }

    /** Nhận sự kiện socket bị từ chối khi máy chủ đã đạt giới hạn phiên. */
    void sessionRejected(SocketAddress remoteAddress);

    /** Nhận lỗi nghiêm trọng từ vòng lặp accept khi listener vẫn được kỳ vọng hoạt động. */
    void acceptFailed(IOException failure);
}
