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
        helper.setText(R.id.num, item.getFiberId());
        helper.setText(R.id.tv_gl_d_num, item.getCblOpCode());
        helper.setText(R.id.tv_gl_d_name, item.getCblOpName());
        helper.setText(R.id.tv_dl_name, item.getCableName());
    }
}
