package com.nsocry.assets;

import com.nsocry.protocol.compat.ClientDataSet;
import com.nsocry.protocol.compat.ClientVersionManifest;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Ảnh chụp bất biến của toàn bộ asset client cần trong một lần thương lượng phiên bản.
 * Session chỉ đọc snapshot đã hoàn chỉnh, không tự truy database hoặc lắp ghép payload.
 */
public final class ClientAssetSnapshot {
    private final ClientVersionManifest manifest;
    private final byte[] appearanceData;
    private final EnumMap<ClientDataSet, byte[]> payloads;

    /** Tạo snapshot và xác minh byte phiên bản đầu mỗi payload khớp manifest. */
    public ClientAssetSnapshot(
            ClientVersionManifest manifest,
            byte[] appearanceData,
            Map<ClientDataSet, byte[]> payloads) {
        this.manifest = Objects.requireNonNull(manifest, "manifest");
        this.appearanceData = copy(appearanceData, "appearanceData");
        Objects.requireNonNull(payloads, "payloads");
        this.payloads = new EnumMap<>(ClientDataSet.class);
        for (ClientDataSet dataSet : ClientDataSet.values()) {
            byte[] payload = copy(payloads.get(dataSet), dataSet.name());
            if (payload.length == 0) {
                throw new IllegalArgumentException(dataSet + " payload must contain its version byte");
            }
            byte expectedVersion = versionOf(dataSet);
            if (payload[0] != expectedVersion) {
                throw new IllegalArgumentException(dataSet + " payload version does not match manifest");
            }
            this.payloads.put(dataSet, payload);
        }
    }

    /** Trả manifest phiên bản gắn cố định với snapshot này. */
    public ClientVersionManifest manifest() {
        return manifest;
    }

    /** Trả bản sao dữ liệu ngoại hình nối sau header UPDATE_VERSION. */
    public byte[] appearanceData() {
        return Arrays.copyOf(appearanceData, appearanceData.length);
    }

    /** Trả bản sao payload của bộ dữ liệu được yêu cầu. */
    public byte[] payload(ClientDataSet dataSet) {
        Objects.requireNonNull(dataSet, "dataSet");
        byte[] payload = payloads.get(dataSet);
        return Arrays.copyOf(payload, payload.length);
    }

    /** Lấy byte phiên bản tương ứng trong manifest. */
    private byte versionOf(ClientDataSet dataSet) {
        return switch (dataSet) {
            case DATA -> manifest.dataVersion();
            case MAP -> manifest.mapVersion();
            case SKILL -> manifest.skillVersion();
            case ITEM -> manifest.itemVersion();
        };
    }

    /** Sao chép phòng vệ và báo đúng tên thành phần nếu đầu vào null. */
    private static byte[] copy(byte[] value, String name) {
        Objects.requireNonNull(value, name);
        return Arrays.copyOf(value, value.length);
    }
}
