package com.nsocry.bootstrap;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nsocry.persistence.ItemAssetSchemaPreflightReport;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;

class ItemAssetSchemaPreflightCommandTest {
    @Test
    void printsReadyWithoutDifferences() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        boolean ready = ItemAssetSchemaPreflightCommand.printReport(
                new ItemAssetSchemaPreflightReport(true, List.of()), output(bytes));

        assertTrue(ready);
        assertTrue(text(bytes).contains("ITEM schema preflight READY"));
    }

    @Test
    void printsEveryDifferenceWhenNotReady() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        boolean ready = ItemAssetSchemaPreflightCommand.printReport(
                new ItemAssetSchemaPreflightReport(false, List.of(
                        "Thiếu cột: client_item_options.id",
                        "Thiếu cột: client_item_templates.id")), output(bytes));

        assertFalse(ready);
        assertTrue(text(bytes).contains("ITEM schema preflight NOT_READY"));
        assertTrue(text(bytes).contains("difference=Thiếu cột: client_item_options.id"));
        assertTrue(text(bytes).contains("difference=Thiếu cột: client_item_templates.id"));
    }

    @Test
    void alwaysReportsThatDatabaseWasNotChanged() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        ItemAssetSchemaPreflightCommand.printReport(
                new ItemAssetSchemaPreflightReport(true, List.of()), output(bytes));

        assertTrue(text(bytes).contains("databaseChanged=false"));
    }

    private static PrintStream output(ByteArrayOutputStream bytes) {
        return new PrintStream(bytes, true, StandardCharsets.UTF_8);
    }

    private static String text(ByteArrayOutputStream bytes) {
        return bytes.toString(StandardCharsets.UTF_8);
    }
}
