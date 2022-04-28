package com.socket.client;

import com.common.CommonConstants;
import com.google.common.base.Strings;
import com.socket.exception.ApplicationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketClientApp {

    private final static Logger logger = LoggerFactory.getLogger(SocketClientApp.class);
    public static void main(String[] args) {
        try {

            SocketClient socketClient = null;
            if (args.length == 2) {
                socketClient = new SocketClient(args[0], Integer.parseInt(args[1]));
            } else if (args.length == 3) {
                socketClient = new SocketClient(args[0], Integer.parseInt(args[1]), createSendMessage(args[2]));
            } else {
                socketClient = new SocketClient("localhost", 7777);
            }
            
            new Thread(socketClient).start();
        } catch (ApplicationException e) {
            logger.error("application exception occured !!", e);
        } catch (NumberFormatException e) {
            logger.error("port value is not number : {}", args[1]);
        }
    }

    private static String createSendMessage(String msg) {
        String ret = "";
        if (msg.length() > CommonConstants.CONTENT_LENGTH) {
            ret = msg.substring(0, CommonConstants.CONTENT_LENGTH);
        } else {
            ret = Strings.padStart(msg, 10, ' ');
        }
        
        ret = String.format("%08d", CommonConstants.BODY_LENGTH) 
                        + CommonConstants.DEFAULT_UUID
                        + CommonConstants.IF_SERVICE_CODE
                        + CommonConstants.SYNC_CODE
                        + CommonConstants.REQUEST_CODE
                        + ret;
        return ret;
    }
}
