package com.nsocry.session;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nsocry.assets.ClientAssetSnapshot;
import com.nsocry.protocol.compat.ClientDataSet;
import com.nsocry.protocol.compat.ClientVersionManifest;
import com.nsocry.protocol.compat.ProtocolFrame;
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

    private static ClientAssetSnapshot snapshot(byte dataVersion) {
        ClientVersionManifest manifest =
                new ClientVersionManifest(dataVersion, (byte) 8, (byte) 9, (byte) 10);
        EnumMap<ClientDataSet, byte[]> payloads = new EnumMap<>(ClientDataSet.class);
        payloads.put(ClientDataSet.DATA, new byte[] {dataVersion, 70});
        payloads.put(ClientDataSet.MAP, new byte[] {8, 80});
        payloads.put(ClientDataSet.SKILL, new byte[] {9, 90});
        payloads.put(ClientDataSet.ITEM, new byte[] {10, 100});
        return new ClientAssetSnapshot(manifest, new byte[] {55}, payloads);
    }
}
