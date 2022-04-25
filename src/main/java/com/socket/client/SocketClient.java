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
                break;
            }

            try {
                byte[] echoHeader = new byte[CommonConstants.CONTENT_HEADER_LENGTH];
                int echoHeaderLength = getHeader(echoHeader);
                System.out.println("****************** echo from server **********************");
                
                System.out.println("****************** echo header length ********************");
                System.out.println("header length : " + echoHeaderLength);
                
                System.out.println("****************** echo body content *********************");
                String echoContent = getBodyContent(echoHeaderLength);
                System.out.println(echoContent);
                System.out.println();
            } catch (IOException e) {

                System.out.println("While recieveing data from server, error occured!!!");
                e.printStackTrace();

                if (in != null) {try { in.close(); } catch (IOException e2) {}}
                if (out != null) {try { out.close(); } catch (IOException e2) {}}
                if (client != null) {try { client.close(); } catch (IOException e2) {}}

                System.out.println("client finished!");
                break;
            }
        }
    }


    private int getHeader(byte[] header) throws IOException {
        int headerLength = in.read(header);
        while (headerLength < CommonConstants.CONTENT_HEADER_LENGTH) {
            int gapLength = CommonConstants.CONTENT_HEADER_LENGTH - headerLength;
            byte[] buf = new byte[gapLength];
            
            int readLength = in.read(buf);
            System.arraycopy(buf, 0, header, headerLength, readLength);
            headerLength += readLength;
        }
        String headerLenStr = new String(header, Charset.forName("EUC-KR"));
        return Integer.parseInt(headerLenStr);
    }

    private String getBodyContent(final int CONTENT_LENGTH) throws IOException {
        byte[] bodyContent = new byte[CONTENT_LENGTH];
        int contentReadLen = in.read(bodyContent);
        while (contentReadLen < CONTENT_LENGTH) {
            int gapLength = CONTENT_LENGTH - contentReadLen;
            byte[] buf = new byte[gapLength];
            
            int readLength = in.read(buf);
            System.arraycopy(buf, 0, bodyContent, contentReadLen, readLength);
            contentReadLen += readLength;
        }
        String content = new String(bodyContent, Charset.forName("EUC-KR"));
        return content;
    }
}
