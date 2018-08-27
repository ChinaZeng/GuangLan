package com.zzw.socketdemo.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.zzw.socketdemo.R;
import com.zzw.socketdemo.base.BaseActivity;
import com.zzw.socketdemo.bottomtab.TabBottomNavigation;
import com.zzw.socketdemo.bottomtab.iterator.TabListIterator;
import com.zzw.socketdemo.ui.home.HomeFragment;
import com.zzw.socketdemo.ui.me.MeFragment;
import com.zzw.socketdemo.ui.workorder.WorkOrderListFragment;
import com.zzw.socketdemo.utils.FragmentHelper;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements TabBottomNavigation.OnCheckChangeListener {


    @BindView(R.id.tab_bottom)
    TabBottomNavigation tabBottom;

    private FragmentHelper fragmentHelper;

    private Fragment homeFragment, workOrderFragment, meFragment;


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

        TabListIterator<MainBottomTabItem> listIterator = new TabListIterator<>();
        listIterator.addItem(new MainBottomTabItem.Builder(this)
                .resIcon(R.drawable.selector_icon_home).text("首页").create());
        listIterator.addItem(new MainBottomTabItem.Builder(this)
                .resIcon(R.drawable.selector_icon_work_order).text("工单").create());
        listIterator.addItem(new MainBottomTabItem.Builder(this)
                .resIcon(R.drawable.selector_icon_me).text("我的").create());
        tabBottom.addTabItem(listIterator);

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
                    workOrderFragment = WorkOrderListFragment.newInstance();
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
}
