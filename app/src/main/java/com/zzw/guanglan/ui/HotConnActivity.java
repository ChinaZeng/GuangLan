package com.zzw.guanglan.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zzw.guanglan.Contacts;
import com.zzw.guanglan.R;
import com.zzw.guanglan.base.BaseActivity;
import com.zzw.guanglan.http.Api;
import com.zzw.guanglan.http.retrofit.RetrofitHttpEngine;
import com.zzw.guanglan.manager.UserManager;
import com.zzw.guanglan.rx.ErrorObserver;
import com.zzw.guanglan.rx.LifeObservableTransformer;
import com.zzw.guanglan.rx.ResultBooleanFunction;
import com.zzw.guanglan.service.SocketService;
import com.zzw.guanglan.socket.CMD;
import com.zzw.guanglan.socket.EventBusTag;
import com.zzw.guanglan.socket.resolve.Packet;
import com.zzw.guanglan.socket.utils.ByteUtil;
import com.zzw.guanglan.socket.utils.MyLog;
import com.zzw.guanglan.utils.ToastUtils;
import com.zzw.guanglan.utils.WifiAPManager;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.io.File;
import java.util.Arrays;

import butterknife.BindView;

/**
 * Created by zzw on 2018/10/14.
 * 描述:
 */
public class HotConnActivity extends BaseActivity {
    @BindView(R.id.tv_hint)
    TextView tvHint;
    @BindView(R.id.tv_hint2)
    TextView tvHint2;
    @BindView(R.id.tv_deveice_num)
    TextView tvDeveiceNum;
    @BindView(R.id.tv_recive)
    TextView tvRecive;
    @BindView(R.id.tv_send)
    TextView tvSend;
    @BindView(R.id.start)
    Button start;

    private WifiAPManager wifiAPManager;
    private HotBroadcastReceiver receiver;
    private final String hotName = "GLCS";
    private boolean isOpen = false;
    private boolean serviceOpen = false;

    public static void open(Context context) {
        context.startActivity(new Intent(context, HotConnActivity.class));
    }

    @Override
    protected int initLayoutId() {
        return R.layout.activity_hot_conn;
    }

    @Override
    protected void initView() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CHANGE_WIFI_STATE,
                        Manifest.permission.WRITE_SETTINGS,}, 5);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            // 判断是否有WRITE_SETTINGS权限if(!Settings.System.canWrite(this))
            if (!Settings.System.canWrite(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1);
            }
        }

        super.initView();
        wifiAPManager = new WifiAPManager(this);
        receiver = new HotBroadcastReceiver();
        IntentFilter mIntentFilter = new IntentFilter("android.net.wifi.WIFI_AP_STATE_CHANGED");
        registerReceiver(receiver, mIntentFilter);
    }


    @Override
    protected void initData() {
        super.initData();
        if (SocketService.isConn()) {
            hintS = "当前链接:" + Contacts.connKey;
            hint();
            start.setText("断开链接");
            start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopSocketServer();
                    initData();
                }
            });
            getDevicesNum();
        } else {
            start.setText("开启共享建立链接");
            start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isOpen) {
                        startWifiHot();
                    } else {
                        getIp();
                    }
                }
            });
        }
    }

    //获取设备序列号
    private void getDevicesNum() {
        EventBus.getDefault().post(0, EventBusTag.GET_DEVICE_SERIAL_NUMBER);
    }


    @Subscriber(tag = EventBusTag.SOCKET_CONN_STATUS_CHANGE)
    public void socketConnChange(boolean change) {
        if (change) {
            hintS = "与" + Contacts.connKey + "建立连接";
            getDevicesNum();
        } else {
            hintS = "断开连接";
        }
        hint();
        initData();
    }


    @Subscriber(tag = EventBusTag.RECIVE_DEVICE_SERIAL_NUMBER)
    public void recieveDeviceNum(String deviceNum) {
        tvDeveiceNum.setText("序列号: " + deviceNum);
        RetrofitHttpEngine.obtainRetrofitService(Api.class)
                .checkSerial(SocketService.getDeviceNum(), UserManager.getInstance().getUserName())
                .map(ResultBooleanFunction.create())
                .compose(LifeObservableTransformer.<Boolean>create(this))
                .subscribe(new ErrorObserver<Boolean>(this) {
                    @Override
                    public void onError(Throwable e) {
//                        super.onError(e);
                        ToastUtils.showToast("该设备没有授权无法测试，请联系管理员。");
                        tvDeveiceNum.append("  验证不通过");
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            tvDeveiceNum.append("  验证通过");
                        }else {
                            ToastUtils.showToast("该设备没有授权无法测试，请联系管理员。");

                        }
                    }
                });
    }


    String hintS = "";

    private class HotBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.net.wifi.WIFI_AP_STATE_CHANGED".equals(action)) {
                //state状态为：10---正在关闭；11---已关闭；12---正在开启；13---已开启
                int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                MyLog.e("state =" + state);
                isOpen = false;
                switch (state) {
                    case 10:
                        hintS = "热点正在关闭";
                        MyLog.e("热点正在关闭");
                        break;
                    case 11:
                        hintS = "热点已关闭";
                        MyLog.e("热点已关闭");
                        break;

                    case 12:
                        hintS = "热点正在开启";
                        MyLog.e("热点正在开启");
                        break;
                    case 13:
                        //开启成功
                        hintS = "热点正在开启";
                        MyLog.e("热点正在开启");
                        getIp();
                        break;
                }
                if (!SocketService.isConn()) {
                    hint();
                }
            }
        }
    }

    private void getIp() {
        //设置个延迟 不然会拿不到
        tvHint.postDelayed(new Runnable() {
            @Override
            public void run() {
                isOpen = true;
                String serverIp = wifiAPManager.getLocalIpAddress();
                Contacts.loaclIp = serverIp;
                hintS = "共享开启成功，请先连接热点，然后socket连接。ip:" + serverIp + "端口:" + 8825;
                MyLog.e("热点已开启 ip=" + serverIp);
                if (!SocketService.isConn()) {
                    startSocketServer();
                    hint();
                }
            }
        }, 2000);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
//        wifiAPManager.closeWifiAp();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    private void startSocketServer() {
        Intent intent = new Intent(this, SocketService.class);
        startService(intent);
    }

    private void stopSocketServer() {
        Intent intent = new Intent(this, SocketService.class);
        stopService(intent);
    }

    private void startWifiHot() {
        //7.0之前
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            wifiAPManager.startWifiAp1(hotName, "1234567890", true);
            //8.0以后
        }
//        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            //7.0
//        }
        else {
            showRequestApDialogOnN_MR1();
        }
    }

    private void showRequestApDialogOnN_MR1() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("android7.1系统以上不支持自动开启热点,需要手动开启热点");
        builder.setPositiveButton("去开启", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                openAP();
            }
        });
        builder.create().show();
    }

    //打开系统的便携式热点界面
    private void openAP() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        ComponentName com = new ComponentName("com.android.settings", "com.android.settings.TetherSettings");
        intent.setComponent(com);
        startActivityForResult(intent, 1000);
    }

    //判断用户是否开启热点 getWiFiAPConfig(); 这个方法去获取本机的wifi热点的信息就不贴了
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (!wifiAPManager.isWifiApEnabled()) {
                showRequestApDialogOnN_MR1();
            } else {
                hintS = "共享开启成功，正在获取ip...";
                hint();
                getIp();
            }
        }
    }
    /*
    @Subscriber(tag = EventBusTag.TAG_SEND_MSG)
    public void sendMsg(Packet packet) {
        StringBuilder builder = new StringBuilder();
        if (packet.cmd == CMD.GET_DEVICE_SERIAL_NUMBER) {
            builder.append("发送到APP询问设备序列号设备命令\n");
        } else if (packet.cmd == CMD.SEND_TEST_ARGS_AND_START_TEST) {
            builder.append("发送到APP给设备下发OTDR测试参数并启动测试命令\n");
        } else if (packet.cmd == CMD.GET_SOR_FILE) {
            builder.append("发送到APP向设备请求传输sor文件命令\n");
        } else if (packet.cmd == CMD.HEART_SEND) {  //屏蔽心跳
            builder.append("发送到心跳包命令\n");
            return;
        } else if (packet.cmd == CMD.HEART_RE) {  //屏蔽心跳
            builder.append("发送到回复心跳包命令\n");
            return;
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
        MyLog.e(builder.toString());
        tvSend.setText(builder.toString());
    }


    @Subscriber(tag = EventBusTag.TAG_RECIVE_MSG)
    public void reciverMsg(Packet packet) {
        StringBuilder builder = new StringBuilder();
        if (packet.cmd == CMD.GET_DEVICE_SERIAL_NUMBER) {
            builder.append("接收到APP询问设备序列号设备命令\n");
        } else if (packet.cmd == CMD.SEND_TEST_ARGS_AND_START_TEST) {
            builder.append("接收到APP给设备下发OTDR测试参数并启动测试命令\n");
        } else if (packet.cmd == CMD.GET_SOR_FILE) {
            builder.append("接收到APP向设备请求传输sor文件命令\n");
        } else if (packet.cmd == CMD.HEART_SEND) {//屏蔽心跳
            builder.append("接收到心跳包命令\n");
            return;
        } else if (packet.cmd == CMD.HEART_RE) { //屏蔽心跳
            builder.append("接收到回复心跳包命令\n");
            return;
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

        MyLog.e(builder.toString());
        tvRecive.setText(builder.toString());
    }

    */

    private void hint() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvHint.setText(hintS);
            }
        });
    }
}
