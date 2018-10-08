package com.zzw.guanglan.ui.workorder;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.zzw.guanglan.R;
import com.zzw.guanglan.base.BaseFragment;

import butterknife.BindView;

public class WorkOrderFragment extends BaseFragment {

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.vp)
    ViewPager vp;

    public static WorkOrderFragment newInstance() {
        WorkOrderFragment fragment = new WorkOrderFragment();
        return fragment;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_work_order;
    }

    @Override
    protected void initView() {
        super.initView();
        final String[] titles = new String[]{"待办工单", "超时工单", "完成工单"};

        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return WorkOrderListFragment.newInstance(position);
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
