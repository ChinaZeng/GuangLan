package com.zzw.guanglan.ui.guangland;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zzw.guanglan.R;
import com.zzw.guanglan.base.BaseFragment;
import com.zzw.guanglan.bean.GuangLanDItemBean;
import com.zzw.guanglan.location.LocationManager;
import com.zzw.guanglan.ui.qianxin.QianXinListActivity;
import com.zzw.guanglan.utils.SPUtil;

import java.util.ArrayList;

import butterknife.BindView;

public class HisGuangLanDuanListFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener {

    @BindView(R.id.recy)
    RecyclerView recy;

    private GuangLanDListAdapter adapter;

    private LocationManager.LocationBean locationBean;


    public static HisGuangLanDuanListFragment newInstance(LocationManager.LocationBean location) {

        Bundle bundle = new Bundle();
        bundle.putSerializable("location", location);
        HisGuangLanDuanListFragment fragment = new HisGuangLanDuanListFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_his_guang_lan_d_list;
    }

    @Override
    protected void initData() {
        super.initData();
        locationBean = (LocationManager.LocationBean) getArguments().getSerializable("location");


        recy.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new GuangLanDListAdapter(new ArrayList<GuangLanDItemBean>());
        adapter.setOnItemClickListener(this);
        recy.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        ArrayList<GuangLanDItemBean> data = SPUtil.getInstance("guanglan")
                .getSerializable("data", null);
        if (data != null && data.size() > 0) {
            adapter.setNewData(data);
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        QianXinListActivity.open(getContext(), (GuangLanDItemBean) adapter.getData().get(position), locationBean);
    }
}
