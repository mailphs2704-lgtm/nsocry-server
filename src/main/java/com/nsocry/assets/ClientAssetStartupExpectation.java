package com.nsocry.assets;

import com.nsocry.protocol.compat.ClientDataSet;
import com.nsocry.protocol.compat.ClientVersionManifest;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Tiêu chuẩn tối thiểu mà một bộ asset client phải đạt trước khi server được phép phục vụ.
 * Giá trị được cấu hình từ artifact đã khóa, không được suy đoán từ snapshot sắp publish.
 */
public final class ClientAssetStartupExpectation {
    private final ClientVersionManifest manifest;
    private final EnumMap<ClientDataSet, Integer> minimumPayloadLengths;
    private final int minimumAppearanceLength;

    /** Tạo tiêu chuẩn gồm đúng bốn payload DATA/MAP/SKILL/ITEM và phần appearance. */
    public ClientAssetStartupExpectation(
            ClientVersionManifest manifest,
            Map<ClientDataSet, Integer> minimumPayloadLengths,
            int minimumAppearanceLength) {
        this.manifest = Objects.requireNonNull(manifest, "manifest");
        Objects.requireNonNull(minimumPayloadLengths, "minimumPayloadLengths");
        this.minimumPayloadLengths = new EnumMap<>(ClientDataSet.class);
        for (ClientDataSet dataSet : ClientDataSet.values()) {
            Integer length = Objects.requireNonNull(
                    minimumPayloadLengths.get(dataSet), dataSet + " minimum payload length");
            if (length < 1) {
                throw new IllegalArgumentException(dataSet + " minimum payload length must be positive");
            }
            this.minimumPayloadLengths.put(dataSet, length);
        }
        if (minimumAppearanceLength < 1) {
            throw new IllegalArgumentException("minimumAppearanceLength must be positive");
        }
        this.minimumAppearanceLength = minimumAppearanceLength;
    }

    /** Manifest phiên bản đã được quản trị viên khóa cho lần startup. */
    public ClientVersionManifest manifest() {
        return manifest;
    }

    /** Kích thước payload tối thiểu của một nhóm dữ liệu, tính cả byte version. */
    public int minimumPayloadLength(ClientDataSet dataSet) {
        return minimumPayloadLengths.get(Objects.requireNonNull(dataSet, "dataSet"));
    }

    /** Kích thước tối thiểu của appearance payload. */
    public int minimumAppearanceLength() {
        return minimumAppearanceLength;
    }
}
