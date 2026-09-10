package com.nsocry.session;

import com.nsocry.assets.ClientAssetSnapshot;
import com.nsocry.assets.ClientAssetSnapshotProvider;
import com.nsocry.protocol.compat.ClientDataSet;
import com.nsocry.protocol.compat.PostLoginVersionPayloadCodec;
import com.nsocry.protocol.compat.ProtocolFrame;
import com.nsocry.protocol.compat.ProtocolLimits;
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

    /**
     * Chọn short/full-size theo độ dài mà chưa quyết định layout wire của command -32.
     */
    public ResponsePlan planResponse(
            ProtocolFrame request, ProtocolLimits limits) throws IOException {
        Objects.requireNonNull(limits, "limits");
        ProtocolFrame response = respond(request);
        int length = response.payload().length;
        int shortRoutingLimit = Math.min(
                limits.maxShortPayload(),
                com.nsocry.protocol.compat.LegacyFrameCodec.LEGACY_SHORT_ROUTING_LIMIT);
        if (length <= shortRoutingLimit) {
            limits.requireAllowed(length, false);
            return new ResponsePlan(response, Delivery.SHORT);
        }
        limits.requireAllowed(length, true);
        return new ResponsePlan(response, Delivery.FULL_SIZE);
    }

    /** Phương thức vận chuyển được chọn sau khi kiểm tra giới hạn payload. */
    public enum Delivery {
        SHORT,
        FULL_SIZE
    }

    /** Logical response và phương thức vận chuyển; chưa encode command -32. */
    public record ResponsePlan(ProtocolFrame frame, Delivery delivery) {
        public ResponsePlan {
            Objects.requireNonNull(frame, "frame");
            Objects.requireNonNull(delivery, "delivery");
        }
    }

    private ClientAssetSnapshot current() {
        return Objects.requireNonNull(snapshots.currentSnapshot(), "client asset snapshot");
    }
}
