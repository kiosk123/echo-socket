package com.socket.server2;

import java.io.ByteArrayOutputStream;
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

public class SocketDataHandler2 implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(SocketDataHandler2.class);
    private Socket socket;
    private InputStream in;
    private OutputStream out;

    public SocketDataHandler2(Socket socket) throws ApplicationException {

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
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int totalReadLen = 0;
            while (true) {
                byte[] buf = new byte[1024];
                int readLen = in.read(buf);
                if (readLen < 0) {
                    break;
                }

                baos.write(buf, 0, readLen);

                totalReadLen += readLen;
                if (totalReadLen == CommonConstants.FULL_TEXT_LENGTH) {
                    break;
                }
            }
            //TODO totalReadLen이 CommonConstants.FULL_TEXT_LENGTH같지 않을때 처리 고민 일단 간단한 에코만 2022-04-28
            byte[] echoBytes = baos.toByteArray();
            setResponseCode(echoBytes);
            logger.info("************************ echo data ************************");
            logger.info(new String(echoBytes, Charset.forName("EUC-KR")));

            out.write(echoBytes);
            out.flush();
                
        } catch (IOException e) {
            logger.error("while handling data, IOException occured !!", e);
        } finally {
            CommonUtil.socketStreamClose(socket, in, out);
            logger.info("socket stream closed!!");
        }
        
    }

    private void setResponseCode(byte[] buf) {
        logger.info("call setResponseCode method");
        final int RESP_COD_POS = CommonConstants.CONTENT_HEADER_LENGTH
                            + CommonConstants.GID_LENGTH
                            + CommonConstants.IF_SERVICE_CODE_LENGTH
                            + CommonConstants.SYNC_CODE_LENGTH;
        
        buf[RESP_COD_POS] = (byte)CommonConstants.RESPONSE_CODE.charAt(0);
    }
}
