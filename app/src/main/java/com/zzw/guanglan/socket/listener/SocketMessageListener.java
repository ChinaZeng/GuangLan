package com.zzw.guanglan.socket.listener;

import com.zzw.guanglan.socket.utils.MyLog;
import com.zzw.guanglan.socket.resolve.Packet;
import com.zzw.guanglan.socket.thread.SocketThread;

public interface SocketMessageListener {

    Packet onReciveMsg(SocketThread socketThread, Packet packet);

    Packet onSendMsgBefore(SocketThread socketThread, Packet packet);

    Packet onSendMsgAgo(SocketThread socketThread, boolean isSuccess, Packet packet);

    SocketMessageListener DEF = new SocketMessageListener() {

        @Override
        public Packet onReciveMsg(SocketThread socketThread, Packet packet) {
            MyLog.e("from" + socketThread.socket.getInetAddress() + ":" + socketThread.socket.getPort()
                    + ":\n", "id=" + packet.pktId + " cmd :" + packet.cmd);
            return packet;
        }


        @Override
        public Packet onSendMsgBefore(SocketThread socketThread, Packet packet) {
            return packet;
        }

        @Override
        public Packet onSendMsgAgo(SocketThread socketThread, boolean isSuccess, Packet packet) {
            MyLog.e("to ago " + "id=" + packet.pktId + " cmd: " + packet.cmd + (isSuccess ? "成功" : "失败"));
            return packet;
        }
    };

}
