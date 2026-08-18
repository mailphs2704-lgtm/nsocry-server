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

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }

    public String version() {
        return version;
    }

    public String reservedUtf1() {
        return reservedUtf1;
    }

    public String reservedUtf2() {
        return reservedUtf2;
    }

    public String clientToken() {
        return clientToken;
    }

    public byte serverId() {
        return serverId;
    }

    @Override
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
