package com.nsocry.operations;

import com.nsocry.assets.MapAssetSeedValidationResult;
import java.util.Arrays;
import java.util.Objects;

/** Nội dung MAP archive đã vượt codec, manifest, count và checksum validation. */
public final class ValidatedMapAssetSeedArchive {
    private final byte[] payload;
    private final String manifestText;
    private final MapAssetSeedValidationResult validation;

    ValidatedMapAssetSeedArchive(
            byte[] payload,
            String manifestText,
            MapAssetSeedValidationResult validation) {
        this.payload = Arrays.copyOf(Objects.requireNonNull(payload, "payload"), payload.length);
        this.manifestText = Objects.requireNonNull(manifestText, "manifestText");
        this.validation = Objects.requireNonNull(validation, "validation");
    }

    /** Trả defensive copy cho importer tương lai. */
    public byte[] payload() {
        return Arrays.copyOf(payload, payload.length);
    }

    /** Trả manifest đã được dùng để xác minh payload. */
    public String manifestText() {
        return manifestText;
    }

    /** Trả count/length/checksum đã xác minh. */
    public MapAssetSeedValidationResult validation() {
        return validation;
    }
}
