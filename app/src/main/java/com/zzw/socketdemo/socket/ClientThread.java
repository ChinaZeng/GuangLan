package com.zzw.socketdemo.socket;

import java.io.IOException;
import java.net.Socket;

public class ClientThread extends SocketThread {

    public ClientThread(String ip, int port, SocketThreadStatusListener socketThreadStatusListener) throws IOException {
        this(new Socket(ip, port), socketThreadStatusListener);
    }

    public ClientThread(Socket socket, SocketThreadStatusListener socketThreadStatusListener) {
        super("client-" + KeyUtils.getKey(socket), socket, socketThreadStatusListener);
    }
}
