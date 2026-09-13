package com.nsocry.assets;

import java.util.List;
import java.util.Objects;

/** Dữ liệu một cấp kỹ năng mà client dùng cho mana, cooldown, tầm đánh và option. */
public record SkillLevelAsset(
        short id,
        byte point,
        byte requiredLevel,
        short manaUse,
        int coolDown,
        short dx,
        short dy,
        byte maxFight,
        List<SkillLevelOptionAsset> options) {

    /** Sao chép danh sách option để giữ read model bất biến. */
    public SkillLevelAsset {
        Objects.requireNonNull(options, "options");
        options = List.copyOf(options);
    }
}
