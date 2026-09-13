package com.nsocry.protocol.compat;

/** Giới hạn cấp phát đã kiểm tra cho payload dạng ngắn và dạng đầy đủ. */
public record ProtocolLimits(int maxShortPayload, int maxFullPayload) {
    public static final ProtocolLimits DEFAULT = new ProtocolLimits(65_535, 1_048_576);

    /** Kiểm tra giới hạn frame ngắn và full-size ngay khi tạo cấu hình. */
    public ProtocolLimits {
        if (maxShortPayload < 0 || maxShortPayload > 65_535) {
            throw new IllegalArgumentException("maxShortPayload must be between 0 and 65535");
        }
        if (maxFullPayload < maxShortPayload) {
            throw new IllegalArgumentException("maxFullPayload must be at least maxShortPayload");
        }
    }

    /** Từ chối độ dài âm hoặc vượt giới hạn tương ứng với loại frame. */
    public void requireAllowed(int length, boolean fullSize) {
        int limit = fullSize ? maxFullPayload : maxShortPayload;
        if (length < 0 || length > limit) {
            throw new IllegalArgumentException("payload length " + length + " exceeds limit " + limit);
        }
    }
}
