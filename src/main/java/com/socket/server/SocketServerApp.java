package com.socket.server;

import com.socket.exception.ApplicationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketServerApp {

    private final static Logger logger = LoggerFactory.getLogger(SocketServerApp.class);
    public static void main(String[] args) {
        try {
            SocketServer socketServer = new SocketServer(7777);
            new Thread(socketServer).start();
        } catch (ApplicationException e) {
            logger.error("application exception occured !!", e);
        }
    }
}
