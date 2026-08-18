package com.nsocry.session;

@FunctionalInterface
/** Ranh giới không phụ thuộc lưu trữ dùng để xác thực yêu cầu đăng nhập đã giải mã. */
public interface AuthenticationPort {
    AuthenticationDecision authenticate(LoginRequest request, ClientInfo clientInfo);
}
