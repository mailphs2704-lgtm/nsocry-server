package com.nsocry.assets;

import com.nsocry.protocol.compat.ClientDataSet;
import com.nsocry.protocol.compat.ClientVersionManifest;
import java.util.Objects;

/**
 * Gate cuối trước startup: chỉ chuyển nguyên snapshot cho publisher khi đủ năm nhóm asset,
 * đúng manifest đã khóa và không có payload giả/rỗng.
 */
public final class ClientAssetStartupGate implements ClientAssetSnapshotPublisher {
    private final ClientAssetStartupExpectation expectation;
    private final ClientAssetSnapshotPublisher publisher;

    /** Khởi tạo gate bằng tiêu chuẩn độc lập và publisher nguyên tử phía sau. */
    public ClientAssetStartupGate(
            ClientAssetStartupExpectation expectation,
            ClientAssetSnapshotPublisher publisher) {
        this.expectation = Objects.requireNonNull(expectation, "expectation");
        this.publisher = Objects.requireNonNull(publisher, "publisher");
    }

    /**
     * Kiểm tra toàn bộ snapshot trước rồi mới gọi publisher đúng một lần.
     * Bất kỳ sai lệch nào cũng ném lỗi và giữ nguyên snapshot đang phục vụ.
     */
    @Override
    public void publish(ClientAssetSnapshot snapshot) {
        Objects.requireNonNull(snapshot, "snapshot");
        requireManifest(snapshot.manifest());
        for (ClientDataSet dataSet : ClientDataSet.values()) {
            int actualLength = snapshot.payload(dataSet).length;
            int minimumLength = expectation.minimumPayloadLength(dataSet);
            if (actualLength < minimumLength) {
                throw new IllegalStateException(dataSet + " payload is incomplete: "
                        + actualLength + " < " + minimumLength);
            }
        }
        int appearanceLength = snapshot.appearanceData().length;
        if (appearanceLength < expectation.minimumAppearanceLength()) {
            throw new IllegalStateException("APPEARANCE payload is incomplete: "
                    + appearanceLength + " < " + expectation.minimumAppearanceLength());
        }
        publisher.publish(snapshot);
    }

    /** So sánh đủ bốn byte version, không chỉ version của một payload riêng lẻ. */
    private void requireManifest(ClientVersionManifest actual) {
        ClientVersionManifest expected = expectation.manifest();
        if (actual.dataVersion() != expected.dataVersion()
                || actual.mapVersion() != expected.mapVersion()
                || actual.skillVersion() != expected.skillVersion()
                || actual.itemVersion() != expected.itemVersion()) {
            throw new IllegalStateException("client asset manifest does not match startup expectation");
        }
    }
}
