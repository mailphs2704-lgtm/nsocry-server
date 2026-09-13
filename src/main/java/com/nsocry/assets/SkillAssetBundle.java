package com.nsocry.assets;

import java.util.List;
import java.util.Objects;

/** Read model hoàn chỉnh dùng để build payload SKILL. */
public record SkillAssetBundle(
        byte version,
        List<String> optionTemplateNames,
        List<SkillClassAsset> classes) {

    /** Sao chép danh sách và kiểm tra mọi tên option không null. */
    public SkillAssetBundle {
        Objects.requireNonNull(optionTemplateNames, "optionTemplateNames");
        Objects.requireNonNull(classes, "classes");
        optionTemplateNames = List.copyOf(optionTemplateNames);
        optionTemplateNames.forEach(name -> Objects.requireNonNull(name, "optionTemplateName"));
        classes = List.copyOf(classes);
    }
}
