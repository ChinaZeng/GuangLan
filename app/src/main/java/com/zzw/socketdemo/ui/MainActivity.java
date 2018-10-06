package com.zzw.socketdemo.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.support.v4.app.Fragment;

import com.zzw.socketdemo.R;
import com.zzw.socketdemo.base.BaseActivity;
import com.zzw.socketdemo.bottomtab.TabBottomNavigation;
import com.zzw.socketdemo.bottomtab.iterator.TabListIterator;
import com.zzw.socketdemo.service.SocketService;
import com.zzw.socketdemo.socket.utils.MyLog;
import com.zzw.socketdemo.ui.home.HomeFragment;
import com.zzw.socketdemo.ui.me.MeFragment;
import com.zzw.socketdemo.ui.workorder.WorkOrderFragment;
import com.zzw.socketdemo.utils.FragmentHelper;
import com.zzw.socketdemo.utils.ToastUtils;
import com.zzw.socketdemo.utils.WifiAPManager;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements TabBottomNavigation.OnCheckChangeListener {


    @BindView(R.id.tab_bottom)
    TabBottomNavigation tabBottom;

    private FragmentHelper fragmentHelper;

    private Fragment homeFragment, workOrderFragment, meFragment;


    private WifiAPManager wifiAPManager;
    private HotBroadcastReceiver receiver;
    private final String hotName = "光缆共享wifi";


    public static void open(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }



    @Override
    protected int initLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        super.initView();

        wifiAPManager = new WifiAPManager(this);
        receiver = new HotBroadcastReceiver();
        IntentFilter mIntentFilter = new IntentFilter("android.net.wifi.WIFI_AP_STATE_CHANGED");
        registerReceiver(receiver, mIntentFilter);

        TabListIterator<MainBottomTabItem> listIterator = new TabListIterator<>();
        listIterator.addItem(new MainBottomTabItem.Builder(this)
                .resIcon(R.drawable.selector_icon_home).text("首页").create());
        listIterator.addItem(new MainBottomTabItem.Builder(this)
                .resIcon(R.drawable.selector_icon_work_order).text("工单").create());
        listIterator.addItem(new MainBottomTabItem.Builder(this)
                .resIcon(R.drawable.selector_icon_me).text("我的").create());
        tabBottom.addTabItem(listIterator);
        tabBottom.setOnCheckChangeListener(this);

        fragmentHelper = new FragmentHelper(getSupportFragmentManager(), R.id.frame_layout);
        onCheckChange(0, 0);




//        startWifiHot();
    }

    @Override
    public void onCheckChange(int oldPos, int newPos) {
        switch (newPos) {
            case 0:
                if (homeFragment == null) {
                    homeFragment = HomeFragment.newInstance();
                }
                fragmentHelper.switchFragment(homeFragment);
                break;

            case 1:
                if (workOrderFragment == null) {
                    workOrderFragment = WorkOrderFragment.newInstance();
                }
                fragmentHelper.switchFragment(workOrderFragment);
                break;

            case 2:
                if (meFragment == null) {
                    meFragment = MeFragment.newInstance();
                }
                fragmentHelper.switchFragment(meFragment);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        wifiAPManager.closeWifiAp();
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
                        MyLog.e("热点正在关闭");
                        break;
                    case 11:
                        MyLog.e("热点已关闭");
                        break;

                    case 12:
                        MyLog.e("热点正在开启");
                        break;
                    case 13:
                        //开启成功
                        MyLog.e("热点正在开启");
                        //设置个延迟 不然会拿不到
                        tabBottom.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                String serverIp = wifiAPManager.getLocalIpAddress();
                                ToastUtils.showToast("共享开启成功，请连接。");
                                MyLog.e("热点已开启 ip=" + serverIp );
//                                serverManager.startServer();
                                startSocketServer();
                            }
                        }, 2000);

                        break;
                }
            }
        }
    }

    @Override
    protected boolean backable() {
        return false;
    }

    private void startSocketServer() {
        startService(new Intent(this, SocketService.class));
    }

    private void startWifiHot() {
        wifiAPManager.startWifiAp1(hotName, "1234567890", true);
    }
}
