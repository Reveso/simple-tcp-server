package com.lukasrosz.simpletcpserver.server.connetion;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

public class ConnectionsManagerImpl implements ConnectionsManager {
    private final int CONNECTION_TIMEOUT = 1000000;

    private final Set<ServerConnection> connections;
    private final ExecutorService connectionsExecutor;

    private final ScheduledExecutorService scheduledExecutor;
    private ScheduledFuture<?> scheduledConnectionRemovalFuture;


    public ConnectionsManagerImpl(boolean connectionRemovalTask) {
        connections = new HashSet<>();
        scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        connectionsExecutor = Executors.newCachedThreadPool();

        if (connectionRemovalTask) {
            scheduleClosedConnectionsRemovalTask(5,5, TimeUnit.MINUTES);
        }
    }
    public void scheduleClosedConnectionsRemovalTask(int initialDelay, int delay, TimeUnit timeUnit) {
        if (scheduledConnectionRemovalFuture != null) {
            scheduledConnectionRemovalFuture.cancel(false);
        }
        scheduledConnectionRemovalFuture = scheduledExecutor.scheduleAtFixedRate(this::removeClosedConnections, delay, delay,
                timeUnit);
    }

    private void removeClosedConnections() {
        connections.forEach(serverConnection -> {
            if (serverConnection.isClosed()) {
                connections.remove(serverConnection);
            }
        });
    }

    private void shutdownAllConnections() {
        connections.forEach(ServerConnection::close);
    }

    public void shutdown() {
        connectionsExecutor.shutdownNow();
        scheduledExecutor.shutdownNow();
        shutdownAllConnections();
    }

    public Socket handleNewConnection(ServerSocket serverSocket)
            throws IOException {

        Socket socket = serverSocket.accept();
        socket.setSoTimeout(CONNECTION_TIMEOUT);

        submitNewConnection(socket);
        return socket;
    }

    private void submitNewConnection(Socket socket) {
        ServerConnection serverConnection = new ServerConnectionImpl(socket);
        connectionsExecutor.submit(serverConnection);
        connections.add(serverConnection);
    }
//    public boolean addConnection(ServerConnectionImpl serverConnection) {
//        if(!serverConnection.isClosed()) {
//            return connections.add(serverConnection);
//        } else return false;
//    }

}
