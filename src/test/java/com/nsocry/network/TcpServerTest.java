package com.nsocry.network;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class TcpServerTest {
    @Test
    void acceptsLoopbackConnectionAndShutsDown() throws Exception {
        CountDownLatch accepted = new CountDownLatch(1);
        TcpServerConfig config = new TcpServerConfig(
                new InetSocketAddress(InetAddress.getLoopbackAddress(), 0),
                8,
                2,
                1_000,
                Duration.ofSeconds(2));
        TcpServer server = new TcpServer(config, socket -> accepted.countDown(), new RecordingEvents());

        try {
            server.start();
            assertTrue(server.isRunning());
            try (Socket ignored = new Socket()) {
                ignored.connect(server.localAddress(), 1_000);
                assertTrue(accepted.await(2, TimeUnit.SECONDS));
            }
        } finally {
            server.close();
        }

        assertFalse(server.isRunning());
    }

    private static final class RecordingEvents implements NetworkEventSink {
        @Override
        public void sessionFailed(java.net.SocketAddress remoteAddress, Exception failure) {
            throw new AssertionError(failure);
        }

        @Override
        public void sessionRejected(java.net.SocketAddress remoteAddress) {
            throw new AssertionError("session unexpectedly rejected");
        }

        @Override
        public void acceptFailed(java.io.IOException failure) {
            throw new AssertionError(failure);
        }
    }
}
