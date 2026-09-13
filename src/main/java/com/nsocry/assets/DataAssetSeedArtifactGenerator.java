package com.nsocry.assets;

import java.io.IOException;
import java.util.Objects;

/** Sinh DATA candidate xác định và tự kiểm định trước khi trả artifact. */
public final class DataAssetSeedArtifactGenerator {
    private static final String FORMAT = "nsocry-data-seed-v1";

    private DataAssetSeedArtifactGenerator() {
    }

    /** Encode bundle, khóa metadata/checksum rồi validate lại cùng candidate. */
    public static DataAssetSeedArtifact generate(DataAssetBundle bundle) {
        Objects.requireNonNull(bundle, "bundle");
        try {
            byte[] payload = DataAssetCodec.encode(bundle);
            DataAssetSeedManifest manifest = new DataAssetSeedManifest(
                    bundle.version(),
                    bundle.taskRoutes().size(),
                    bundle.experienceThresholds().length,
                    payload.length,
                    DataAssetSeedValidator.sha256(payload));
            DataAssetSeedValidator.validate(bundle, manifest);
            return new DataAssetSeedArtifact(payload, manifest, manifestText(manifest));
        } catch (IOException exception) {
            throw new IllegalArgumentException("Không thể sinh DATA seed artifact", exception);
        }
    }

    /** Tạo manifest độc lập locale và dùng newline cố định trên mọi hệ điều hành. */
    private static String manifestText(DataAssetSeedManifest manifest) {
        return "format=" + FORMAT + "\n"
                + "version=" + Byte.toUnsignedInt(manifest.version()) + "\n"
                + "taskGroupCount=" + manifest.taskGroupCount() + "\n"
                + "experienceCount=" + manifest.experienceCount() + "\n"
                + "payloadLength=" + manifest.payloadLength() + "\n"
                + "sha256=" + manifest.payloadSha256() + "\n";
    }
}
