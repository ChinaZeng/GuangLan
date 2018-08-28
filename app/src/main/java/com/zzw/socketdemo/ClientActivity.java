package com.zzw.socketdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.zzw.socketdemo.socket.utils.ByteUtil;
import com.zzw.socketdemo.socket.CMD;
import com.zzw.socketdemo.socket.manager.ClientManager;
import com.zzw.socketdemo.socket.EventBusTag;
import com.zzw.socketdemo.socket.resolve.Packet;
import com.zzw.socketdemo.socket.listener.STATUS;
import com.zzw.socketdemo.socket.listener.StatusListener;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.Arrays;

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
            StringBuilder builder = new StringBuilder();
            if (packet.cmd == CMD.GET_DEVICE_SERIAL_NUMBER) {
                builder.append("接收到APP询问设备序列号设备命令\n");
            } else if (packet.cmd == CMD.SEND_TEST_ARGS_AND_START_TEST) {
                builder.append("接收到APP给设备下发OTDR测试参数并启动测试命令\n");
            } else if (packet.cmd == CMD.GET_SOR_FILE) {
                builder.append("接收到APP向设备请求传输sor文件命令\n");
            } else if (packet.cmd == CMD.HEART_SEND) {
                builder.append("接收到心跳包命令\n");
            } else if (packet.cmd == CMD.HEART_RE) {
                builder.append("接收到回复心跳包命令\n");
            } else if (packet.cmd == CMD._RE) {
                builder.append("接收到错误代码命令\n");
            }
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


    @Subscriber(tag = EventBusTag.TAG_SEND_MSG)
    public void sendMsg(Packet packet) {
        if (TextUtils.equals(packet.key(), key)) {

        }
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
