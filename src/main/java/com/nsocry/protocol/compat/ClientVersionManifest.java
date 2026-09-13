package com.nsocry.protocol.compat;

/** Bốn phiên bản dữ liệu mà client V7 so sánh trước khi xác nhận CLIENT_OK. */
public record ClientVersionManifest(
        byte dataVersion,
        byte mapVersion,
        byte skillVersion,
        byte itemVersion) {
}
