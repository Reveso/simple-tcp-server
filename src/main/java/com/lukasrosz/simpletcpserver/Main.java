package com.lukasrosz.simpletcpserver;

import com.lukasrosz.simpletcpserver.config.Configuration;
import com.lukasrosz.simpletcpserver.server.Server;
import lombok.extern.log4j.Log4j;

import java.util.Scanner;

@Log4j
public class Main {

    public static void main(String[] args) {

        Server simpleTCPServer = Configuration.getServer();

        Thread serverThread = new Thread(simpleTCPServer);
        serverThread.start();

        Scanner scanner = new Scanner(System.in);
        while(true) {
            String cmd = scanner.nextLine();
            if(cmd.equals("quit")) {
                simpleTCPServer.shutdown();
                break;
            }
        }
    }
}