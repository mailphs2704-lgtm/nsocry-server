package com.nsocry.assets;

import java.io.IOException;
import java.util.Objects;

/** Sinh artifact SKILL xác định và tự kiểm định trước khi trả kết quả. */
public final class SkillAssetSeedArtifactGenerator {
    private static final String FORMAT = "nsocry-skill-seed-v1";

    private SkillAssetSeedArtifactGenerator() {
    }

    public static SkillAssetSeedArtifact generate(SkillAssetBundle bundle) {
        Objects.requireNonNull(bundle, "bundle");
        try {
            byte[] payload = SkillAssetCodec.encode(bundle);
            SkillAssetValidationReport structure = SkillAssetStructureValidator.validate(bundle);
            SkillAssetSeedManifest provisional = new SkillAssetSeedManifest(
                    bundle.version(), structure.optionTemplateCount(), structure.classCount(),
                    structure.skillTemplateCount(), structure.skillLevelCount(), structure.skillLevelOptionCount(),
                    SkillAssetSeedValidator.rawByteDifferences(bundle).size(), payload.length,
                    SkillAssetSeedValidator.sha256(payload));
            SkillAssetSeedValidationResult validation = SkillAssetSeedValidator.validate(bundle, provisional);
            return new SkillAssetSeedArtifact(payload, manifestText(provisional), validation);
        } catch (IOException exception) {
            throw new IllegalArgumentException("Không thể sinh SKILL seed artifact", exception);
        }
    }

    private static String manifestText(SkillAssetSeedManifest manifest) {
        return "format=" + FORMAT + "\n"
                + "version=" + Byte.toUnsignedInt(manifest.version()) + "\n"
                + "optionTemplateCount=" + manifest.optionTemplateCount() + "\n"
                + "classCount=" + manifest.classCount() + "\n"
                + "skillTemplateCount=" + manifest.skillTemplateCount() + "\n"
                + "skillLevelCount=" + manifest.skillLevelCount() + "\n"
                + "skillLevelOptionCount=" + manifest.skillLevelOptionCount() + "\n"
                + "rawByteDifferenceCount=" + manifest.rawByteDifferenceCount() + "\n"
                + "payloadLength=" + manifest.payloadLength() + "\n"
                + "sha256=" + manifest.payloadSha256() + "\n";
    }
}
