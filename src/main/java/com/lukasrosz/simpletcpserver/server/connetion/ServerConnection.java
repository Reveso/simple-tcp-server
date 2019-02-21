package com.lukasrosz.simpletcpserver.server.connetion;

import com.lukasrosz.simpletcpserver.packet.TCPPacket;
import lombok.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Date;

@Getter
@Setter
@ToString
public class ServerConnection implements Runnable {
    private Socket socket;
    private BufferedReader inputToServer;
//    private PrintWriter outputToClient;

    public ServerConnection(Socket socket) {
        this.socket = socket;
    }

    private void initialize() throws IOException {
        inputToServer = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
//        outputToClient = new PrintWriter(socket.getOutputStream(), true);
    }

    @Override
    public void run() {
        System.out.println("Connection open: " + socket);
        try {
            initialize();
            handleCommunication();

        } catch (IOException e) {
            System.err.println("Connection exception:\n\t" + socket + "\n\t" + e);
//            e.printStackTrace();
        } finally {
            shutdownConnection();
        }
    }

    private void handleCommunication() throws IOException {
        while(true) {
            TCPPacket packet = receivePacket();
            System.out.println("Packet received: " + packet);

//                String response = "RESPONSE to request: " + packet;
//                outputToClient.println(response);

            if(packet.getMessage() == null || packet.getMessage().toLowerCase().equals("quit")) {
                break;
            }
        }
    }

    private TCPPacket receivePacket() throws IOException {
        return new TCPPacket(socket.getInetAddress().toString(),
                socket.getPort(), new Date(), inputToServer.readLine());
    }

    public void shutdownConnection() {
        try {
            System.out.println("Closing connection: " + socket);
            socket.close();
        } catch (IOException e) {
            System.err.println("Error during connection termination:\n\t" + socket + "\n\t" + e);
//                e.printStackTrace();
        }
    }
}
