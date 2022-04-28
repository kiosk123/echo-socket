package com.socket.server2;

import com.socket.exception.ApplicationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketServerApp2 {

    private final static Logger logger = LoggerFactory.getLogger(SocketServerApp2.class);
    public static void main(String[] args) {
        try {
            SocketServer2 socketServer = null;
            if (args.length == 1) {
                int port = Integer.parseInt(args[0]);
                socketServer = new SocketServer2(port);
            } else {
                logger.info("default init port [7777]");
                socketServer = new SocketServer2(7777);
            }
            new Thread(socketServer).start();
        } catch (ApplicationException e) {
            logger.error("application exception occured !!", e);
        } catch (NumberFormatException e) {
            logger.error("port value is not number : {}", args[0]);
        }
    }
}
