package com.zzw.guanglan.ui.gongdan;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zzw.guanglan.R;
import com.zzw.guanglan.bean.GongDanBean;

import java.util.List;

/**
 * Created by zzw on 2018/10/3.
 * 描述:
 */
public class GongDanListAdapter extends BaseQuickAdapter<GongDanBean, BaseViewHolder> {
    public GongDanListAdapter(@Nullable List<GongDanBean> data) {
        super(R.layout.item_gongdan, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, GongDanBean item) {

        helper.setText(R.id.tv_guangdan_num, "工单" + item.getR())

                .setText(R.id.tv_bianma, "工单编码:" + (TextUtils.isEmpty(item.getWO_ID())?"":item.getWO_ID()))
                    .setText(R.id.tv_gongdan_test_gl_name,"测试光缆名称:"+(TextUtils.isEmpty(item.getCABL_OP_NAME())?"":item.getCABL_OP_NAME()))
                .setText(R.id.tv_guangdan_name,"工单名称:"+(TextUtils.isEmpty(item.getWO_NAME())?"":item.getWO_NAME()))
                .setText(R.id.tv_gongdan_test_gl_num,"测试光缆编码:"+(TextUtils.isEmpty(item.getCABL_OP_CODE())?"":item.getCABL_OP_CODE()))

                //TODO 测试光缆长度  测试光缆纤芯数
                .setText(R.id.tv_gongdan_test_gl_len,"测试光缆长度:"+(TextUtils.isEmpty(item.getCABL_OP_CODE())?"":item.getCABL_OP_CODE()))
                .setText(R.id.tv_gongdan_test_gl_qianxin_num,"测试光缆纤芯数:"+(TextUtils.isEmpty(item.getCABL_OP_CODE())?"":item.getCABL_OP_CODE()));





    }
}
