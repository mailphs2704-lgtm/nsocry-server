package com.nsocry.session;

import java.util.concurrent.atomic.AtomicReference;

/** Máy trạng thái an toàn luồng, từ chối chuyển trạng thái sai hoặc không đúng thứ tự. */
public final class HandshakeStateMachine {
    private final AtomicReference<SessionPhase> phase = new AtomicReference<>(SessionPhase.CONNECTED);

    /** Trả phase hiện tại bằng phép đọc nguyên tử. */
    public SessionPhase phase() {
        return phase.get();
    }

    /** Chuyển CONNECTED sang KEY_SENT sau khi gửi khóa. */
    public void keySent() {
        transition(SessionPhase.CONNECTED, SessionPhase.KEY_SENT);
    }

    /** Chuyển KEY_SENT sang CLIENT_INFO_RECEIVED. */
    public void clientInfoReceived() {
        transition(SessionPhase.KEY_SENT, SessionPhase.CLIENT_INFO_RECEIVED);
    }

    /** Chuyển CLIENT_INFO_RECEIVED sang LOGIN_PENDING. */
    public void loginStarted() {
        transition(SessionPhase.CLIENT_INFO_RECEIVED, SessionPhase.LOGIN_PENDING);
    }

    /** Chuyển LOGIN_PENDING sang AUTHENTICATED. */
    public void loginSucceeded() {
        transition(SessionPhase.LOGIN_PENDING, SessionPhase.AUTHENTICATED);
    }

    /** Đưa LOGIN_PENDING về CLIENT_INFO_RECEIVED để chính sách ngoài có thể xử lý thử lại. */
    public void loginRejected() {
        transition(SessionPhase.LOGIN_PENDING, SessionPhase.CLIENT_INFO_RECEIVED);
    }

    /** Đóng state machine theo cách idempotent; trả true nếu lời gọi này thực hiện chuyển trạng thái. */
    public boolean close() {
        return phase.getAndSet(SessionPhase.CLOSED) != SessionPhase.CLOSED;
    }

    /** Kiểm tra phiên đã xác thực thành công hay chưa. */
    public boolean isAuthenticated() {
        return phase.get() == SessionPhase.AUTHENTICATED;
    }

    /** Kiểm tra phiên đã ở trạng thái CLOSED hay chưa. */
    public boolean isClosed() {
        return phase.get() == SessionPhase.CLOSED;
    }

    /** Thực hiện compare-and-set và ném lỗi mô tả nếu phase hiện tại không đúng. */
    private void transition(SessionPhase expected, SessionPhase requested) {
        SessionPhase actual = phase.get();
        if (actual == SessionPhase.CLOSED || !phase.compareAndSet(expected, requested)) {
            throw new ProtocolStateException(actual, expected, requested);
        }
    }
}
