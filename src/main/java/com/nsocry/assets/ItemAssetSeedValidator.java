package com.nsocry.assets;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Objects;

/** Xác minh count, version, round-trip codec và SHA-256 trước khi chấp nhận seed ITEM. */
public final class ItemAssetSeedValidator {
    private ItemAssetSeedValidator() {
    }

    /** Validate bundle theo manifest và trả metadata có thể ghi vào nhật ký vận hành. */
    public static ItemAssetValidationResult validate(
            ItemAssetBundle bundle,
            ItemAssetSeedManifest manifest) throws ItemAssetSeedValidationException {
        Objects.requireNonNull(bundle, "bundle");
        Objects.requireNonNull(manifest, "manifest");
        require(bundle.version() == manifest.version(), "ITEM version không khớp manifest");
        require(bundle.options().size() == manifest.optionCount(), "Số item option không khớp manifest");
        require(bundle.items().size() == manifest.itemCount(), "Số item template không khớp manifest");

        try {
            byte[] payload = ItemAssetCodec.encode(bundle);
            require(payload.length == manifest.payloadLength(), "Độ dài ITEM payload không khớp manifest");
            ItemAssetBundle decoded = ItemAssetCodec.decode(payload);
            require(bundle.equals(decoded), "ITEM payload không round-trip chính xác");
            String sha256 = sha256(payload);
            require(MessageDigest.isEqual(
                    HexFormat.of().parseHex(manifest.payloadSha256()),
                    HexFormat.of().parseHex(sha256)), "ITEM payload SHA-256 không khớp manifest");
            return new ItemAssetValidationResult(
                    bundle.version(), bundle.options().size(), bundle.items().size(), payload.length, sha256);
        } catch (IOException | IllegalArgumentException exception) {
            throw new ItemAssetSeedValidationException("ITEM seed không qua được codec", exception);
        }
    }

    /** Tính checksum bằng thuật toán luôn có trong Java SE. */
    private static String sha256(byte[] payload) throws ItemAssetSeedValidationException {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(payload));
        } catch (NoSuchAlgorithmException exception) {
            throw new ItemAssetSeedValidationException("JVM không hỗ trợ SHA-256", exception);
        }
    }

    /** Ném lỗi validation có thông điệp ngắn, không log dữ liệu asset. */
    private static void require(boolean condition, String message) throws ItemAssetSeedValidationException {
        if (!condition) {
            throw new ItemAssetSeedValidationException(message);
        }
    }
}
