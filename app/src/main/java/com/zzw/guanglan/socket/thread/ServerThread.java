package com.zzw.guanglan.socket.thread;


import com.zzw.guanglan.socket.resolve.Packet;
import com.zzw.guanglan.socket.listener.SocketMessageListenerAdapter;
import com.zzw.guanglan.socket.EventBusTag;
import com.zzw.guanglan.socket.listener.SocketThreadStatusListener;
import com.zzw.guanglan.socket.utils.KeyUtils;

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
