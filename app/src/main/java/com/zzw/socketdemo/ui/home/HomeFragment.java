package com.zzw.socketdemo.ui.home;

import com.zzw.socketdemo.R;
import com.zzw.socketdemo.base.BaseFragment;


public class HomeFragment extends BaseFragment {

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }


}
