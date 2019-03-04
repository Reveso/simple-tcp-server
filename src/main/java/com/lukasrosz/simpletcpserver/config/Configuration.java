package com.lukasrosz.simpletcpserver.config;

import com.lukasrosz.simpletcpserver.server.SimpleTCPServer;
import lombok.extern.log4j.Log4j;
import org.apache.log4j.BasicConfigurator;

@Log4j
public abstract class Configuration {

    private static SimpleTCPServer simpleTCPServer;
    private static ResourceLoader resourceLoader = new ResourceLoader("config");

    static {
        //Log4j basic configuration
        BasicConfigurator.configure();
    }

    public static SimpleTCPServer getServer() {
        if(simpleTCPServer != null) {
            return simpleTCPServer;
        }

        initiateServer();
        return simpleTCPServer;
    }

    private static void initiateServer() {
        int serverPort = resourceLoader.getServerPort();
        if(serverPort == 0) {
            log.warn("Auto port");
        }
        simpleTCPServer = new SimpleTCPServer(serverPort);
    }

}
