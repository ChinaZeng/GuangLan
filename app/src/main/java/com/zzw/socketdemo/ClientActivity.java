package com.zzw.socketdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.zzw.socketdemo.socket.CMD;
import com.zzw.socketdemo.socket.ClientManager;
import com.zzw.socketdemo.socket.EventBusTag;
import com.zzw.socketdemo.socket.Packet;
import com.zzw.socketdemo.socket.STATUS;
import com.zzw.socketdemo.socket.StatusListener;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

public class ClientActivity extends AppCompatActivity {

    private EditText etIp, etPort, etSendData;
    private TextView tvContent;
    private ClientManager manager;
    private String key;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        etIp = findViewById(R.id.ip);
        etPort = findViewById(R.id.port);
        tvContent = findViewById(R.id.content);
        etSendData = findViewById(R.id.et_content);

        EventBus.getDefault().register(this);
        manager = new ClientManager();
        manager.setListener(new StatusListener() {
            @Override
            public void statusChange(String key, STATUS status) {
                if (status == STATUS.END) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvContent.setText("与连接断开，请重新连接...");
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        manager.exit();
        super.onDestroy();
    }

    @Subscriber(tag = EventBusTag.TAG_RECIVE_MSG)
    public void reciverMsg(Packet packet) {
        if (TextUtils.equals(packet.key(), key)) {
            String s = tvContent.getText().toString();
            byte cmd = packet.cmd;
            byte flog = packet.flog;
            int dataLen = packet.data.length;
            String content = s + "\n" + "来自" + packet.ip() + "的消息：" + "cmd:" + cmd + "  flog:" + flog + " len=" + dataLen;
            if (cmd == CMD.CMD_TEXT_MSG) {
                content = content + "\n" + packet.string();
            } else if (cmd == CMD.CMD_FILE_MSG) {
                content = content + "\n" + "文件消息";
            }
            tvContent.setText(content);
        }
    }

    @Subscriber(tag = EventBusTag.TAG_RECIVE_MSG)
    public void reciverMsg(int len) {
        tvContent.setText("收到文件 大小：len=" + len);
    }


    @Subscriber(tag = EventBusTag.TAG_SEND_MSG)
    public void sendMsg(Packet packet) {
        if (TextUtils.equals(packet.key(), key)) {
            String s = tvContent.getText().toString();
            byte cmd = packet.cmd;
            byte flog = packet.flog;
            int dataLen = packet.data.length;
//            String content = s + "\n" + "来自" + packet.ip() + "的消息：" + packet.string();
            String content = s + "\n" + "发到到" + packet.ip() + "的消息：" + "cmd:" + cmd + "  flog:" + flog + " len=" + dataLen;
            tvContent.setText(content);
        }
    }


    public void sendData(View view) {
        if (TextUtils.isEmpty(key)) return;
        String s = etSendData.getText().toString().trim();
        if (s.length() > 0)
            manager.sendTextData(key, s);
    }

    public void connWifiHot(View view) {
        tvContent.setText("连接中...");
        final String ipStr = etIp.getText().toString();
        final String PORT = etPort.getText().toString();
        new Thread(new Runnable() {
            @Override
            public void run() {
                key = manager.conn(ipStr, Integer.parseInt(PORT));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!TextUtils.isEmpty(key)) {
                            tvContent.setText("连接成功");
                        } else {
                            tvContent.setText("连接失败");
                        }
                    }
                });
            }
        }).start();
    }


}
