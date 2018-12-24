package com.zzw.guanglan.ui.resource;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zzw.guanglan.R;
import com.zzw.guanglan.bean.ResBean;

import java.util.List;

/**
 * Create by zzw on 2018/12/24
 */
public class ResourceAdapter extends BaseQuickAdapter<ResBean, BaseViewHolder> {

    public ResourceAdapter(@Nullable List<ResBean> data) {
        super(R.layout.item_res, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ResBean item) {

    }
}
