package com.nsocry.protocol.compat;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

/** Ghi, flush frame có giới hạn và duy trì liên tục con trỏ mã hóa chiều ra. */
public final class LegacyFrameWriter {
    private final OutputStream output;
    private final ProtocolLimits limits;

    /** Khởi tạo bộ ghi ra stream với giới hạn payload bắt buộc. */
    public LegacyFrameWriter(OutputStream output, ProtocolLimits limits) {
        this.output = Objects.requireNonNull(output, "output");
        this.limits = Objects.requireNonNull(limits, "limits");
    }

    /** Ghi và flush một frame ngắn chưa mã hóa. */
    public synchronized void writeUnencryptedShortFrame(ProtocolFrame frame) throws IOException {
        limits.requireAllowed(frame.payload().length, false);
        write(LegacyFrameCodec.encodeShortFrame(frame.command(), frame.payload(), null));
    }

    /** Ghi và flush một frame ngắn đã mã hóa bằng cipher chiều ra. */
    public synchronized void writeEncryptedShortFrame(
            ProtocolFrame frame, RollingXorCipher cipher) throws IOException {
        Objects.requireNonNull(cipher, "cipher");
        limits.requireAllowed(frame.payload().length, false);
        write(LegacyFrameCodec.encodeShortFrame(frame.command(), frame.payload(), cipher));
    }

    /** Ghi và flush payload full-size đã mã hóa sau khi kiểm tra giới hạn. */
    public synchronized void writeEncryptedFullSizeFrame(
            byte[] payload, RollingXorCipher cipher) throws IOException {
        Objects.requireNonNull(cipher, "cipher");
        limits.requireAllowed(payload.length, true);
        write(LegacyFrameCodec.encodeFullSizeFrame(payload, cipher));
    }

    /** Ghi toàn bộ byte frame và flush ngay để client nhận dữ liệu. */
    private void write(byte[] frame) throws IOException {
        output.write(frame);
        output.flush();
    }
}
