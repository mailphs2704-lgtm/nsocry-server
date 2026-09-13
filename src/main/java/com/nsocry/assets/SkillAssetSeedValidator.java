package com.nsocry.assets;

import com.nsocry.assets.conversion.SkillRawByteDifference;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;

/** Đối chiếu cấu trúc, checksum và raw-byte SKILL trước khi chấp nhận candidate. */
public final class SkillAssetSeedValidator {
    private SkillAssetSeedValidator() {
    }

    /** Encode lại bundle rồi so toàn bộ metadata với manifest. */
    public static SkillAssetSeedValidationResult validate(SkillAssetBundle bundle, SkillAssetSeedManifest manifest) {
        Objects.requireNonNull(bundle, "bundle");
        Objects.requireNonNull(manifest, "manifest");
        try {
            byte[] payload = SkillAssetCodec.encode(bundle);
            SkillAssetValidationReport structure = SkillAssetStructureValidator.validate(bundle);
            List<SkillRawByteDifference> differences = rawByteDifferences(bundle);
            String sha256 = sha256(payload);
            require(bundle.version() == manifest.version(), "version");
            require(structure.optionTemplateCount() == manifest.optionTemplateCount(), "optionTemplateCount");
            require(structure.classCount() == manifest.classCount(), "classCount");
            require(structure.skillTemplateCount() == manifest.skillTemplateCount(), "skillTemplateCount");
            require(structure.skillLevelCount() == manifest.skillLevelCount(), "skillLevelCount");
            require(structure.skillLevelOptionCount() == manifest.skillLevelOptionCount(), "skillLevelOptionCount");
            require(differences.size() == manifest.rawByteDifferenceCount(), "rawByteDifferenceCount");
            require(payload.length == manifest.payloadLength(), "payloadLength");
            require(sha256.equals(manifest.payloadSha256()), "sha256");
            return new SkillAssetSeedValidationResult(bundle.version(), structure, differences, payload.length, sha256);
        } catch (IOException exception) {
            throw new IllegalArgumentException("Không thể encode SKILL để kiểm định", exception);
        }
    }

    /** Tái dựng difference từ raw bit; source SKILL dùng miền nghiệp vụ không âm 0–255. */
    static List<SkillRawByteDifference> rawByteDifferences(SkillAssetBundle bundle) {
        List<SkillRawByteDifference> result = new ArrayList<>();
        for (SkillClassAsset skillClass : bundle.classes()) {
            for (SkillTemplateAsset template : skillClass.templates()) {
                add(result, "template", Byte.toUnsignedInt(template.id()), "maxPoint", template.maxPoint());
                add(result, "template", Byte.toUnsignedInt(template.id()), "type", template.type());
                for (SkillLevelAsset level : template.levels()) {
                    add(result, "level", level.id(), "point", level.point());
                    add(result, "level", level.id(), "requiredLevel", level.requiredLevel());
                    add(result, "level", level.id(), "maxFight", level.maxFight());
                }
            }
        }
        return List.copyOf(result);
    }

    private static void add(List<SkillRawByteDifference> target, String type, int id, String field, byte value) {
        int raw = Byte.toUnsignedInt(value);
        if (raw > Byte.MAX_VALUE) target.add(new SkillRawByteDifference(type, id, field, raw));
    }

    static String sha256(byte[] payload) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(payload));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("JVM không hỗ trợ SHA-256", exception);
        }
    }

    private static void require(boolean condition, String field) {
        if (!condition) throw new IllegalArgumentException("SKILL manifest không khớp " + field);
    }
}
