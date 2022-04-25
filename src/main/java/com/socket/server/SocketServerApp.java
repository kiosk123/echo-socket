package com.socket.server;

import com.socket.exception.ApplicationException;

public class SocketServerApp {
    public static void main(String[] args) {
        try {
            SocketServer socketServer = new SocketServer(7777);
            new Thread(socketServer).start();
            
        } catch (ApplicationException e) {
            System.out.println("application exception occured !!");
            e.printStackTrace();
        }
    }
}
