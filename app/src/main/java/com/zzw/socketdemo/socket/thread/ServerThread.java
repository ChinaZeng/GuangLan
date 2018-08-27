package com.zzw.socketdemo.socket.thread;


import com.zzw.socketdemo.socket.resolve.Packet;
import com.zzw.socketdemo.socket.listener.SocketMessageListenerAdapter;
import com.zzw.socketdemo.socket.EventBusTag;
import com.zzw.socketdemo.socket.listener.SocketThreadStatusListener;
import com.zzw.socketdemo.socket.utils.KeyUtils;

import org.simple.eventbus.EventBus;

import java.net.Socket;

public class ServerThread extends SocketThread {

    public ServerThread(Socket socket, SocketThreadStatusListener socketThreadStatusListener) {
        super("server-" + KeyUtils.getKey(socket), socket, socketThreadStatusListener);
    }

    @Override
    protected void init() {
        super.init();
        addListener(new SocketMessageListenerAdapter() {
            @Override
            public Packet onReciveMsg(SocketThread socketThread, Packet packet) {
                //TODO 侵入式太高  这里为了省事
                EventBus.getDefault().post(packet, EventBusTag.TAG_RECIVE_MSG);
                return packet;
            }

            @Override
            public Packet onSendMsgAgo(SocketThread socketThread, boolean isSuccess, Packet packet) {
                if (isSuccess) {
                    EventBus.getDefault().post(packet, EventBusTag.TAG_SEND_MSG);
                }
                return packet;
            }
        });
    }
}
