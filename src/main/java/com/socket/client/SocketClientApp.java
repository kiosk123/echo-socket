package com.socket.client;

import com.socket.exception.ApplicationException;

public class SocketClientApp {
    public static void main(String[] args) {
        try {
            SocketClient socketClient = new SocketClient("localhost", 7777);
            new Thread(socketClient).start();
        } catch (ApplicationException e) {
            System.out.println("application exception occured !!");
            e.printStackTrace();
        }

    }
}
