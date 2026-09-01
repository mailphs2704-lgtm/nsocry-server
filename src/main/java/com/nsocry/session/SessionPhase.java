package com.nsocry.session;

/** Các giai đoạn theo thứ tự của vòng đời kết nối và khởi tạo đăng nhập. */
public enum SessionPhase {
    CONNECTED,
    KEY_SENT,
    CLIENT_INFO_RECEIVED,
    LOGIN_PENDING,
    AUTHENTICATED,
    CLOSED
}
