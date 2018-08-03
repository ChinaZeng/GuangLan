package com.zzw.socketdemo.socket;

import org.simple.eventbus.EventBus;

import java.io.IOException;
import java.net.Socket;

public class ClientThread extends SocketThread {

    public ClientThread(String ip, int port, SocketThreadStatusListener socketThreadStatusListener) throws IOException {
        this(new Socket(ip, port), socketThreadStatusListener);
    }

    public ClientThread(Socket socket, SocketThreadStatusListener socketThreadStatusListener) {
        super("client-" + KeyUtils.getKey(socket), socket, socketThreadStatusListener);
    }

    int len = 0;

    @Override
    protected void init() {
        super.init();

        addListener(new SocketMessageListenerAdapter() {
            @Override
            public Packet onReciveMsg(SocketThread socketThread, Packet packet) {
                byte cmd = packet.cmd;
                if (cmd == CMD.CMD_FILE_MSG) {
                    byte flog = packet.flog;
                    if (flog == CMD.FLOG.FLOG_FILE_START) {
                        len = 0;
                        FileHelper.saveFileToLocal(packet.data, true, "src.mp4");
                    } else if (flog == CMD.FLOG.FLOG_FILE_DATA) {
                        FileHelper.saveFileToLocal(packet.data, false, "src.mp4");
                        len += packet.data.length;
                    }else {
                        //TODO 侵入式太高  这里为了省事
                        EventBus.getDefault().post(len, EventBusTag.TAG_RECIVE_MSG);
                    }
                }else {

                    //TODO 侵入式太高  这里为了省事
                    EventBus.getDefault().post(packet, EventBusTag.TAG_RECIVE_MSG);
                }


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
