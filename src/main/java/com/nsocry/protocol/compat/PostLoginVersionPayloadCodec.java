package com.nsocry.protocol.compat;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

/** Codec cho bước thương lượng phiên bản dữ liệu ngay sau khi xác thực thành công. */
public final class PostLoginVersionPayloadCodec {
    public static final byte NOT_MAP_ENVELOPE = -28;
    public static final byte UPDATE_VERSION_COMMAND = -123;

    private PostLoginVersionPayloadCodec() {
    }

    /**
     * Tạo frame UPDATE_VERSION gồm bốn byte phiên bản và phần dữ liệu ngoại hình nối tiếp.
     * Dữ liệu ngoại hình phải do asset pipeline cung cấp; codec không tự tạo hoặc đoán nội dung.
     */
    public static ProtocolFrame encodeVersion(
            ClientVersionManifest manifest,
            byte[] appearanceData) throws IOException {
        Objects.requireNonNull(manifest, "manifest");
        Objects.requireNonNull(appearanceData, "appearanceData");

        ByteArrayOutputStream buffer = new ByteArrayOutputStream(5 + appearanceData.length);
        try (DataOutputStream output = new DataOutputStream(buffer)) {
            output.writeByte(UPDATE_VERSION_COMMAND);
            output.writeByte(manifest.dataVersion());
            output.writeByte(manifest.mapVersion());
            output.writeByte(manifest.skillVersion());
            output.writeByte(manifest.itemVersion());
            output.write(appearanceData);
        }
        return new ProtocolFrame(NOT_MAP_ENVELOPE, buffer.toByteArray());
    }

    /** Giải mã yêu cầu cập nhật rỗng của client và từ chối mọi payload phụ không xác định. */
    public static ClientDataSet decodeDataRequest(ProtocolFrame frame) throws IOException {
        Objects.requireNonNull(frame, "frame");
        if (frame.command() != NOT_MAP_ENVELOPE) {
            throw new IOException("unexpected version request envelope");
        }
        byte[] payload = frame.payload();
        if (payload.length != 1) {
            throw new IOException("version data request must contain only its nested command");
        }
        try {
            return ClientDataSet.fromRequestCommand(payload[0]);
        } catch (IllegalArgumentException exception) {
            throw new IOException("unexpected version data request command", exception);
        } finally {
            Arrays.fill(payload, (byte) 0);
        }
    }
}
