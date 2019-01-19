package com.zzw.guanglan.ui.resource;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zzw.guanglan.R;
import com.zzw.guanglan.bean.ResBean;

import java.util.ArrayList;

/**
 * Create by zzw on 2018/12/24
 */
public class ResourceAdapter extends BaseQuickAdapter<ResBean, BaseViewHolder> {
    private boolean hideDistance;

    public ResourceAdapter(boolean hideDistance) {
        super(R.layout.item_res, new ArrayList<ResBean>());
        this.hideDistance = hideDistance;
    }

    @Override
    protected void convert(BaseViewHolder helper, ResBean item) {
        //这里先影藏
        helper.setVisible(R.id.tv_guanglan_xinshu,false);
//        helper.setText(R.id.tv_guanglan_xinshu, "光缆芯数:" +item.getAreaId());

        helper.setText(R.id.tv_name, "名称:" + item.getRoomName());
        helper.setText(R.id.tv_type, "机房类型:" +item.getRoomType());
        helper.setText(R.id.tv_distance, "距离:" +item.getDistance());

        if(hideDistance){
            helper.setVisible(R.id.tv_distance,false);
        }

    }
}
