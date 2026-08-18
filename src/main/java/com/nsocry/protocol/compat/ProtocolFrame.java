package com.nsocry.protocol.compat;

import java.util.Arrays;

/** Giá trị bất biến gồm command và payload; dữ liệu payload luôn được sao chép phòng vệ. */
public record ProtocolFrame(byte command, byte[] payload) {
    public ProtocolFrame {
        payload = Arrays.copyOf(payload, payload.length);
    }

    @Override
    public byte[] payload() {
        return Arrays.copyOf(payload, payload.length);
    }
}
