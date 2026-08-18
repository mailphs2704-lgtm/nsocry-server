package com.nsocry.assets;

import java.io.IOException;
import java.util.Objects;

/** Điều phối đọc năm nguồn, mã hóa và publish snapshot theo nguyên tắc tất cả hoặc không. */
public final class ClientAssetSnapshotBuildService {
    private final DataAssetSource dataSource;
    private final MapAssetSource mapSource;
    private final SkillAssetSource skillSource;
    private final ItemAssetSource itemSource;
    private final AppearanceAssetSource appearanceSource;
    private final ClientAssetSnapshotPublisher publisher;

    /** Khởi tạo dịch vụ bằng các cổng nguồn và cổng publish bắt buộc. */
    public ClientAssetSnapshotBuildService(
            DataAssetSource dataSource,
            MapAssetSource mapSource,
            SkillAssetSource skillSource,
            ItemAssetSource itemSource,
            AppearanceAssetSource appearanceSource,
            ClientAssetSnapshotPublisher publisher) {
        this.dataSource = Objects.requireNonNull(dataSource, "dataSource");
        this.mapSource = Objects.requireNonNull(mapSource, "mapSource");
        this.skillSource = Objects.requireNonNull(skillSource, "skillSource");
        this.itemSource = Objects.requireNonNull(itemSource, "itemSource");
        this.appearanceSource = Objects.requireNonNull(appearanceSource, "appearanceSource");
        this.publisher = Objects.requireNonNull(publisher, "publisher");
    }

    /**
     * Dựng rồi publish một snapshot mới. Snapshot cũ không đổi nếu đọc nguồn hoặc mã hóa thất bại.
     *
     * @return snapshot vừa được publish
     */
    public ClientAssetSnapshot rebuild() throws ClientAssetSourceException, IOException {
        DataAssetBundle data = Objects.requireNonNull(dataSource.load(), "dataSource result");
        MapAssetBundle map = Objects.requireNonNull(mapSource.load(), "mapSource result");
        SkillAssetBundle skill = Objects.requireNonNull(skillSource.load(), "skillSource result");
        ItemAssetBundle item = Objects.requireNonNull(itemSource.load(), "itemSource result");
        AppearanceAssetBundle appearance = Objects.requireNonNull(
                appearanceSource.load(), "appearanceSource result");

        ClientAssetSnapshot snapshot = ClientAssetSnapshotAssembler.assemble(
                data, map, skill, item, appearance);
        publisher.publish(snapshot);
        return snapshot;
    }
}
