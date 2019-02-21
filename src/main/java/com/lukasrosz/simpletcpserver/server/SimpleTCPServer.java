package com.lukasrosz.simpletcpserver.server;

import com.lukasrosz.simpletcpserver.server.connetion.ConnectionsManager;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
@Setter
@ToString
public class SimpleTCPServer implements Server {
    private ServerSocket serverSocket;
    private int serverPort;

    private ConnectionsManager connectionsManager;
    private ExecutorService connectionsExecutor;


    public SimpleTCPServer(int serverPort) {
        this.serverPort = serverPort;
    }

    private boolean createServerSocket() {
        try {
            System.out.println("Starting server on port: " + serverPort);
            serverSocket = new ServerSocket(serverPort);

        } catch (IOException e) {
            System.err.println("Error during server startup on port:\n\t" + serverPort + "\n\t" + e);
            return false;
//            e.printStackTrace();
        }

        System.out.println("SimpleTCPServer started successfully: " + serverSocket);
        return true;
    }

    @Override
    public void startServer() {
        if (!createServerSocket()) {
            return;
        }

        connectionsManager = new ConnectionsManager(true);
        connectionsExecutor = Executors.newCachedThreadPool();

        try {
            while (true) {
                connectionsManager.handleNewConnection(serverSocket, connectionsExecutor);
            }
        } catch (IOException e) {
            System.err.println("SimpleTCPServer exception:\n\t" + serverSocket + "\n\t" + e);
//            e.printStackTrace();
        } finally {
            shutdownServer();
        }
    }

    private void shutdownServer() {
        System.out.println("Terminating server: " + serverSocket);

        try {
            connectionsManager.shutdownConnectionHandler();
            connectionsExecutor.shutdownNow();
            serverSocket.close();
        } catch (IOException e) {
            System.err.println("Error during server termination:\n\t" + serverSocket + "\n\t" + e);
//                e.printStackTrace();
        }
    }

}

