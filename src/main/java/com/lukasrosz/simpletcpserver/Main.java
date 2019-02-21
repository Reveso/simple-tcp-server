package com.lukasrosz.simpletcpserver;

import com.lukasrosz.simpletcpserver.server.Server;
import com.lukasrosz.simpletcpserver.server.SimpleTCPServer;

import java.util.ResourceBundle;

public class Main {

    public static void main(String[] args) {
        String resourceName = "serverPort";
        ResourceBundle resourceBundle = ResourceBundle.getBundle("config");
        int serverPort = Integer.parseInt((resourceBundle.getString(resourceName)));

        Server simpleTCPServer = new SimpleTCPServer(serverPort);
        simpleTCPServer.startServer();
    }
}