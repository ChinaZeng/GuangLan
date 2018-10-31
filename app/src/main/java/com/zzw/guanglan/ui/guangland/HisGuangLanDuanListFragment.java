package com.zzw.guanglan.ui.guangland;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.zzw.guanglan.R;
import com.zzw.guanglan.base.BaseFragment;
import com.zzw.guanglan.bean.GuangLanDItemBean;
import com.zzw.guanglan.utils.SPUtil;

import java.util.ArrayList;

import butterknife.BindView;

public class HisGuangLanDuanListFragment extends BaseFragment {

    @BindView(R.id.recy)
    RecyclerView recy;

    private GuangLanDListAdapter adapter;

    public static HisGuangLanDuanListFragment newInstance() {
        return new HisGuangLanDuanListFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_his_guang_lan_d_list;
    }

    @Override
    protected void initData() {
        super.initData();
        recy.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new GuangLanDListAdapter(new ArrayList<GuangLanDItemBean>());
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
}
