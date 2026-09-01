package com.nsocry.bootstrap;

import com.nsocry.assets.DataAssetSeedValidationResult;
import com.nsocry.operations.DataAssetSeedArchiveService;
import java.nio.file.Path;

/** Đọc lại và kiểm định DATA archive offline, không mutation database/runtime/startup. */
public final class DataAssetSeedArchiveDryRunCommand {
    private DataAssetSeedArchiveDryRunCommand() {
    }

    /** Entry CLI yêu cầu đúng một archive path và in metadata đã xác minh. */
    public static void main(String[] args) throws Exception {
        if (args == null || args.length != 1) {
            throw new IllegalArgumentException("data-seed-archive-dry-run yêu cầu đúng một archive path");
        }
        DataAssetSeedValidationResult result =
                new DataAssetSeedArchiveService().dryRun(Path.of(args[0]));
        System.out.println("DATA seed archive dry-run VERIFIED");
        System.out.println("version=" + Byte.toUnsignedInt(result.version()));
        System.out.println("taskGroupCount=" + result.taskGroupCount());
        System.out.println("experienceCount=" + result.experienceCount());
        System.out.println("payloadLength=" + result.payloadLength());
        System.out.println("sha256=" + result.payloadSha256());
        System.out.println("databaseChanged=false");
        System.out.println("runtimeSnapshotPublished=false");
        System.out.println("serverStartupWired=false");
    }
}
