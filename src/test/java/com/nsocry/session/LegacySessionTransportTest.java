package com.nsocry.session;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nsocry.protocol.compat.LegacyFrameCodec;
import com.nsocry.protocol.compat.LegacyKeyCodec;
import com.nsocry.protocol.compat.ProtocolFrame;
import com.nsocry.protocol.compat.ProtocolLimits;
import com.nsocry.protocol.compat.RollingXorCipher;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;

class LegacySessionTransportTest {
    private static final byte[] KEY = "CryTestKey".getBytes(StandardCharsets.US_ASCII);

    @Test
    void validatesTriggerSendsKeyThenReadsEncryptedClientFrame() throws Exception {
        byte[] trigger = LegacyFrameCodec.encodeShortFrame((byte) -27, new byte[0], null);
        byte[] encryptedClientFrame = LegacyFrameCodec.encodeShortFrame(
                (byte) -29, new byte[] {(byte) -125, 1}, new RollingXorCipher(KEY));
        ByteArrayOutputStream inputBytes = new ByteArrayOutputStream();
        inputBytes.write(trigger);
        inputBytes.write(encryptedClientFrame);
        ByteArrayOutputStream serverOutput = new ByteArrayOutputStream();
        AtomicBoolean closed = new AtomicBoolean();

        LegacySessionTransport transport = new LegacySessionTransport(
                new ByteArrayInputStream(inputBytes.toByteArray()),
                serverOutput,
                ProtocolLimits.DEFAULT,
                () -> closed.set(true));

        transport.beginHandshake(KEY);
        assertEquals(SessionPhase.KEY_SENT, transport.state().phase());
        assertArrayEquals(
                LegacyFrameCodec.encodeShortFrame(
                        (byte) -27, LegacyKeyCodec.encodePayload(KEY), null),
                serverOutput.toByteArray());

        ProtocolFrame frame = transport.readClientFrame();
        assertEquals((byte) -29, frame.command());
        assertArrayEquals(new byte[] {(byte) -125, 1}, frame.payload());

        transport.close();
        assertTrue(closed.get());
        assertTrue(transport.state().isClosed());
        transport.close();
        assertTrue(closed.get());
    }
}
