package com.zzw.guanglan.ui.qianxin;

import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zzw.guanglan.R;
import com.zzw.guanglan.bean.QianXinItemBean;
import com.zzw.guanglan.ui.qianxin.test.QianXinTestActivity;

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
    protected void convert(final BaseViewHolder helper, final QianXinItemBean item) {
        helper.setText(R.id.num, "光缆序号:" + item.getNo());
        helper.setText(R.id.tv_gl_d_num, "光缆段名称:" + item.getCblOpName());
        helper.setText(R.id.tv_gl_d_name, "光缆段编码:" + item.getCblOpCode());

        helper.setOnClickListener(R.id.qianxin_test, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QianXinTestActivity.open(helper.itemView.getContext(), item.getFiberId());
            }
        });

    }


}
