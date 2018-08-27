package com.zzw.socketdemo.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public class SocketServiceHelper {
    private ServiceConnection serviceConnection;
    private SocketService updateService;
    private Context context;

    public SocketServiceHelper(Context context) {
        this.context = context;
    }

    public void bindService() {
        Intent intent = new Intent(this.context, SocketService.class);
        this.serviceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName name, IBinder service) {
                SocketServiceHelper.this.updateService = ((SocketService.ServerBinder) service).getService();
            }

            public void onServiceDisconnected(ComponentName name) {

            }
        };

        this.context.bindService(intent, this.serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void unBindService() {
        if (this.serviceConnection != null) {
            this.context.unbindService(this.serviceConnection);
        }
    }
}
