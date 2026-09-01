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

/** TCP acceptor có giới hạn, sở hữu listener, bộ thực thi phiên và vòng đời dừng an toàn. */
public final class TcpServer implements Closeable {
    private final TcpServerConfig config;
    private final SessionConnectionHandler handler;
    private final NetworkEventSink events;
    private final AtomicBoolean running = new AtomicBoolean();
    private final ThreadPoolExecutor sessions;
    private ServerSocket serverSocket;
    private Thread acceptThread;

    /** Tạo TCP server cùng executor giới hạn theo cấu hình số phiên tối đa. */
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

    /** Bind listener và khởi chạy accept thread; từ chối nếu server đã chạy. */
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

    /** Trả trạng thái hoạt động hiện tại của listener. */
    public boolean isRunning() {
        return running.get();
    }

    /** Trả địa chỉ thực tế đã bind, bao gồm cổng tạm nếu cấu hình dùng cổng 0. */
    public synchronized InetSocketAddress localAddress() {
        if (serverSocket == null || !serverSocket.isBound()) {
            throw new IllegalStateException("server is not bound");
        }
        return (InetSocketAddress) serverSocket.getLocalSocketAddress();
    }

    @Override
    /** Dừng listener, chờ phiên kết thúc trong timeout và bảo toàn interrupt của thread gọi. */
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

    /** Nhận socket liên tục khi server còn chạy và chuyển lỗi đến event sink. */
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

    /** Áp dụng read timeout, TCP_NODELAY và keep-alive cho socket mới. */
    private void configure(Socket socket) throws SocketException {
        socket.setSoTimeout(config.readTimeoutMillis());
        socket.setTcpNoDelay(true);
        socket.setKeepAlive(true);
    }

    /** Giao socket cho executor; từ chối và đóng ngay khi đã đạt giới hạn. */
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

    /** Đóng listener nếu đã tồn tại và chưa đóng. */
    private void closeServerSocket() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
    }

    /** Tạo thread factory đặt tên tuần tự để dễ chẩn đoán runtime. */
    private static ThreadFactory namedThreads(String prefix) {
        AtomicInteger sequence = new AtomicInteger();
        return task -> new Thread(task, prefix + sequence.incrementAndGet());
    }
}
