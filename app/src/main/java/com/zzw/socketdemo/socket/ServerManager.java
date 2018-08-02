package com.zzw.socketdemo.socket;

import java.util.Set;

public class ServerManager implements SocketThreadStatusListener {
    private ListenerThread listenerThread;
    private StatusListener listener;

    public void setListener(StatusListener listener) {
        this.listener = listener;
    }

    public ServerManager(int port) {
        listenerThread = new ListenerThread(port);
        listenerThread.setSocketThreadStatusListener(this);
    }

    public void startServer() {
        listenerThread.start();
    }


    public void sendTextData(String msg) {
        Set<String> keys = listenerThread.getServerThreads().keySet();
        for (String key : keys) {
            sendTextData(key, msg);
        }
    }

    public void sendTextData(String key, String msg) {
        SocketThread thread = listenerThread.getServerThreads().get(key);
        if (thread != null) {
            thread.sendTextMsg(msg);
        }
    }

    public void close() {
        listenerThread.exit();
    }

    @Override
    public void onStatusChange(SocketThread socketThread, STATUS status) {
        if (listener != null) {
            listener.statusChange(KeyUtils.getKey(socketThread.socket), status);
        }
    }
}
