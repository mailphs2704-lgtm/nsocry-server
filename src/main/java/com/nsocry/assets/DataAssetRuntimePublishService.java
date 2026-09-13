package com.nsocry.assets;

import java.io.IOException;
import java.util.Objects;

/** Đọc, xác minh và publish DATA runtime theo nguyên tắc tất cả hoặc không. */
public final class DataAssetRuntimePublishService {
    private final DataAssetSource source;
    private final DataAssetSeedManifest manifest;
    private final AtomicDataAssetRuntimeSnapshotStore store;

    /** Tạo service từ source, manifest authoritative và atomic store. */
    public DataAssetRuntimePublishService(
            DataAssetSource source,
            DataAssetSeedManifest manifest,
            AtomicDataAssetRuntimeSnapshotStore store) {
        this.source = Objects.requireNonNull(source, "source");
        this.manifest = Objects.requireNonNull(manifest, "manifest");
        this.store = Objects.requireNonNull(store, "store");
    }

    /** Publish chỉ sau khi bundle database khớp toàn bộ manifest. */
    public DataAssetRuntimeSnapshot rebuildAndPublish()
            throws ClientAssetSourceException, IOException {
        DataAssetBundle bundle = Objects.requireNonNull(source.load(), "data source result");
        DataAssetSeedValidationResult validation = DataAssetSeedValidator.validate(bundle, manifest);
        byte[] payload = DataAssetCodec.encode(bundle);
        DataAssetRuntimeSnapshot snapshot = DataAssetRuntimeSnapshot.verified(validation, payload);
        store.publish(snapshot);
        return snapshot;
    }
}
