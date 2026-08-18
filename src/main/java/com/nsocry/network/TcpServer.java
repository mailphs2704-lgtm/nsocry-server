package com.nsocry.network;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Objects;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public final class TcpServer implements Closeable {
    private final TcpServerConfig config;
    private final SessionConnectionHandler handler;
    private final NetworkEventSink events;
    private final AtomicBoolean running = new AtomicBoolean();
    private final ThreadPoolExecutor sessions;
    private ServerSocket serverSocket;
    private Thread acceptThread;

    public TcpServer(
            TcpServerConfig config,
            SessionConnectionHandler handler,
            NetworkEventSink events) {
        this.config = Objects.requireNonNull(config, "config");
        this.handler = Objects.requireNonNull(handler, "handler");
        this.events = Objects.requireNonNull(events, "events");
        this.sessions = new ThreadPoolExecutor(
                0,
                config.maxSessions(),
                30,
                TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                namedThreads("nsocry-session-"),
                new ThreadPoolExecutor.AbortPolicy());
    }

    public synchronized void start() throws IOException {
        if (!running.compareAndSet(false, true)) {
            throw new IllegalStateException("server is already running");
        }
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(config.bindAddress(), config.backlog());
            acceptThread = new Thread(this::acceptLoop, "nsocry-acceptor");
            acceptThread.start();
        } catch (IOException exception) {
            running.set(false);
            closeServerSocket();
            throw exception;
        }
    }

    public boolean isRunning() {
        return running.get();
    }

    public synchronized InetSocketAddress localAddress() {
        if (serverSocket == null || !serverSocket.isBound()) {
            throw new IllegalStateException("server is not bound");
        }
        return (InetSocketAddress) serverSocket.getLocalSocketAddress();
    }

    @Override
    public void close() throws IOException {
        if (!running.compareAndSet(true, false)) {
            return;
        }
        closeServerSocket();
        sessions.shutdown();
        try {
            if (!sessions.awaitTermination(config.shutdownTimeout().toMillis(), TimeUnit.MILLISECONDS)) {
                sessions.shutdownNow();
            }
        } catch (InterruptedException exception) {
            sessions.shutdownNow();
            Thread.currentThread().interrupt();
        }
        Thread thread = acceptThread;
        if (thread != null && thread != Thread.currentThread()) {
            try {
                thread.join(config.shutdownTimeout().toMillis());
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void acceptLoop() {
        while (running.get()) {
            try {
                Socket socket = serverSocket.accept();
                configure(socket);
                dispatch(socket);
            } catch (SocketException exception) {
                if (running.get()) {
                    running.set(false);
                    events.acceptFailed(exception);
                }
            } catch (IOException exception) {
                if (running.get()) {
                    running.set(false);
                    events.acceptFailed(exception);
                }
            }
        }
    }

    private void configure(Socket socket) throws SocketException {
        socket.setSoTimeout(config.readTimeoutMillis());
        socket.setTcpNoDelay(true);
        socket.setKeepAlive(true);
    }

    private void dispatch(Socket socket) throws IOException {
        try {
            sessions.execute(() -> {
                try (socket) {
                    handler.handle(socket);
                } catch (Exception failure) {
                    events.sessionFailed(socket.getRemoteSocketAddress(), failure);
                }
            });
        } catch (RejectedExecutionException exception) {
            events.sessionRejected(socket.getRemoteSocketAddress());
            socket.close();
        }
    }

    private void closeServerSocket() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
    }

    private static ThreadFactory namedThreads(String prefix) {
        AtomicInteger sequence = new AtomicInteger();
        return task -> new Thread(task, prefix + sequence.incrementAndGet());
    }
}
