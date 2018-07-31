package com.zzw.socketdemo.socket;

interface SocketThreadStatusListener {
    void onStatusChange(SocketThread socketThread, STATUS status);
}
