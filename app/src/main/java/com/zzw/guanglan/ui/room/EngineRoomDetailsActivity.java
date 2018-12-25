package com.zzw.guanglan.ui.room;

import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import com.zzw.guanglan.R;
import com.zzw.guanglan.base.BaseActivity;
import com.zzw.guanglan.bean.ResBean;

import butterknife.BindView;

/**
 * Create by zzw on 2018/12/25
 */
public class EngineRoomDetailsActivity extends BaseActivity {

    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_no)
    TextView tvNo;
    @BindView(R.id.tv_area)
    TextView tvArea;
    @BindView(R.id.tv_station)
    TextView tvStation;
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.tv_time)
    TextView tvTime;

    private ResBean resBean;

    public static void open(Context context, ResBean resBean) {
        context.startActivity(new Intent(context, EngineRoomDetailsActivity.class).putExtra("resBean", resBean));
    }

    @Override
    protected int initLayoutId() {
        return R.layout.activity_engine_room;
    }

    @Override
    protected void initData() {
        super.initData();
        resBean = (ResBean) getIntent().getSerializableExtra("resBean");

        if (resBean == null)
            return;

        tvName.setText("机房名称: " + resBean.getRoomName());
        tvNo.setText("机房编号: " + resBean.getRoomNo());
        tvArea.setText("地址: " + resBean.getCityName() + " " + resBean.getAreaName() + " " + resBean.getRoomAddress());
        tvStation.setText("局站名称: " + resBean.getStationName());
        tvType.setText("机房类型: " + resBean.getRoomType());
        tvTime.setText("创建事件: " + resBean.getCreateDate());

    }
}
