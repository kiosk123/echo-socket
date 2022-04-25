package com.socket.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Scanner;

import com.common.CommonConstants;
import com.socket.exception.ApplicationException;

public class SocketClient implements Runnable {
    private Socket client;
    private InputStream in;
    private OutputStream out;

    public SocketClient(String host, int port) throws ApplicationException {
        try {
            client = new Socket(host, port);
            System.out.println("host: " + host + " port: " + port + " connection success.");

            in = client.getInputStream();
            out = client.getOutputStream();

        } catch (IOException e) {
            System.out.println("connecting server failed !!");
            e.printStackTrace();
            throw new ApplicationException(e);
        }
    }

    @Override
    public void run() {
        while (true) {
            Scanner sc = new Scanner(System.in);
            System.out.print("input sending messsage : ");
            
            String sendMessage = sc.nextLine();
            System.out.println("sending message : " + sendMessage);

            byte[] contentBytes = sendMessage.getBytes(Charset.forName("EUC-KR"));
            
            int headerLength = CommonConstants.CONTENT_HEADER_LENGTH;
            byte[] headerBytes = String.format("%0" + headerLength + "d", contentBytes.length).getBytes();
            
            int sendLength = headerBytes.length + contentBytes.length;
            byte[] sendBytes = new byte[sendLength];

            System.arraycopy(headerBytes, 0, sendBytes, 0, headerBytes.length);
            System.arraycopy(contentBytes, 0, sendBytes, headerBytes.length, contentBytes.length);

            try {
                out.write(sendBytes);
                out.flush();
            } catch (IOException e) {
                System.out.println("While sending data to server, error occured!!!");
                e.printStackTrace();

                if (in != null) {try { in.close(); } catch (IOException e2) {}}
                if (out != null) {try { out.close(); } catch (IOException e2) {}}
                if (client != null) {try { client.close(); } catch (IOException e2) {}}

                System.out.println("client finished!");
            }
        }
    }
}
