package com.nsocry.assets;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Objects;

/** Đối chiếu MAP bundle với manifest bằng count, payload length và SHA-256. */
public final class MapAssetSeedValidator {
    private MapAssetSeedValidator() {
    }

    /** Encode lại bundle và từ chối candidate nếu bất kỳ metadata nào lệch manifest. */
    public static MapAssetSeedValidationResult validate(
            MapAssetBundle bundle,
            MapAssetSeedManifest manifest) {
        Objects.requireNonNull(bundle, "bundle");
        Objects.requireNonNull(manifest, "manifest");
        try {
            byte[] payload = MapAssetCodec.encode(bundle);
            String sha256 = sha256(payload);
            require(bundle.version() == manifest.version(), "version");
            require(bundle.mapNames().size() == manifest.mapCount(), "mapCount");
            require(bundle.npcs().size() == manifest.npcCount(), "npcCount");
            require(bundle.mobs().size() == manifest.mobCount(), "mobCount");
            require(payload.length == manifest.payloadLength(), "payloadLength");
            require(sha256.equals(manifest.payloadSha256()), "sha256");
            return new MapAssetSeedValidationResult(
                    bundle.version(),
                    bundle.mapNames().size(),
                    bundle.npcs().size(),
                    bundle.mobs().size(),
                    payload.length,
                    sha256);
        } catch (IOException exception) {
            throw new IllegalArgumentException("Không thể encode MAP để kiểm định", exception);
        }
    }

    static String sha256(byte[] payload) {
        Objects.requireNonNull(payload, "payload");
        try {
            return HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256").digest(payload));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("JVM không hỗ trợ SHA-256", exception);
        }
    }

    private static void require(boolean condition, String field) {
        if (!condition) {
            throw new IllegalArgumentException("MAP manifest không khớp " + field);
        }
    }
}
