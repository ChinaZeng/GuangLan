package com.zzw.socketdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.zzw.socketdemo.socket.ClientManager;
import com.zzw.socketdemo.socket.ClientThread;
import com.zzw.socketdemo.socket.EventBusTag;
import com.zzw.socketdemo.socket.MyLog;
import com.zzw.socketdemo.socket.Packet;
import com.zzw.socketdemo.socket.STATUS;
import com.zzw.socketdemo.socket.ServerManager;
import com.zzw.socketdemo.socket.StatusListener;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.io.IOException;


public class ServerActivity extends AppCompatActivity {

    WifiAPManager wifiAPManager;

    private final String hotName = "hehe";


    private TextView tv, tvContent;
    private EditText etContent;
    private HotBroadcastReceiver receiver;
    private ServerManager serverManager;
    private final int PORT = 8825;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 判断是否有WRITE_SETTINGS权限if(!Settings.System.canWrite(this))
            if (!Settings.System.canWrite(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1);
            }
        }

        EventBus.getDefault().register(this);

        tv = findViewById(R.id.tv);
        etContent = findViewById(R.id.et_content);
        tvContent = findViewById(R.id.content);

        wifiAPManager = new WifiAPManager(this);
        IntentFilter mIntentFilter = new IntentFilter("android.net.wifi.WIFI_AP_STATE_CHANGED");
        receiver = new HotBroadcastReceiver();
        registerReceiver(receiver, mIntentFilter);
        serverManager = new ServerManager(PORT);
        serverManager.setListener(new StatusListener() {
            @Override
            public void statusChange(final String key, final STATUS status) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (status == STATUS.END) {
                            String s = tvContent.getText().toString();
                            String content = s + "\n" + key + "断开连接";
                            tvContent.setText(content);
                        } else if (status == STATUS.INIT) {
                            String s = tvContent.getText().toString();
                            String content = s + "\n" + key + "连接";
                            tvContent.setText(content);
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        EventBus.getDefault().unregister(this);
        serverManager.close();
        wifiAPManager.closeWifiAp();
        super.onDestroy();
    }

    @Subscriber(tag = EventBusTag.TAG_RECIVE_MSG)
    public void reciverMsg(Packet packet) {
        String s = tvContent.getText().toString();
        String content = s + "\n" + "来自" + packet.ip() + "的消息：" + packet.string();
        tvContent.setText(content);
    }

    @Subscriber(tag = EventBusTag.TAG_SEND_MSG)
    public void sendMsg(Packet packet) {
        String s = tvContent.getText().toString();
        String content = s + "\n" + "发送到" + packet.ip() + "的消息：" + packet.string();
        tvContent.setText(content);
    }

    public void sendData(View view) {
        String s = etContent.getText().toString().trim();
        if (s.length() > 0)
            serverManager.sendData(s);
    }


    public void startWifiHot(View view) {
        wifiAPManager.startWifiAp(hotName, "1234567890", true);
    }


    private class HotBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.net.wifi.WIFI_AP_STATE_CHANGED".equals(action)) {
                //state状态为：10---正在关闭；11---已关闭；12---正在开启；13---已开启
                int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                MyLog.e("state =" + state);
                switch (state) {
                    case 10:
                        tv.setText("热点正在关闭");
                        break;
                    case 11:
                        tv.setText("热点已关闭");
                        break;

                    case 12:
                        tv.setText("热点正在开启");
                        break;
                    case 13:
                        tv.setText("热点正在开启");
                        //设置个延迟 不然会拿不到
                        tv.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                String serverIp = wifiAPManager.getLocalIpAddress();
                                tv.setText("热点已开启 ip=" + serverIp + ":" + PORT);
                                serverManager.startServer();
                            }
                        }, 2000);

                        break;
                }
            }
        }
    }


}
