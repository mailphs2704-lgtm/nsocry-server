package com.nsocry.assets;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/** Kho runtime thay toàn bộ MAP snapshot nguyên tử, không lộ trạng thái bán phần. */
public final class AtomicMapAssetRuntimeSnapshotStore {
    private final AtomicReference<MapAssetRuntimeSnapshot> current = new AtomicReference<>();

    /** Trả snapshot hiện hành hoặc rỗng khi chưa vượt gate. */
    public Optional<MapAssetRuntimeSnapshot> currentSnapshot() {
        return Optional.ofNullable(current.get());
    }

    /** Publish một snapshot hoàn chỉnh bằng atomic swap. */
    public void publish(MapAssetRuntimeSnapshot snapshot) {
        current.set(Objects.requireNonNull(snapshot, "snapshot"));
    }
}
