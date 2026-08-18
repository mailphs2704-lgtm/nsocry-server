package com.nsocry.assets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Objects;

/** Encoder và parser cấp container cho payload DATA tổng hợp. */
public final class DataAssetCodec {
    private static final int MAX_SIGNED_BYTE_COUNT = 127;

    private DataAssetCodec() {
    }

    /** Mã hóa năm graphics block, task routes, EXP, progression và effect-template tail. */
    public static byte[] encode(DataAssetBundle bundle) throws IOException {
        Objects.requireNonNull(bundle, "bundle");
        requireSignedCount(bundle.taskRoutes().size(), "task route groups");
        requireSignedCount(bundle.experienceThresholds().length, "experience thresholds");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (DataOutputStream output = new DataOutputStream(buffer)) {
            output.writeByte(bundle.version());
            for (ClientGraphicBlock block : ClientGraphicBlock.values()) {
                byte[] data = bundle.graphic(block);
                output.writeInt(data.length);
                output.write(data);
            }
            output.writeByte(bundle.taskRoutes().size());
            for (List<TaskRouteAsset> group : bundle.taskRoutes()) {
                requireSignedCount(group.size(), "task routes");
                output.writeByte(group.size());
                for (TaskRouteAsset route : group) {
                    output.writeByte(route.npcId());
                    output.writeByte(route.mapId());
                }
            }
            long[] experience = bundle.experienceThresholds();
            output.writeByte(experience.length);
            for (long value : experience) {
                output.writeLong(value);
            }
            for (ProgressionTable table : ProgressionTable.values()) {
                int[] values = bundle.progression(table);
                requireSignedCount(values.length, table.name());
                output.writeByte(values.length);
                for (int value : values) {
                    output.writeInt(value);
                }
            }
            output.write(bundle.effectTemplates());
        }
        return buffer.toByteArray();
    }

    /** Parse lại container; effect-template là block cuối nên nhận toàn bộ byte còn lại. */
    public static DataAssetBundle decode(byte[] payload) throws IOException {
        Objects.requireNonNull(payload, "payload");
        try (DataInputStream input = new DataInputStream(new ByteArrayInputStream(payload))) {
            byte version = input.readByte();
            EnumMap<ClientGraphicBlock, byte[]> graphics = new EnumMap<>(ClientGraphicBlock.class);
            for (ClientGraphicBlock block : ClientGraphicBlock.values()) {
                int length = input.readInt();
                graphics.put(block, readBlock(input, length, block.name()));
            }
            int groupCount = readSignedCount(input, "task route groups");
            List<List<TaskRouteAsset>> groups = new ArrayList<>(groupCount);
            for (int groupIndex = 0; groupIndex < groupCount; groupIndex++) {
                int routeCount = readSignedCount(input, "task routes");
                List<TaskRouteAsset> group = new ArrayList<>(routeCount);
                for (int routeIndex = 0; routeIndex < routeCount; routeIndex++) {
                    group.add(new TaskRouteAsset(input.readByte(), input.readByte()));
                }
                groups.add(group);
            }
            int experienceCount = readSignedCount(input, "experience thresholds");
            long[] experience = new long[experienceCount];
            for (int index = 0; index < experienceCount; index++) {
                experience[index] = input.readLong();
            }
            EnumMap<ProgressionTable, int[]> progression = new EnumMap<>(ProgressionTable.class);
            for (ProgressionTable table : ProgressionTable.values()) {
                int count = readSignedCount(input, table.name());
                int[] values = new int[count];
                for (int index = 0; index < count; index++) {
                    values[index] = input.readInt();
                }
                progression.put(table, values);
            }
            byte[] effectTemplates = input.readAllBytes();
            return new DataAssetBundle(version, graphics, groups, experience, progression, effectTemplates);
        }
    }

    /** Đọc block có length-prefix và bảo đảm length không vượt số byte còn lại. */
    private static byte[] readBlock(DataInputStream input, int length, String name) throws IOException {
        if (length < 0 || length > input.available()) {
            throw new EOFException(name + " block length exceeds remaining payload");
        }
        byte[] data = new byte[length];
        input.readFully(data);
        return data;
    }

    /** Đọc signed-byte count và từ chối giá trị âm. */
    private static int readSignedCount(DataInputStream input, String name) throws IOException {
        int count = input.readByte();
        if (count < 0) {
            throw new IOException(name + " count is negative");
        }
        return count;
    }

    /** Kiểm soát count mà client đọc bằng readByte. */
    private static void requireSignedCount(int count, String name) {
        if (count > MAX_SIGNED_BYTE_COUNT) {
            throw new IllegalArgumentException(name + " count exceeds wire limit 127");
        }
    }
}
