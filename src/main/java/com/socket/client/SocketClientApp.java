package com.socket.client;

import com.socket.exception.ApplicationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketClientApp {

    private final static Logger logger = LoggerFactory.getLogger(SocketClientApp.class);
    public static void main(String[] args) {
        try {
            SocketClient socketClient = new SocketClient("localhost", 7777);
            new Thread(socketClient).start();
        } catch (ApplicationException e) {
            logger.error("application exception occured !!", e);
        }
    }
}
