package com.nsocry.bootstrap;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nsocry.persistence.SkillAssetSchemaPreflightReport;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;

class SkillAssetSchemaPreflightCommandTest {
    @Test
    void printsReadyWithoutDatabaseMutation() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        boolean ready = SkillAssetSchemaPreflightCommand.printReport(
                new SkillAssetSchemaPreflightReport(true, List.of()), printStream(bytes));
        assertTrue(ready);
        assertTrue(bytes.toString(StandardCharsets.UTF_8).contains("SKILL schema preflight READY"));
        assertTrue(bytes.toString(StandardCharsets.UTF_8).contains("databaseChanged=false"));
    }

    @Test
    void printsNotReadyWithEveryDifference() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        boolean ready = SkillAssetSchemaPreflightCommand.printReport(
                new SkillAssetSchemaPreflightReport(false, List.of("Thiếu cột: client_skill_options.id")),
                printStream(bytes));
        assertFalse(ready);
        assertTrue(bytes.toString(StandardCharsets.UTF_8).contains("difference=Thiếu cột"));
    }

    @Test
    void rejectsMoreThanOneConfigurationPath() {
        assertThrows(IllegalArgumentException.class,
                () -> SkillAssetSchemaPreflightCommand.main(new String[] {"one", "two"}));
    }

    private static PrintStream printStream(ByteArrayOutputStream bytes) {
        return new PrintStream(bytes, true, StandardCharsets.UTF_8);
    }
}
