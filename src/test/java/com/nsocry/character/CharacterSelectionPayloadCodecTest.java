package com.nsocry.character;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nsocry.protocol.compat.ProtocolFrame;
import java.io.IOException;
import java.util.HexFormat;
import java.util.List;
import org.junit.jupiter.api.Test;

class CharacterSelectionPayloadCodecTest {
    @Test
    void encodesCharacterListInVerifiedWireOrder() throws Exception {
        CharacterSummary character = new CharacterSummary(
                (byte) 1, "cryone", "Kiem", (byte) 25,
                (short) 10, (short) 20, (short) 30, (short) 40);

        ProtocolFrame frame = CharacterSelectionPayloadCodec.encodeCharacterList(List.of(character));

        assertEquals(-28, frame.command());
        assertArrayEquals(
                HexFormat.of().parseHex("82010100066372796f6e6500044b69656d19000a0014001e0028"),
                frame.payload());
    }

    @Test
    void encodesEmptyCharacterList() throws Exception {
        ProtocolFrame frame = CharacterSelectionPayloadCodec.encodeCharacterList(List.of());

        assertArrayEquals(new byte[] {(byte) -126, 0}, frame.payload());
    }

    @Test
    void decodesSelectedCharacterName() throws Exception {
        ProtocolFrame frame = new ProtocolFrame((byte) -28,
                HexFormat.of().parseHex("8200066372796f6e65"));

        assertEquals("cryone", CharacterSelectionPayloadCodec.decodeSelectedCharacterName(frame));
    }

    @Test
    void decodesCreateCharacterWithoutInventingBusinessRules() throws Exception {
        ProtocolFrame frame = new ProtocolFrame((byte) -28,
                HexFormat.of().parseHex("8300066372796f6e650102"));

        assertEquals(new CreateCharacterRequest("cryone", (byte) 1, (byte) 2),
                CharacterSelectionPayloadCodec.decodeCreateCharacterRequest(frame));
    }

    @Test
    void rejectsUnexpectedEnvelope() {
        ProtocolFrame frame = new ProtocolFrame((byte) -29,
                HexFormat.of().parseHex("82000161"));

        assertThrows(IOException.class,
                () -> CharacterSelectionPayloadCodec.decodeSelectedCharacterName(frame));
    }

    @Test
    void rejectsTrailingBytes() {
        ProtocolFrame frame = new ProtocolFrame((byte) -28,
                HexFormat.of().parseHex("8200016100"));

        assertThrows(IOException.class,
                () -> CharacterSelectionPayloadCodec.decodeSelectedCharacterName(frame));
    }
}
