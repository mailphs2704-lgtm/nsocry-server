package com.nsocry.session;

import com.nsocry.assets.DataAssetRuntimeSnapshot;
import com.nsocry.protocol.compat.ClientDataSet;
import com.nsocry.protocol.compat.PostLoginVersionPayloadCodec;
import com.nsocry.protocol.compat.ProtocolFrame;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/** Cầu nối DATA snapshot đã VERIFIED sang response đồng bộ sau đăng nhập của client V7. */
public final class PostLoginDataSyncService {
    private final Supplier<Optional<DataAssetRuntimeSnapshot>> snapshots;

    /** Nhận accessor read-only; service không sở hữu quyền publish hoặc truy cập database. */
    public PostLoginDataSyncService(
            Supplier<Optional<DataAssetRuntimeSnapshot>> snapshots) {
        this.snapshots = Objects.requireNonNull(snapshots, "snapshots");
    }

    /**
     * Giải mã request -122 rồi tạo response DATA; dataset khác thuộc service tương ứng.
     */
    public ProtocolFrame respond(ProtocolFrame request) throws IOException {
        ClientDataSet requested = PostLoginVersionPayloadCodec.decodeDataRequest(request);
        if (requested != ClientDataSet.DATA) {
            throw new IOException("post-login DATA service cannot serve " + requested);
        }
        Optional<DataAssetRuntimeSnapshot> available =
                Objects.requireNonNull(snapshots.get(), "snapshot result");
        DataAssetRuntimeSnapshot snapshot = available.orElseThrow(
                () -> new IllegalStateException("DATA runtime snapshot chưa sẵn sàng"));
        return PostLoginVersionPayloadCodec.encodeDataResponse(
                ClientDataSet.DATA, snapshot.payload());
    }
}
