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

    public byte[] payload() {
        return Arrays.copyOf(payload, payload.length);
    }

    public String manifestText() {
        return manifestText;
    }

    public SkillAssetSeedValidationResult validation() {
        return validation;
    }
}
