package com.zzw.socketdemo.socket.listener;

import com.zzw.socketdemo.socket.thread.SocketThread;

public interface SocketThreadStatusListener {
    void onStatusChange(SocketThread socketThread, STATUS status);
}
