package com.nsocry.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nsocry.protocol.compat.LegacyFrameCodec;
import com.nsocry.protocol.compat.ProtocolLimits;
import com.nsocry.protocol.compat.RollingXorCipher;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

class HandshakeProcessorTest {
    private static final byte[] KEY = "CryTestKey".getBytes(StandardCharsets.US_ASCII);

    @Test
    void processesTriggerClientInfoAndLoginInOrder() throws Exception {
        List<byte[]> plainFrames = fixtureFrames();
        RollingXorCipher clientCipher = new RollingXorCipher(KEY);
        ByteArrayOutputStream inbound = new ByteArrayOutputStream();
        inbound.write(LegacyFrameCodec.encodeShortFrame((byte) -27, new byte[0], null));
        inbound.write(clientCipher.transform(plainFrames.get(0)));
        inbound.write(clientCipher.transform(plainFrames.get(1)));

        LegacySessionTransport transport = new LegacySessionTransport(
                new ByteArrayInputStream(inbound.toByteArray()),
                new ByteArrayOutputStream(),
                ProtocolLimits.DEFAULT,
                () -> {});
        HandshakeProcessor processor = new HandshakeProcessor(transport);

        assertEquals(HandshakeEvent.KEY_ESTABLISHED, processor.begin(KEY));
        assertEquals(HandshakeEvent.CLIENT_INFO_ACCEPTED,
                processor.receiveNext((request, info) -> AuthenticationDecision.REJECTED));
        assertNotNull(processor.clientInfo());
        assertEquals(HandshakeEvent.AUTHENTICATED,
                processor.receiveNext((request, info) -> {
                    assertEquals("testuser", request.username());
                    assertEquals(240, info.width());
                    return AuthenticationDecision.ACCEPTED;
                }));
        assertTrue(transport.state().isAuthenticated());
    }

    private static List<byte[]> fixtureFrames() throws Exception {
        String json = Files.readString(Path.of("docs/protocol/fixtures/handshake-login-v1.json"));
        Matcher matcher = Pattern.compile("\\\"plainFrameHex\\\"\\s*:\\s*\\\"([^\\\"]*)\\\"").matcher(json);
        List<byte[]> frames = new ArrayList<>();
        while (matcher.find()) {
            frames.add(HexFormat.of().parseHex(matcher.group(1).replace(" ", "")));
        }
        return frames;
    }
}
