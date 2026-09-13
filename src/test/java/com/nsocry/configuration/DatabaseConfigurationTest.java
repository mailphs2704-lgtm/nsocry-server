package com.nsocry.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import java.util.Properties;
import org.junit.jupiter.api.Test;

class DatabaseConfigurationTest {
    @Test
    void environmentOverridesPropertiesAndToStringRedactsPassword() {
        Properties properties = properties("property-user", "property-password");
        DatabaseConfiguration configuration = DatabaseConfiguration.from(properties, Map.of(
                DatabaseConfiguration.ENV_USER, "environment-user",
                DatabaseConfiguration.ENV_PASSWORD, "environment-secret"));
        assertEquals("environment-user", configuration.user());
        assertEquals("environment-secret", configuration.password());
        assertFalse(configuration.toString().contains("environment-secret"));
    }

    @Test
    void readsPropertiesWhenEnvironmentIsMissing() {
        DatabaseConfiguration configuration = DatabaseConfiguration.from(
                properties("nsocry-user", "local-secret"), Map.of());
        assertEquals("jdbc:mariadb://127.0.0.1:3306/nsocry", configuration.url());
        assertEquals("nsocry-user", configuration.user());
    }

    @Test
    void rejectsMissingPassword() {
        Properties properties = properties("nsocry-user", "secret");
        properties.remove(DatabaseConfiguration.PASSWORD);
        assertThrows(IllegalArgumentException.class,
                () -> DatabaseConfiguration.from(properties, Map.of()));
    }

    @Test
    void rejectsNonMariaDbJdbcUrl() {
        Properties properties = properties("nsocry-user", "secret");
        properties.setProperty(DatabaseConfiguration.URL, "jdbc:mysql://127.0.0.1/nsocry");
        assertThrows(IllegalArgumentException.class,
                () -> DatabaseConfiguration.from(properties, Map.of()));
    }

    private static Properties properties(String user, String password) {
        Properties properties = new Properties();
        properties.setProperty(DatabaseConfiguration.URL, "jdbc:mariadb://127.0.0.1:3306/nsocry");
        properties.setProperty(DatabaseConfiguration.USER, user);
        properties.setProperty(DatabaseConfiguration.PASSWORD, password);
        return properties;
    }
}
