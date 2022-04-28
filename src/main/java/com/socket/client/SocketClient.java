package com.socket.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;

import javax.swing.SpringLayout.Constraints;

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

        logger.info("sending message : {}", sendMessage);
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
            logger.info("****************** echo from server **********************");
            int echoHeader = getHeader(CommonConstants.CONTENT_HEADER_LENGTH);

            logger.info("****************** echo header length ********************");
            logger.info("header length : {}", echoHeader);
            
            logger.info("********************** echo GID **************************");
            String echoGid = getGID(CommonConstants.GID_LENGTH);
            logger.info("echo gid : {}", echoGid);

            logger.info("**************** echo IF_SERVICE_CODE ********************");
            String echoIfSvcCode = getIfServiceCode(CommonConstants.IF_SERVICE_CODE_LENGTH);
            logger.info("Integration Service Id : {}", echoIfSvcCode);
            
            logger.info("******************* echo Sync code ***********************");
            String echoSyncCode = getSyncCode(CommonConstants.SYNC_CODE_LENGTH);
            logger.info("Sync Code : {}", echoSyncCode);

            
        } catch (IOException e) {

            logger.error("While recieveing data from server, error occured!!!", e);
            CommonUtil.socketStreamClose(client, in, out);
            logger.error("client finished!");
        }

        CommonUtil.socketStreamClose(client, in, out);
    }


    private int getHeader(final int HEADER_LENGTH) throws IOException {
        int byteRead = 0;
        byte[] buf = new byte[HEADER_LENGTH];

        while (byteRead < HEADER_LENGTH) {
            byteRead += in.read(buf, byteRead, HEADER_LENGTH - byteRead);
        }
        String headerStr = new String(buf, Charset.forName("EUC-KR"));
        return Integer.parseInt(headerStr);
    }
    
    private String getGID(final int GID_LENGTH) throws IOException {
        int byteRead = 0;
        byte[] buf = new byte[GID_LENGTH];
    
        while (byteRead < GID_LENGTH) {
            byteRead += in.read(buf, byteRead, GID_LENGTH - byteRead);
        }
        String gid = new String(buf, Charset.forName("EUC-KR"));
        return gid;
    }

    private String getIfServiceCode(final int IF_SERVICE_CODE_LENGTH) throws IOException {
        int byteRead = 0;
        byte[] buf = new byte[IF_SERVICE_CODE_LENGTH];
    
        while (byteRead < IF_SERVICE_CODE_LENGTH) {
            byteRead += in.read(buf, byteRead, IF_SERVICE_CODE_LENGTH - byteRead);
        }
        String ifSvcCode = new String(buf, Charset.forName("EUC-KR"));
        return ifSvcCode;
    }
    
    private String getSyncCode(final int SYNC_CODE_LEN) throws IOException {
        int byteRead = 0;
        byte[] buf = new byte[SYNC_CODE_LEN];
    
        while (byteRead < SYNC_CODE_LEN) {
            byteRead += in.read(buf, byteRead, SYNC_CODE_LEN - byteRead);
        }
        String syncCode = new String(buf, Charset.forName("EUC-KR"));
        return syncCode;
    }
    
    private String getResponseCode(final int REQ_AND_RESP_CODE_LENGTH) throws IOException {
        int byteRead = 0;
        byte[] buf = new byte[REQ_AND_RESP_CODE_LENGTH];
    
        while (byteRead < REQ_AND_RESP_CODE_LENGTH) {
            byteRead += in.read(buf, byteRead, REQ_AND_RESP_CODE_LENGTH - byteRead);
        }
        String respcode = new String(buf, Charset.forName("EUC-KR"));
        return respcode;
    }

    private String getContent(final int CONTENT_LENGTH) throws IOException {
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
