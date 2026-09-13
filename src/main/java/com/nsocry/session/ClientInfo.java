package com.nsocry.session;

/** Thông tin khả năng và tương thích của client đã được giải mã trước khi đăng nhập. */
public record ClientInfo(
        byte clientType,
        byte zoomLevel,
        boolean gps,
        int width,
        int height,
        boolean qwerty,
        boolean touch,
        String platform,
        byte wireField9,
        int wireField10,
        byte languageId,
        int provider,
        String agent) {
}
