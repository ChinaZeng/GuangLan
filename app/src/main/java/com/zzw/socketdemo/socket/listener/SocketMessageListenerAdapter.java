package com.zzw.socketdemo.socket.listener;

import com.zzw.socketdemo.socket.resolve.Packet;
import com.zzw.socketdemo.socket.thread.SocketThread;

public class SocketMessageListenerAdapter implements SocketMessageListener {
    @Override
    public Packet onReciveMsg(SocketThread socketThread, Packet packet) {
        return packet;
    }

    @Override
    public Packet onSendMsgBefore(SocketThread socketThread, Packet packet) {
        return packet;
    }

    @Override
    public Packet onSendMsgAgo(SocketThread socketThread, boolean isSuccess, Packet packet) {
        return packet;
    }
}
