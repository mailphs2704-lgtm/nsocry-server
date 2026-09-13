package com.nsocry.assets;

import java.util.List;
import java.util.Objects;

/** Nhóm kỹ năng của một môn phái/lớp nhân vật phía client. */
public record SkillClassAsset(String name, List<SkillTemplateAsset> templates) {
    /** Từ chối tên null và giữ danh sách template bất biến. */
    public SkillClassAsset {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(templates, "templates");
        templates = List.copyOf(templates);
    }
}
