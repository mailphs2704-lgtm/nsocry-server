package com.nsocry.bootstrap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class NsocryLauncherTest {
    @Test
    void defaultsToHelpInsteadOfStartingServer() {
        NsocryLauncher.LaunchRequest request = NsocryLauncher.parse(new String[0]);
        assertEquals(NsocryLauncher.LaunchCommand.HELP, request.command());
        assertNull(request.configurationPath());
    }

    @Test
    void parsesServerWithConfigurationPath() {
        NsocryLauncher.LaunchRequest request = NsocryLauncher.parse(
                new String[] {"server", "config/local.properties"});
        assertEquals(NsocryLauncher.LaunchCommand.SERVER, request.command());
        assertEquals(Path.of("config/local.properties"), request.configurationPath());
    }

    @Test
    void parsesCreateAdministratorCommand() {
        assertEquals(NsocryLauncher.LaunchCommand.CREATE_ADMIN,
                NsocryLauncher.parse(new String[] {"create-admin"}).command());
    }

    @Test
    void parsesItemSeedDryRunArchive() {
        NsocryLauncher.LaunchRequest request = NsocryLauncher.parse(
                new String[] {"item-seed-dry-run", "seed/item.zip"});
        assertEquals(NsocryLauncher.LaunchCommand.ITEM_SEED_DRY_RUN, request.command());
        assertEquals(Path.of("seed/item.zip"), request.configurationPath());
    }

    @Test
    void parsesItemSeedConvertDump() {
        NsocryLauncher.LaunchRequest request = NsocryLauncher.parse(
                new String[] {"item-seed-convert", "source/database.sql"});
        assertEquals(NsocryLauncher.LaunchCommand.ITEM_SEED_CONVERT, request.command());
        assertEquals(Path.of("source/database.sql"), request.configurationPath());
    }

    @Test
    void parsesItemSchemaPreflightWithDefaultConfig() {
        NsocryLauncher.LaunchRequest request = NsocryLauncher.parse(
                new String[] {"item-schema-preflight"});
        assertEquals(NsocryLauncher.LaunchCommand.ITEM_SCHEMA_PREFLIGHT, request.command());
        assertNull(request.configurationPath());
    }

    @Test
    void parsesItemSchemaPreflightWithExplicitConfig() {
        NsocryLauncher.LaunchRequest request = NsocryLauncher.parse(
                new String[] {"item-schema-preflight", "config/local.properties"});
        assertEquals(NsocryLauncher.LaunchCommand.ITEM_SCHEMA_PREFLIGHT, request.command());
        assertEquals(Path.of("config/local.properties"), request.configurationPath());
    }

    @Test
    void parsesInteractiveItemSeedImport() {
        NsocryLauncher.LaunchRequest request = NsocryLauncher.parse(
                new String[] {"item-seed-import", "seed/item.zip"});
        assertEquals(NsocryLauncher.LaunchCommand.ITEM_SEED_IMPORT, request.command());
        assertEquals(Path.of("seed/item.zip"), request.configurationPath());
    }

    @Test
    void parsesItemDatabaseVerification() {
        NsocryLauncher.LaunchRequest request = NsocryLauncher.parse(
                new String[] {"item-seed-db-verify", "seed/item.zip"});
        assertEquals(NsocryLauncher.LaunchCommand.ITEM_SEED_DB_VERIFY, request.command());
        assertEquals(Path.of("seed/item.zip"), request.configurationPath());
    }

    @Test
    void rejectsUnknownOrExcessArguments() {
        assertThrows(IllegalArgumentException.class,
                () -> NsocryLauncher.parse(new String[] {"unknown"}));
        assertThrows(IllegalArgumentException.class,
                () -> NsocryLauncher.parse(new String[] {"server", "one", "two"}));
    }
}
