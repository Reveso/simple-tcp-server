package com.lukasrosz.simpletcpserver.server.connetion;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public interface ConnectionsManager {

    void scheduleClosedConnectionsRemovalTask(int initialDelay, int delay, TimeUnit timeUnit);

    Socket handleNewConnection(ServerSocket serverSocket) throws IOException;

    void shutdown();
}
