package com.nsocry.persistence;

/** Lỗi validation hoặc transaction khi thay thế SKILL seed. */
public final class SkillAssetSeedImportException extends Exception {
    public SkillAssetSeedImportException(String message, Throwable cause) {
        super(message, cause);
    }
}
