package com.zzw.socketdemo.socket.manager;

import com.zzw.socketdemo.socket.event.GetSorFileBean;
import com.zzw.socketdemo.socket.event.ReBean;
import com.zzw.socketdemo.socket.event.TestArgsAndStartBean;
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

    public void sendTestArgsAndStartTestPacket(String key, TestArgsAndStartBean bean) {
        SocketThread thread = listenerThread.getServerThreads().get(key);
        if (thread != null) {
            thread.socketSender.sendTestArgsAndStartTest(bean.rang, bean.wl, bean.pw, bean.time, bean.mode, bean.gi);
        }
    }

    public void sendTestArgsAndStopTestPacket(String key) {
        SocketThread thread = listenerThread.getServerThreads().get(key);
        if (thread != null) {
            thread.socketSender.sendTestArgsAndStopTest();
        }
    }


    public void getSorFile(String key, GetSorFileBean bean) {
        SocketThread thread = listenerThread.getServerThreads().get(key);
        if (thread != null) {
            thread.socketSender.getSorFile(bean.fileName, bean.fileDir);
        }
    }

    public void sendRe(String key, ReBean bean) {
        SocketThread thread = listenerThread.getServerThreads().get(key);
        if (thread != null) {
            thread.socketSender.sendRe(bean.errorCode, bean.cmdCode);
        }
    }

    public void sendHeart(String key) {
        SocketThread thread = listenerThread.getServerThreads().get(key);
        if (thread != null) {
            thread.socketSender.sendHeart();
        }
    }

    public void reHeart(String key) {
        SocketThread thread = listenerThread.getServerThreads().get(key);
        if (thread != null) {
            thread.socketSender.reHeart();
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
