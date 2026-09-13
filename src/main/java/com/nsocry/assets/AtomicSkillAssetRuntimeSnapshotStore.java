package com.nsocry.assets;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/** Kho runtime thay toàn bộ SKILL snapshot nguyên tử và không cho thấy trạng thái bán phần. */
public final class AtomicSkillAssetRuntimeSnapshotStore {
    private final AtomicReference<SkillAssetRuntimeSnapshot> current = new AtomicReference<>();

    /** Trả snapshot đã publish, hoặc rỗng khi startup chưa hoàn tất gate. */
    public Optional<SkillAssetRuntimeSnapshot> currentSnapshot() {
        return Optional.ofNullable(current.get());
    }

    /** Publish đúng một snapshot đã hoàn chỉnh bằng atomic swap. */
    public void publish(SkillAssetRuntimeSnapshot snapshot) {
        current.set(Objects.requireNonNull(snapshot, "snapshot"));
    }
}
