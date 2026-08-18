package com.nsocry.protocol.compat;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class LegacyFrameStreamTest {
    private static final byte[] KEY = "CryTestKey".getBytes(StandardCharsets.US_ASCII);

    @Test
    void readsEncryptedShortFrameWithoutResettingCursor() throws Exception {
        RollingXorCipher writerCipher = new RollingXorCipher(KEY);
        byte[] first = LegacyFrameCodec.encodeShortFrame((byte) -29, new byte[] {-125, 1}, writerCipher);
        byte[] second = LegacyFrameCodec.encodeShortFrame((byte) -28, new byte[] {-101}, writerCipher);
        ByteArrayOutputStream wire = new ByteArrayOutputStream();
        wire.write(first);
        wire.write(second);

        LegacyFrameReader reader = new LegacyFrameReader(
                new ByteArrayInputStream(wire.toByteArray()), ProtocolLimits.DEFAULT);
        RollingXorCipher readerCipher = new RollingXorCipher(KEY);

        assertArrayEquals(new byte[] {-125, 1}, reader.readEncryptedFrame(readerCipher, false).payload());
        assertArrayEquals(new byte[] {-101}, reader.readEncryptedFrame(readerCipher, false).payload());
        assertEquals(writerCipher.cursor(), readerCipher.cursor());
    }

    @Test
    void rejectsFullSizeFrameFromClientDirection() {
        byte[] wire = LegacyFrameCodec.encodeFullSizeFrame(new byte[8], new RollingXorCipher(KEY));
        LegacyFrameReader reader = new LegacyFrameReader(
                new ByteArrayInputStream(wire), ProtocolLimits.DEFAULT);

        assertThrows(IOException.class,
                () -> reader.readEncryptedFrame(new RollingXorCipher(KEY), false));
    }

    @Test
    void rejectsPayloadBeforeAllocatingBeyondConfiguredLimit() {
        byte[] header = {(byte) -29, 0, 16};
        LegacyFrameReader reader = new LegacyFrameReader(
                new ByteArrayInputStream(header), new ProtocolLimits(8, 16));

        assertThrows(IllegalArgumentException.class, reader::readUnencryptedShortFrame);
    }
}
