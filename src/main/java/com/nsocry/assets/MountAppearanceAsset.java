package com.nsocry.assets;

import java.util.List;
import java.util.Objects;

/** Dữ liệu ngoại hình thú cưỡi gồm item id và đúng sáu dãy frame. */
public record MountAppearanceAsset(short itemId, List<List<Short>> frameGroups) {
    private static final int REQUIRED_GROUPS = 6;

    /** Sao chép sâu và bắt buộc đúng sáu group như parser client. */
    public MountAppearanceAsset {
        Objects.requireNonNull(frameGroups, "frameGroups");
        if (frameGroups.size() != REQUIRED_GROUPS) {
            throw new IllegalArgumentException("mount appearance requires exactly 6 frame groups");
        }
        frameGroups = frameGroups.stream().map(List::copyOf).toList();
    }
}
