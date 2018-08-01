package com.zzw.socketdemo.socket;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

public class ClientManager implements SocketThreadStatusListener {
    private HashMap<String, ClientThread> serverThreads = new HashMap<>();


    private StatusListener listener;


    public ClientManager() {
    }


    public String conn(String ip, int port) {
        String key = KeyUtils.getKey(ip);
        if (serverThreads.containsKey(key)) {
            SocketThread thread = serverThreads.get(key);
            thread.exit();
        }

        try {
            ClientThread clientThread = new ClientThread(ip, port, this);
            clientThread.addListener(new SocketMessageListener() {
                @Override
                public void onReciveMsg(SocketThread socketThread, Packet packet) {

                }

                @Override
                public void onSendMsgBefore(SocketThread socketThread, Packet packet) {

                }

                @Override
                public void onSendMsgAgo(SocketThread socketThread, boolean isSuccess, Packet packet) {
                    if (!isSuccess) {
                        //发送消息失败触发关闭
                        SocketThread s = serverThreads.remove(packet.key());
                        if (s != null) {
                            s.exit();
                        }
                    }
                }
            });
            serverThreads.put(key, clientThread);
            clientThread.start();
            return key;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void sendData(String key, byte[] msg) {
        ClientThread clientThread = serverThreads.get(key);
        if (clientThread != null) {
            clientThread.sendData(msg);
        }
    }

    public void sendData(String key, String msg) {
        ClientThread clientThread = serverThreads.get(key);
        if (clientThread != null) {
            clientThread.sendData(msg);
        }
    }

    public void exit() {
        Collection<ClientThread> socketThreads = serverThreads.values();
        for (SocketThread socketThread : socketThreads) {
            socketThread.exit();
        }
        serverThreads.clear();
    }

    @Override
    public void onStatusChange(SocketThread socketThread, STATUS status) {
        if (status == STATUS.END) {
            serverThreads.remove(KeyUtils.getKey(socketThread.socket));
        }

        if (listener != null) {
            listener.statusChange(KeyUtils.getKey(socketThread.socket), status);
        }
    }

    public void setListener(StatusListener listener) {
        this.listener = listener;
    }

}
