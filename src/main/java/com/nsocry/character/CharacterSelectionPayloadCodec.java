package com.nsocry.character;

import com.nsocry.protocol.compat.ProtocolFrame;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/** Mã hóa và giải mã payload màn hình nhân vật tương thích luồng client V7 đã xác minh tĩnh. */
public final class CharacterSelectionPayloadCodec {
    public static final byte NOT_MAP_ENVELOPE = -28;
    public static final byte SELECT_CHARACTER_COMMAND = -126;
    public static final byte CREATE_CHARACTER_COMMAND = -125;

    private CharacterSelectionPayloadCodec() {
    }

    /** Tạo frame danh sách nhân vật theo đúng thứ tự trường mà client cũ đọc. */
    public static ProtocolFrame encodeCharacterList(List<CharacterSummary> characters) throws IOException {
        Objects.requireNonNull(characters, "characters");
        if (characters.size() > 255) {
            throw new IllegalArgumentException("character list cannot exceed 255 entries");
        }

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (DataOutputStream output = new DataOutputStream(buffer)) {
            output.writeByte(SELECT_CHARACTER_COMMAND);
            output.writeByte(characters.size());
            for (CharacterSummary character : characters) {
                Objects.requireNonNull(character, "character");
                output.writeByte(character.gender());
                output.writeUTF(character.name());
                output.writeUTF(character.school());
                output.writeByte(character.level());
                output.writeShort(character.head());
                output.writeShort(character.weapon());
                output.writeShort(character.body());
                output.writeShort(character.leg());
            }
        }
        return new ProtocolFrame(NOT_MAP_ENVELOPE, buffer.toByteArray());
    }

    /** Giải mã tên nhân vật client muốn chọn và từ chối envelope, command hoặc byte đuôi sai. */
    public static String decodeSelectedCharacterName(ProtocolFrame frame) throws IOException {
        DataInputStream input = nestedPayload(frame, SELECT_CHARACTER_COMMAND);
        String name = input.readUTF();
        requireFullyConsumed(input);
        return name;
    }

    /** Giải mã đúng ba trường wire của yêu cầu tạo nhân vật, chưa trộn quy tắc nghiệp vụ vào codec. */
    public static CreateCharacterRequest decodeCreateCharacterRequest(ProtocolFrame frame) throws IOException {
        DataInputStream input = nestedPayload(frame, CREATE_CHARACTER_COMMAND);
        CreateCharacterRequest request = new CreateCharacterRequest(
                input.readUTF(), input.readByte(), input.readByte());
        requireFullyConsumed(input);
        return request;
    }

    /** Xác minh frame thuộc nhóm chưa vào bản đồ và có command con mong đợi. */
    private static DataInputStream nestedPayload(ProtocolFrame frame, byte expectedNested) throws IOException {
        Objects.requireNonNull(frame, "frame");
        if (frame.command() != NOT_MAP_ENVELOPE) {
            throw new IOException("unexpected character envelope");
        }
        DataInputStream input = new DataInputStream(new ByteArrayInputStream(frame.payload()));
        if (input.readByte() != expectedNested) {
            throw new IOException("unexpected nested character command");
        }
        return input;
    }

    /** Bảo đảm không âm thầm bỏ qua dữ liệu lạ ở cuối payload. */
    private static void requireFullyConsumed(DataInputStream input) throws IOException {
        if (input.available() != 0) {
            throw new IOException("unexpected trailing character payload bytes");
        }
    }
}
