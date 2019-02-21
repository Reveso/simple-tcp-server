package com.lukasrosz.simpletcpserver.server.connetion;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ConnectionsManager {
    private final int CONNECTION_TIMEOUT = 1000000;

    private final Set<ServerConnection> connections;

    private ScheduledExecutorService scheduledExecutor;

    public ConnectionsManager(boolean connectionRemovalTask) {
        connections = new HashSet<>();
        if(connectionRemovalTask) {
            scheduleClosedConnectionsRemovalTask();
        }
    }

    private void scheduleClosedConnectionsRemovalTask() {
        scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutor.scheduleWithFixedDelay(this::removeClosedConnections,
                5, 5, TimeUnit.MINUTES);
    }

    private void removeClosedConnections() {
        connections.forEach(serverConnection -> {
            if(serverConnection.getSocket().isClosed()) {
                connections.remove(serverConnection);
            }
        });
    }

    public void shutdownAllConnections() {
        connections.forEach(ServerConnection::shutdownConnection);
    }

    public void shutdownConnectionHandler() {
        shutdownAllConnections();
        scheduledExecutor.shutdownNow();
    }

    public boolean addConnection(ServerConnection serverConnection) {
        if(!serverConnection.getSocket().isClosed()) {
            return connections.add(serverConnection);
        } else return false;
    }

    public void submitNewConnection(Socket socket, ExecutorService connectionExecutor) {
        ServerConnection serverConnection = new ServerConnection(socket);
        connectionExecutor.submit(serverConnection);
        connections.add(serverConnection);
    }

    public Socket handleNewConnection(ServerSocket serverSocket, ExecutorService connectionExecutor)
            throws IOException{

        Socket socket = serverSocket.accept();
        socket.setSoTimeout(CONNECTION_TIMEOUT);

        submitNewConnection(socket, connectionExecutor);
        return socket;
    }

}
