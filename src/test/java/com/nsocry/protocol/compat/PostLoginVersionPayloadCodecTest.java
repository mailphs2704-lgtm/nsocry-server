package com.nsocry.protocol.compat;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import org.junit.jupiter.api.Test;

class PostLoginVersionPayloadCodecTest {
    @Test
    void encodesFourVersionsBeforeAppearanceData() throws Exception {
        ClientVersionManifest manifest = new ClientVersionManifest(
                (byte) 7, (byte) 8, (byte) 9, (byte) 10);

        ProtocolFrame frame = PostLoginVersionPayloadCodec.encodeVersion(
                manifest, new byte[] {11, 12});

        assertEquals(-28, frame.command());
        assertArrayEquals(new byte[] {(byte) -123, 7, 8, 9, 10, 11, 12}, frame.payload());
    }

    @Test
    void decodesAllFourEmptyDataRequests() throws Exception {
        assertRequest((byte) -122, ClientDataSet.DATA);
        assertRequest((byte) -121, ClientDataSet.MAP);
        assertRequest((byte) -120, ClientDataSet.SKILL);
        assertRequest((byte) -119, ClientDataSet.ITEM);
    }

    @Test
    void rejectsUnknownNestedCommand() {
        ProtocolFrame frame = new ProtocolFrame((byte) -28, new byte[] {(byte) -118});

        assertThrows(IOException.class,
                () -> PostLoginVersionPayloadCodec.decodeDataRequest(frame));
    }

    @Test
    void rejectsTrailingRequestPayload() {
        ProtocolFrame frame = new ProtocolFrame((byte) -28, new byte[] {(byte) -122, 0});

        assertThrows(IOException.class,
                () -> PostLoginVersionPayloadCodec.decodeDataRequest(frame));
    }

    @Test
    void rejectsWrongEnvelope() {
        ProtocolFrame frame = new ProtocolFrame((byte) -29, new byte[] {(byte) -122});

        assertThrows(IOException.class,
                () -> PostLoginVersionPayloadCodec.decodeDataRequest(frame));
    }

    @Test
    void encodesRequestedDataSetWithNestedCommand() {
        ProtocolFrame frame = PostLoginVersionPayloadCodec.encodeDataResponse(
                ClientDataSet.MAP, new byte[] {7, 8});

        assertEquals(-28, frame.command());
        assertArrayEquals(new byte[] {(byte) -121, 7, 8}, frame.payload());
    }

    private static void assertRequest(byte command, ClientDataSet expected) throws Exception {
        ProtocolFrame frame = new ProtocolFrame((byte) -28, new byte[] {command});
        assertEquals(expected, PostLoginVersionPayloadCodec.decodeDataRequest(frame));
    }
}
