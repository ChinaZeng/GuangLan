package com.zzw.guanglan.socket.listener;

import com.zzw.guanglan.socket.thread.SocketThread;

public interface SocketThreadStatusListener {
    void onStatusChange(SocketThread socketThread, STATUS status);
}
