package com.nsocry.assets;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Objects;

/** Sinh artifact seed ITEM xác định, không tạo SQL động và không truy cập database. */
public final class ItemAssetSeedArtifactGenerator {
    private static final String FORMAT = "nsocry-item-seed-v1";

    private ItemAssetSeedArtifactGenerator() {
    }

    /** Encode bundle, tạo manifest rồi tự kiểm định trước khi trả artifact. */
    public static ItemAssetSeedArtifact generate(ItemAssetBundle bundle)
            throws ItemAssetSeedValidationException {
        Objects.requireNonNull(bundle, "bundle");
        try {
            byte[] payload = ItemAssetCodec.encode(bundle);
            String sha256 = sha256(payload);
            ItemAssetSeedManifest manifest = new ItemAssetSeedManifest(
                    bundle.version(), bundle.options().size(), bundle.items().size(), payload.length, sha256);
            ItemAssetValidationResult validation = ItemAssetSeedValidator.validate(bundle, manifest);
            return new ItemAssetSeedArtifact(payload, manifest, validation, manifestText(validation));
        } catch (IOException | IllegalArgumentException exception) {
            throw new ItemAssetSeedValidationException("Không thể sinh ITEM seed artifact", exception);
        }
    }

    /** Tạo manifest không phụ thuộc locale hoặc ký tự xuống dòng của hệ điều hành. */
    private static String manifestText(ItemAssetValidationResult validation) {
        return "format=" + FORMAT + "\n"
                + "version=" + Byte.toUnsignedInt(validation.version()) + "\n"
                + "optionCount=" + validation.optionCount() + "\n"
                + "itemCount=" + validation.itemCount() + "\n"
                + "payloadLength=" + validation.payloadLength() + "\n"
                + "sha256=" + validation.payloadSha256() + "\n";
    }

    /** Tính SHA-256 bằng implementation chuẩn của JVM. */
    private static String sha256(byte[] payload) throws ItemAssetSeedValidationException {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(payload));
        } catch (NoSuchAlgorithmException exception) {
            throw new ItemAssetSeedValidationException("JVM không hỗ trợ SHA-256", exception);
        }
    }
}
