package com.nsocry.assets;

import java.io.IOException;
import java.util.Objects;

/** Sinh MAP seed artifact xác định và tự kiểm định trước khi trả kết quả. */
public final class MapAssetSeedArtifactGenerator {
    private static final String FORMAT = "nsocry-map-seed-v1";

    private MapAssetSeedArtifactGenerator() {
    }

    /** Encode bundle, tạo manifest checksum rồi validate lại cùng candidate. */
    public static MapAssetSeedArtifact generate(MapAssetBundle bundle) {
        Objects.requireNonNull(bundle, "bundle");
        try {
            byte[] payload = MapAssetCodec.encode(bundle);
            MapAssetSeedManifest manifest = new MapAssetSeedManifest(
                    bundle.version(),
                    bundle.mapNames().size(),
                    bundle.npcs().size(),
                    bundle.mobs().size(),
                    payload.length,
                    MapAssetSeedValidator.sha256(payload));
            MapAssetSeedValidationResult validation =
                    MapAssetSeedValidator.validate(bundle, manifest);
            return new MapAssetSeedArtifact(
                    payload, manifestText(manifest), validation);
        } catch (IOException exception) {
            throw new IllegalArgumentException(
                    "Không thể sinh MAP seed artifact", exception);
        }
    }

    private static String manifestText(MapAssetSeedManifest manifest) {
        return "format=" + FORMAT + "\n"
                + "version=" + Byte.toUnsignedInt(manifest.version()) + "\n"
                + "mapCount=" + manifest.mapCount() + "\n"
                + "npcCount=" + manifest.npcCount() + "\n"
                + "mobCount=" + manifest.mobCount() + "\n"
                + "payloadLength=" + manifest.payloadLength() + "\n"
                + "sha256=" + manifest.payloadSha256() + "\n";
    }
}
