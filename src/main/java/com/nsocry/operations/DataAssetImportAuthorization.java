package com.nsocry.operations;

import com.nsocry.persistence.DataAssetOverwriteMode;
import java.nio.file.Path;
import java.util.Objects;

/** Bằng chứng offline đã qua backup/checksum/overwrite gate trước khi được phép mở JDBC. */
public record DataAssetImportAuthorization(
        Path backupPath,
        long backupSize,
        String backupSha256,
        String candidateSha256,
        DataAssetOverwriteMode overwriteMode) {
    /** Khóa giá trị bất biến và chỉ nhận backup không rỗng. */
    public DataAssetImportAuthorization {
        backupPath = Objects.requireNonNull(backupPath, "backupPath").toAbsolutePath().normalize();
        if (backupSize <= 0) {
            throw new IllegalArgumentException("DATA backup phải có kích thước lớn hơn 0");
        }
        backupSha256 = Objects.requireNonNull(backupSha256, "backupSha256");
        candidateSha256 = Objects.requireNonNull(candidateSha256, "candidateSha256");
        overwriteMode = Objects.requireNonNull(overwriteMode, "overwriteMode");
    }
}
