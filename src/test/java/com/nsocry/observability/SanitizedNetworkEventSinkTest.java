package com.nsocry.observability;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class SanitizedNetworkEventSinkTest {
    @Test
    void excludesExceptionMessageFromSessionEvent() {
        List<String> lines = new ArrayList<>();
        SanitizedNetworkEventSink sink = new SanitizedNetworkEventSink(lines::add);
        sink.sessionFailed(new InetSocketAddress("127.0.0.1", 14444),
                new IOException("password=must-not-appear"));
        assertTrue(lines.get(0).contains("IOException"));
        assertFalse(lines.get(0).contains("must-not-appear"));
    }

    @Test
    void handlesNullDiagnosticValuesSafely() {
        List<String> lines = new ArrayList<>();
        SanitizedNetworkEventSink sink = new SanitizedNetworkEventSink(lines::add);
        sink.sessionFailed(null, null);
        assertTrue(lines.get(0).contains("remote=unknown"));
        assertTrue(lines.get(0).contains("type=UnknownException"));
    }
}
