package com.nsocry.assets;

import com.nsocry.protocol.compat.ClientDataSet;
import com.nsocry.protocol.compat.ClientVersionManifest;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Objects;

/** Ghép các bundle đã kiểm tra thành một snapshot đồng bộ có thể publish cho session. */
public final class ClientAssetSnapshotAssembler {
    private ClientAssetSnapshotAssembler() {
    }

    /**
     * Mã hóa toàn bộ bundle trước, sau đó mới tạo snapshot; nếu một codec lỗi thì không có
     * snapshot bán phần nào được trả về.
     */
    public static ClientAssetSnapshot assemble(
            DataAssetBundle data,
            MapAssetBundle map,
            SkillAssetBundle skill,
            ItemAssetBundle item,
            AppearanceAssetBundle appearance) throws IOException {
        Objects.requireNonNull(data, "data");
        Objects.requireNonNull(map, "map");
        Objects.requireNonNull(skill, "skill");
        Objects.requireNonNull(item, "item");
        Objects.requireNonNull(appearance, "appearance");

        byte[] dataPayload = DataAssetCodec.encode(data);
        byte[] mapPayload = MapAssetCodec.encode(map);
        byte[] skillPayload = SkillAssetCodec.encode(skill);
        byte[] itemPayload = ItemAssetCodec.encode(item);
        byte[] appearancePayload = AppearanceAssetCodec.encode(appearance);

        ClientVersionManifest manifest = new ClientVersionManifest(
                data.version(), map.version(), skill.version(), item.version());
        EnumMap<ClientDataSet, byte[]> payloads = new EnumMap<>(ClientDataSet.class);
        payloads.put(ClientDataSet.DATA, dataPayload);
        payloads.put(ClientDataSet.MAP, mapPayload);
        payloads.put(ClientDataSet.SKILL, skillPayload);
        payloads.put(ClientDataSet.ITEM, itemPayload);
        return new ClientAssetSnapshot(manifest, appearancePayload, payloads);
    }
}
