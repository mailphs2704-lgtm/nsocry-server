package com.nsocry.assets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Encoder và parser kiểm chứng payload MAP dành riêng cho client V7 build 217. */
public final class MapAssetCodec {
    private static final int MAX_UNSIGNED_BYTE_COUNT = 255;
    private static final int MAX_SIGNED_BYTE_COUNT = 127;
    private static final int MAX_SIGNED_SHORT_COUNT = 32_767;

    private MapAssetCodec() {
    }

    /** Mã hóa map/NPC/mob catalog và kiểm soát đúng giới hạn kiểu count trên wire. */
    public static byte[] encode(MapAssetBundle bundle) throws IOException {
        Objects.requireNonNull(bundle, "bundle");
        requireCount(bundle.mapNames().size(), MAX_UNSIGNED_BYTE_COUNT, "maps");
        requireCount(bundle.npcs().size(), MAX_SIGNED_BYTE_COUNT, "npcs");
        requireCount(bundle.mobs().size(), MAX_SIGNED_SHORT_COUNT, "mobs");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (DataOutputStream output = new DataOutputStream(buffer)) {
            output.writeByte(bundle.version());
            output.writeByte(bundle.mapNames().size());
            for (String name : bundle.mapNames()) {
                output.writeUTF(name);
            }
            output.writeByte(bundle.npcs().size());
            for (NpcTemplateAsset npc : bundle.npcs()) {
                writeNpc(output, npc);
            }
            output.writeShort(bundle.mobs().size());
            for (MobTemplateAsset mob : bundle.mobs()) {
                output.writeByte(mob.type());
                output.writeUTF(mob.name());
                output.writeInt(mob.health());
                output.writeByte(mob.moveRange());
                output.writeByte(mob.speed());
            }
        }
        return buffer.toByteArray();
    }

    /** Parse lại payload MAP và từ chối count âm hoặc byte dư. */
    public static MapAssetBundle decode(byte[] payload) throws IOException {
        Objects.requireNonNull(payload, "payload");
        try (DataInputStream input = new DataInputStream(new ByteArrayInputStream(payload))) {
            byte version = input.readByte();
            int mapCount = input.readUnsignedByte();
            List<String> maps = new ArrayList<>(mapCount);
            for (int index = 0; index < mapCount; index++) {
                maps.add(input.readUTF());
            }
            int npcCount = readSignedByteCount(input, "npcs");
            List<NpcTemplateAsset> npcs = new ArrayList<>(npcCount);
            for (int index = 0; index < npcCount; index++) {
                npcs.add(readNpc(input));
            }
            int mobCount = input.readShort();
            if (mobCount < 0) {
                throw new IOException("mob count is negative");
            }
            List<MobTemplateAsset> mobs = new ArrayList<>(mobCount);
            for (int index = 0; index < mobCount; index++) {
                mobs.add(new MobTemplateAsset(input.readByte(), input.readUTF(), input.readInt(),
                        input.readByte(), input.readByte()));
            }
            if (input.available() != 0) {
                throw new IOException("unexpected trailing map asset bytes");
            }
            return new MapAssetBundle(version, maps, npcs, mobs);
        }
    }

    /** Ghi NPC cùng menu hai chiều. */
    private static void writeNpc(DataOutputStream output, NpcTemplateAsset npc) throws IOException {
        requireCount(npc.menu().size(), MAX_SIGNED_BYTE_COUNT, "npc menu rows");
        output.writeUTF(npc.name());
        output.writeShort(npc.head());
        output.writeShort(npc.body());
        output.writeShort(npc.leg());
        output.writeByte(npc.menu().size());
        for (List<String> row : npc.menu()) {
            requireCount(row.size(), MAX_SIGNED_BYTE_COUNT, "npc menu choices");
            output.writeByte(row.size());
            for (String text : row) {
                output.writeUTF(text);
            }
        }
    }

    /** Đọc NPC và menu theo parser client. */
    private static NpcTemplateAsset readNpc(DataInputStream input) throws IOException {
        String name = input.readUTF();
        short head = input.readShort();
        short body = input.readShort();
        short leg = input.readShort();
        int rowCount = readSignedByteCount(input, "npc menu rows");
        List<List<String>> menu = new ArrayList<>(rowCount);
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            int choiceCount = readSignedByteCount(input, "npc menu choices");
            List<String> row = new ArrayList<>(choiceCount);
            for (int choiceIndex = 0; choiceIndex < choiceCount; choiceIndex++) {
                row.add(input.readUTF());
            }
            menu.add(row);
        }
        return new NpcTemplateAsset(name, head, body, leg, menu);
    }

    /** Đọc count signed byte và từ chối giá trị âm. */
    private static int readSignedByteCount(DataInputStream input, String name) throws IOException {
        int count = input.readByte();
        if (count < 0) {
            throw new IOException(name + " count is negative");
        }
        return count;
    }

    /** Kiểm soát count theo giới hạn parser client. */
    private static void requireCount(int count, int maximum, String name) {
        if (count > maximum) {
            throw new IllegalArgumentException(name + " count exceeds wire limit " + maximum);
        }
    }
}
