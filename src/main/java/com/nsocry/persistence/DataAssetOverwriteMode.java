package com.nsocry.persistence;

/** Chính sách ghi đè DATA seed phải được caller chọn rõ ràng. */
public enum DataAssetOverwriteMode {
    /** Từ chối nếu version đã tồn tại; đây là chế độ an toàn mặc định. */
    REJECT_EXISTING,
    /** Cho phép thay đúng row cùng version trong transaction đã khóa. */
    REPLACE_SAME_VERSION
}
