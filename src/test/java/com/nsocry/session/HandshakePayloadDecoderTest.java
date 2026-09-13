package com.nsocry.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.nsocry.protocol.compat.LegacyFrameCodec;
import com.nsocry.protocol.compat.ProtocolFrame;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

class HandshakePayloadDecoderTest {
    private static final Path FIXTURE = Path.of("docs/protocol/fixtures/handshake-login-v1.json");

    @Test
    void decodesClientWireOrderInsteadOfReferenceReadOrder() throws Exception {
        ProtocolFrame frame = fixtureFrame(0);
        ClientInfo info = HandshakePayloadDecoder.decodeClientInfo(frame);

        assertEquals(1, info.clientType());
        assertEquals(2, info.zoomLevel());
        assertEquals(240, info.width());
        assertEquals(320, info.height());
        assertEquals("J2ME", info.platform());
        assertEquals(0, info.wireField9());
        assertEquals(0, info.wireField10());
        assertEquals("0", info.agent());
    }

    @Test
    void decodesLoginAndRedactsSensitiveToStringFields() throws Exception {
        LoginRequest request = HandshakePayloadDecoder.decodeLogin(fixtureFrame(1));

        assertEquals("testuser", request.username());
        assertEquals("testpass", request.password());
        assertEquals("2.17.0", request.version());
        assertEquals("", request.reservedUtf1());
        assertEquals("", request.reservedUtf2());
        assertEquals("fixture", request.clientToken());
        assertFalse(request.toString().contains("testpass"));
        assertFalse(request.toString().contains("fixture"));
    }

    private static ProtocolFrame fixtureFrame(int index) throws Exception {
        String json = Files.readString(FIXTURE);
        Matcher matcher = Pattern.compile("\\\"plainFrameHex\\\"\\s*:\\s*\\\"([^\\\"]*)\\\"").matcher(json);
        List<String> frames = new ArrayList<>();
        while (matcher.find()) {
            frames.add(matcher.group(1));
        }
        byte[] bytes = HexFormat.of().parseHex(frames.get(index).replace(" ", ""));
        return LegacyFrameCodec.decodeFrame(bytes, null);
    }
}
