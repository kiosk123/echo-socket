package com.socket.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;

import com.common.CommonConstants;
import com.socket.exception.ApplicationException;

public class SocketDataHandler implements Runnable {

    private Socket socket;
    private InputStream in;
    private OutputStream out;

    public SocketDataHandler(Socket socket) throws ApplicationException {
        this.socket = socket;
        try {
            in = socket.getInputStream();
            out = socket.getOutputStream();
        } catch (IOException e) {
            System.out.println("broken input or output stream");
            throw new ApplicationException(e);
        }
    }

    @Override
    public void run() {
        while (true) {
            byte[] header = new byte[CommonConstants.CONTENT_HEADER_LENGTH];

            try {
                int CONTENT_LENGTH = getHeader(header);
                System.out.println("************************ header length ************************");
                System.out.println("reading content length is " + CONTENT_LENGTH);

                System.out.println("************************ body conetent ************************");
                String bodyContent = getBodyContent(CONTENT_LENGTH);
                System.out.println(bodyContent);



            } catch (IOException e) {
                System.out.println("while proccessing data, IOException occured !!");
                e.printStackTrace();
                
                if (in != null) {try { in.close(); } catch (IOException e2) {}}
                if (out != null) {try { out.close(); } catch (IOException e2) {}}
                if (socket != null) {try { socket.close(); } catch (IOException e2) {}}
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