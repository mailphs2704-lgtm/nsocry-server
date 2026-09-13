package com.nsocry.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Properties;
import org.junit.jupiter.api.Test;

class ServerConfigurationTest {
    @Test
    void usesDocumentedDefaults() {
        ServerConfiguration configuration = ServerConfiguration.from(new Properties());
        assertEquals(14_444, configuration.tcp().bindAddress().getPort());
        assertEquals(500, configuration.tcp().maxSessions());
        assertEquals(32, configuration.sessionKeyLength());
    }

    @Test
    void readsExplicitValues() {
        Properties properties = new Properties();
        properties.setProperty(ServerConfiguration.HOST, "127.0.0.1");
        properties.setProperty(ServerConfiguration.PORT, "0");
        properties.setProperty(ServerConfiguration.MAX_SESSIONS, "12");
        properties.setProperty(ServerConfiguration.SESSION_KEY_LENGTH, "24");
        ServerConfiguration configuration = ServerConfiguration.from(properties);
        assertEquals("127.0.0.1", configuration.tcp().bindAddress().getHostString());
        assertEquals(0, configuration.tcp().bindAddress().getPort());
        assertEquals(12, configuration.tcp().maxSessions());
        assertEquals(24, configuration.sessionKeyLength());
    }

    @Test
    void rejectsNonNumericPortWithPropertyName() {
        Properties properties = new Properties();
        properties.setProperty(ServerConfiguration.PORT, "wrong");
        IllegalArgumentException failure = assertThrows(
                IllegalArgumentException.class, () -> ServerConfiguration.from(properties));
        assertEquals(ServerConfiguration.PORT + " must be an integer", failure.getMessage());
    }

    @Test
    void rejectsOutOfRangeKeyLength() {
        Properties properties = new Properties();
        properties.setProperty(ServerConfiguration.SESSION_KEY_LENGTH, "256");
        assertThrows(IllegalArgumentException.class, () -> ServerConfiguration.from(properties));
    }
}
