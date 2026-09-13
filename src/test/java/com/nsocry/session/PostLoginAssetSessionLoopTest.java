package com.nsocry.session;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nsocry.assets.ClientAssetSnapshot;
import com.nsocry.protocol.compat.ClientDataSet;
import com.nsocry.protocol.compat.ClientVersionManifest;
import com.nsocry.protocol.compat.ProtocolFrame;
import com.nsocry.protocol.compat.ProtocolLimits;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import org.junit.jupiter.api.Test;

class PostLoginAssetSessionLoopTest {
    @Test
    void announcesVersionThenServesRequestsUntilClientDisconnects() throws Exception {
        FakeTransport transport = new FakeTransport();
        transport.inbound.add(new ProtocolFrame((byte) -28, new byte[] {(byte) -122}));
        transport.inbound.add(new ProtocolFrame((byte) -28, new byte[] {(byte) -121}));

        new PostLoginAssetSessionLoop(
                transport,
                new PostLoginAssetSyncService(() -> snapshot()),
                ProtocolLimits.DEFAULT).run();

        assertEquals(3, transport.shortFrames.size());
        assertEquals((byte) -123, transport.shortFrames.get(0).payload()[0]);
        assertEquals((byte) -122, transport.shortFrames.get(1).payload()[0]);
        assertEquals((byte) -121, transport.shortFrames.get(2).payload()[0]);
        assertEquals(0, transport.fullFrames.size());
    }

    @Test
    void usesFullSizeDeliveryForLargeDataset() throws Exception {
        FakeTransport transport = new FakeTransport();
        transport.inbound.add(new ProtocolFrame((byte) -28, new byte[] {(byte) -122}));

        new PostLoginAssetSessionLoop(
                transport,
                new PostLoginAssetSyncService(() -> snapshotWithLargeData()),
                ProtocolLimits.DEFAULT).run();

        assertEquals(1, transport.shortFrames.size());
        assertEquals(1, transport.fullFrames.size());
        assertEquals((byte) -122, transport.fullFrames.get(0).payload()[0]);
    }

    private static ClientAssetSnapshot snapshot() {
        return snapshot(new byte[] {7, 70});
    }

    private static ClientAssetSnapshot snapshotWithLargeData() {
        byte[] data = new byte[65_536];
        data[0] = 7;
        return snapshot(data);
    }

    private static ClientAssetSnapshot snapshot(byte[] data) {
        EnumMap<ClientDataSet, byte[]> payloads = new EnumMap<>(ClientDataSet.class);
        payloads.put(ClientDataSet.DATA, data);
        payloads.put(ClientDataSet.MAP, new byte[] {8, 80});
        payloads.put(ClientDataSet.SKILL, new byte[] {9, 90});
        payloads.put(ClientDataSet.ITEM, new byte[] {10, 100});
        return new ClientAssetSnapshot(
                new ClientVersionManifest((byte) 7, (byte) 8, (byte) 9, (byte) 10),
                new byte[] {55},
                payloads);
    }

    private static final class FakeTransport implements PostLoginSessionTransport {
        private final ArrayDeque<ProtocolFrame> inbound = new ArrayDeque<>();
        private final List<ProtocolFrame> shortFrames = new ArrayList<>();
        private final List<ProtocolFrame> fullFrames = new ArrayList<>();

        @Override
        public ProtocolFrame readClientFrame() throws IOException {
            ProtocolFrame frame = inbound.poll();
            if (frame == null) {
                throw new EOFException();
            }
            return frame;
        }

        @Override
        public void sendShortFrame(ProtocolFrame frame) {
            shortFrames.add(frame);
        }

        @Override
        public void sendFullSizeFrame(ProtocolFrame frame) {
            fullFrames.add(frame);
        }
    }
}
