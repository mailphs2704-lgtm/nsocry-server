package com.nsocry.assets;

import java.util.List;
import java.util.Objects;

/** Metadata hiển thị và menu tĩnh của một NPC phía client. */
public record NpcTemplateAsset(
        String name,
        short head,
        short body,
        short leg,
        List<List<String>> menu) {

    /** Sao chép sâu cấu trúc menu để NPC asset không đổi trong lúc build. */
    public NpcTemplateAsset {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(menu, "menu");
        menu = menu.stream().map(row -> {
            Objects.requireNonNull(row, "menuRow");
            List<String> copy = List.copyOf(row);
            copy.forEach(value -> Objects.requireNonNull(value, "menuText"));
            return copy;
        }).toList();
    }
}
