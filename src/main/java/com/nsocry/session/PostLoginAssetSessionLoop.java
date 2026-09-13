package com.nsocry.session;

import com.nsocry.protocol.compat.ProtocolFrame;
import com.nsocry.protocol.compat.ProtocolLimits;
import java.io.EOFException;
import java.io.IOException;
import java.util.Objects;

/** Duy trì phiên sau login: công bố version rồi phục vụ các yêu cầu asset đến khi client ngắt. */
public final class PostLoginAssetSessionLoop {
    private final PostLoginSessionTransport transport;
    private final PostLoginAssetSyncService assets;
    private final ProtocolLimits limits;

    /** Tạo vòng lặp bằng transport, nguồn snapshot nhất quán và giới hạn wire. */
    public PostLoginAssetSessionLoop(
            PostLoginSessionTransport transport,
            PostLoginAssetSyncService assets,
            ProtocolLimits limits) {
        this.transport = Objects.requireNonNull(transport, "transport");
        this.assets = Objects.requireNonNull(assets, "assets");
        this.limits = Objects.requireNonNull(limits, "limits");
    }

    /** Gửi UPDATE_VERSION trước, sau đó trả từng DATA/MAP/SKILL/ITEM request. */
    public void run() throws IOException {
        transport.sendShortFrame(assets.versionAnnouncement());
        while (true) {
            ProtocolFrame request;
            try {
                request = transport.readClientFrame();
            } catch (EOFException disconnected) {
                return;
            }
            PostLoginAssetSyncService.ResponsePlan response =
                    assets.planResponse(request, limits);
            if (response.delivery() == PostLoginAssetSyncService.Delivery.SHORT) {
                transport.sendShortFrame(response.frame());
            } else {
                transport.sendFullSizeFrame(response.frame());
            }
        }
    }
}
