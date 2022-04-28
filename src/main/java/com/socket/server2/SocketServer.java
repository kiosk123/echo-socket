package com.socket.server2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.socket.exception.ApplicationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketServer implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(SocketServer.class);

    ServerSocket serverSocket;

    public SocketServer(int port) throws ApplicationException {
        try {
            serverSocket = new ServerSocket(port);
            logger.info("creating server socket success. port number : {}", port);
        } catch (IOException e) {
            logger.error("creating server socket failed !!!", e);
            throw new ApplicationException(e);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                logger.info("waiting...");
                Socket socket = serverSocket.accept();

                logger.info("created socket!");
                SocketDataHandler handler = new SocketDataHandler(socket);
                new Thread(handler).start();

            } catch (IOException e) {
                logger.error("server socket accepting exception occurred !!!", e);
                break;
            } catch (ApplicationException e) {
                logger.error("application exception occured !!!", e);
            }
        }
    }
}
