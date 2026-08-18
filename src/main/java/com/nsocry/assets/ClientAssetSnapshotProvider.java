package com.nsocry.assets;

/** Cổng cung cấp snapshot asset hoàn chỉnh cho tầng session mà không lộ nguồn lưu trữ bên dưới. */
@FunctionalInterface
public interface ClientAssetSnapshotProvider {
    /** Trả snapshot hiện hành; provider phải thay cả snapshot theo một thao tác nguyên tử khi reload. */
    ClientAssetSnapshot currentSnapshot();
}
