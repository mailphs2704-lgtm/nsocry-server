package com.nsocry.bootstrap;

import com.nsocry.assets.SkillAssetSeedValidationResult;
import com.nsocry.operations.SkillAssetSeedArchiveService;
import java.nio.file.Path;

/** Command xác minh SKILL archive mà không mở database hoặc publish snapshot. */
public final class SkillAssetSeedDryRunCommand {
    private SkillAssetSeedDryRunCommand() {
    }

    public static void main(String[] args) throws Exception {
        if (args == null || args.length != 1) {
            throw new IllegalArgumentException("skill-seed-dry-run yêu cầu đúng một archive path");
        }
        SkillAssetSeedValidationResult result = new SkillAssetSeedArchiveService().dryRun(Path.of(args[0]));
        var structure = result.structure();
        System.out.println("SKILL seed dry-run VERIFIED");
        System.out.println("version=" + Byte.toUnsignedInt(result.version()));
        System.out.println("optionTemplateCount=" + structure.optionTemplateCount());
        System.out.println("classCount=" + structure.classCount());
        System.out.println("skillTemplateCount=" + structure.skillTemplateCount());
        System.out.println("skillLevelCount=" + structure.skillLevelCount());
        System.out.println("skillLevelOptionCount=" + structure.skillLevelOptionCount());
        System.out.println("rawByteDifferences=" + result.rawByteDifferences());
        System.out.println("payloadLength=" + result.payloadLength());
        System.out.println("sha256=" + result.payloadSha256());
        System.out.println("databaseChanged=false");
    }
}
