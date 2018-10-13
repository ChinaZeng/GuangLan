package com.zzw.guanglan.ui.guanglan;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zzw.guanglan.R;
import com.zzw.guanglan.bean.GuangLanItemBean;

import java.util.List;

/**
 * Created by zzw on 2018/10/3.
 * 描述:
 */
public class GuangLanListAdapter extends BaseQuickAdapter<GuangLanItemBean, BaseViewHolder> {
    public GuangLanListAdapter(@Nullable List<GuangLanItemBean> data) {
        super(R.layout.item_guanglan, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, GuangLanItemBean item) {
//        helper.setText(R.id.tv_gl_d_name, "光缆段名称:" + item.getCabelOpName());
//        helper.setText(R.id.tv_gl_d_num, "所在区域:" + item.getCabelOpCode());
//        helper.setText(R.id.num, "id:" + item.getId());
//        helper.setText(R.id.tv_gd_are_name, "区域:" + item.getAreaName());

        helper.setText(R.id.num, "光缆名称:" + item.getCableName());

        helper.setText(R.id.tv_gl_leave, "当前级别:" + item.getDescChina());
        helper.setText(R.id.tv_gl_state, "当前状态:" + item.getStateName());
        helper.setText(R.id.tv_gl_d_num, "所在区域:" + item.getAreaName());
        helper.setText(R.id.tv_gl_address, "安装地址:" + item.getAddress());
        helper.setText(R.id.tv_gl_len, "长度:" + item.getLength());
        helper.setText(R.id.tv_gl_rongliang, "容量:" + item.getCapacity());
    }
}
