package com.nsocry.session;

@FunctionalInterface
/** Cấp khóa mới cho từng phiên mà không ràng buộc handshake với chính sách sinh khóa. */
public interface SessionKeyProvider {
    /** Tạo khóa mới cho đúng một phiên kết nối. */
    byte[] createKey();
}
