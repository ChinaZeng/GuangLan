package com.zzw.socketdemo.socket.utils;

import java.net.Socket;

public class KeyUtils {

    public static String getKey(String ip) {
        return "/"+ip ;
    }


    public static String getKey(Socket socket) {
        return socket.getInetAddress().toString() ;
    }

}
