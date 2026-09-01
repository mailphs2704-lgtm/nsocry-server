package com.nsocry.assets;

import java.util.Arrays;
import java.util.Objects;

/** Artifact SKILL bất biến gồm payload codec và manifest UTF-8 xác định. */
public final class SkillAssetSeedArtifact {
    private final byte[] payload;
    private final String manifestText;
    private final SkillAssetSeedValidationResult validation;

    SkillAssetSeedArtifact(byte[] payload, String manifestText, SkillAssetSeedValidationResult validation) {
        this.payload = Arrays.copyOf(Objects.requireNonNull(payload, "payload"), payload.length);
        this.manifestText = Objects.requireNonNull(manifestText, "manifestText");
        this.validation = Objects.requireNonNull(validation, "validation");
    }

    /** Trả defensive copy của payload seed. */
    public byte[] payload() {
        return Arrays.copyOf(payload, payload.length);
    }

    /** Trả manifest UTF-8 xác định đi kèm payload. */
    public String manifestText() {
        return manifestText;
    }

    /** Trả metadata/checksum đã xác minh của candidate. */
    public SkillAssetSeedValidationResult validation() {
        return validation;
    }
}
