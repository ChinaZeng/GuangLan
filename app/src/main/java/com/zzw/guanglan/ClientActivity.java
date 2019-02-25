package com.zzw.guanglan;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.zzw.guanglan.socket.utils.ByteUtil;
import com.zzw.guanglan.socket.CMD;
import com.zzw.guanglan.socket.manager.ClientManager;
import com.zzw.guanglan.socket.EventBusTag;
import com.zzw.guanglan.socket.resolve.Packet;
import com.zzw.guanglan.socket.listener.STATUS;
import com.zzw.guanglan.socket.listener.StatusListener;
import com.zzw.guanglan.socket.utils.MyLog;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.io.File;
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
                            flog = false;
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
        flog = false;
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
                sendSorInfo();
            } else if (packet.cmd == CMD.GET_SOR_FILE) {
                builder.append("接收到APP向设备请求传输sor文件命令\n");
                if (packet.data.length >= (32 + 16)) {
                    byte[] fileNameB = ByteUtil.subBytes(packet.data, 0, 32);
                    byte[] fileLocB = ByteUtil.subBytes(packet.data, 32, 16);
                    String fileName = ByteUtil.bytes2Str(fileNameB);
                    String fileDir = ByteUtil.bytes2Str(fileLocB);
                    MyLog.e("接收到APP向设备请求传输sor文件命令 fileName = " + fileName + "  fileLoc = " + fileDir);
                    String name = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "dir" + File.separator + "test.sor";
                    sendSorFile(name);
                }
            } else if (packet.cmd == CMD.HEART_SEND) {
                builder.append("接收到心跳包命令\n");
            } else if (packet.cmd == CMD.HEART_RE) {
                builder.append("接收到回复心跳包命令\n");
            } else if (packet.cmd == CMD._RE) {
                builder.append("接收到错误代码命令\n");
            } else if (packet.cmd == CMD.SEND_TEST_ARGS_AND_STOP_TEST) {
                builder.append("接收到APP向设备发送停止OTDR测试命令\n");
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
            Log.e("zzz",builder.toString());

            tvContent.setText(builder.toString());
        }
    }


    @Subscriber(tag = EventBusTag.TAG_SEND_MSG)
    public void sendMsg(Packet packet) {
        if (TextUtils.equals(packet.key(), key)) {
            StringBuilder builder = new StringBuilder();
            if (packet.cmd == CMD.GET_DEVICE_SERIAL_NUMBER) {
                builder.append("发送到APP询问设备序列号设备命令\n");
            } else if (packet.cmd == CMD.SEND_TEST_ARGS_AND_START_TEST) {
                builder.append("发送到APP给设备下发OTDR测试参数并启动测试命令\n");
                sendSorInfo();
            } else if (packet.cmd == CMD.GET_SOR_FILE) {
                builder.append("发送到APP向设备请求传输sor文件命令\n");
                if (packet.data.length >= (32 + 16)) {
                    byte[] fileNameB = ByteUtil.subBytes(packet.data, 0, 32);
                    byte[] fileLocB = ByteUtil.subBytes(packet.data, 32, 16);
                    String fileName = ByteUtil.bytes2Str(fileNameB);
                    String fileDir = ByteUtil.bytes2Str(fileLocB);
                    MyLog.e("发送到APP向设备请求传输sor文件命令 fileName = " + fileName + "  fileLoc = " + fileDir);
                    String name = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "dir" + File.separator + "test.sor";
                    sendSorFile(name);
                }
            } else if (packet.cmd == CMD.HEART_SEND) {
                builder.append("发送到心跳包命令\n");
            } else if (packet.cmd == CMD.HEART_RE) {
                builder.append("发送到回复心跳包命令\n");
            } else if (packet.cmd == CMD._RE) {
                builder.append("发送到错误代码命令\n");
            } else if (packet.cmd == CMD.SEND_TEST_ARGS_AND_STOP_TEST) {
                builder.append("发送到APP向设备发送停止OTDR测试命令\n");
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
            Log.e("zzz",builder.toString());
            tvContent.setText(builder.toString());
        }
    }

    public void sendData(View view) {

    }

    void sendSorFile(String path) {
        if (key != null) {
            manager.sendFile(key, path);
        }
    }

    void sendSorInfo() {
        if (key != null) {
            String name = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "dir" + File.separator + "test.sor";
            manager.sendSorInfo(key, "test.sor", Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "dir", (int) new File(name).length());
        }
    }

    void sendHeart() {
        if (key != null) {
            manager.sendHeart(key);
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
                            flog = true;
                            new HeartThrad().start();
                        } else {
                            tvContent.setText("连接失败");
                        }
                    }
                });


            }
        }).start();
    }

    private boolean flog = false;

    class HeartThrad extends Thread {
        @Override
        public void run() {
            super.run();
            while (flog) {
                try {
                    Thread.sleep(1000);
                    sendHeart();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }


}
