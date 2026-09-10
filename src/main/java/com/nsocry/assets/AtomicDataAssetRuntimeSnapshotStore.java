package com.nsocry.assets;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/** Kho runtime thay toàn bộ DATA snapshot nguyên tử, không lộ trạng thái bán phần. */
public final class AtomicDataAssetRuntimeSnapshotStore {
    private final AtomicReference<DataAssetRuntimeSnapshot> current = new AtomicReference<>();

    /** Trả snapshot hiện hành hoặc rỗng khi chưa publish. */
    public Optional<DataAssetRuntimeSnapshot> currentSnapshot() {
        return Optional.ofNullable(current.get());
    }

    /**
     * Trả snapshot bắt buộc cho startup sau khi đối chiếu version và SHA-256 authoritative.
     * Listener phải gọi gate này trước khi bind; trạng thái rỗng hoặc lệch đều fail closed.
     */
    public DataAssetRuntimeSnapshot requireCurrent(byte expectedVersion, String expectedSha256) {
        Objects.requireNonNull(expectedSha256, "expectedSha256");
        DataAssetRuntimeSnapshot snapshot = current.get();
        if (snapshot == null) {
            throw new IllegalStateException("DATA runtime snapshot chưa sẵn sàng");
        }
        if (snapshot.version() != expectedVersion
                || !snapshot.payloadSha256().equals(expectedSha256)) {
            throw new IllegalStateException("DATA runtime snapshot không khớp bản authoritative");
        }
        return snapshot;
    }

    /** Atomic swap đúng một snapshot hoàn chỉnh. */
    public void publish(DataAssetRuntimeSnapshot snapshot) {
        current.set(Objects.requireNonNull(snapshot, "snapshot"));
    }
}
