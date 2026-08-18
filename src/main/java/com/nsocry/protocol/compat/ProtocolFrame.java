package com.nsocry.protocol.compat;

import java.util.Arrays;

/** Immutable command and payload value; payload bytes are defensively copied. */
public record ProtocolFrame(byte command, byte[] payload) {
    public ProtocolFrame {
        payload = Arrays.copyOf(payload, payload.length);
    }

    @Override
    public byte[] payload() {
        return Arrays.copyOf(payload, payload.length);
    }
}
