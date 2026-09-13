package com.nsocry.operations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nsocry.assets.AppearanceAssetBundle;
import com.nsocry.assets.AppearanceAssetSeedArtifact;
import com.nsocry.assets.AppearanceAssetSeedArtifactGenerator;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class AppearanceAssetSeedArchiveServiceTest {
    @TempDir
    Path directory;

    @Test
    void exportsAndValidatesRoundTrip() throws Exception {
        AppearanceAssetSeedArtifact artifact =
                AppearanceAssetSeedArtifactGenerator.generate(emptyBundle());
        Path archive = directory.resolve("appearance.zip");
        AppearanceAssetSeedArchiveService service = new AppearanceAssetSeedArchiveService();

        service.export(artifact, archive);
        var result = service.dryRun(archive);

        assertEquals(artifact.payloadLength(), result.payloadLength());
        assertEquals(artifact.payloadSha256(), result.payloadSha256());
        assertTrue(result.roundTripVerified());
    }

    @Test
    void refusesOverwrite() throws Exception {
        AppearanceAssetSeedArtifact artifact =
                AppearanceAssetSeedArtifactGenerator.generate(emptyBundle());
        Path archive = directory.resolve("appearance.zip");
        AppearanceAssetSeedArchiveService service = new AppearanceAssetSeedArchiveService();

        service.export(artifact, archive);
        assertThrows(IOException.class, () -> service.export(artifact, archive));
    }

    private static AppearanceAssetBundle emptyBundle() {
        return new AppearanceAssetBundle(
                List.of(), List.of(), List.of(), List.of(),
                List.of(), List.of(), List.of(), List.of());
    }
}
