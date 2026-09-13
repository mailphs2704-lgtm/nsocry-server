package com.nsocry.bootstrap;

import com.nsocry.assets.AtomicDataAssetRuntimeSnapshotStore;
import com.nsocry.assets.DataAssetRuntimePublishService;
import com.nsocry.assets.DataAssetSeedManifest;
import com.nsocry.assets.DataAssetSource;
import com.nsocry.persistence.JdbcDataAssetSource;
import java.util.Objects;
import javax.sql.DataSource;

/**
 * Readiness production điều phối source → validate → publish → identity gate trước TCP bind.
 */
public final class DataAssetServerStartupReadiness
        implements NsocryServerApplication.StartupReadiness {
    public static final byte AUTHORITATIVE_VERSION = 7;
    public static final int AUTHORITATIVE_TASK_GROUP_COUNT = 43;
    public static final int AUTHORITATIVE_EXPERIENCE_COUNT = 131;
    public static final int AUTHORITATIVE_PAYLOAD_LENGTH = 85154;
    public static final String AUTHORITATIVE_PAYLOAD_SHA256 =
            "242a3551cc110c4eda9f8e40f06fcd0f0b0b2d32bcab6f1b07669dbd0c9b148b";
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

    /** Tạo readiness production khóa đúng DATA v7 authoritative đã import và read-back VERIFIED. */
    public static DataAssetServerStartupReadiness authoritativeV7(
            DataSource dataSource,
            AtomicDataAssetRuntimeSnapshotStore store) {
        Objects.requireNonNull(dataSource, "dataSource");
        DataAssetSeedManifest manifest = new DataAssetSeedManifest(
                AUTHORITATIVE_VERSION,
                AUTHORITATIVE_TASK_GROUP_COUNT,
                AUTHORITATIVE_EXPERIENCE_COUNT,
                AUTHORITATIVE_PAYLOAD_LENGTH,
                AUTHORITATIVE_PAYLOAD_SHA256);
        return new DataAssetServerStartupReadiness(
                new JdbcDataAssetSource(dataSource, AUTHORITATIVE_VERSION), manifest, store);
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
