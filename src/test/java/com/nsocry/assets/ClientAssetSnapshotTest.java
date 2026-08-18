package com.nsocry.assets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nsocry.protocol.compat.ClientDataSet;
import com.nsocry.protocol.compat.ClientVersionManifest;
import java.util.EnumMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ClientAssetSnapshotTest {
    @Test
    void returnsDefensiveCopies() {
        byte[] appearance = {9};
        Map<ClientDataSet, byte[]> payloads = validPayloads();
        ClientAssetSnapshot snapshot = new ClientAssetSnapshot(manifest(), appearance, payloads);

        appearance[0] = 99;
        payloads.get(ClientDataSet.DATA)[0] = 99;
        byte[] returned = snapshot.payload(ClientDataSet.DATA);
        returned[0] = 88;

        assertArrayEquals(new byte[] {9}, snapshot.appearanceData());
        assertArrayEquals(new byte[] {1, 11}, snapshot.payload(ClientDataSet.DATA));
    }

    @Test
    void requiresAllFourPayloads() {
        Map<ClientDataSet, byte[]> payloads = validPayloads();
        payloads.remove(ClientDataSet.ITEM);

        assertThrows(NullPointerException.class,
                () -> new ClientAssetSnapshot(manifest(), new byte[0], payloads));
    }

    @Test
    void rejectsEmptyPayload() {
        Map<ClientDataSet, byte[]> payloads = validPayloads();
        payloads.put(ClientDataSet.MAP, new byte[0]);

        assertThrows(IllegalArgumentException.class,
                () -> new ClientAssetSnapshot(manifest(), new byte[0], payloads));
    }

    @Test
    void rejectsPayloadVersionDifferentFromManifest() {
        Map<ClientDataSet, byte[]> payloads = validPayloads();
        payloads.put(ClientDataSet.SKILL, new byte[] {99});

        assertThrows(IllegalArgumentException.class,
                () -> new ClientAssetSnapshot(manifest(), new byte[0], payloads));
    }

    private static ClientVersionManifest manifest() {
        return new ClientVersionManifest((byte) 1, (byte) 2, (byte) 3, (byte) 4);
    }

    private static Map<ClientDataSet, byte[]> validPayloads() {
        EnumMap<ClientDataSet, byte[]> payloads = new EnumMap<>(ClientDataSet.class);
        payloads.put(ClientDataSet.DATA, new byte[] {1, 11});
        payloads.put(ClientDataSet.MAP, new byte[] {2, 12});
        payloads.put(ClientDataSet.SKILL, new byte[] {3, 13});
        payloads.put(ClientDataSet.ITEM, new byte[] {4, 14});
        return payloads;
    }
}
