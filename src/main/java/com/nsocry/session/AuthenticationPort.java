package com.nsocry.session;

@FunctionalInterface
/** Ranh giới không phụ thuộc lưu trữ dùng để xác thực yêu cầu đăng nhập đã giải mã. */
public interface AuthenticationPort {
    /** Xác thực yêu cầu đăng nhập cùng thông tin client mà không làm lộ tầng lưu trữ. */
    AuthenticationDecision authenticate(LoginRequest request, ClientInfo clientInfo);
}
