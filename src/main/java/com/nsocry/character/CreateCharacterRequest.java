package com.nsocry.character;

import java.util.Objects;

/** Yêu cầu tạo nhân vật đã giải mã từ client; quy tắc nghiệp vụ sẽ được kiểm tra ở tầng dịch vụ. */
public record CreateCharacterRequest(String name, byte gender, byte head) {
    /** Bảo đảm tên không null nhưng chưa áp đặt quy tắc độ dài chưa được NSOCry quyết định. */
    public CreateCharacterRequest {
        Objects.requireNonNull(name, "name");
    }
}
