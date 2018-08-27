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
import android.view.View;
import android.widget.TextView;

import com.zzw.socketdemo.socket.utils.ByteUtil;
import com.zzw.socketdemo.socket.CMD;
import com.zzw.socketdemo.socket.EventBusTag;
import com.zzw.socketdemo.socket.utils.MyLog;
import com.zzw.socketdemo.socket.resolve.Packet;
import com.zzw.socketdemo.socket.listener.STATUS;
import com.zzw.socketdemo.socket.manager.ServerManager;
import com.zzw.socketdemo.socket.listener.StatusListener;
import com.zzw.socketdemo.utils.WifiAPManager;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.Arrays;


public class ServerActivity extends AppCompatActivity {

    WifiAPManager wifiAPManager;

    private final String hotName = "hehe";


    private TextView tv, tvContent;
    private HotBroadcastReceiver receiver;
    private ServerManager serverManager;
    private final int PORT = 8825;

    private String key;

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
                            ServerActivity.this.key = null;
                        } else if (status == STATUS.INIT) {
                            String s = tvContent.getText().toString();
                            String content = s + "\n" + key + "连接";
                            tvContent.setText(content);
                            ServerActivity.this.key = key;
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

    }

    @Subscriber(tag = EventBusTag.TAG_SEND_MSG)
    public void sendMsg(Packet packet) {
        if (packet.cmd == CMD.GET_DEVICE_SERIAL_NUMBER) {
            StringBuilder builder = new StringBuilder();
            builder.append("发送获取设备号命令成功\n");
            builder.append("起始值:" + Arrays.toString(ByteUtil.intToBytes(Packet.START_FRAME)) + "\n");
            builder.append("总帧长度:" + Arrays.toString(ByteUtil.intToBytes(packet.pkAllLen)) + "\n");
            builder.append("版本号:" + Arrays.toString(ByteUtil.intToBytes(packet.rev)) + "\n");
            builder.append("源地址:" + Arrays.toString(ByteUtil.intToBytes(packet.src)) + "\n");
            builder.append("目标地址:" + Arrays.toString(ByteUtil.intToBytes(packet.dst)) + "\n");
            builder.append("帧类型:" + Arrays.toString(ByteUtil.shortToBytes(packet.pkType)) + "\n");
            builder.append("流水号:" + Arrays.toString(ByteUtil.shortToBytes((short) packet.pktId)) + "\n");
            builder.append("保留字节:" + Arrays.toString(ByteUtil.intToBytes(packet.keep)) + "\n");
            builder.append("cmd:" + Arrays.toString(ByteUtil.intToBytes(packet.cmd)) + "\n");
            builder.append("数据长度:" + Arrays.toString(ByteUtil.intToBytes(packet.cmdDataLength)) + "\n");
            builder.append("数据:" + Arrays.toString(packet.data) + "\n");
            builder.append("结尾值:" + Arrays.toString(ByteUtil.intToBytes(Packet.END_FRAME)) + "\n");
            tvContent.setText(builder.toString());
        }
    }

    public void click1(View view) {
        if (key != null) {
            serverManager.getDeviceSerialNumber(key);
        }
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
