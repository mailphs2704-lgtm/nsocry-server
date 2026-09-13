package com.nsocry.bootstrap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nsocry.assets.SkillAssetBundle;
import com.nsocry.assets.SkillAssetSeedArtifactGenerator;
import com.nsocry.assets.SkillClassAsset;
import com.nsocry.assets.SkillLevelAsset;
import com.nsocry.assets.SkillLevelOptionAsset;
import com.nsocry.assets.SkillTemplateAsset;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;

class SkillAssetDatabaseVerifyCommandTest {
    @Test
    void verifiesSourceBundleAgainstCandidateManifest() throws Exception {
        var artifact = SkillAssetSeedArtifactGenerator.generate(fixture());
        var result = SkillAssetDatabaseVerifyCommand.verify(
                SkillAssetDatabaseVerifyCommandTest::fixture, artifact.manifestText());

        assertEquals(artifact.validation().payloadSha256(), result.payloadSha256());
        assertEquals(1, result.structure().skillLevelCount());
    }

    @Test
    void reportStatesDatabaseAndRuntimeWereNotChanged() {
        var result = SkillAssetSeedArtifactGenerator.generate(fixture()).validation();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        SkillAssetDatabaseVerifyCommand.printReport(
                result, new PrintStream(bytes, true, StandardCharsets.UTF_8));

        String report = bytes.toString(StandardCharsets.UTF_8);
        assertTrue(report.contains("SKILL database payload VERIFIED"));
        assertTrue(report.contains("databaseChanged=false"));
        assertTrue(report.contains("runtimeSnapshotPublished=false"));
    }

    private static SkillAssetBundle fixture() {
        var level = new SkillLevelAsset((short) 0, (byte) 150, (byte) 10, (short) 20,
                500, (short) 30, (short) 18, (byte) 1,
                List.of(new SkillLevelOptionAsset((short) 15, (byte) 0)));
        var template = new SkillTemplateAsset((byte) 0, "Chiêu Cry", (byte) 12,
                (byte) 1, (short) 318, "Mô tả", List.of(level));
        return new SkillAssetBundle((byte) 26, List.of("Tấn công"),
                List.of(new SkillClassAsset("Ninja Cry", List.of(template))));
    }
}
