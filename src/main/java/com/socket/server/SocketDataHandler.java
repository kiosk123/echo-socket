package com.socket.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;

import com.common.CommonConstants;
import com.common.CommonUtility;
import com.socket.exception.ApplicationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketDataHandler implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(SocketDataHandler.class);
    private Socket socket;
    private InputStream in;
    private OutputStream out;

    public SocketDataHandler(Socket socket) throws ApplicationException {

        this.socket = socket;
        try {
            in = socket.getInputStream();
            out = socket.getOutputStream();
        } catch (IOException e) {
            logger.error("broken input or output stream", e);
            throw new ApplicationException(e);
        }
    }

    @Override
    public void run() {
        while (true) {
            byte[] header = new byte[CommonConstants.CONTENT_HEADER_LENGTH];

            try {
                int contentLength = getHeader(header);
                logger.info("************************ header length ************************");
                logger.info("reading content length is {}", contentLength);

                logger.info("************************ body conetent ************************");
                String bodyContent = getBodyContent(contentLength);
                logger.info(bodyContent);

                /**
                 * send echo data to client
                 */
                byte[] contentBytes = bodyContent.getBytes(Charset.forName("EUC-KR"));
            
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
                    logger.error("While sending data from server to client, error occured!!!", e);
                    CommonUtility.socketStreamClose(socket, in, out);
                    logger.error("client finished!");
                    break;
                }

            } catch (IOException e) {
                logger.error("while proccessing data, IOException occured !!", e);
                CommonUtility.socketStreamClose(socket, in, out);
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
