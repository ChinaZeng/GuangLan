package com.zzw.guanglan;

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
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.zzw.guanglan.base.BaseActivity;
import com.zzw.guanglan.service.SocketService;
import com.zzw.guanglan.socket.CMD;
import com.zzw.guanglan.socket.EventBusTag;
import com.zzw.guanglan.socket.event.ConnBean;
import com.zzw.guanglan.socket.event.SorFileBean;
import com.zzw.guanglan.socket.event.ReBean;
import com.zzw.guanglan.socket.event.TestArgsAndStartBean;
import com.zzw.guanglan.socket.listener.STATUS;
import com.zzw.guanglan.socket.resolve.Packet;
import com.zzw.guanglan.socket.utils.ByteUtil;
import com.zzw.guanglan.socket.utils.MyLog;
import com.zzw.guanglan.utils.ToastUtils;
import com.zzw.guanglan.utils.WifiAPManager;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;

import butterknife.BindView;

public class TestActivity extends BaseActivity {


    @BindView(R.id.recy1)
    RecyclerView recy1;
    @BindView(R.id.recy2)
    RecyclerView recy2;
    private PacketAdapter sendAdapter, reciveAdapter;

    @BindView(R.id.ip)
    TextView ip;

    private WifiAPManager wifiAPManager;
    private HotBroadcastReceiver receiver;
    private final String hotName = "光缆共享wifi";


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

        recy1.setLayoutManager(new LinearLayoutManager(this));
        recy2.setLayoutManager(new LinearLayoutManager(this));

        sendAdapter = new PacketAdapter();
        reciveAdapter = new PacketAdapter();

        recy1.setAdapter(sendAdapter);
        recy2.setAdapter(reciveAdapter);

    }

    //清空发送数据
    public void clear1(View view) {
        sendAdapter.replaceData(new ArrayList<Packet>());
    }

    //清空接收数据
    public void clear2(View view) {
        reciveAdapter.replaceData(new ArrayList<Packet>());
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

    private AlertDialog dialog;

    //APP给设备下发OTDR测试参数并启动测试
    public void click3(View view) {

        if (dialog == null) {
            View dialogView = LayoutInflater.from(this).inflate(R.layout.layout_edit_args, null);
            final EditText range = dialogView.findViewById(R.id.et_range);
            final EditText wl = dialogView.findViewById(R.id.et_wl);
            final EditText pw = dialogView.findViewById(R.id.et_pw);
            final EditText time = dialogView.findViewById(R.id.et_time);
            final EditText mode = dialogView.findViewById(R.id.et_mode);
            final EditText gi = dialogView.findViewById(R.id.et_gi);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(dialogView);
            dialog = builder.create();
            dialogView.findViewById(R.id.start_test).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String rangeStr = range.getText().toString().trim();
                        String wlStr = wl.getText().toString().trim();
                        String pwStr = pw.getText().toString().trim();
                        String timeStr = time.getText().toString().trim();
                        String modeStr = mode.getText().toString().trim();
                        String giStr = gi.getText().toString().trim();
                        if (rangeStr.length() == 0 || wlStr.length() == 0
                                || pwStr.length() == 0 || timeStr.length() == 0
                                || modeStr.length() == 0 || giStr.length() == 0) {
                            ToastUtils.showToast("请先填取参数");
                            return;
                        }
                        int r = Integer.parseInt(rangeStr);
                        int w = Integer.parseInt(wlStr);
                        int p = Integer.parseInt(pwStr);
                        int t = Integer.parseInt(timeStr);
                        int m = Integer.parseInt(modeStr);
                        int g = Integer.parseInt(giStr);

                        TestArgsAndStartBean bean = new TestArgsAndStartBean();
                        bean.rang = r;
                        bean.wl = w;
                        bean.pw = p;
                        bean.time = t;
                        bean.mode = m;
                        bean.gi = g;
                        EventBus.getDefault().post(bean, EventBusTag.SEND_TEST_ARGS_AND_START_TEST);

                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtils.showToast("出现异常了，请填取数值");
                    }

                    dialog.dismiss();
                }
            });

        }
        dialog.show();

    }

    //APP向设备发送停止OTDR测试命令
    public void click10(View view) {
        EventBus.getDefault().post(0, EventBusTag.SEND_TEST_ARGS_AND_STOP_TEST);
    }

    //设备向APP反馈sor文件信息
    public void click4(View view) {
        ToastUtils.showToast("未接入");
    }

    //APP向设备请求传输sor文件
    public void click5(View view) {
        getSorFile();
    }

    private void getSorFile() {
        if (Contacts.fileName != null && Contacts.fileDir != null && Contacts.fileSize != 0) {
            SorFileBean bean = new SorFileBean();
            bean.fileDir = Contacts.fileDir;
            bean.fileName = Contacts.fileName;
            bean.fileSize = Contacts.fileSize;
            EventBus.getDefault().post(bean, EventBusTag.GET_SOR_FILE);
        } else {
            ToastUtils.showToast("请先设备向APP反馈sor文件信息");
        }
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
        reciveAdapter.addData(0, packet);

        if (packet.cmd == CMD.RECIVE_SOR_INFO && packet.data.length >= (32 + 16 + 4)) {
            byte[] fileNameB = ByteUtil.subBytes(packet.data, 0, 32);
            byte[] fileLocB = ByteUtil.subBytes(packet.data, 32, 16);
            byte[] fileSizeB = ByteUtil.subBytes(packet.data, 32 + 16, 4);
            Contacts.fileName = ByteUtil.bytes2Str(fileNameB);
            Contacts.fileDir = ByteUtil.bytes2Str(fileLocB);
            Contacts.fileSize = ByteUtil.bytesToInt(fileSizeB);
            MyLog.e("fileName = " + Contacts.fileName + "  fileLoc = " + Contacts.fileDir + " fileSize = " + Contacts.fileSize);
        } else if (packet.cmd == CMD.HEART_SEND) {
            EventBus.getDefault().post(0, EventBusTag.RE_HEART);
        }
    }

    @Subscriber(tag = EventBusTag.SOR_RECIVE_SUCCESS)
    public void reciveSorSuccess(SorFileBean bean) {
        ToastUtils.showToast("接收sor文件成功: " + bean.fileName);
    }

    @Subscriber(tag = EventBusTag.SOR_RECIVE_FAIL)
    public void reciveSorFail(SorFileBean bean) {
        getSorFile();
    }


    @Subscriber(tag = EventBusTag.TAG_SEND_MSG)
    public void sendMsg(Packet packet) {
        sendAdapter.addData(0, packet);
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
                        getIp();
                        break;
                }
                hint();
            }
        }
    }


    private void getIp() {

        //设置个延迟 不然会拿不到
        ip.postDelayed(new Runnable() {
            @Override
            public void run() {
                String serverIp = wifiAPManager.getLocalIpAddress();
                hintS = "共享开启成功，请先连接热点，然后socket连接。ip:" + serverIp + "端口:" + 8825;
                MyLog.e("热点已开启 ip=" + serverIp);
                startSocketServer();
                hint();
            }
        }, 2000);
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


    private void hint() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ip.setText(hintS);
            }
        });
    }

}
