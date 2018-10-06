package com.zzw.socketdemo.ui.guangland;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zzw.socketdemo.R;
import com.zzw.socketdemo.bean.GuanLanItemBean;

import java.util.List;

/**
 * Created by zzw on 2018/10/3.
 * 描述:
 */
public class GuangLanDListAdapter extends BaseQuickAdapter<GuanLanItemBean, BaseViewHolder> {
    public GuangLanDListAdapter(@Nullable List<GuanLanItemBean> data) {
        super(R.layout.item_guanglan, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, GuanLanItemBean item) {
        helper.setText(R.id.tv_gl_d_name, "光缆段名称:" + item.getCabelOpName());
        helper.setText(R.id.tv_gl_d_num, "区域:" + item.getCabelOpCode());
        helper.setText(R.id.num, "id:" + item.getId());
        helper.setText(R.id.tv_gd_are_name, "区域:" + item.getAreaName());
        helper.setText(R.id.tv_gl_name, "光缆名称:" + item.getPaCableName());
        helper.setText(R.id.tv_gl_leave, "级别:" + item.getPaCableLevel());
        helper.setText(R.id.tv_gl_state, "状态:" + item.getStateName());
    }
}
