package com.nsocry.protocol.compat;

import java.util.Arrays;

/** Giá trị bất biến gồm command và payload; dữ liệu payload luôn được sao chép phòng vệ. */
public record ProtocolFrame(byte command, byte[] payload) {
    /** Sao chép payload khi tạo record để ngăn bên gọi thay đổi dữ liệu nội bộ. */
    public ProtocolFrame {
        payload = Arrays.copyOf(payload, payload.length);
    }

    @Override
    /** Trả bản sao payload nhằm giữ tính bất biến của frame. */
    public byte[] payload() {
        return Arrays.copyOf(payload, payload.length);
    }
}
