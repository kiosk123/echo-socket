package com.socket.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;

import com.common.CommonConstants;
import com.common.CommonUtil;
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
            try {
                int contentLength = getHeader(CommonConstants.CONTENT_HEADER_LENGTH);
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
                byte[] headerBytes = String.format("%0" + headerLength + "d", contentBytes.length).getBytes(Charset.forName("EUC-KR"));
                
                int sendLength = headerBytes.length + contentBytes.length;
                byte[] sendBytes = new byte[sendLength];
    
                System.arraycopy(headerBytes, 0, sendBytes, 0, headerBytes.length);
                System.arraycopy(contentBytes, 0, sendBytes, headerBytes.length, contentBytes.length);
    
                out.write(sendBytes);
                out.flush();

            } catch (IOException e) {
                logger.error("while handling data, IOException occured !!", e);
                CommonUtil.socketStreamClose(socket, in, out);
                logger.error("socket stream closed!!");
                break;
            }
        }
    }

    private int getHeader(final int HEADER_LENGTH) throws IOException {
        byte[] header = new byte[HEADER_LENGTH];
        int bytesRead = 0;

        while (bytesRead < HEADER_LENGTH) {
            bytesRead += in.read(header, bytesRead, HEADER_LENGTH + bytesRead);
        }
        
        String headerLenStr = new String(header, Charset.forName("EUC-KR"));
        return Integer.parseInt(headerLenStr);
    }

    private String getBodyContent(final int CONTENT_LENGTH) throws IOException {
        byte[] bodyContent = new byte[CONTENT_LENGTH];
        int bytesRead = 0;
        
        while (bytesRead < CONTENT_LENGTH) {
            bytesRead += in.read(bodyContent, bytesRead, CONTENT_LENGTH + bytesRead);
        }

        String content = new String(bodyContent, Charset.forName("EUC-KR"));
        return content;
    }
}
