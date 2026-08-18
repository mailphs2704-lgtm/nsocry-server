package com.nsocry.assets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Encoder và parser kiểm chứng phần appearance của UPDATE_VERSION. */
public final class AppearanceAssetCodec {
    private static final int MAX_UNSIGNED_BYTE_COUNT = 255;
    private static final int MAX_SIGNED_BYTE_COUNT = 127;
    private static final int MAX_LAYERS = 41;
    private static final int MOUNT_FRAME_GROUPS = 6;

    private AppearanceAssetCodec() {
    }

    /** Mã hóa ba head group, leg, ba body group và mount theo thứ tự client đọc. */
    public static byte[] encode(AppearanceAssetBundle bundle) throws IOException {
        Objects.requireNonNull(bundle, "bundle");
        requireCount(bundle.jumpingHeads().size(), MAX_UNSIGNED_BYTE_COUNT, "heads");
        requireCount(bundle.legs().size(), MAX_UNSIGNED_BYTE_COUNT, "legs");
        requireCount(bundle.jumpingBodies().size(), MAX_UNSIGNED_BYTE_COUNT, "bodies");
        requireCount(bundle.mounts().size(), MAX_SIGNED_BYTE_COUNT, "mounts");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (DataOutputStream output = new DataOutputStream(buffer)) {
            output.writeByte(bundle.jumpingHeads().size());
            writeParts(output, bundle.jumpingHeads());
            writeParts(output, bundle.normalHeads());
            writeParts(output, bundle.coveredHeads());
            output.writeByte(bundle.legs().size());
            for (LegAppearanceAsset leg : bundle.legs()) {
                output.writeShort(leg.id());
                output.writeShort(leg.smallImageId());
            }
            output.writeByte(bundle.jumpingBodies().size());
            writeParts(output, bundle.jumpingBodies());
            writeParts(output, bundle.normalBodies());
            writeParts(output, bundle.coveredBodies());
            output.writeByte(bundle.mounts().size());
            for (MountAppearanceAsset mount : bundle.mounts()) {
                output.writeShort(mount.itemId());
                for (List<Short> frames : mount.frameGroups()) {
                    requireCount(frames.size(), MAX_SIGNED_BYTE_COUNT, "mount frames");
                    output.writeByte(frames.size());
                    for (short frame : frames) {
                        output.writeShort(frame);
                    }
                }
            }
        }
        return buffer.toByteArray();
    }

    /** Parse lại appearance payload và từ chối byte dư. */
    public static AppearanceAssetBundle decode(byte[] payload) throws IOException {
        Objects.requireNonNull(payload, "payload");
        try (DataInputStream input = new DataInputStream(new ByteArrayInputStream(payload))) {
            int headCount = input.readUnsignedByte();
            List<AppearancePartAsset> jumpingHeads = readParts(input, headCount);
            List<AppearancePartAsset> normalHeads = readParts(input, headCount);
            List<AppearancePartAsset> coveredHeads = readParts(input, headCount);
            int legCount = input.readUnsignedByte();
            List<LegAppearanceAsset> legs = new ArrayList<>(legCount);
            for (int index = 0; index < legCount; index++) {
                legs.add(new LegAppearanceAsset(input.readShort(), input.readShort()));
            }
            int bodyCount = input.readUnsignedByte();
            List<AppearancePartAsset> jumpingBodies = readParts(input, bodyCount);
            List<AppearancePartAsset> normalBodies = readParts(input, bodyCount);
            List<AppearancePartAsset> coveredBodies = readParts(input, bodyCount);
            int mountCount = readSignedCount(input, "mounts");
            List<MountAppearanceAsset> mounts = new ArrayList<>(mountCount);
            for (int mountIndex = 0; mountIndex < mountCount; mountIndex++) {
                short itemId = input.readShort();
                List<List<Short>> groups = new ArrayList<>(MOUNT_FRAME_GROUPS);
                for (int groupIndex = 0; groupIndex < MOUNT_FRAME_GROUPS; groupIndex++) {
                    int frameCount = readSignedCount(input, "mount frames");
                    List<Short> frames = new ArrayList<>(frameCount);
                    for (int frameIndex = 0; frameIndex < frameCount; frameIndex++) {
                        frames.add(input.readShort());
                    }
                    groups.add(frames);
                }
                mounts.add(new MountAppearanceAsset(itemId, groups));
            }
            if (input.available() != 0) {
                throw new IOException("unexpected trailing appearance bytes");
            }
            return new AppearanceAssetBundle(jumpingHeads, normalHeads, coveredHeads, legs,
                    jumpingBodies, normalBodies, coveredBodies, mounts);
        }
    }

    /** Ghi danh sách part; descriptor byte mã hóa layerCount * 3 + 2. */
    private static void writeParts(DataOutputStream output, List<AppearancePartAsset> parts)
            throws IOException {
        for (AppearancePartAsset part : parts) {
            requireCount(part.layers().size(), MAX_LAYERS, "appearance layers");
            output.writeByte(part.layers().size() * 3 + 2);
            output.writeShort(part.id());
            output.writeShort(part.smallImageId());
            for (AppearanceLayerAsset layer : part.layers()) {
                output.writeShort(layer.imageId());
                output.writeShort(layer.dx());
                output.writeShort(layer.dy());
            }
        }
    }

    /** Đọc danh sách part bằng descriptor byte của client. */
    private static List<AppearancePartAsset> readParts(DataInputStream input, int count)
            throws IOException {
        List<AppearancePartAsset> parts = new ArrayList<>(count);
        for (int index = 0; index < count; index++) {
            int descriptor = readSignedCount(input, "appearance descriptor");
            if (descriptor < 2 || (descriptor - 2) % 3 != 0) {
                throw new IOException("invalid appearance descriptor");
            }
            int layerCount = (descriptor - 2) / 3;
            short id = input.readShort();
            short smallImage = input.readShort();
            List<AppearanceLayerAsset> layers = new ArrayList<>(layerCount);
            for (int layerIndex = 0; layerIndex < layerCount; layerIndex++) {
                layers.add(new AppearanceLayerAsset(
                        input.readShort(), input.readShort(), input.readShort()));
            }
            parts.add(new AppearancePartAsset(id, smallImage, layers));
        }
        return parts;
    }

    /** Đọc signed-byte count/descriptor và từ chối giá trị âm. */
    private static int readSignedCount(DataInputStream input, String name) throws IOException {
        int count = input.readByte();
        if (count < 0) {
            throw new IOException(name + " is negative");
        }
        return count;
    }

    /** Kiểm soát count theo giới hạn byte tương ứng. */
    private static void requireCount(int count, int maximum, String name) {
        if (count > maximum) {
            throw new IllegalArgumentException(name + " count exceeds wire limit " + maximum);
        }
    }
}
