package com.zzw.socketdemo.ui.home;

import android.view.View;

import com.zzw.socketdemo.R;
import com.zzw.socketdemo.base.BaseFragment;
import com.zzw.socketdemo.ui.guangland.GuangLanDListActivity;
import com.zzw.socketdemo.widgets.RingView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


public class HomeFragment extends BaseFragment {

    @BindView(R.id.ring_view)
    RingView ringView;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initData() {
        super.initData();

        // 添加的是颜色
        List<Integer> colorList = new ArrayList<>();
        colorList.add(R.color.colorf30000);
        colorList.add(R.color.color70f31f);
        colorList.add(R.color.coloredf382);

        //  添加的是百分比
        List<Float> rateList = new ArrayList<>();
        rateList.add(20f);
        rateList.add(30f);
        rateList.add(50f);
        ringView.setShow(colorList, rateList, false, false);

    }


    @OnClick({R.id.res_look, R.id.com_conf})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.res_look:
                GuangLanDListActivity.open(getContext());
                break;
            case R.id.com_conf:

                break;
        }
    }
}
