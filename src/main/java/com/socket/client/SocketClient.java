package com.socket.client;

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

public class SocketClient implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(SocketClient.class);
    private final static String MESSAGE_CONSTANT;

    static {
        MESSAGE_CONSTANT = String.format("%08d", CommonConstants.BODY_LENGTH) 
                        + CommonConstants.DEFAULT_UUID
                        + CommonConstants.IF_SERVICE_CODE
                        + CommonConstants.SYNC_CODE
                        + CommonConstants.REQUEST_CODE
                        + CommonConstants.DEFAULT_CONTENT;
    }

    private Socket client;
    private InputStream in;
    private OutputStream out;
    private String sendMessageParam;

    public SocketClient(String host, int port) throws ApplicationException {

        try {
            client = new Socket(host, port);
            logger.info("host: {} port: {} connection success.", host, port);

            in = client.getInputStream();
            out = client.getOutputStream();

        } catch (IOException e) {
            logger.info("connecting server failed !!", e);
            throw new ApplicationException(e);
        }
    }

    public SocketClient(String host, int port, String sendMessageParam) throws ApplicationException {
        this(host, port);
        this.sendMessageParam = sendMessageParam;
    }

    @Override
    public void run() {

        String sendMessage = sendMessageParam;
        if (!CommonUtil.hasText(sendMessage)) {
            sendMessage = MESSAGE_CONSTANT;
        }

        logger.info("sending message : " + sendMessage);
        byte[] sendBytes = sendMessage.getBytes(Charset.forName("EUC-KR"));

        try {
            out.write(sendBytes);
            out.flush();
        } catch (IOException e) {
            logger.error("While sending data to server, error occured!!!", e);
            CommonUtil.socketStreamClose(client, in, out);
            logger.error("client finished!");
        }

        try {
            byte[] echoHeader = new byte[CommonConstants.CONTENT_HEADER_LENGTH];
            int echoHeaderLength = getHeader(echoHeader);
            logger.info("****************** echo from server **********************");
            
            logger.info("****************** echo header length ********************");
            logger.info("header length : {}", echoHeaderLength);
            
            logger.info("****************** echo body content *********************");
        } catch (IOException e) {

            logger.error("While recieveing data from server, error occured!!!", e);
            CommonUtil.socketStreamClose(client, in, out);
            logger.error("client finished!");
        }

        CommonUtil.socketStreamClose(client, in, out);
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

    private String getGID() {
        return null;
    }

    private String getSyncCode() {
        return null;
    }

    private String getResponseCode() {
        return null;
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
