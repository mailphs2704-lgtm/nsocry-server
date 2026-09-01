package com.nsocry.session;

import com.nsocry.protocol.compat.LegacyFrameCodec;
import com.nsocry.protocol.compat.LegacyFrameReader;
import com.nsocry.protocol.compat.LegacyFrameWriter;
import com.nsocry.protocol.compat.LegacyKeyCodec;
import com.nsocry.protocol.compat.ProtocolFrame;
import com.nsocry.protocol.compat.ProtocolLimits;
import com.nsocry.protocol.compat.RollingXorCipher;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/** Sở hữu I/O frame, trạng thái mã hóa hai chiều và thao tác đóng của một client. */
public final class LegacySessionTransport implements Closeable {
    private final LegacyFrameReader reader;
    private final LegacyFrameWriter writer;
    private final HandshakeStateMachine state = new HandshakeStateMachine();
    private final AtomicBoolean closed = new AtomicBoolean();
    private final Closeable closeTarget;
    private RollingXorCipher inboundCipher;
    private RollingXorCipher outboundCipher;

    /** Tạo transport từ hai stream, giới hạn protocol và tài nguyên cần đóng cuối phiên. */
    public LegacySessionTransport(
            InputStream input,
            OutputStream output,
            ProtocolLimits limits,
            Closeable closeTarget) {
        this.reader = new LegacyFrameReader(input, limits);
        this.writer = new LegacyFrameWriter(output, limits);
        this.closeTarget = Objects.requireNonNull(closeTarget, "closeTarget");
    }

    /** Xác minh trigger -27 rỗng, gửi khóa và kích hoạt cipher độc lập hai chiều. */
    public void beginHandshake(byte[] key) throws IOException {
        requireOpen();
        ProtocolFrame trigger = reader.readUnencryptedShortFrame();
        if (trigger.command() != LegacyFrameCodec.KEY_EXCHANGE_COMMAND || trigger.payload().length != 0) {
            throw new IOException("invalid key-exchange trigger");
        }

        byte[] safeKey = Arrays.copyOf(key, key.length);
        ProtocolFrame response = new ProtocolFrame(
                LegacyFrameCodec.KEY_EXCHANGE_COMMAND,
                LegacyKeyCodec.encodePayload(safeKey));
        writer.writeUnencryptedShortFrame(response);

        inboundCipher = new RollingXorCipher(safeKey);
        outboundCipher = new RollingXorCipher(safeKey);
        state.keySent();
    }

    /** Đọc frame mã hóa tiếp theo từ client sau khi trao đổi khóa hoàn tất. */
    public ProtocolFrame readClientFrame() throws IOException {
        requireOpen();
        if (inboundCipher == null) {
            throw new IOException("handshake is not complete");
        }
        return reader.readEncryptedFrame(inboundCipher, false);
    }

    /** Gửi frame ngắn mã hóa cho client bằng con trỏ chiều ra. */
    public void sendShortFrame(ProtocolFrame frame) throws IOException {
        requireOpen();
        if (outboundCipher == null) {
            throw new IOException("handshake is not complete");
        }
        writer.writeEncryptedShortFrame(frame, outboundCipher);
    }

    /** Gửi payload full-size mã hóa cho client bằng con trỏ chiều ra. */
    public void sendFullSizePayload(byte[] payload) throws IOException {
        requireOpen();
        if (outboundCipher == null) {
            throw new IOException("handshake is not complete");
        }
        writer.writeEncryptedFullSizeFrame(payload, outboundCipher);
    }

    /** Trả state machine thuộc transport để processor điều phối phase. */
    public HandshakeStateMachine state() {
        return state;
    }

    @Override
    /** Đóng phase và tài nguyên nền đúng một lần. */
    public void close() throws IOException {
        if (closed.compareAndSet(false, true)) {
            state.close();
            closeTarget.close();
        }
    }

    /** Từ chối thao tác I/O sau khi transport đã đóng. */
    private void requireOpen() throws IOException {
        if (closed.get()) {
            throw new IOException("session transport is closed");
        }
    }
}
