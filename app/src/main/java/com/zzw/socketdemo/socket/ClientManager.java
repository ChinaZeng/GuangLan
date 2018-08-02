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
            clientThread.addListener(new SocketMessageListenerAdapter() {
                @Override
                public Packet onSendMsgAgo(SocketThread socketThread, boolean isSuccess, Packet packet) {
                    if (!isSuccess) {
                        //发送消息失败触发关闭  本来应该用心跳触发关闭的 。这里就不那么麻烦了
                        SocketThread s = serverThreads.remove(packet.key());
                        if (s != null) {
                            s.exit();
                        }
                    }
                    return packet;
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


    public void sendTextData(String key, String msg) {
        ClientThread clientThread = serverThreads.get(key);
        if (clientThread != null) {
            clientThread.sendTextMsg(msg);
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
