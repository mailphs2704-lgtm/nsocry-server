package com.nsocry.bootstrap;

import com.nsocry.assets.AtomicDataAssetRuntimeSnapshotStore;
import com.nsocry.assets.DataAssetRuntimePublishService;
import com.nsocry.assets.DataAssetSeedManifest;
import com.nsocry.assets.DataAssetSource;
import java.util.Objects;

/**
 * Readiness production điều phối source → validate → publish → identity gate trước TCP bind.
 */
public final class DataAssetServerStartupReadiness
        implements NsocryServerApplication.StartupReadiness {
    private final DataAssetRuntimePublishService publisher;
    private final DataAssetSeedManifest manifest;
    private final AtomicDataAssetRuntimeSnapshotStore store;

    /** Tạo readiness từ source JDBC-compatible, manifest authoritative và store do server sở hữu. */
    public DataAssetServerStartupReadiness(
            DataAssetSource source,
            DataAssetSeedManifest manifest,
            AtomicDataAssetRuntimeSnapshotStore store) {
        this.manifest = Objects.requireNonNull(manifest, "manifest");
        this.store = Objects.requireNonNull(store, "store");
        publisher = new DataAssetRuntimePublishService(
                Objects.requireNonNull(source, "source"), manifest, store);
    }

    /** Publish và bắt buộc snapshot hiện hành khớp identity trước khi trả quyền bind listener. */
    @Override
    public void verify() {
        try {
            publisher.rebuildAndPublish();
            store.requireCurrent(manifest.version(), manifest.payloadSha256());
        } catch (RuntimeException failure) {
            throw failure;
        } catch (Exception failure) {
            throw new IllegalStateException("Không thể chuẩn bị DATA runtime cho server startup", failure);
        }
    }
}
