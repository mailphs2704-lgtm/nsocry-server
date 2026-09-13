package com.nsocry.assets;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

/** Sinh appearance candidate xác định và bắt buộc codec round-trip trước khi trả artifact. */
public final class AppearanceAssetSeedArtifactGenerator {
    private AppearanceAssetSeedArtifactGenerator() {
    }

    /** Encode, decode, encode lại rồi khóa checksum; từ chối mọi byte không ổn định. */
    public static AppearanceAssetSeedArtifact generate(AppearanceAssetBundle bundle) {
        Objects.requireNonNull(bundle, "bundle");
        try {
            byte[] payload = AppearanceAssetCodec.encode(bundle);
            AppearanceAssetBundle decoded = AppearanceAssetCodec.decode(payload);
            byte[] roundTrip = AppearanceAssetCodec.encode(decoded);
            if (!bundle.equals(decoded) || !Arrays.equals(payload, roundTrip)) {
                throw new IllegalArgumentException("Appearance codec round-trip không ổn định");
            }
            return new AppearanceAssetSeedArtifact(
                    payload, DataAssetSeedValidator.sha256(payload));
        } catch (IOException exception) {
            throw new IllegalArgumentException("Không thể sinh appearance seed artifact", exception);
        }
    }
}
