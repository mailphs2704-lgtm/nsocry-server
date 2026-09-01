package com.nsocry.assets;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Objects;

/** Kiểm định DATA bundle bằng cách encode lại và đối chiếu toàn bộ manifest candidate. */
public final class DataAssetSeedValidator {
    private DataAssetSeedValidator() {
    }

    /** Từ chối mọi khác biệt version/count/length/checksum so với đúng bundle được cung cấp. */
    public static DataAssetSeedValidationResult validate(
            DataAssetBundle bundle,
            DataAssetSeedManifest manifest) {
        Objects.requireNonNull(bundle, "bundle");
        Objects.requireNonNull(manifest, "manifest");
        try {
            byte[] payload = DataAssetCodec.encode(bundle);
            require(Byte.toUnsignedInt(bundle.version()) == Byte.toUnsignedInt(manifest.version()), "version");
            require(bundle.taskRoutes().size() == manifest.taskGroupCount(), "taskGroupCount");
            require(bundle.experienceThresholds().length == manifest.experienceCount(), "experienceCount");
            require(payload.length == manifest.payloadLength(), "payloadLength");
            String sha256 = sha256(payload);
            require(sha256.equals(manifest.payloadSha256()), "sha256");
            return new DataAssetSeedValidationResult(
                    bundle.version(),
                    bundle.taskRoutes().size(),
                    bundle.experienceThresholds().length,
                    payload.length,
                    sha256);
        } catch (IOException exception) {
            throw new IllegalArgumentException("Không thể encode DATA để kiểm định", exception);
        }
    }

    /** Tính SHA-256 lowercase bằng provider chuẩn của JVM. */
    static String sha256(byte[] payload) {
        try {
            return HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256").digest(Objects.requireNonNull(payload, "payload")));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("JVM không hỗ trợ SHA-256", exception);
        }
    }

    private static void require(boolean condition, String field) {
        if (!condition) {
            throw new IllegalArgumentException("DATA manifest không khớp " + field);
        }
    }
}
