package com.nsocry.operations;

import com.nsocry.assets.SkillAssetSeedValidationResult;
import java.util.Arrays;
import java.util.Objects;

/** Nội dung SKILL archive đã qua codec, manifest, checksum và raw-byte validation. */
public final class ValidatedSkillAssetSeedArchive {
    private final byte[] payload;
    private final String manifestText;
    private final SkillAssetSeedValidationResult validation;

    ValidatedSkillAssetSeedArchive(
            byte[] payload,
            String manifestText,
            SkillAssetSeedValidationResult validation) {
        this.payload = Arrays.copyOf(Objects.requireNonNull(payload, "payload"), payload.length);
        this.manifestText = Objects.requireNonNull(manifestText, "manifestText");
        this.validation = Objects.requireNonNull(validation, "validation");
    }

    /** Trả defensive copy dùng cho transactional importer. */
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
