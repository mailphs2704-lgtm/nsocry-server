package com.nsocry.operations;

import com.nsocry.assets.ItemAssetValidationResult;
import java.util.Arrays;
import java.util.Objects;

/** Nội dung archive ITEM đã qua parse/codec/checksum validation. */
public final class ValidatedItemAssetSeedArchive {
    private final byte[] payload;
    private final String manifestText;
    private final ItemAssetValidationResult validation;

    /** Chỉ archive service tạo kết quả sau khi toàn bộ validation thành công. */
    ValidatedItemAssetSeedArchive(
            byte[] payload,
            String manifestText,
            ItemAssetValidationResult validation) {
        this.payload = Arrays.copyOf(Objects.requireNonNull(payload, "payload"), payload.length);
        this.manifestText = Objects.requireNonNull(manifestText, "manifestText");
        this.validation = Objects.requireNonNull(validation, "validation");
    }

    /** Trả defensive copy của payload dùng cho transactional importer. */
    public byte[] payload() {
        return Arrays.copyOf(payload, payload.length);
    }

    /** Trả manifest canonical đã được kiểm định. */
    public String manifestText() {
        return manifestText;
    }

    /** Trả metadata đã khớp payload và manifest. */
    public ItemAssetValidationResult validation() {
        return validation;
    }
}
