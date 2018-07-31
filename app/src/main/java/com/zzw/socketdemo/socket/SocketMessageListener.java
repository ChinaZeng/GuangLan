package com.zzw.socketdemo.socket;

public interface SocketMessageListener {

    void onReciveMsg(SocketThread socketThread, Packet packet);


    void onSendMsgBefore(SocketThread socketThread, Packet packet);

    void onSendMsgAgo(SocketThread socketThread, boolean isSuccess, Packet packet);

    SocketMessageListener DEF = new SocketMessageListener() {

        @Override
        public void onReciveMsg(SocketThread socketThread, Packet packet) {
            MyLog.e("来自" + socketThread.socket.getInetAddress() + ":" + socketThread.socket.getPort()
                    + ":\n", packet.hexString());
        }


        @Override
        public void onSendMsgBefore(SocketThread socketThread, Packet packet) {
            MyLog.e("即将发送消息到:" + socketThread.socket.getInetAddress() + ":" + socketThread.socket.getPort()
                    + ":\n", packet.hexString());
        }

        @Override
        public void onSendMsgAgo(SocketThread socketThread, boolean isSuccess, Packet packet) {
            MyLog.e("发送消息" + (isSuccess ? "成功" : "失败"));
        }
    };

}
