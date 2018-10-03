package com.zzw.socketdemo.ui.guanglan;

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
public class GuangLanListAdapter extends BaseQuickAdapter<GuanLanItemBean, BaseViewHolder> {
    public GuangLanListAdapter(@Nullable List<GuanLanItemBean> data) {
        super(R.layout.item_guanglan, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, GuanLanItemBean item) {
        helper.setText(R.id.tv_gl_d_name, item.getCabelOpName());
        helper.setText(R.id.tv_gl_d_num, item.getCabelOpCode());
        helper.setText(R.id.num, item.getId());
        helper.setText(R.id.tv_gd_are_name, item.getAreaName());
        helper.setText(R.id.tv_gl_name, item.getPaCableName());
        helper.setText(R.id.tv_gl_leave, item.getPaCableLevel());
        helper.setText(R.id.tv_gl_state, item.getStateName());
    }
}
