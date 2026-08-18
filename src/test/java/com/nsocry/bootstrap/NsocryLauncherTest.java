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
    void rejectsUnknownOrExcessArguments() {
        assertThrows(IllegalArgumentException.class,
                () -> NsocryLauncher.parse(new String[] {"unknown"}));
        assertThrows(IllegalArgumentException.class,
                () -> NsocryLauncher.parse(new String[] {"server", "one", "two"}));
    }
}
