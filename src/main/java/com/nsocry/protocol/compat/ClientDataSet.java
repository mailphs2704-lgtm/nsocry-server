package com.nsocry.protocol.compat;

/** Bộ dữ liệu tương thích mà client V7 có thể yêu cầu cập nhật sau khi đăng nhập. */
public enum ClientDataSet {
    DATA((byte) -122),
    MAP((byte) -121),
    SKILL((byte) -120),
    ITEM((byte) -119);

    private final byte requestCommand;

    ClientDataSet(byte requestCommand) {
        this.requestCommand = requestCommand;
    }

    /** Trả command con nằm trong envelope NOT_MAP của yêu cầu cập nhật. */
    public byte requestCommand() {
        return requestCommand;
    }

    /** Ánh xạ command wire sang bộ dữ liệu, từ chối command không thuộc bước đồng bộ. */
    public static ClientDataSet fromRequestCommand(byte command) {
        for (ClientDataSet value : values()) {
            if (value.requestCommand == command) {
                return value;
            }
        }
        throw new IllegalArgumentException("unknown client data request command");
    }
}
