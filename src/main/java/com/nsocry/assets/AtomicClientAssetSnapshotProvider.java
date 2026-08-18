package com.nsocry.assets;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/** Provider cho phép thay toàn bộ asset snapshot nguyên tử mà session đang chạy không bị khóa. */
public final class AtomicClientAssetSnapshotProvider implements ClientAssetSnapshotProvider {
    private final AtomicReference<ClientAssetSnapshot> current;

    /** Khởi tạo provider bằng một snapshot hoàn chỉnh bắt buộc. */
    public AtomicClientAssetSnapshotProvider(ClientAssetSnapshot initialSnapshot) {
        current = new AtomicReference<>(Objects.requireNonNull(initialSnapshot, "initialSnapshot"));
    }

    @Override
    /** Trả đúng một snapshot nhất quán tại thời điểm đọc. */
    public ClientAssetSnapshot currentSnapshot() {
        return current.get();
    }

    /** Publish snapshot hoàn chỉnh mới trong một thao tác nguyên tử. */
    public void publish(ClientAssetSnapshot snapshot) {
        current.set(Objects.requireNonNull(snapshot, "snapshot"));
    }
}
