package com.zzw.guanglan.ui.guangland;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zzw.guanglan.R;
import com.zzw.guanglan.bean.GuangLanDItemBean;

import java.util.List;

/**
 * Created by zzw on 2018/10/3.
 * 描述:
 */
public class GuangLanDListAdapter extends BaseQuickAdapter<GuangLanDItemBean, BaseViewHolder> {
    public GuangLanDListAdapter(@Nullable List<GuangLanDItemBean> data) {
        super(R.layout.item_guangland, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, GuangLanDItemBean item) {
//        helper.setText(R.id.tv_gl_d_name, "光缆段名称:" + item.getCabelOpName());
//        helper.setText(R.id.tv_gl_d_num, "所在区域:" + item.getCabelOpCode());
//        helper.setText(R.id.num, "id:" + item.getId());
//        helper.setText(R.id.tv_gd_are_name, "区域:" + item.getAreaName());

        helper.setText(R.id.tv_gl_leave, "当前级别:" + item.getPaCableLevel());
        helper.setText(R.id.tv_gl_state, "当前状态:" + item.getStateName());
        helper.setText(R.id.guang_lan_d_name, "光缆段名称:" + item.getCabelOpName());
        helper.setText(R.id.tv_gl_d_name, "所在区域:" + item.getAreaName());
        helper.setText(R.id.tv_gl_name, "光缆名称:" + item.getPaCableName());
    }
}
