package com.nsocry.session;

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
