package com.zzw.socketdemo.ui.qianxin;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zzw.socketdemo.R;
import com.zzw.socketdemo.bean.GuanLanItemBean;
import com.zzw.socketdemo.bean.QianXinItemBean;

import java.util.List;

/**
 * Created by zzw on 2018/10/3.
 * 描述:
 */
public class QianXinListAdapter extends BaseQuickAdapter<QianXinItemBean, BaseViewHolder> {
    public QianXinListAdapter(@Nullable List<QianXinItemBean> data) {
        super(R.layout.item_qianxin, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, QianXinItemBean item) {
        helper.setText(R.id.num, "id:" + item.getFiberId());
        helper.setText(R.id.tv_gl_d_num, "光缆段编码:" + item.getCblOpCode());
        helper.setText(R.id.tv_gl_d_name, "光缆段名称:" + item.getCblOpName());
        helper.setText(R.id.tv_dl_name, "电缆名称:" + item.getCableName());
    }
}
