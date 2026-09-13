package com.nsocry.operations;

import com.nsocry.assets.DataAssetSeedValidationResult;
import java.util.Arrays;
import java.util.Objects;

/** Nội dung archive DATA đã qua parse, decode và kiểm định checksum. */
public final class ValidatedDataAssetSeedArchive {
    private final byte[] payload;
    private final String manifestText;
    private final DataAssetSeedValidationResult validation;

    /** Chỉ archive service tạo kết quả sau khi toàn bộ validation thành công. */
    ValidatedDataAssetSeedArchive(
            byte[] payload,
            String manifestText,
            DataAssetSeedValidationResult validation) {
        this.payload = Arrays.copyOf(Objects.requireNonNull(payload, "payload"), payload.length);
        this.manifestText = Objects.requireNonNull(manifestText, "manifestText");
        this.validation = Objects.requireNonNull(validation, "validation");
    }

    /** Trả defensive copy để persistence tương lai không sửa candidate đã xác minh. */
    public byte[] payload() {
        return Arrays.copyOf(payload, payload.length);
    }

    /** Trả nguyên manifest canonical UTF-8 đã kiểm định. */
    public String manifestText() {
        return manifestText;
    }

    /** Trả metadata đã khớp payload và manifest. */
    public DataAssetSeedValidationResult validation() {
        return validation;
    }
}
