package com.nsocry.protocol.compat;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

/** Writes and flushes bounded frames while preserving the outbound cipher cursor. */
public final class LegacyFrameWriter {
    private final OutputStream output;
    private final ProtocolLimits limits;

    public LegacyFrameWriter(OutputStream output, ProtocolLimits limits) {
        this.output = Objects.requireNonNull(output, "output");
        this.limits = Objects.requireNonNull(limits, "limits");
    }

    public synchronized void writeUnencryptedShortFrame(ProtocolFrame frame) throws IOException {
        limits.requireAllowed(frame.payload().length, false);
        write(LegacyFrameCodec.encodeShortFrame(frame.command(), frame.payload(), null));
    }

    public synchronized void writeEncryptedShortFrame(
            ProtocolFrame frame, RollingXorCipher cipher) throws IOException {
        Objects.requireNonNull(cipher, "cipher");
        limits.requireAllowed(frame.payload().length, false);
        write(LegacyFrameCodec.encodeShortFrame(frame.command(), frame.payload(), cipher));
    }

    public synchronized void writeEncryptedFullSizeFrame(
            byte[] payload, RollingXorCipher cipher) throws IOException {
        Objects.requireNonNull(cipher, "cipher");
        limits.requireAllowed(payload.length, true);
        write(LegacyFrameCodec.encodeFullSizeFrame(payload, cipher));
    }

    private void write(byte[] frame) throws IOException {
        output.write(frame);
        output.flush();
    }
}
