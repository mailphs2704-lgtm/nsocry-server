package com.nsocry.bootstrap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nsocry.persistence.DataAssetSchemaPreflightReport;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;

class DataAssetSchemaPreflightCommandTest {
    @Test
    void launcherRoutesDataSchemaPreflightWithOptionalConfiguration() {
        var request = NsocryLauncher.parse(
                new String[] {"data-schema-preflight", "config/nsocry.properties"});
        assertEquals(NsocryLauncher.LaunchCommand.DATA_SCHEMA_PREFLIGHT, request.command());
        assertEquals("config/nsocry.properties",
                request.configurationPath().toString().replace('\\', '/'));
    }

    @Test
    void printsReadyWithoutDatabaseMutation() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        boolean ready = DataAssetSchemaPreflightCommand.printReport(
                new DataAssetSchemaPreflightReport(true, List.of()), printStream(bytes));
        assertTrue(ready);
        assertTrue(bytes.toString(StandardCharsets.UTF_8).contains("DATA schema preflight READY"));
        assertTrue(bytes.toString(StandardCharsets.UTF_8).contains("databaseChanged=false"));
    }

    @Test
    void printsNotReadyAndRejectsExtraConfigurationPath() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        boolean ready = DataAssetSchemaPreflightCommand.printReport(
                new DataAssetSchemaPreflightReport(false,
                        List.of("Thiếu cột: client_data_assets.version")), printStream(bytes));
        assertFalse(ready);
        assertTrue(bytes.toString(StandardCharsets.UTF_8).contains("difference=Thiếu cột"));
        assertThrows(IllegalArgumentException.class,
                () -> DataAssetSchemaPreflightCommand.main(new String[] {"one", "two"}));
    }

    private static PrintStream printStream(ByteArrayOutputStream bytes) {
        return new PrintStream(bytes, true, StandardCharsets.UTF_8);
    }
}
