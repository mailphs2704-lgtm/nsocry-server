package com.nsocry.assets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Mã hóa và parse kiểm chứng payload ITEM theo byte layout của client V7. */
public final class ItemAssetCodec {
    private static final int MAX_OPTIONS = 255;
    private static final int MAX_ITEMS = 32_767;

    private ItemAssetCodec() {
    }

    /** Mã hóa bundle thành payload bắt đầu bằng item version. */
    public static byte[] encode(ItemAssetBundle bundle) throws IOException {
        Objects.requireNonNull(bundle, "bundle");
        requireCount(bundle.options().size(), MAX_OPTIONS, "item options");
        requireCount(bundle.items().size(), MAX_ITEMS, "items");

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (DataOutputStream output = new DataOutputStream(buffer)) {
            output.writeByte(bundle.version());
            output.writeByte(bundle.options().size());
            for (ItemOptionAsset option : bundle.options()) {
                output.writeUTF(option.name());
                output.writeByte(option.type());
            }
            output.writeShort(bundle.items().size());
            for (ItemTemplateAsset item : bundle.items()) {
                output.writeByte(item.type());
                output.writeByte(item.gender());
                output.writeUTF(item.name());
                output.writeUTF(item.description());
                output.writeByte(item.level());
                output.writeShort(item.icon());
                output.writeShort(item.part());
                output.writeBoolean(item.upgradable());
            }
        }
        return buffer.toByteArray();
    }

    /** Parse lại payload đã build để validator có thể chứng minh schema và không còn byte dư. */
    public static ItemAssetBundle decode(byte[] payload) throws IOException {
        Objects.requireNonNull(payload, "payload");
        try (DataInputStream input = new DataInputStream(new ByteArrayInputStream(payload))) {
            byte version = input.readByte();
            int optionCount = input.readUnsignedByte();
            List<ItemOptionAsset> options = new ArrayList<>(optionCount);
            for (int index = 0; index < optionCount; index++) {
                options.add(new ItemOptionAsset(input.readUTF(), input.readByte()));
            }
            int itemCount = input.readShort();
            if (itemCount < 0) {
                throw new IOException("item count is negative");
            }
            List<ItemTemplateAsset> items = new ArrayList<>(itemCount);
            for (int index = 0; index < itemCount; index++) {
                items.add(new ItemTemplateAsset(
                        input.readByte(),
                        input.readByte(),
                        input.readUTF(),
                        input.readUTF(),
                        input.readByte(),
                        input.readShort(),
                        input.readShort(),
                        input.readBoolean()));
            }
            if (input.available() != 0) {
                throw new IOException("unexpected trailing item asset bytes");
            }
            return new ItemAssetBundle(version, options, items);
        }
    }

    /** Kiểm tra count vừa với kiểu số lượng trên wire. */
    private static void requireCount(int count, int maximum, String name) {
        if (count > maximum) {
            throw new IllegalArgumentException(name + " count exceeds wire limit " + maximum);
        }
    }
}
