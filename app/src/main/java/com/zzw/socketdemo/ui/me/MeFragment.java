package com.zzw.socketdemo.ui.me;

import com.zzw.socketdemo.R;
import com.zzw.socketdemo.base.BaseFragment;

public class MeFragment extends BaseFragment {


    public static MeFragment newInstance() {
        MeFragment fragment = new MeFragment();
        return fragment;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_me;
    }
}
