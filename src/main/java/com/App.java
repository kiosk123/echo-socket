package com;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Strings;

public class App {
    public static void main(String[] args) {
        String param = "00000123aaaaaaaaaaaaa";
        String lenStr = param.substring(0, 8);
        int len = Integer.parseInt(lenStr);

        String content = param.substring(8);
        if (len > content.length()) {
            content = Strings.padStart(content, len, ' ');
        } else {
            content = content.substring(0, len);
        }
        String validMessage = lenStr + content;
        System.out.println(validMessage);
    }
}
