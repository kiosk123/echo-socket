package com.socket.client;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Strings;
import com.socket.exception.ApplicationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketClientApp {

    private final static Logger logger = LoggerFactory.getLogger(SocketClientApp.class);
    public static void main(String[] args) {
        try {

            SocketClient socketClient = null;
            if (args.length > 0 && isValidMessageParam(args[0])) {
                socketClient = new SocketClient("localhost", 7777, makeValidMessage(args[0]));
            } else {
                socketClient = new SocketClient("localhost", 7777);
            }
            
            new Thread(socketClient).start();
        } catch (ApplicationException e) {
            logger.error("application exception occured !!", e);
        }
    }

    private static boolean isValidMessageParam(String param) {
        Pattern p = Pattern.compile("[0-9]{8}.+");
        Matcher m = p.matcher(param);
        return m.matches();
    }

    private static String makeValidMessage(String param) {
        String lenStr = param.substring(0, 8);
        int len = Integer.parseInt(lenStr);

        String content = param.substring(8);
        if (len > content.length()) {
            content = Strings.padStart(content, len, ' ');
        } else {
            content = content.substring(0, len);
        }
        String validMessage = lenStr + content;
        return validMessage;
    }
}
