package com.nsocry.bootstrap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nsocry.assets.ItemAssetBundle;
import com.nsocry.assets.ItemAssetSeedArtifact;
import com.nsocry.assets.ItemAssetSeedArtifactGenerator;
import com.nsocry.assets.ItemAssetSeedValidationException;
import com.nsocry.assets.ItemAssetValidationResult;
import com.nsocry.assets.ItemOptionAsset;
import com.nsocry.assets.ItemTemplateAsset;
import com.nsocry.persistence.ItemAssetSchemaPreflightReport;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;

class ItemAssetDatabaseVerifyCommandTest {
    @Test
    void verifiesSourceBundleAgainstCandidateManifest() throws Exception {
        ItemAssetSeedArtifact artifact = ItemAssetSeedArtifactGenerator.generate(fixture());

        ItemAssetValidationResult result = ItemAssetDatabaseVerifyCommand.verify(
                ItemAssetDatabaseVerifyCommandTest::fixture, artifact.manifestText());

        assertEquals(1, result.optionCount());
        assertEquals(1, result.itemCount());
        assertEquals(artifact.manifest().payloadSha256(), result.payloadSha256());
    }

    @Test
    void rejectsDatabaseBundleDifferentFromCandidate() throws Exception {
        ItemAssetSeedArtifact artifact = ItemAssetSeedArtifactGenerator.generate(fixture());
        ItemAssetBundle changed = new ItemAssetBundle(
                (byte) 26,
                List.of(new ItemOptionAsset("Đã thay đổi", (byte) 2)),
                fixture().items());

        assertThrows(ItemAssetSeedValidationException.class,
                () -> ItemAssetDatabaseVerifyCommand.verify(() -> changed, artifact.manifestText()));
    }

    @Test
    void rejectsSchemaThatIsNotReady() {
        assertThrows(IllegalStateException.class, () -> ItemAssetDatabaseVerifyCommand.requireReady(
                new ItemAssetSchemaPreflightReport(false, List.of("Thiếu cột"))));
    }

    @Test
    void reportStatesDatabaseAndRuntimeWereNotChanged() throws Exception {
        ItemAssetSeedArtifact artifact = ItemAssetSeedArtifactGenerator.generate(fixture());
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        ItemAssetDatabaseVerifyCommand.printReport(
                artifact.validation(), new PrintStream(bytes, true, StandardCharsets.UTF_8));

        String report = bytes.toString(StandardCharsets.UTF_8);
        assertTrue(report.contains("ITEM database payload VERIFIED"));
        assertTrue(report.contains("databaseChanged=false"));
        assertTrue(report.contains("runtimeSnapshotPublished=false"));
    }

    private static ItemAssetBundle fixture() {
        return new ItemAssetBundle(
                (byte) 26,
                List.of(new ItemOptionAsset("Tang HP", (byte) 2)),
                List.of(new ItemTemplateAsset(
                        (byte) 3, (byte) 1, "Kiem", "Mo ta",
                        (byte) 10, (short) 12, (short) 20, true)));
    }
}
