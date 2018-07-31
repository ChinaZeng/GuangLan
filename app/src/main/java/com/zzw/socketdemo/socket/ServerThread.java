package com.zzw.socketdemo.socket;


import java.net.Socket;

public class ServerThread extends SocketThread {

    public ServerThread(Socket socket, SocketThreadStatusListener socketThreadStatusListener) {
        super("server-" + KeyUtils.getKey(socket), socket, socketThreadStatusListener);
    }
}
