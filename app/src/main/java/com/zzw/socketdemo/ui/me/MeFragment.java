package com.zzw.socketdemo.ui.me;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zzw.socketdemo.ClientActivity;
import com.zzw.socketdemo.R;
import com.zzw.socketdemo.TestActivity;
import com.zzw.socketdemo.base.BaseFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MeFragment extends BaseFragment {


    public static MeFragment newInstance() {
        MeFragment fragment = new MeFragment();
        return fragment;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_me;
    }




    @OnClick({R.id.tv_message, R.id.tv_change_pwd, R.id.tv_setting, R.id.tv_bluetooth, R.id.tv_socket, R.id.tv_sms_share, R.id.tv_about_us})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_message:
                startActivity(new Intent(getContext(), TestActivity.class));
                break;
            case R.id.tv_change_pwd:
//                startActivity(new Intent(getContext(), ClientActivity.class));
                break;
            case R.id.tv_setting:
                break;
            case R.id.tv_bluetooth:
                break;
            case R.id.tv_socket:
                break;
            case R.id.tv_sms_share:
                break;
            case R.id.tv_about_us:
                break;
        }
    }
}
