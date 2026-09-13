package com.nsocry.assets;

import java.util.List;
import java.util.Objects;

/** Metadata và các cấp độ của một mẫu kỹ năng thuộc một môn phái. */
public record SkillTemplateAsset(
        byte id,
        String name,
        byte maxPoint,
        byte type,
        short icon,
        String description,
        List<SkillLevelAsset> levels) {

    /** Từ chối chuỗi null và sao chép danh sách cấp độ. */
    public SkillTemplateAsset {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(description, "description");
        Objects.requireNonNull(levels, "levels");
        levels = List.copyOf(levels);
    }
}
