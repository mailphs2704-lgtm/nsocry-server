package com.nsocry.assets;

import java.util.List;
import java.util.Objects;

/** Read model gồm tên map, NPC template và mob template cho payload MAP. */
public record MapAssetBundle(
        byte version,
        List<String> mapNames,
        List<NpcTemplateAsset> npcs,
        List<MobTemplateAsset> mobs) {

    /** Sao chép các danh sách và từ chối tên map null. */
    public MapAssetBundle {
        Objects.requireNonNull(mapNames, "mapNames");
        Objects.requireNonNull(npcs, "npcs");
        Objects.requireNonNull(mobs, "mobs");
        mapNames = List.copyOf(mapNames);
        mapNames.forEach(name -> Objects.requireNonNull(name, "mapName"));
        npcs = List.copyOf(npcs);
        mobs = List.copyOf(mobs);
    }
}
