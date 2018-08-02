package com.zzw.socketdemo.socket;

public interface SocketMessageListener {

    Packet onReciveMsg(SocketThread socketThread, Packet packet);


    Packet onSendMsgBefore(SocketThread socketThread, Packet packet);

    Packet onSendMsgAgo(SocketThread socketThread, boolean isSuccess, Packet packet);

    SocketMessageListener DEF = new SocketMessageListener() {

        @Override
        public Packet onReciveMsg(SocketThread socketThread, Packet packet) {
            MyLog.e("from" + socketThread.socket.getInetAddress() + ":" + socketThread.socket.getPort()
                    + ":\n", "id="+packet.getId()+" cmd :"+packet.cmd+ " flog="+packet.flog);
            return packet;
        }


        @Override
        public Packet onSendMsgBefore(SocketThread socketThread, Packet packet) {
            MyLog.e("to" + socketThread.socket.getInetAddress() + ":" + socketThread.socket.getPort()
                    + ":\n", "id="+packet.getId()+" cmd :"+packet.cmd+ " flog="+packet.flog);
            return packet;
        }

        @Override
        public Packet onSendMsgAgo(SocketThread socketThread, boolean isSuccess, Packet packet) {
            MyLog.e("发送消息"+"id="+packet.getId() + (isSuccess ? "成功" : "失败"));
            return packet;
        }
    };

}
