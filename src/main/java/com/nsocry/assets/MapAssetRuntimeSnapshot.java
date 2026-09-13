package com.nsocry.assets;

import java.util.Arrays;
import java.util.Objects;

/** Snapshot MAP bất biến đã vượt version, count, length và checksum gate. */
public final class MapAssetRuntimeSnapshot {
    private final byte version;
    private final int mapCount;
    private final int npcCount;
    private final int mobCount;
    private final int payloadLength;
    private final String payloadSha256;
    private final byte[] payload;

    private MapAssetRuntimeSnapshot(MapAssetSeedValidationResult validation, byte[] payload) {
        Objects.requireNonNull(validation, "validation");
        this.payload = Arrays.copyOf(Objects.requireNonNull(payload, "payload"), payload.length);
        if (payload.length != validation.payloadLength()) {
            throw new IllegalArgumentException("MAP payload length không khớp validation");
        }
        String actualSha256 = MapAssetSeedValidator.sha256(payload);
        if (!actualSha256.equals(validation.payloadSha256())) {
            throw new IllegalArgumentException("MAP payload SHA-256 không khớp validation");
        }
        version = validation.version();
        mapCount = validation.mapCount();
        npcCount = validation.npcCount();
        mobCount = validation.mobCount();
        payloadLength = validation.payloadLength();
        payloadSha256 = validation.payloadSha256();
    }

    /** Factory duy nhất: từ chối payload nếu length hoặc checksum lệch validation. */
    static MapAssetRuntimeSnapshot verified(MapAssetSeedValidationResult validation, byte[] payload) {
        return new MapAssetRuntimeSnapshot(validation, payload);
    }

    /** Trả version wire đã khóa. */
    public byte version() { return version; }
    /** Trả số map name trong payload. */
    public int mapCount() { return mapCount; }
    /** Trả số NPC template trong payload. */
    public int npcCount() { return npcCount; }
    /** Trả số mob template trong payload. */
    public int mobCount() { return mobCount; }
    /** Trả độ dài payload đã khóa. */
    public int payloadLength() { return payloadLength; }
    /** Trả SHA-256 đã đối chiếu với payload. */
    public String payloadSha256() { return payloadSha256; }
    /** Trả defensive copy để session không sửa snapshot dùng chung. */
    public byte[] payload() { return Arrays.copyOf(payload, payload.length); }
}
