package com.nsocry.assets.conversion;

import java.util.Objects;

/** Ghi nhận giá trị MAP raw byte 128..255 để phân biệt wire byte với signed Java byte. */
public record MapRawByteDifference(String entityType, int entityId, String field, int value) {
    /** Chỉ chấp nhận đúng trường hợp cần giữ bit pattern raw byte ngoài signed byte dương. */
    public MapRawByteDifference {
        Objects.requireNonNull(entityType, "entityType");
        Objects.requireNonNull(field, "field");
        if (entityId < 0 || value <= Byte.MAX_VALUE || value > 255) {
            throw new IllegalArgumentException("raw byte difference không hợp lệ");
        }
    }
}
