package com.nsocry.assets;

/** Cổng publish nguyên tử một snapshot đã build và kiểm tra đầy đủ. */
@FunctionalInterface
public interface ClientAssetSnapshotPublisher {
    /** Thay snapshot hiện hành bằng một snapshot hoàn chỉnh. */
    void publish(ClientAssetSnapshot snapshot);
}
