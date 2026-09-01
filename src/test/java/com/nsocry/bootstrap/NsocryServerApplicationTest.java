package com.nsocry.bootstrap;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nsocry.configuration.ServerConfiguration;
import com.nsocry.network.NetworkEventSink;
import com.nsocry.network.TcpServerConfig;
import com.nsocry.session.AuthenticationDecision;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.time.Duration;
import org.junit.jupiter.api.Test;

class NsocryServerApplicationTest {
    @Test
    void startsAndClosesComposedServer() throws Exception {
        ServerConfiguration configuration = new ServerConfiguration(
                new TcpServerConfig(
                        new InetSocketAddress(InetAddress.getLoopbackAddress(), 0),
                        8, 2, 1_000, Duration.ofSeconds(2)),
                16);
        NetworkEventSink events = new NoOpEvents();
        NsocryServerApplication application = new NsocryServerApplication(
                configuration,
                (login, client) -> AuthenticationDecision.REJECTED,
                events);
        try {
            application.start();
            assertTrue(application.server().isRunning());
        } finally {
            application.close();
        }
        assertFalse(application.server().isRunning());
    }

    private static final class NoOpEvents implements NetworkEventSink {
        public void sessionFailed(java.net.SocketAddress remoteAddress, Exception failure) { }
        public void sessionRejected(java.net.SocketAddress remoteAddress) { }
        public void acceptFailed(java.io.IOException failure) { }
    }
}
