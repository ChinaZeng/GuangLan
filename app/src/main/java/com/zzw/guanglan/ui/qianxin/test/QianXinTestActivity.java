package com.zzw.guanglan.ui.qianxin.test;

import android.Manifest;
import android.app.ProgressDialog;
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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
import com.zzw.guanglan.socket.event.ConnBean;
import com.zzw.guanglan.socket.event.SorFileBean;
import com.zzw.guanglan.socket.event.TestArgsAndStartBean;
import com.zzw.guanglan.socket.listener.STATUS;
import com.zzw.guanglan.socket.resolve.Packet;
import com.zzw.guanglan.socket.utils.ByteUtil;
import com.zzw.guanglan.socket.utils.FileHelper;
import com.zzw.guanglan.socket.utils.MyLog;
import com.zzw.guanglan.utils.RequestBodyUtils;
import com.zzw.guanglan.utils.ToastUtils;
import com.zzw.guanglan.utils.WifiAPManager;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.io.File;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class QianXinTestActivity extends BaseActivity {

    private final String hotName = "光缆共享wifi";

    @BindView(R.id.tv_hint)
    TextView tvHint;
    @BindView(R.id.tv_hint2)
    TextView tvHint2;
    @BindView(R.id.start)
    Button start;
    private static final String FIBER_ID = "FIBER_ID";

    private HotBroadcastReceiver receiver;
    private WifiAPManager wifiAPManager;

    private String fiberId;


    public static void open(Context context, String fiberId) {
        context.startActivity(new Intent(context, QianXinTestActivity.class).putExtra(FIBER_ID, fiberId));
    }

    @Override
    protected int initLayoutId() {
        return R.layout.activity_qianxin_test;
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

        this.fiberId = getIntent().getStringExtra(FIBER_ID);
        wifiAPManager = new WifiAPManager(this);
        receiver = new HotBroadcastReceiver();
        IntentFilter mIntentFilter = new IntentFilter("android.net.wifi.WIFI_AP_STATE_CHANGED");
        registerReceiver(receiver, mIntentFilter);
    }

    private String filePath;

    @OnClick({R.id.start, R.id.upload})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.start:
                if (!wifiIsOpen) {
                    startWifiHot();
                    return;
                }

                if (!isConn) {
                    ToastUtils.showToast("请设备连接手机热点");
                    return;
                }
                chooseArgs();
                break;
            case R.id.upload:
                if (TextUtils.isEmpty(filePath)) {
                    ToastUtils.showToast("请先进行测试");
                    return;
                }

                progressDialog = new ProgressDialog(QianXinTestActivity.this);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setTitle("正在上传sor文件");
                progressDialog.show();

                final File file = new File(filePath);
                RequestBody requestFile =
                        RequestBody.create(MediaType.parse("multipart/form-data"), file);
                String name = file.getName();
                MultipartBody.Part fileBody =
                        MultipartBody.Part.createFormData("file", name, requestFile);

                String struffix = null;
                if (name.contains(".")) {
                    String[] split = name.split("\\.");
                    struffix = split[split.length - 1];
                }

                final String finalStruffix = struffix;
                RetrofitHttpEngine.obtainRetrofitService(Api.class)
                        .saveFiberFile(RequestBodyUtils.generateRequestBody(new HashMap<String, String>() {
                            {
                                put("struffix", finalStruffix);
                                put("fiberId", fiberId);
                                put("userId", UserManager.getInstance().getUserId());
                            }
                        }), fileBody)
                        .map(ResultBooleanFunction.create())
                        .compose(LifeObservableTransformer.<Boolean>create(this))
                        .subscribe(new ErrorObserver<Boolean>(this) {
                            @Override
                            public void onNext(Boolean bo) {
                                if (bo) {
                                    ToastUtils.showToast("上传成功");
                                    finish();
                                }else {
                                    ToastUtils.showToast("上传失败");
                                }
                                progressDialog.dismiss();
                            }

                            @Override
                            public void onError(Throwable e) {
                                super.onError(e);
//                                ToastUtils.showToast("请关闭热点切换网络,保证网络连接畅通,然后点击上传");
                                progressDialog.dismiss();
                            }
                        });

                break;
        }
    }


    @Subscriber(tag = EventBusTag.TAG_RECIVE_MSG)
    public void reciverMsg(Packet packet) {

        if (packet.cmd == CMD.RECIVE_SOR_INFO && packet.data.length >= (32 + 16 + 4)) {
            byte[] fileNameB = ByteUtil.subBytes(packet.data, 0, 32);
            byte[] fileLocB = ByteUtil.subBytes(packet.data, 32, 16);
            byte[] fileSizeB = ByteUtil.subBytes(packet.data, 32 + 16, 4);
            fileName = ByteUtil.bytes2Str(fileNameB);
            fileDir = ByteUtil.bytes2Str(fileLocB);
            fileSize = ByteUtil.bytesToInt(fileSizeB);
            MyLog.e("fileName = " + fileName + "  fileLoc = " + fileDir + " fileSize = " + fileSize);

            tvHint2.setText("");
            getSorFilecount = 0;
            filePath = null;

            getSorFile();
        }
    }


    @Subscriber(tag = EventBusTag.SOR_RECIVE_SUCCESS)
    public void reciveSorSuccess(SorFileBean bean) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        filePath = bean.filePath;
        ToastUtils.showToast("接收sor文件成功: " + bean.filePath);
        tvHint2.setText("测试完毕,请关闭热点切换网络,保证网络连接畅通,然后点击上传");
        hint();
    }

    @Subscriber(tag = EventBusTag.SOR_RECIVE_FAIL)
    public void reciveSorFail(SorFileBean bean) {
        getSorFile();
    }

    private String fileName;
    private String fileDir;
    private int fileSize;
    private int getSorFilecount = 0;

    private ProgressDialog progressDialog;
    private AlertDialog chooseArgsDialog;

    private void getSorFile() {
        if (fileName != null && fileDir != null && fileSize != 0) {
            getSorFilecount++;
            if (getSorFilecount > 3) {
                getSorFilecount = 0;
                ToastUtils.showToast("文件保存失败!");
                return;
            }

            //这一步很重要，因为协议原因。请求之前必须先删除之前的文件
            String localFileName = FileHelper.SAVE_FILE_DIR + File.separator + fileName;
            File file = new File(localFileName);
            if (file.exists()) {
                file.delete();
            }

            SorFileBean bean = new SorFileBean();
            bean.fileDir = fileDir;
            bean.fileName = fileName;
            bean.fileSize = fileSize;
            EventBus.getDefault().post(bean, EventBusTag.GET_SOR_FILE);

            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            progressDialog = new ProgressDialog(QianXinTestActivity.this);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setTitle("正在获取sor文件");
            progressDialog.show();
        } else {
            ToastUtils.showToast("请先设备向APP反馈sor文件信息");
        }
    }

    boolean isConn = false;
    boolean wifiIsOpen = false;

    @Subscriber
    public void conn(ConnBean connBean) {
        isConn = false;
        if (connBean.status == STATUS.RUNNING) {
            hintS = "与" + connBean.key + "建立连接";
            isConn = true;
            chooseArgs();
        } else if (connBean.status == STATUS.END) {
            hintS = "与" + connBean.key + "断开连接";
        } else if (connBean.status == STATUS.INIT) {
            hintS = "与" + connBean.key + "初始化接收线程";
        }
        hint();
    }

    private void chooseArgs() {
        if (chooseArgsDialog == null) {
            View dialogView = LayoutInflater.from(this).inflate(R.layout.layout_edit_args, null);
            final EditText range = dialogView.findViewById(R.id.et_range);
            final EditText wl = dialogView.findViewById(R.id.et_wl);
            final EditText pw = dialogView.findViewById(R.id.et_pw);
            final EditText time = dialogView.findViewById(R.id.et_time);
            final EditText mode = dialogView.findViewById(R.id.et_mode);
            final EditText gi = dialogView.findViewById(R.id.et_gi);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(dialogView);
            chooseArgsDialog = builder.create();
            chooseArgsDialog.setCanceledOnTouchOutside(false);
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


                        progressDialog = new ProgressDialog(QianXinTestActivity.this);
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.setTitle("正在获取sor文件相关参数信息");
                        progressDialog.show();

                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtils.showToast("出现异常了，请填取数值");
                    }
                    chooseArgsDialog.dismiss();
                }
            });
        }
        chooseArgsDialog.show();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        stopSocketServer();
        unregisterReceiver(receiver);
        wifiAPManager.closeWifiAp();
    }

    String hintS;


    private class HotBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.net.wifi.WIFI_AP_STATE_CHANGED".equals(action)) {
                //state状态为：10---正在关闭；11---已关闭；12---正在开启；13---已开启
                int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                MyLog.e("state =" + state);
                wifiIsOpen = false;
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
        tvHint.postDelayed(new Runnable() {
            @Override
            public void run() {
                String serverIp = wifiAPManager.getLocalIpAddress();
                isConn = false;
                wifiIsOpen = true;
                hintS = "共享开启成功，请先连接热点，然后socket连接。ip:" + serverIp + "端口:" + 8825;
                MyLog.e("热点已开启 ip=" + serverIp);
                startSocketServer();
                hint();
            }
        }, 2000);
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            // 判断是否有WRITE_SETTINGS权限if(!Settings.System.canWrite(this))
            if (!Settings.System.canWrite(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1);
            } else {
                wifiAPManager.startWifiAp1(hotName, "1234567890", true);
            }
            //8.0以后
        }
//        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            //7.0
//        }
        else {
            showRequestApDialogOnN_MR1();
        }
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
        } else if (requestCode == 1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                // 判断是否有WRITE_SETTINGS权限if(!Settings.System.canWrite(this))
                if (!Settings.System.canWrite(this)) {
                    ToastUtils.showToast("请开启相关权限!");
                } else {
                    wifiAPManager.startWifiAp1(hotName, "1234567890", true);
                }
            }
        }
    }

    private void hint() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvHint.setText(hintS);
            }
        });
    }
}
