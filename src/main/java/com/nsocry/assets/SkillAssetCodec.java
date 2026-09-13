package com.nsocry.assets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Encoder và parser kiểm chứng payload SKILL theo đúng thứ tự client V7 đọc. */
public final class SkillAssetCodec {
    private static final int MAX_SIGNED_BYTE_COUNT = 127;
    private static final int MAX_UNSIGNED_BYTE_COUNT = 255;

    private SkillAssetCodec() {
    }

    /** Mã hóa toàn bộ skill catalog, kiểm tra count trước khi ghi một byte. */
    public static byte[] encode(SkillAssetBundle bundle) throws IOException {
        Objects.requireNonNull(bundle, "bundle");
        requireCount(bundle.optionTemplateNames().size(), MAX_SIGNED_BYTE_COUNT, "skill options");
        requireCount(bundle.classes().size(), MAX_UNSIGNED_BYTE_COUNT, "skill classes");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (DataOutputStream output = new DataOutputStream(buffer)) {
            output.writeByte(bundle.version());
            output.writeByte(bundle.optionTemplateNames().size());
            for (String name : bundle.optionTemplateNames()) {
                output.writeUTF(name);
            }
            output.writeByte(bundle.classes().size());
            for (SkillClassAsset skillClass : bundle.classes()) {
                requireCount(skillClass.templates().size(), MAX_SIGNED_BYTE_COUNT, "skill templates");
                output.writeUTF(skillClass.name());
                output.writeByte(skillClass.templates().size());
                for (SkillTemplateAsset template : skillClass.templates()) {
                    writeTemplate(output, template);
                }
            }
        }
        return buffer.toByteArray();
    }

    /** Parse lại output để validator phát hiện sai layout hoặc byte dư. */
    public static SkillAssetBundle decode(byte[] payload) throws IOException {
        Objects.requireNonNull(payload, "payload");
        try (DataInputStream input = new DataInputStream(new ByteArrayInputStream(payload))) {
            byte version = input.readByte();
            int optionCount = readSignedCount(input, "skill options");
            List<String> optionNames = new ArrayList<>(optionCount);
            for (int index = 0; index < optionCount; index++) {
                optionNames.add(input.readUTF());
            }
            int classCount = input.readUnsignedByte();
            List<SkillClassAsset> classes = new ArrayList<>(classCount);
            for (int index = 0; index < classCount; index++) {
                String name = input.readUTF();
                int templateCount = readSignedCount(input, "skill templates");
                List<SkillTemplateAsset> templates = new ArrayList<>(templateCount);
                for (int templateIndex = 0; templateIndex < templateCount; templateIndex++) {
                    templates.add(readTemplate(input));
                }
                classes.add(new SkillClassAsset(name, templates));
            }
            if (input.available() != 0) {
                throw new IOException("unexpected trailing skill asset bytes");
            }
            return new SkillAssetBundle(version, optionNames, classes);
        }
    }

    /** Ghi metadata template, các cấp kỹ năng và option của từng cấp. */
    private static void writeTemplate(DataOutputStream output, SkillTemplateAsset template) throws IOException {
        requireCount(template.levels().size(), MAX_SIGNED_BYTE_COUNT, "skill levels");
        output.writeByte(template.id());
        output.writeUTF(template.name());
        output.writeByte(template.maxPoint());
        output.writeByte(template.type());
        output.writeShort(template.icon());
        output.writeUTF(template.description());
        output.writeByte(template.levels().size());
        for (SkillLevelAsset level : template.levels()) {
            requireCount(level.options().size(), MAX_SIGNED_BYTE_COUNT, "skill level options");
            output.writeShort(level.id());
            output.writeByte(level.point());
            output.writeByte(level.requiredLevel());
            output.writeShort(level.manaUse());
            output.writeInt(level.coolDown());
            output.writeShort(level.dx());
            output.writeShort(level.dy());
            output.writeByte(level.maxFight());
            output.writeByte(level.options().size());
            for (SkillLevelOptionAsset option : level.options()) {
                output.writeShort(option.parameter());
                output.writeByte(option.optionTemplateId());
            }
        }
    }

    /** Đọc một template theo cùng schema mà client V7 sử dụng. */
    private static SkillTemplateAsset readTemplate(DataInputStream input) throws IOException {
        byte id = input.readByte();
        String name = input.readUTF();
        byte maxPoint = input.readByte();
        byte type = input.readByte();
        short icon = input.readShort();
        String description = input.readUTF();
        int levelCount = readSignedCount(input, "skill levels");
        List<SkillLevelAsset> levels = new ArrayList<>(levelCount);
        for (int index = 0; index < levelCount; index++) {
            short levelId = input.readShort();
            byte point = input.readByte();
            byte requiredLevel = input.readByte();
            short manaUse = input.readShort();
            int coolDown = input.readInt();
            short dx = input.readShort();
            short dy = input.readShort();
            byte maxFight = input.readByte();
            int optionCount = readSignedCount(input, "skill level options");
            List<SkillLevelOptionAsset> options = new ArrayList<>(optionCount);
            for (int optionIndex = 0; optionIndex < optionCount; optionIndex++) {
                options.add(new SkillLevelOptionAsset(input.readShort(), input.readByte()));
            }
            levels.add(new SkillLevelAsset(levelId, point, requiredLevel, manaUse,
                    coolDown, dx, dy, maxFight, options));
        }
        return new SkillTemplateAsset(id, name, maxPoint, type, icon, description, levels);
    }

    /** Đọc count signed byte và từ chối giá trị âm gây cấp phát sai. */
    private static int readSignedCount(DataInputStream input, String name) throws IOException {
        int count = input.readByte();
        if (count < 0) {
            throw new IOException(name + " count is negative");
        }
        return count;
    }

    /** Kiểm tra count không vượt giới hạn parser client tương ứng. */
    private static void requireCount(int count, int maximum, String name) {
        if (count > maximum) {
            throw new IllegalArgumentException(name + " count exceeds wire limit " + maximum);
        }
    }
}
