package com.nsocry.assets;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nsocry.protocol.compat.ClientDataSet;
import com.nsocry.protocol.compat.ClientVersionManifest;
import java.util.EnumMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ClientAssetStartupGateTest {
    @Test
    void publishesCompleteSnapshotExactlyAsBuilt() {
        RecordingPublisher publisher = new RecordingPublisher();
        ClientAssetStartupGate gate = new ClientAssetStartupGate(expectation(2, 1), publisher);
        ClientAssetSnapshot snapshot = snapshot(manifest(), 2, 1);

        gate.publish(snapshot);

        assertSame(snapshot, publisher.published);
    }

    @Test
    void rejectsWrongManifestWithoutPublishing() {
        RecordingPublisher publisher = new RecordingPublisher();
        ClientAssetStartupGate gate = new ClientAssetStartupGate(expectation(2, 1), publisher);
        ClientVersionManifest wrong = new ClientVersionManifest((byte) 9, (byte) 2, (byte) 3, (byte) 4);

        assertThrows(IllegalStateException.class, () -> gate.publish(snapshot(wrong, 2, 1)));
        assertSame(null, publisher.published);
    }

    @Test
    void rejectsShortDatasetWithoutPublishing() {
        RecordingPublisher publisher = new RecordingPublisher();
        ClientAssetStartupGate gate = new ClientAssetStartupGate(expectation(3, 1), publisher);

        assertThrows(IllegalStateException.class, () -> gate.publish(snapshot(manifest(), 2, 1)));
        assertSame(null, publisher.published);
    }

    @Test
    void rejectsShortAppearanceWithoutPublishing() {
        RecordingPublisher publisher = new RecordingPublisher();
        ClientAssetStartupGate gate = new ClientAssetStartupGate(expectation(2, 2), publisher);

        assertThrows(IllegalStateException.class, () -> gate.publish(snapshot(manifest(), 2, 1)));
        assertSame(null, publisher.published);
    }

    @Test
    void expectationRequiresEveryDatasetAndPositiveLengths() {
        EnumMap<ClientDataSet, Integer> missing = minimumLengths(2);
        missing.remove(ClientDataSet.ITEM);
        assertThrows(NullPointerException.class,
                () -> new ClientAssetStartupExpectation(manifest(), missing, 1));

        EnumMap<ClientDataSet, Integer> invalid = minimumLengths(2);
        invalid.put(ClientDataSet.MAP, 0);
        assertThrows(IllegalArgumentException.class,
                () -> new ClientAssetStartupExpectation(manifest(), invalid, 1));
        assertThrows(IllegalArgumentException.class,
                () -> new ClientAssetStartupExpectation(manifest(), minimumLengths(2), 0));
    }

    @Test
    void constructorRejectsMissingDependencies() {
        assertThrows(NullPointerException.class,
                () -> new ClientAssetStartupGate(null, snapshot -> { }));
        assertThrows(NullPointerException.class,
                () -> new ClientAssetStartupGate(expectation(2, 1), null));
    }

    private static ClientAssetStartupExpectation expectation(int payloadLength, int appearanceLength) {
        return new ClientAssetStartupExpectation(manifest(), minimumLengths(payloadLength), appearanceLength);
    }

    private static EnumMap<ClientDataSet, Integer> minimumLengths(int length) {
        EnumMap<ClientDataSet, Integer> lengths = new EnumMap<>(ClientDataSet.class);
        for (ClientDataSet dataSet : ClientDataSet.values()) {
            lengths.put(dataSet, length);
        }
        return lengths;
    }

    private static ClientAssetSnapshot snapshot(
            ClientVersionManifest manifest,
            int payloadLength,
            int appearanceLength) {
        EnumMap<ClientDataSet, byte[]> payloads = new EnumMap<>(ClientDataSet.class);
        payloads.put(ClientDataSet.DATA, payload(manifest.dataVersion(), payloadLength));
        payloads.put(ClientDataSet.MAP, payload(manifest.mapVersion(), payloadLength));
        payloads.put(ClientDataSet.SKILL, payload(manifest.skillVersion(), payloadLength));
        payloads.put(ClientDataSet.ITEM, payload(manifest.itemVersion(), payloadLength));
        return new ClientAssetSnapshot(manifest, new byte[appearanceLength], payloads);
    }

    private static byte[] payload(byte version, int length) {
        byte[] payload = new byte[length];
        payload[0] = version;
        return payload;
    }

    private static ClientVersionManifest manifest() {
        return new ClientVersionManifest((byte) 1, (byte) 2, (byte) 3, (byte) 4);
    }

    private static final class RecordingPublisher implements ClientAssetSnapshotPublisher {
        private ClientAssetSnapshot published;

        @Override
        public void publish(ClientAssetSnapshot snapshot) {
            published = snapshot;
        }
    }
}
