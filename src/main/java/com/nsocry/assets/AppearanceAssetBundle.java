package com.nsocry.assets;

import java.util.List;
import java.util.Objects;

/** Read model đầy đủ cho phần appearance nối sau bốn byte version. */
public record AppearanceAssetBundle(
        List<AppearancePartAsset> jumpingHeads,
        List<AppearancePartAsset> normalHeads,
        List<AppearancePartAsset> coveredHeads,
        List<LegAppearanceAsset> legs,
        List<AppearancePartAsset> jumpingBodies,
        List<AppearancePartAsset> normalBodies,
        List<AppearancePartAsset> coveredBodies,
        List<MountAppearanceAsset> mounts) {

    /** Sao chép danh sách và bảo đảm ba biến thể head/body có count giống nhau. */
    public AppearanceAssetBundle {
        jumpingHeads = copy(jumpingHeads, "jumpingHeads");
        normalHeads = copy(normalHeads, "normalHeads");
        coveredHeads = copy(coveredHeads, "coveredHeads");
        legs = copy(legs, "legs");
        jumpingBodies = copy(jumpingBodies, "jumpingBodies");
        normalBodies = copy(normalBodies, "normalBodies");
        coveredBodies = copy(coveredBodies, "coveredBodies");
        mounts = copy(mounts, "mounts");
        requireSameSize(jumpingHeads, normalHeads, coveredHeads, "head variants");
        requireSameSize(jumpingBodies, normalBodies, coveredBodies, "body variants");
    }

    /** Sao chép một danh sách không-null. */
    private static <T> List<T> copy(List<T> source, String name) {
        Objects.requireNonNull(source, name);
        return List.copyOf(source);
    }

    /** Kiểm tra ba biến thể dùng chung count byte trên wire. */
    private static void requireSameSize(List<?> first, List<?> second, List<?> third, String name) {
        if (first.size() != second.size() || first.size() != third.size()) {
            throw new IllegalArgumentException(name + " must have identical counts");
        }
    }
}
