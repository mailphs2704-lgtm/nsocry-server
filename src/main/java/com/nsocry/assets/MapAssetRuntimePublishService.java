package com.nsocry.assets;

import java.io.IOException;
import java.util.Objects;

/** Đọc, xác minh và publish MAP runtime theo nguyên tắc tất cả hoặc không. */
public final class MapAssetRuntimePublishService {
    private final MapAssetSource source;
    private final MapAssetSeedManifest manifest;
    private final AtomicMapAssetRuntimeSnapshotStore store;

    /** Tạo service từ source, manifest khóa và atomic store đích. */
    public MapAssetRuntimePublishService(
            MapAssetSource source,
            MapAssetSeedManifest manifest,
            AtomicMapAssetRuntimeSnapshotStore store) {
        this.source = Objects.requireNonNull(source, "source");
        this.manifest = Objects.requireNonNull(manifest, "manifest");
        this.store = Objects.requireNonNull(store, "store");
    }

    /** Publish chỉ sau khi JDBC bundle khớp version/count/length/SHA-256. */
    public MapAssetRuntimeSnapshot rebuildAndPublish()
            throws ClientAssetSourceException, IOException {
        MapAssetBundle bundle = Objects.requireNonNull(source.load(), "map source result");
        MapAssetSeedValidationResult validation = MapAssetSeedValidator.validate(bundle, manifest);
        byte[] payload = MapAssetCodec.encode(bundle);
        MapAssetRuntimeSnapshot snapshot = MapAssetRuntimeSnapshot.verified(validation, payload);
        store.publish(snapshot);
        return snapshot;
    }
}
