package com.zzw.socketdemo.socket;

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
