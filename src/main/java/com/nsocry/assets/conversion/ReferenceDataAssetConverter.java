package com.nsocry.assets.conversion;

import com.nsocry.assets.ClientGraphicBlock;
import com.nsocry.assets.DataAssetBundle;
import com.nsocry.assets.ProgressionTable;
import com.nsocry.assets.TaskRouteAsset;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Ghép dump DATA authoritative và progression đã truy vết thành bundle client V7 hoàn chỉnh. */
public final class ReferenceDataAssetConverter {
    private static final int MAX_SIGNED_BYTE_COUNT = 127;
    private static final int MAX_UNSIGNED_BYTE_COUNT = 255;

    private ReferenceDataAssetConverter() {
    }

    /**
     * Tạo bundle bất biến mà không mở database hoặc publish runtime.
     *
     * @param dump dump SQL chứa đúng tám nguồn DATA đã kiểm kê
     * @param baseVersion version DATA gốc theo miền raw byte 0..255
     * @param maxPercentAdd hệ số cộng cho MAX_PERCENT; giá trị dương đồng thời tăng version một đơn vị
     * @param progression đủ mười bảng progression authoritative theo thứ tự enum
     * @return bundle sẵn sàng để DataAssetCodec encode
     */
    public static DataAssetBundle convert(
            String dump,
            int baseVersion,
            double maxPercentAdd,
            Map<ProgressionTable, int[]> progression) {
        Objects.requireNonNull(dump, "dump");
        validateConfiguration(baseVersion, maxPercentAdd);
        ReferenceDataDumpInventoryParser.parse(dump);

        EnumMap<ClientGraphicBlock, byte[]> graphics = ReferenceDataWireEncoder.encodeGraphics(dump);
        List<List<TaskRouteAsset>> taskRoutes = taskRoutes(dump);
        long[] experience = experience(dump);
        EnumMap<ProgressionTable, int[]> tables = progression(progression, maxPercentAdd);
        byte[] effectTemplates = ReferenceDataWireEncoder.encodeEffectTemplates(dump);
        int version = baseVersion + (maxPercentAdd > 0 ? 1 : 0);
        if (version > 255) {
            throw new IllegalArgumentException("DATA version vượt raw byte sau khi tăng: " + version);
        }
        return new DataAssetBundle(
                (byte) version, graphics, taskRoutes, experience, tables, effectTemplates);
    }

    /** Parse từng row task thành các cặp NPC/map, giữ nguyên thứ tự dump và thứ tự JSON. */
    private static List<List<TaskRouteAsset>> taskRoutes(String dump) {
        List<List<String>> rows = ReferenceDataDumpInventoryParser.rows(
                dump, ReferenceDataDumpInventoryParser.TASK_MARKER, 3, "task");
        requireSignedCount(rows.size(), "task group count");
        List<List<TaskRouteAsset>> groups = new ArrayList<>(rows.size());
        for (List<String> row : rows) {
            List<?> npcs = ReferenceDataDumpInventoryParser.array(row.get(1), "task.npcs");
            List<?> maps = ReferenceDataDumpInventoryParser.array(row.get(2), "task.maps");
            if (npcs.size() != maps.size()) {
                throw new IllegalArgumentException("task npcs/maps lệch chiều dài");
            }
            requireSignedCount(npcs.size(), "task route count");
            List<TaskRouteAsset> routes = new ArrayList<>(npcs.size());
            for (int index = 0; index < npcs.size(); index++) {
                routes.add(new TaskRouteAsset(
                        wireByte(ReferenceDataDumpInventoryParser.number(npcs.get(index), "task npc"), "task npc"),
                        wireByte(ReferenceDataDumpInventoryParser.number(maps.get(index), "task map"), "task map")));
            }
            groups.add(List.copyOf(routes));
        }
        return List.copyOf(groups);
    }

    /** Lấy đúng row others.exp và bảo toàn toàn bộ giá trị long theo thứ tự JSON. */
    private static long[] experience(String dump) {
        List<List<String>> rows = ReferenceDataDumpInventoryParser.rows(
                dump, ReferenceDataDumpInventoryParser.OTHERS_MARKER, 3, "others");
        List<?> values = null;
        for (List<String> row : rows) {
            if ("exp".equals(row.get(1))) {
                if (values != null) {
                    throw new IllegalArgumentException("others phải có đúng một row exp");
                }
                values = ReferenceDataDumpInventoryParser.array(row.get(2), "others.exp");
            }
        }
        if (values == null) {
            throw new IllegalArgumentException("others thiếu row exp");
        }
        requireUnsignedCount(values.size(), "experience count");
        long[] result = new long[values.size()];
        for (int index = 0; index < values.size(); index++) {
            result[index] = ReferenceDataDumpInventoryParser.number(values.get(index), "experience");
        }
        return result;
    }

    /** Sao chép đủ mười bảng và chỉ biến đổi MAX_PERCENT khi cấu hình yêu cầu. */
    private static EnumMap<ProgressionTable, int[]> progression(
            Map<ProgressionTable, int[]> source,
            double maxPercentAdd) {
        Objects.requireNonNull(source, "progression");
        EnumMap<ProgressionTable, int[]> result = new EnumMap<>(ProgressionTable.class);
        for (ProgressionTable table : ProgressionTable.values()) {
            int[] values = Objects.requireNonNull(source.get(table), table.name());
            requireSignedCount(values.length, table.name());
            int[] copy = values.clone();
            if (table == ProgressionTable.MAX_PERCENT && maxPercentAdd > 0) {
                for (int index = 0; index < copy.length; index++) {
                    copy[index] = (int) (copy[index] + copy[index] * maxPercentAdd);
                }
            }
            result.put(table, copy);
        }
        return result;
    }

    /** Khóa version raw-byte và hệ số hữu hạn, không âm trước khi tạo candidate. */
    private static void validateConfiguration(int baseVersion, double maxPercentAdd) {
        if (baseVersion < 0 || baseVersion > 255) {
            throw new IllegalArgumentException("DATA version phải nằm trong 0..255");
        }
        if (!Double.isFinite(maxPercentAdd) || maxPercentAdd < 0) {
            throw new IllegalArgumentException("maxPercentAdd phải hữu hạn và không âm");
        }
    }

    /** EXP count có wire unsigned byte theo dữ liệu live 131 phần tử. */
    private static void requireUnsignedCount(int count, String field) {
        if (count > MAX_UNSIGNED_BYTE_COUNT) {
            throw new IllegalArgumentException(field + " vượt giới hạn 255");
        }
    }

    /** Bảo toàn bit pattern raw byte 128..255 thay vì từ chối do Java byte có dấu. */
    private static byte wireByte(long value, String field) {
        if (value < Byte.MIN_VALUE || value > 255) {
            throw new IllegalArgumentException(field + " vượt raw byte: " + value);
        }
        return (byte) value;
    }

    /** Kiểm soát các count mà client đọc bằng readByte. */
    private static void requireSignedCount(int count, String field) {
        if (count > MAX_SIGNED_BYTE_COUNT) {
            throw new IllegalArgumentException(field + " vượt giới hạn 127");
        }
    }
}
