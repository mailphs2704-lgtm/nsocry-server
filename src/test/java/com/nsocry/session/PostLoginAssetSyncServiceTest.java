package com.nsocry.session;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nsocry.assets.ClientAssetSnapshot;
import com.nsocry.protocol.compat.ClientDataSet;
import com.nsocry.protocol.compat.ClientVersionManifest;
import com.nsocry.protocol.compat.ProtocolFrame;
import com.nsocry.protocol.compat.ProtocolLimits;
import java.util.EnumMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class PostLoginAssetSyncServiceTest {
    @Test
    void announcesAllVersionsAndAppearanceFromOneSnapshot() throws Exception {
        PostLoginAssetSyncService service = new PostLoginAssetSyncService(
                () -> snapshot((byte) 7));

        ProtocolFrame frame = service.versionAnnouncement();

        assertEquals(-28, frame.command());
        assertArrayEquals(new byte[] {(byte) -123, 7, 8, 9, 10, 55}, frame.payload());
    }

    @Test
    void servesEveryRequestedDataset() throws Exception {
        PostLoginAssetSyncService service = new PostLoginAssetSyncService(
                () -> snapshot((byte) 7));
        byte command = (byte) -122;
        for (ClientDataSet dataSet : ClientDataSet.values()) {
            ProtocolFrame response = service.respond(
                    new ProtocolFrame((byte) -28, new byte[] {command}));
            assertEquals(-28, response.command());
            assertEquals(command, response.payload()[0]);
            assertEquals(dataSet.ordinal() + 7, response.payload()[1]);
            command++;
        }
    }

    @Test
    void readsProviderExactlyOncePerOperation() throws Exception {
        AtomicInteger reads = new AtomicInteger();
        PostLoginAssetSyncService service = new PostLoginAssetSyncService(() -> {
            reads.incrementAndGet();
            return snapshot((byte) 7);
        });

        service.versionAnnouncement();
        assertEquals(1, reads.get());
        service.respond(new ProtocolFrame((byte) -28, new byte[] {(byte) -122}));
        assertEquals(2, reads.get());
    }

    @Test
    void malformedRequestDoesNotReadSnapshot() {
        AtomicInteger reads = new AtomicInteger();
        PostLoginAssetSyncService service = new PostLoginAssetSyncService(() -> {
            reads.incrementAndGet();
            return snapshot((byte) 7);
        });

        assertThrows(Exception.class, () -> service.respond(
                new ProtocolFrame((byte) -28, new byte[] {(byte) -122, 0})));
        assertEquals(0, reads.get());
    }

    @Test
    void plansSmallResponseAsShortFrame() throws Exception {
        PostLoginAssetSyncService service = new PostLoginAssetSyncService(
                () -> snapshot((byte) 7));

        var plan = service.planResponse(
                new ProtocolFrame((byte) -28, new byte[] {(byte) -122}),
                ProtocolLimits.DEFAULT);

        assertEquals(PostLoginAssetSyncService.Delivery.SHORT, plan.delivery());
    }

    @Test
    void plansLargeDataResponseAsFullSizeWithoutEncodingWireLayout() throws Exception {
        ClientVersionManifest manifest =
                new ClientVersionManifest((byte) 7, (byte) 8, (byte) 9, (byte) 10);
        EnumMap<ClientDataSet, byte[]> payloads = payloads((byte) 7);
        payloads.put(ClientDataSet.DATA, new byte[65_536]);
        payloads.get(ClientDataSet.DATA)[0] = 7;
        ClientAssetSnapshot large =
                new ClientAssetSnapshot(manifest, new byte[] {55}, payloads);
        PostLoginAssetSyncService service =
                new PostLoginAssetSyncService(() -> large);

        var plan = service.planResponse(
                new ProtocolFrame((byte) -28, new byte[] {(byte) -122}),
                ProtocolLimits.DEFAULT);

        assertEquals(PostLoginAssetSyncService.Delivery.FULL_SIZE, plan.delivery());
        assertEquals(65_537, plan.frame().payload().length);
    }

    @Test
    void rejectsResponseBeyondFullSizeLimit() {
        ClientVersionManifest manifest =
                new ClientVersionManifest((byte) 7, (byte) 8, (byte) 9, (byte) 10);
        EnumMap<ClientDataSet, byte[]> payloads = payloads((byte) 7);
        payloads.put(ClientDataSet.DATA, new byte[20]);
        payloads.get(ClientDataSet.DATA)[0] = 7;
        PostLoginAssetSyncService service = new PostLoginAssetSyncService(
                () -> new ClientAssetSnapshot(manifest, new byte[] {55}, payloads));

        assertThrows(IllegalArgumentException.class, () -> service.planResponse(
                new ProtocolFrame((byte) -28, new byte[] {(byte) -122}),
                new ProtocolLimits(8, 16)));
    }

    private static ClientAssetSnapshot snapshot(byte dataVersion) {
        ClientVersionManifest manifest =
                new ClientVersionManifest(dataVersion, (byte) 8, (byte) 9, (byte) 10);
        return new ClientAssetSnapshot(
                manifest, new byte[] {55}, payloads(dataVersion));
    }

    private static EnumMap<ClientDataSet, byte[]> payloads(byte dataVersion) {
        EnumMap<ClientDataSet, byte[]> payloads = new EnumMap<>(ClientDataSet.class);
        payloads.put(ClientDataSet.DATA, new byte[] {dataVersion, 70});
        payloads.put(ClientDataSet.MAP, new byte[] {8, 80});
        payloads.put(ClientDataSet.SKILL, new byte[] {9, 90});
        payloads.put(ClientDataSet.ITEM, new byte[] {10, 100});
        return payloads;
    }
}
