package com.zzw.socketdemo.socket.manager;

import com.zzw.socketdemo.socket.thread.ListenerThread;
import com.zzw.socketdemo.socket.listener.STATUS;
import com.zzw.socketdemo.socket.listener.SocketThreadStatusListener;
import com.zzw.socketdemo.socket.listener.StatusListener;
import com.zzw.socketdemo.socket.thread.SocketThread;
import com.zzw.socketdemo.socket.utils.KeyUtils;

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

    public void getDeviceSerialNumber(String key) {
        SocketThread thread = listenerThread.getServerThreads().get(key);
        if (thread != null) {
            thread.socketSender.getDeviceSerialNumber();
        }
    }

    public void sendTestArgsAndStartTestPacket(String key) {
        SocketThread thread = listenerThread.getServerThreads().get(key);
        if (thread != null) {
            thread.socketSender.sendTestArgsAndStartTest();
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
