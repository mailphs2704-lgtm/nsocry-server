package com.nsocry.session;

import java.util.Objects;

/** Dữ liệu đăng nhập đã giải mã; biểu diễn chuỗi luôn che mật khẩu và client token. */
public final class LoginRequest {
    private final String username;
    private final String password;
    private final String version;
    private final String reservedUtf1;
    private final String reservedUtf2;
    private final String clientToken;
    private final byte serverId;

    /** Tạo yêu cầu đăng nhập; mọi trường bắt buộc phải khác null. */
    public LoginRequest(
            String username,
            String password,
            String version,
            String reservedUtf1,
            String reservedUtf2,
            String clientToken,
            byte serverId) {
        this.username = Objects.requireNonNull(username, "username");
        this.password = Objects.requireNonNull(password, "password");
        this.version = Objects.requireNonNull(version, "version");
        this.reservedUtf1 = Objects.requireNonNull(reservedUtf1, "reservedUtf1");
        this.reservedUtf2 = Objects.requireNonNull(reservedUtf2, "reservedUtf2");
        this.clientToken = Objects.requireNonNull(clientToken, "clientToken");
        this.serverId = serverId;
    }

    /** Trả tên đăng nhập cho port xác thực. */
    public String username() {
        return username;
    }

    /** Trả mật khẩu cho port xác thực; không được ghi log giá trị này. */
    public String password() {
        return password;
    }

    /** Trả phiên bản client đã khai báo. */
    public String version() {
        return version;
    }

    /** Trả trường UTF dự phòng thứ nhất để giữ tương thích wire. */
    public String reservedUtf1() {
        return reservedUtf1;
    }

    /** Trả trường UTF dự phòng thứ hai để giữ tương thích wire. */
    public String reservedUtf2() {
        return reservedUtf2;
    }

    /** Trả token client cho port xác thực; không được ghi log giá trị này. */
    public String clientToken() {
        return clientToken;
    }

    /** Trả mã server mà client yêu cầu. */
    public byte serverId() {
        return serverId;
    }

    @Override
    /** Trả mô tả an toàn, luôn che mật khẩu và token. */
    public String toString() {
        return "LoginRequest[username=" + username
                + ", password=<redacted>"
                + ", version=" + version
                + ", reservedUtf1=" + reservedUtf1
                + ", reservedUtf2=" + reservedUtf2
                + ", clientToken=<redacted>"
                + ", serverId=" + serverId + "]";
    }
}
