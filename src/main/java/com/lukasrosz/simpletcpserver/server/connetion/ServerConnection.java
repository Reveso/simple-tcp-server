package com.lukasrosz.simpletcpserver.server.connetion;

public interface ServerConnection extends Runnable {

    void sendMessage(String message);

    void close();

    boolean isClosed();
}
