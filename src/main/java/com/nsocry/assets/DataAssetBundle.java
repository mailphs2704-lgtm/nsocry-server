package com.nsocry.assets;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Read model bất biến của payload DATA tổng hợp. */
public final class DataAssetBundle {
    private final byte version;
    private final EnumMap<ClientGraphicBlock, byte[]> graphics;
    private final List<List<TaskRouteAsset>> taskRoutes;
    private final long[] experienceThresholds;
    private final EnumMap<ProgressionTable, int[]> progression;
    private final byte[] effectTemplates;

    /** Sao chép sâu mọi mảng và danh sách trước khi publish bundle. */
    public DataAssetBundle(
            byte version,
            Map<ClientGraphicBlock, byte[]> graphics,
            List<List<TaskRouteAsset>> taskRoutes,
            long[] experienceThresholds,
            Map<ProgressionTable, int[]> progression,
            byte[] effectTemplates) {
        this.version = version;
        this.graphics = copyGraphics(graphics);
        Objects.requireNonNull(taskRoutes, "taskRoutes");
        this.taskRoutes = taskRoutes.stream().map(List::copyOf).toList();
        Objects.requireNonNull(experienceThresholds, "experienceThresholds");
        this.experienceThresholds = Arrays.copyOf(experienceThresholds, experienceThresholds.length);
        this.progression = copyProgression(progression);
        Objects.requireNonNull(effectTemplates, "effectTemplates");
        this.effectTemplates = Arrays.copyOf(effectTemplates, effectTemplates.length);
    }

    public byte version() {
        return version;
    }

    /** Trả bản sao block graphics được yêu cầu. */
    public byte[] graphic(ClientGraphicBlock block) {
        Objects.requireNonNull(block, "block");
        byte[] data = graphics.get(block);
        return Arrays.copyOf(data, data.length);
    }

    public List<List<TaskRouteAsset>> taskRoutes() {
        return taskRoutes;
    }

    public long[] experienceThresholds() {
        return Arrays.copyOf(experienceThresholds, experienceThresholds.length);
    }

    /** Trả bản sao bảng progression được yêu cầu. */
    public int[] progression(ProgressionTable table) {
        Objects.requireNonNull(table, "table");
        int[] data = progression.get(table);
        return Arrays.copyOf(data, data.length);
    }

    public byte[] effectTemplates() {
        return Arrays.copyOf(effectTemplates, effectTemplates.length);
    }

    /** Sao chép và yêu cầu đủ năm graphics block. */
    private static EnumMap<ClientGraphicBlock, byte[]> copyGraphics(
            Map<ClientGraphicBlock, byte[]> source) {
        Objects.requireNonNull(source, "graphics");
        EnumMap<ClientGraphicBlock, byte[]> copy = new EnumMap<>(ClientGraphicBlock.class);
        for (ClientGraphicBlock block : ClientGraphicBlock.values()) {
            byte[] data = Objects.requireNonNull(source.get(block), block.name());
            copy.put(block, Arrays.copyOf(data, data.length));
        }
        return copy;
    }

    /** Sao chép và yêu cầu đủ mười bảng progression. */
    private static EnumMap<ProgressionTable, int[]> copyProgression(
            Map<ProgressionTable, int[]> source) {
        Objects.requireNonNull(source, "progression");
        EnumMap<ProgressionTable, int[]> copy = new EnumMap<>(ProgressionTable.class);
        for (ProgressionTable table : ProgressionTable.values()) {
            int[] data = Objects.requireNonNull(source.get(table), table.name());
            copy.put(table, Arrays.copyOf(data, data.length));
        }
        return copy;
    }
}
