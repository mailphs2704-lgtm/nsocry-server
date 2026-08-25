package com.nsocry.assets;

import java.io.IOException;
import java.util.Objects;

/** Đọc, xác minh và publish SKILL runtime theo nguyên tắc tất cả hoặc không. */
public final class SkillAssetRuntimePublishService {
    private final SkillAssetSource source;
    private final SkillAssetSeedManifest manifest;
    private final AtomicSkillAssetRuntimeSnapshotStore store;

    public SkillAssetRuntimePublishService(
            SkillAssetSource source,
            SkillAssetSeedManifest manifest,
            AtomicSkillAssetRuntimeSnapshotStore store) {
        this.source = Objects.requireNonNull(source, "source");
        this.manifest = Objects.requireNonNull(manifest, "manifest");
        this.store = Objects.requireNonNull(store, "store");
    }

    /** Publish sau khi bundle database khớp version, count, payload length và SHA-256 đã khóa. */
    public SkillAssetRuntimeSnapshot rebuildAndPublish() throws ClientAssetSourceException, IOException {
        SkillAssetBundle bundle = Objects.requireNonNull(source.load(), "skill source result");
        SkillAssetSeedValidationResult validation = SkillAssetSeedValidator.validate(bundle, manifest);
        byte[] payload = SkillAssetCodec.encode(bundle);
        SkillAssetRuntimeSnapshot snapshot = SkillAssetRuntimeSnapshot.verified(validation, payload);
        store.publish(snapshot);
        return snapshot;
    }
}
