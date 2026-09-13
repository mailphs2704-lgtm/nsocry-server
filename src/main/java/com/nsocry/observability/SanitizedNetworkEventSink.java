package com.nsocry.observability;

import com.nsocry.network.NetworkEventSink;
import java.io.IOException;
import java.net.SocketAddress;
import java.util.Objects;
import java.util.function.Consumer;

/** Chuyển sự kiện mạng thành dòng log tối thiểu, không chứa message hoặc stack trace từ client. */
public final class SanitizedNetworkEventSink implements NetworkEventSink {
    private final Consumer<String> output;

    /** Tạo event sink ghi các dòng đã làm sạch đến đích được cung cấp. */
    public SanitizedNetworkEventSink(Consumer<String> output) {
        this.output = Objects.requireNonNull(output, "output");
    }

    /** Ghi loại lỗi phiên và địa chỉ từ xa, không ghi exception message. */
    @Override
    public void sessionFailed(SocketAddress remoteAddress, Exception failure) {
        output.accept("SESSION_FAILED remote=" + safeAddress(remoteAddress)
                + " type=" + safeType(failure));
    }

    /** Ghi sự kiện từ chối do đạt giới hạn phiên. */
    @Override
    public void sessionRejected(SocketAddress remoteAddress) {
        output.accept("SESSION_REJECTED remote=" + safeAddress(remoteAddress));
    }

    /** Ghi loại lỗi accept, không ghi message hoặc dữ liệu nội bộ của exception. */
    @Override
    public void acceptFailed(IOException failure) {
        output.accept("ACCEPT_FAILED type=" + safeType(failure));
    }

    /** Chuyển địa chỉ null thành giá trị cố định để event sink không phát sinh lỗi phụ. */
    private static String safeAddress(SocketAddress address) {
        return address == null ? "unknown" : address.toString();
    }

    /** Chỉ lấy tên class của exception, tránh làm lộ message có dữ liệu không tin cậy. */
    private static String safeType(Exception failure) {
        return failure == null ? "UnknownException" : failure.getClass().getSimpleName();
    }
}
