package com.nsocry.session;

import com.nsocry.protocol.compat.ProtocolFrame;
import java.io.IOException;

/** Ranh giới I/O tối thiểu cho vòng lặp đồng bộ asset sau khi xác thực. */
public interface PostLoginSessionTransport {
    /** Đọc frame mã hóa tiếp theo từ client. */
    ProtocolFrame readClientFrame() throws IOException;

    /** Gửi frame mã hóa dùng layout độ dài ngắn. */
    void sendShortFrame(ProtocolFrame frame) throws IOException;

    /** Gửi frame mã hóa dùng layout độ dài đầy đủ. */
    void sendFullSizeFrame(ProtocolFrame frame) throws IOException;
}
