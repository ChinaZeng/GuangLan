package com.zzw.guanglan.ui.guangland.param;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zzw.guanglan.R;
import com.zzw.guanglan.bean.GuangLanDItemBean;
import com.zzw.guanglan.bean.GuangLanParamBean;

import java.util.List;

/**
 * Created by zzw on 2018/10/3.
 * 描述:
 */
public class GuangLanParamListAdapter extends BaseQuickAdapter<GuangLanParamBean, BaseViewHolder> {
    public GuangLanParamListAdapter(@Nullable List<GuangLanParamBean> data) {
        super(R.layout.item_guanglan_param, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, GuangLanParamBean item) {
        helper.setText(R.id.tv_id, item.getID());
        helper.setText(R.id.tv_name, item.getNAME());
    }
}
