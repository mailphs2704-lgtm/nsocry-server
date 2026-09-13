package com.nsocry.assets;

import java.util.Arrays;
import java.util.Objects;

/** Artifact seed ITEM bất biến gồm payload codec và manifest văn bản xác định. */
public final class ItemAssetSeedArtifact {
    private final byte[] payload;
    private final ItemAssetSeedManifest manifest;
    private final ItemAssetValidationResult validation;
    private final String manifestText;

    /** Chỉ generator trong package được tạo artifact sau khi validation thành công. */
    ItemAssetSeedArtifact(
            byte[] payload,
            ItemAssetSeedManifest manifest,
            ItemAssetValidationResult validation,
            String manifestText) {
        this.payload = Arrays.copyOf(Objects.requireNonNull(payload, "payload"), payload.length);
        this.manifest = Objects.requireNonNull(manifest, "manifest");
        this.validation = Objects.requireNonNull(validation, "validation");
        this.manifestText = Objects.requireNonNull(manifestText, "manifestText");
    }

    /** Trả bản sao payload để bên gọi không thể sửa artifact đã kiểm định. */
    public byte[] payload() {
        return Arrays.copyOf(payload, payload.length);
    }

    /** Trả manifest có thể dùng để kiểm định lại trước khi import. */
    public ItemAssetSeedManifest manifest() {
        return manifest;
    }

    /** Trả metadata validation của payload. */
    public ItemAssetValidationResult validation() {
        return validation;
    }

    /** Trả manifest dạng UTF-8 key=value có thứ tự dòng cố định. */
    public String manifestText() {
        return manifestText;
    }
}
