package com.nsocry.bootstrap;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nsocry.persistence.MapAssetSchemaPreflightReport;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;

class MapAssetSchemaPreflightCommandTest {
    @Test
    void launcherRoutesMapSchemaPreflightWithOptionalConfiguration() {
        var request = NsocryLauncher.parse(
                new String[] {"map-schema-preflight", "config/nsocry.properties"});
        assertEquals(NsocryLauncher.LaunchCommand.MAP_SCHEMA_PREFLIGHT, request.command());
        assertEquals("config/nsocry.properties", request.configurationPath().toString().replace('\\', '/'));
    }

    @Test
    void printsReadyWithoutDatabaseMutation() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        boolean ready = MapAssetSchemaPreflightCommand.printReport(
                new MapAssetSchemaPreflightReport(true, List.of()), printStream(bytes));
        assertTrue(ready);
        assertTrue(bytes.toString(StandardCharsets.UTF_8).contains("MAP schema preflight READY"));
        assertTrue(bytes.toString(StandardCharsets.UTF_8).contains("databaseChanged=false"));
    }

    @Test
    void printsNotReadyAndRejectsExtraConfigurationPath() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        boolean ready = MapAssetSchemaPreflightCommand.printReport(
                new MapAssetSchemaPreflightReport(false,
                        List.of("Thiếu cột: client_map_names.id")), printStream(bytes));
        assertFalse(ready);
        assertTrue(bytes.toString(StandardCharsets.UTF_8).contains("difference=Thiếu cột"));
        assertThrows(IllegalArgumentException.class,
                () -> MapAssetSchemaPreflightCommand.main(new String[] {"one", "two"}));
    }

    private static PrintStream printStream(ByteArrayOutputStream bytes) {
        return new PrintStream(bytes, true, StandardCharsets.UTF_8);
    }
}
