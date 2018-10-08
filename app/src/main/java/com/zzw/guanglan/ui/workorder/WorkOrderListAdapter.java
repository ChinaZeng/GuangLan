package com.zzw.guanglan.ui.workorder;


import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zzw.guanglan.R;

import java.util.List;


public class WorkOrderListAdapter extends BaseQuickAdapter<String, BaseViewHolder> {


    public WorkOrderListAdapter(@Nullable List<String> data) {
        super(R.layout.item_work_order, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {

    }
}
