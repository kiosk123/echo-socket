package com.socket.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.socket.exception.ApplicationException;

public class SocketServer implements Runnable {
    ServerSocket serverSocket;

    public SocketServer(int port) throws ApplicationException {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("creating server socket success. port number : " + port);
        } catch (IOException e) {
            System.out.println("creating server socket failed !!!");
            e.printStackTrace();
            throw new ApplicationException(e);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                System.out.println("waiting...");
                Socket socket = serverSocket.accept();

                System.out.println("created socket!");
                SocketDataHandler handler = new SocketDataHandler(socket);
                new Thread(handler).start();

            } catch (IOException e) {
                System.out.println("server socket accepting exception occurred !!!");
                break;
            } catch (ApplicationException e) {
                System.out.println("application exception occured !!!");
                e.printStackTrace();
            }
        }
    }
}
