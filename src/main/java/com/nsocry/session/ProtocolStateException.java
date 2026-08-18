package com.nsocry.session;

/** Mô tả yêu cầu chuyển trạng thái handshake không hợp lệ tại giai đoạn hiện tại. */
public final class ProtocolStateException extends IllegalStateException {
    public ProtocolStateException(SessionPhase actual, SessionPhase expected, SessionPhase requested) {
        super("cannot transition from " + actual + " to " + requested + "; expected " + expected);
    }
}
