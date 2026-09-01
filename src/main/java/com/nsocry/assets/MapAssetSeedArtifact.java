package com.nsocry.assets;

import java.util.Arrays;
import java.util.Objects;

/** MAP seed artifact bất biến gồm payload codec, manifest text và validation result. */
public final class MapAssetSeedArtifact {
    private final byte[] payload;
    private final String manifestText;
    private final MapAssetSeedValidationResult validation;

    MapAssetSeedArtifact(
            byte[] payload,
            String manifestText,
            MapAssetSeedValidationResult validation) {
        this.payload = Arrays.copyOf(
                Objects.requireNonNull(payload, "payload"), payload.length);
        this.manifestText = Objects.requireNonNull(manifestText, "manifestText");
        this.validation = Objects.requireNonNull(validation, "validation");
    }

    /** Trả defensive copy để caller không thể sửa candidate đã kiểm định. */
    public byte[] payload() {
        return Arrays.copyOf(payload, payload.length);
    }

    /** Trả manifest UTF-8 xác định dùng khi ghi archive. */
    public String manifestText() {
        return manifestText;
    }

    /** Trả metadata đã xác minh của đúng payload candidate. */
    public MapAssetSeedValidationResult validation() {
        return validation;
    }
}
