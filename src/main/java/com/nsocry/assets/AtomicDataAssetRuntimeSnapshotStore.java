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

    /** Atomic swap đúng một snapshot hoàn chỉnh. */
    public void publish(DataAssetRuntimeSnapshot snapshot) {
        current.set(Objects.requireNonNull(snapshot, "snapshot"));
    }
}
