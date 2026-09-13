package com.nsocry.assets.conversion;

import java.util.Objects;

/** Một giá trị raw byte 128–255 cần quyết định compatibility trước khi cast sang byte Java. */
public record SkillRawByteDifference(String entityType, int entityId, String field, int value) {
    /** Kiểm tra identity và chỉ chấp nhận đúng miền difference 128–255. */
    public SkillRawByteDifference {
        Objects.requireNonNull(entityType, "entityType");
        Objects.requireNonNull(field, "field");
        if (entityId < 0 || value < 128 || value > 255) {
            throw new IllegalArgumentException("raw byte difference ngoài phạm vi");
        }
    }
}
