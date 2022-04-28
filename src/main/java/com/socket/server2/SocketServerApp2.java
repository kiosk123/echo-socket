package com.socket.server2;

import com.socket.exception.ApplicationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketServerApp2 {

    private final static Logger logger = LoggerFactory.getLogger(SocketServerApp2.class);
    public static void main(String[] args) {
        try {
            SocketServer2 socketServer = new SocketServer2(7777);
            new Thread(socketServer).start();
        } catch (ApplicationException e) {
            logger.error("application exception occured !!", e);
        }
    }
}
