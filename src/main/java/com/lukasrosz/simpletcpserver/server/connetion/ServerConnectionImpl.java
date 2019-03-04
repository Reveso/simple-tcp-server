package com.lukasrosz.simpletcpserver.server.connetion;

import com.lukasrosz.simpletcpserver.message.Message;
import lombok.ToString;
import lombok.extern.log4j.Log4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

@Log4j
@ToString
public class ServerConnectionImpl implements ServerConnection {
    private Socket socket;
    private BufferedReader inputToServer;
    private PrintWriter outputToClient;

    private boolean shutdown = false;

    public ServerConnectionImpl(Socket socket) {
        this.socket = socket;
    }

    private void initialize() throws IOException {
        inputToServer = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        outputToClient = new PrintWriter(socket.getOutputStream(), true);
    }

    @Override
    public void run() {
        log.info("Connection open: " + socket);
        try {
            initialize();
            handleCommunication();

        } catch (IOException e) {
            if (!shutdown)
                log.error("Connection exception: " + socket, e);
        } finally {
            if (!shutdown)
                close();
        }
    }

    private void handleCommunication() throws IOException {
        while (true) {
            Message packet = receiveMessage();
            log.info("Packet received: " + packet);

            if (packet.getMessage() == null || packet.getMessage().toLowerCase().equals("quit")) {
                break;
            }
        }
    }

    private Message receiveMessage() throws IOException {
        return new Message(socket.getInetAddress().toString(),
                socket.getPort(), new Date(), inputToServer.readLine());
    }

    @Override
    public void sendMessage(String message) {
        outputToClient.println(message);
    }

    @Override
    public void close() {
        shutdown = true;
        log.info("Closing connection: " + socket);
        sendMessage("Closing connection. Server shutdown.");
        try {
            socket.close();
        } catch (IOException e) {
            log.error("Error during connection termination: " + socket, e);
        }
    }

    @Override
    public boolean isClosed() {
        return socket.isClosed();
    }
}
