package com.nsocry.assets;

import java.util.Arrays;
import java.util.Objects;

/** DATA candidate bất biến gồm payload đã kiểm định và manifest xác định. */
public final class DataAssetSeedArtifact {
    private final byte[] payload;
    private final DataAssetSeedManifest manifest;
    private final String manifestText;

    DataAssetSeedArtifact(byte[] payload, DataAssetSeedManifest manifest, String manifestText) {
        this.payload = Arrays.copyOf(Objects.requireNonNull(payload, "payload"), payload.length);
        this.manifest = Objects.requireNonNull(manifest, "manifest");
        this.manifestText = Objects.requireNonNull(manifestText, "manifestText");
    }

    /** Trả defensive copy để caller không sửa candidate đã khóa checksum. */
    public byte[] payload() {
        return Arrays.copyOf(payload, payload.length);
    }

    /** Trả manifest đã dùng để tự kiểm định candidate. */
    public DataAssetSeedManifest manifest() {
        return manifest;
    }

    /** Trả manifest UTF-8 key=value với thứ tự dòng cố định. */
    public String manifestText() {
        return manifestText;
    }
}
