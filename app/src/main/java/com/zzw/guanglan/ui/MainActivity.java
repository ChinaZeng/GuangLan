package com.zzw.guanglan.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.zzw.guanglan.R;
import com.zzw.guanglan.base.BaseActivity;
import com.zzw.guanglan.bottomtab.TabBottomNavigation;
import com.zzw.guanglan.bottomtab.iterator.TabListIterator;
import com.zzw.guanglan.manager.LocationManager;
import com.zzw.guanglan.service.SocketService;
import com.zzw.guanglan.socket.utils.MyLog;
import com.zzw.guanglan.ui.home.HomeFragment;
import com.zzw.guanglan.ui.me.MeFragment;
import com.zzw.guanglan.ui.workorder.WorkOrderFragment;
import com.zzw.guanglan.utils.FragmentHelper;

import butterknife.BindView;

public class MainActivity extends BaseActivity
        implements TabBottomNavigation.OnCheckChangeListener, LocationManager.OnLocationListener {


    @BindView(R.id.tab_bottom)
    TabBottomNavigation tabBottom;

    private FragmentHelper fragmentHelper;

    private Fragment homeFragment, workOrderFragment, meFragment;



    public static void open(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (SocketService.isConn()) {
            stopService(new Intent(this, SocketService.class));
        }


    }

    @Override
    protected int initLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        super.initView();

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
    protected boolean backable() {
        return false;
    }

    @Override
    public void onSuccess(LocationManager.LocationBean bean) {
        MyLog.e(bean.toString());
    }

    @Override
    public void onError(int code, String msg) {
        MyLog.e("error " + msg);
    }
}
