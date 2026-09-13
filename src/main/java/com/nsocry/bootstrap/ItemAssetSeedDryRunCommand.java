package com.nsocry.bootstrap;

import com.nsocry.assets.ItemAssetValidationResult;
import com.nsocry.operations.ItemAssetSeedArchiveService;
import java.nio.file.Path;

/** Command kiểm định archive ITEM seed và chỉ in metadata, không mở database. */
public final class ItemAssetSeedDryRunCommand {
    private ItemAssetSeedDryRunCommand() {
    }

    /** Chấp nhận đúng một đường dẫn archive và in báo cáo dry-run. */
    public static void main(String[] args) throws Exception {
        if (args == null || args.length != 1) {
            throw new IllegalArgumentException("item-seed-dry-run yêu cầu đúng một archive path");
        }
        ItemAssetValidationResult result = new ItemAssetSeedArchiveService().dryRun(Path.of(args[0]));
        System.out.println("ITEM seed dry-run VERIFIED");
        System.out.println("version=" + Byte.toUnsignedInt(result.version()));
        System.out.println("optionCount=" + result.optionCount());
        System.out.println("itemCount=" + result.itemCount());
        System.out.println("payloadLength=" + result.payloadLength());
        System.out.println("sha256=" + result.payloadSha256());
    }
}
