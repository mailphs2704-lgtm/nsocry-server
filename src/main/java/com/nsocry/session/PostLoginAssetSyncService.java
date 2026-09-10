package com.nsocry.session;

import com.nsocry.assets.ClientAssetSnapshot;
import com.nsocry.assets.ClientAssetSnapshotProvider;
import com.nsocry.protocol.compat.ClientDataSet;
import com.nsocry.protocol.compat.PostLoginVersionPayloadCodec;
import com.nsocry.protocol.compat.ProtocolFrame;
import java.io.IOException;
import java.util.Objects;

/** Điều phối thương lượng đủ DATA/MAP/SKILL/ITEM từ một client asset snapshot nhất quán. */
public final class PostLoginAssetSyncService {
    private final ClientAssetSnapshotProvider snapshots;

    /** Nhận provider read-only; không lộ publisher hoặc nguồn persistence cho session. */
    public PostLoginAssetSyncService(ClientAssetSnapshotProvider snapshots) {
        this.snapshots = Objects.requireNonNull(snapshots, "snapshots");
    }

    /** Tạo UPDATE_VERSION từ đúng một snapshot hiện hành. */
    public ProtocolFrame versionAnnouncement() throws IOException {
        ClientAssetSnapshot snapshot = current();
        return PostLoginVersionPayloadCodec.encodeVersion(
                snapshot.manifest(), snapshot.appearanceData());
    }

    /** Decode dataset request rồi trả payload cùng snapshot đọc tại đầu lời gọi. */
    public ProtocolFrame respond(ProtocolFrame request) throws IOException {
        ClientDataSet dataSet = PostLoginVersionPayloadCodec.decodeDataRequest(request);
        ClientAssetSnapshot snapshot = current();
        return PostLoginVersionPayloadCodec.encodeDataResponse(
                dataSet, snapshot.payload(dataSet));
    }

    private ClientAssetSnapshot current() {
        return Objects.requireNonNull(snapshots.currentSnapshot(), "client asset snapshot");
    }
}
