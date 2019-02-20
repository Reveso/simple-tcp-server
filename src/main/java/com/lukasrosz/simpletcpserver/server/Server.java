package com.lukasrosz.simpletcpserver.server;

import com.lukasrosz.simpletcpserver.connetion.ServerConnection;
import com.sun.xml.internal.ws.Closeable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.ws.WebServiceException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
@Setter
@ToString
public class Server {
    private final int CONNECTION_TIMEOUT = 1000000;

    private ServerSocket serverSocket;
    private int serverPort;

    private ExecutorService executorService;
//    private final Set<ServerConnection> connections = new HashSet<>();

    public Server(int serverPort) {
        this.serverPort = serverPort;
    }

    private boolean createServerSocket() {
        try {
            System.out.println("Starting server on port: " + serverPort);
            serverSocket = new ServerSocket(serverPort);

        } catch (IOException e) {
            System.err.println("Error during server startup:\n\t" + serverSocket + "\n\t" + e);
            return false;
//            e.printStackTrace();
        }

        System.out.println("Server started successfully: " + serverSocket);
        return true;
    }

    public void run() {
        if (!createServerSocket()) {
            return;
        }

        executorService = Executors.newCachedThreadPool();
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                socket.setSoTimeout(CONNECTION_TIMEOUT);

                ServerConnection serverConnection = new ServerConnection(socket);
                executorService.submit(serverConnection);
//                connections.add(serverConnection);

            }
        } catch (IOException e) {
            System.err.println("Server exception:\n\t" + serverSocket + "\n\t" + e);
//            e.printStackTrace();
        } finally {
            shutdownServer();
        }
    }

    private void shutdownServer() {
        System.out.println("Terminating server: " + serverSocket);
//        connections.forEach(ServerConnection::shutdownConnection);

        try {
            executorService.shutdownNow();
            serverSocket.close();

        } catch (IOException e) {
            System.err.println("Error during server termination:\n\t" + serverSocket + "\n\t" + e);
//                e.printStackTrace();
        }
    }

}

