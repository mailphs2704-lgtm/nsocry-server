package com.nsocry.protocol.compat;

/** Validated allocation limits for short and full-size protocol payloads. */
public record ProtocolLimits(int maxShortPayload, int maxFullPayload) {
    public static final ProtocolLimits DEFAULT = new ProtocolLimits(65_535, 1_048_576);

    public ProtocolLimits {
        if (maxShortPayload < 0 || maxShortPayload > 65_535) {
            throw new IllegalArgumentException("maxShortPayload must be between 0 and 65535");
        }
        if (maxFullPayload < maxShortPayload) {
            throw new IllegalArgumentException("maxFullPayload must be at least maxShortPayload");
        }
    }

    public void requireAllowed(int length, boolean fullSize) {
        int limit = fullSize ? maxFullPayload : maxShortPayload;
        if (length < 0 || length > limit) {
            throw new IllegalArgumentException("payload length " + length + " exceeds limit " + limit);
        }
    }
}
