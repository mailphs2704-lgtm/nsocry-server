package com.nsocry.bootstrap;

import com.nsocry.assets.MapAssetSeedValidationResult;
import com.nsocry.operations.MapAssetSeedArchiveService;
import java.nio.file.Path;

/** Xác minh MAP archive offline; không mở database hoặc publish runtime. */
public final class MapAssetSeedDryRunCommand {
    private MapAssetSeedDryRunCommand() {
    }

    /** Entry CLI yêu cầu đúng một archive path rồi xác minh không mutation. */
    public static void main(String[] args) throws Exception {
        if (args == null || args.length != 1) {
            throw new IllegalArgumentException("map-seed-dry-run yêu cầu đúng một archive path");
        }
        MapAssetSeedValidationResult result =
                new MapAssetSeedArchiveService().dryRun(Path.of(args[0]));
        System.out.println("MAP seed dry-run VERIFIED");
        System.out.println("version=" + Byte.toUnsignedInt(result.version()));
        System.out.println("mapCount=" + result.mapCount());
        System.out.println("npcCount=" + result.npcCount());
        System.out.println("mobCount=" + result.mobCount());
        System.out.println("payloadLength=" + result.payloadLength());
        System.out.println("sha256=" + result.payloadSha256());
        System.out.println("databaseChanged=false");
        System.out.println("runtimeSnapshotPublished=false");
    }
}
