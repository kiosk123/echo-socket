package com.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class CommonUtility {
    public static void socketStreamClose(Socket socket, InputStream in, OutputStream out) {
        if (in != null) {try { in.close(); } catch (IOException e2) {}}
        if (out != null) {try { out.close(); } catch (IOException e2) {}}
        if (socket != null) {try { socket.close(); } catch (IOException e2) {}}
    }
}
