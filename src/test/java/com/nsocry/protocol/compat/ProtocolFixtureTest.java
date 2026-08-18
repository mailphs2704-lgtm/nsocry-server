package com.nsocry.protocol.compat;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

class ProtocolFixtureTest {
    private static final Path FIXTURE = Path.of("docs/protocol/fixtures/handshake-login-v1.json");
    private static final byte[] KEY = "CryTestKey".getBytes(StandardCharsets.US_ASCII);

    @Test
    void keyExchangeRoundTripsFixture() throws Exception {
        String fixture = Files.readString(FIXTURE);
        assertEquals("E5 00 00", stringValue(fixture, "clientTriggerFrameHex"));

        byte[] encodedPayload = LegacyKeyCodec.encodePayload(KEY);
        byte[] keyFrame = LegacyFrameCodec.encodeShortFrame(
                LegacyFrameCodec.KEY_EXCHANGE_COMMAND, encodedPayload, null);

        assertArrayEquals(hex(stringValue(fixture, "serverKeyFrameHex")), keyFrame);
        assertArrayEquals(KEY, LegacyKeyCodec.decodePayload(encodedPayload));
    }

    @Test
    void clientFramesKeepOneContinuousCursor() throws Exception {
        String fixture = Files.readString(FIXTURE);
        List<String> plainFrames = allStringValues(fixture, "plainFrameHex");
        List<String> encryptedFrames = allStringValues(fixture, "encryptedFrameHex");
        List<Integer> cursors = allIntValues(fixture, "cursorAfter");
        RollingXorCipher cipher = new RollingXorCipher(KEY);

        for (int index = 0; index < plainFrames.size(); index++) {
            assertArrayEquals(hex(encryptedFrames.get(index)), cipher.transform(hex(plainFrames.get(index))));
            assertEquals(cursors.get(index), cipher.cursor());
        }
    }

    @Test
    void fullSizeServerFrameMatchesFixtureHash() throws Exception {
        String fixture = Files.readString(FIXTURE);
        byte[] encrypted = LegacyFrameCodec.encodeFullSizeFrame(
                new byte[32768], new RollingXorCipher(KEY));

        assertEquals(intValue(fixture, "encryptedFrameByteLength"), encrypted.length);
        assertArrayEquals(hex(stringValue(fixture, "encryptedHeaderHex")), slice(encrypted, 0, 5));
        assertEquals(stringValue(fixture, "encryptedFrameSha256"),
                HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(encrypted)));

        ProtocolFrame decoded = LegacyFrameCodec.decodeFrame(encrypted, new RollingXorCipher(KEY));
        assertEquals(LegacyFrameCodec.FULL_SIZE_COMMAND, decoded.command());
        assertEquals(32768, decoded.payload().length);
    }

    private static String stringValue(String json, String key) {
        Matcher matcher = Pattern.compile("\\\"" + key + "\\\"\\s*:\\s*\\\"([^\\\"]*)\\\"").matcher(json);
        if (!matcher.find()) {
            throw new IllegalArgumentException("missing fixture field: " + key);
        }
        return matcher.group(1);
    }

    private static int intValue(String json, String key) {
        Matcher matcher = Pattern.compile("\\\"" + key + "\\\"\\s*:\\s*(\\d+)").matcher(json);
        if (!matcher.find()) {
            throw new IllegalArgumentException("missing fixture field: " + key);
        }
        return Integer.parseInt(matcher.group(1));
    }

    private static List<String> allStringValues(String json, String key) {
        Matcher matcher = Pattern.compile("\\\"" + key + "\\\"\\s*:\\s*\\\"([^\\\"]*)\\\"").matcher(json);
        List<String> values = new ArrayList<>();
        while (matcher.find()) {
            values.add(matcher.group(1));
        }
        return values;
    }

    private static List<Integer> allIntValues(String json, String key) {
        Matcher matcher = Pattern.compile("\\\"" + key + "\\\"\\s*:\\s*(\\d+)").matcher(json);
        List<Integer> values = new ArrayList<>();
        while (matcher.find()) {
            values.add(Integer.parseInt(matcher.group(1)));
        }
        return values;
    }

    private static byte[] hex(String value) {
        return HexFormat.of().parseHex(value.replace(" ", ""));
    }

    private static byte[] slice(byte[] source, int from, int to) {
        byte[] result = new byte[to - from];
        System.arraycopy(source, from, result, 0, result.length);
        return result;
    }
}
