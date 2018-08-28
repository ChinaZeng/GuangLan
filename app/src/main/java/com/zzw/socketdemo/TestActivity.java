package com.zzw.socketdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.zzw.socketdemo.base.BaseActivity;
import com.zzw.socketdemo.service.SocketService;
import com.zzw.socketdemo.socket.CMD;
import com.zzw.socketdemo.socket.EventBusTag;
import com.zzw.socketdemo.socket.event.ConnBean;
import com.zzw.socketdemo.socket.event.GetSorFileBean;
import com.zzw.socketdemo.socket.event.ReBean;
import com.zzw.socketdemo.socket.event.TestArgsAndStartBean;
import com.zzw.socketdemo.socket.listener.STATUS;
import com.zzw.socketdemo.socket.resolve.Packet;
import com.zzw.socketdemo.socket.utils.MyLog;
import com.zzw.socketdemo.utils.ToastUtils;
import com.zzw.socketdemo.utils.WifiAPManager;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import butterknife.BindView;

public class TestActivity extends BaseActivity {

    @BindView(R.id.hint)
    TextView hint;

    private WifiAPManager wifiAPManager;
    private HotBroadcastReceiver receiver;
    private final String hotName = "光缆共享wifi";


    @Override
    protected void initView() {
        super.initView();
        wifiAPManager = new WifiAPManager(this);
        receiver = new HotBroadcastReceiver();
        IntentFilter mIntentFilter = new IntentFilter("android.net.wifi.WIFI_AP_STATE_CHANGED");
        registerReceiver(receiver, mIntentFilter);
    }

    //开启热点
    public void click0(View view) {
        startWifiHot();
    }

    //APP询问设备序列号
    public void click1(View view) {
        EventBus.getDefault().post(0, EventBusTag.GET_DEVICE_SERIAL_NUMBER);
    }


    //OTDR上报设备序列号给APP
    public void click2(View view) {
        ToastUtils.showToast("未接入");
    }

    //APP给设备下发OTDR测试参数并启动测试
    public void click3(View view) {
        TestArgsAndStartBean bean = new TestArgsAndStartBean();
        bean.rang = 10;
        bean.wl = 10;
        bean.pw = 10;
        bean.time = 10;
        bean.mode = 1;
        bean.gi = 146850;
        EventBus.getDefault().post(bean, EventBusTag.SEND_TEST_ARGS_AND_START_TEST);
    }

    //设备向APP反馈sor文件信息
    public void click4(View view) {
        ToastUtils.showToast("未接入");
    }

    //APP向设备请求传输sor文件
    public void click5(View view) {
        GetSorFileBean bean = new GetSorFileBean();
        bean.fileDir = "fileDir";
        bean.fileName = "fileName.txt";
        EventBus.getDefault().post(bean, EventBusTag.GET_SOR_FILE);
    }

    //设备向APP发送OTDR测试结果文件
    public void click6(View view) {
        ToastUtils.showToast("未接入");
    }

    //错误代码
    public void click7(View view) {
        ReBean bean = new ReBean();
        bean.cmdCode = CMD.GET_DEVICE_SERIAL_NUMBER;
        bean.errorCode = CMD._CODE.SUCCESS;
        EventBus.getDefault().post(bean, EventBusTag.SEND_RE);
    }

    //发送心跳命令
    public void click8(View view) {
        EventBus.getDefault().post(0, EventBusTag.SEND_HEART);
    }

    //回复心跳命令
    public void click9(View view) {
        EventBus.getDefault().post(0, EventBusTag.RE_HEART);
    }


    @Subscriber
    public void conn(ConnBean connBean) {
        if (connBean.status == STATUS.RUNNING) {
            hintS = "与" + connBean.key + "建立连接";
        } else if (connBean.status == STATUS.END) {
            hintS = "与" + connBean.key + "断开连接";
        } else if (connBean.status == STATUS.INIT) {
            hintS = "与" + connBean.key + "初始化接收线程";
        }
        hint();
    }


    @Subscriber(tag = EventBusTag.TAG_RECIVE_MSG)
    public void reciverMsg(Packet packet) {
//        hint();
    }

    @Subscriber(tag = EventBusTag.TAG_SEND_MSG)
    public void sendMsg(Packet packet) {
        if (packet.cmd == CMD.GET_DEVICE_SERIAL_NUMBER) {
            hintS = "发送获取设备号命令成功";
        } else if (packet.cmd == CMD.SEND_TEST_ARGS_AND_START_TEST) {
            hintS = "发送APP给设备下发OTDR测试参数并启动测试命令成功";
        } else if (packet.cmd == CMD.GET_SOR_FILE) {
            hintS = "发送APP向设备请求传输sor文件命令成功";
        } else if (packet.cmd == CMD.HEART_SEND) {
            hintS = "发送心跳包命令成功";
        } else if (packet.cmd == CMD.HEART_RE) {
            hintS = "发送回复心跳包命令成功";
        } else if (packet.cmd == CMD._RE) {
            hintS = "发送错误代码命令成功";
        }
        hint();
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
                        //设置个延迟 不然会拿不到
                        hint.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                String serverIp = wifiAPManager.getLocalIpAddress();
                                hintS = "共享开启成功，请先连接热点，然后socket连接。ip:" + serverIp + "端口:" + 8825;
                                MyLog.e("热点已开启 ip=" + serverIp);
                                startSocketServer();
                                hint();
                            }
                        }, 2000);
                        break;
                }
                hint();
            }
        }
    }


    @Override
    protected int initLayoutId() {
        return R.layout.activity_test;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unregisterReceiver(receiver);
        wifiAPManager.closeWifiAp();
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

    private void startWifiHot() {
        wifiAPManager.startWifiAp(hotName, "1234567890", true);
    }

    private void hint() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hint.setText(hintS);
            }
        });
    }

}
