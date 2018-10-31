package com.zzw.guanglan.ui.guangland;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.zzw.guanglan.R;
import com.zzw.guanglan.base.BaseActivity;
import com.zzw.guanglan.ui.workorder.WorkOrderListFragment;

import butterknife.BindView;

public class GuangLanDListActivity extends BaseActivity {

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.vp)
    ViewPager vp;

    public static void open(Context context) {
        context.startActivity(new Intent(context, GuangLanDListActivity.class));
    }


    @Override
    protected int initLayoutId() {
        return R.layout.activity_guang_lan_d_list;
    }


    @Override
    protected void initView() {
        super.initView();
        super.initView();
        final String[] titles = new String[]{"光缆段", "已测试光缆段"};
        final Fragment[] fragments = new Fragment[]{GuangLanDuanListFragment.newInstance(), HisGuangLanDuanListFragment.newInstance()};

        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments[position];
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return titles[position];
            }

            @Override
            public int getCount() {
                return titles.length;
            }
        };
        vp.setAdapter(adapter);
        tabLayout.setupWithViewPager(vp);
    }
}
