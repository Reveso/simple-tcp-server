package com.lukasrosz.simpletcpserver.server;

import com.lukasrosz.simpletcpserver.server.connetion.ConnectionsManager;
import com.lukasrosz.simpletcpserver.server.connetion.ConnectionsManagerImpl;
import lombok.ToString;
import lombok.extern.log4j.Log4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.TimeUnit;

@Log4j
@ToString
public class SimpleTCPServer implements Server {
    private final int TIMEOUT_MS = 10000;
    private boolean shutdown = false;

    private ServerSocket serverSocket;
    private final ConnectionsManager connectionsManager;

    public SimpleTCPServer(int serverPort) {
        createServerSocket(serverPort);
        connectionsManager = new ConnectionsManagerImpl(false);
    }

    private void createServerSocket(int serverPort) {
        log.info("Starting server on port: " + serverPort);
        try {
            serverSocket = new ServerSocket(serverPort);
        } catch (IOException e) {
            log.fatal("Error during server startup on port: " + serverPort, e);
            return;
        }

        log.info("SimpleTCPServer started successfully: " + serverSocket);
    }

    @Override
    public void run() {
        if (serverSocket == null) {
            log.fatal("Server was not properly initialized");
            return;
        }

        connectionsManager.scheduleClosedConnectionsRemovalTask(5, TimeUnit.MINUTES);

        try {
            while (true) {
                log.info("Awaiting connection");
                connectionsManager.handleNewConnection(serverSocket);
            }
        } catch (IOException e) {
            if(!shutdown)
                log.fatal(serverSocket, e);
        } finally {
            if(!shutdown)
                performShutdown();
        }
    }

    @Override
    public void shutdown() {
        shutdown = true;
        performShutdown();
    }

    private void performShutdown() {
        log.info("Terminating server: " + serverSocket);

        connectionsManager.shutdown();
        try {
            serverSocket.close();
        } catch (IOException e) {
            log.error("Error during server termination: " + serverSocket, e);
        }
    }

}

