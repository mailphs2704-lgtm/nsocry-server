package com.nsocry.persistence;

import com.nsocry.assets.DataAssetSeedValidationResult;
import java.util.Objects;

/** Kết quả commit DATA seed, gồm metadata và việc row cũ có bị thay hay không. */
public record DataAssetSeedImportResult(
        DataAssetSeedValidationResult validation,
        boolean overwritten) {
    /** Không chấp nhận kết quả thiếu metadata đã kiểm định. */
    public DataAssetSeedImportResult {
        Objects.requireNonNull(validation, "validation");
    }
}
