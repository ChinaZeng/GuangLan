package com.zzw.guanglan.ui.juzhan.add;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zzw.guanglan.R;
import com.zzw.guanglan.base.BaseActivity;
import com.zzw.guanglan.bean.AreaBean;
import com.zzw.guanglan.bean.ListDataBean;
import com.zzw.guanglan.bean.RoomTypeBean;
import com.zzw.guanglan.dialogs.BottomListDialog;
import com.zzw.guanglan.dialogs.area.AreaDialog;
import com.zzw.guanglan.dialogs.multilevel.OnConfirmCallback;
import com.zzw.guanglan.http.Api;
import com.zzw.guanglan.http.retrofit.RetrofitHttpEngine;
import com.zzw.guanglan.manager.LocationManager;
import com.zzw.guanglan.manager.UserManager;
import com.zzw.guanglan.rx.ErrorObserver;
import com.zzw.guanglan.rx.LifeObservableTransformer;
import com.zzw.guanglan.rx.ResultBooleanFunction;
import com.zzw.guanglan.utils.ToastUtils;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

/**
 * Create by zzw on 2018/12/25
 */
public class JuZhanAddActivity extends BaseActivity implements LocationManager.OnLocationListener {
    @BindView(R.id.tv_location)
    TextView tvLocation;
    @BindView(R.id.tv_room_name)
    EditText tvRoomName;
    @BindView(R.id.tv_area)
    TextView tvArea;
    @BindView(R.id.tv_room_type)
    TextView tvRoomType;

    private LocationManager locationManager;


    public static void open(Context context) {
        context.startActivity(new Intent(context, JuZhanAddActivity.class));
    }

    @Override
    protected int initLayoutId() {
        return R.layout.activity_juzhan_add;
    }

    @Override
    protected void initData() {
        super.initData();
        startLocation();
    }


    @OnClick({R.id.tv_location, R.id.tv_area, R.id.tv_room_type, R.id.add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_location:
                startLocation();
                break;
            case R.id.tv_area:
                area();
                break;
            case R.id.tv_room_type:
                roomType();
                break;
            case R.id.add:
                submit();
                break;
        }
    }

    private String areaIdStr, roomTypeIdStr;


    private void submit() {

        if (locationBean == null) {
            ToastUtils.showToast("请完成定位操作!");
        }


        final String roomName = tvRoomName.getText().toString().trim();

        if (TextUtils.isEmpty(roomName)) {
            ToastUtils.showToast("请填取局站名称!");
            return;
        }

        if (TextUtils.isEmpty(areaIdStr)) {
            ToastUtils.showToast("请选择地区!");
            return;
        }

        if (TextUtils.isEmpty(roomTypeIdStr)) {
            ToastUtils.showToast("请选择局站类型!");
            return;
        }

        RetrofitHttpEngine.obtainRetrofitService(Api.class)
                .appAddJfInfo(new HashMap<String, String>() {
                    {
                        put("areaId", areaIdStr);
                        put("roomName", roomName);
                        put("roomTypeId", roomTypeIdStr);
                        put("userId", UserManager.getInstance().getUserId());
                        put("longitude", String.valueOf(locationBean.longitude));
                        put("latitude", String.valueOf(locationBean.latitude));
                    }
                })
                .map(ResultBooleanFunction.create())
                .compose(LifeObservableTransformer.<Boolean>create(this))
                .subscribe(new ErrorObserver<Boolean>(this) {
                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            ToastUtils.showToast("新增成功");
                            finish();
                        } else {
                            ToastUtils.showToast("新增失败");
                        }
                    }
                });
    }


    private void area() {
        AreaDialog.createCityDialog(this, "选择地区", new OnConfirmCallback<AreaBean>() {
            @Override
            public void onConfirm(List<AreaBean> selectedEntities) {
                if (selectedEntities.size() > 0) {
                    AreaBean bean = selectedEntities.get(selectedEntities.size() - 1);
                    tvArea.setText(bean.getText());
                    areaIdStr = bean.getId();
                }
            }
        }).show(getSupportFragmentManager(), "area");
    }

    private void roomType() {
        RetrofitHttpEngine.obtainRetrofitService(Api.class)
                .getAppTypeJfInfo()
                .compose(LifeObservableTransformer.<ListDataBean<RoomTypeBean>>create(this))
                .subscribe(new ErrorObserver<ListDataBean<RoomTypeBean>>(this) {
                    @Override
                    public void onNext(ListDataBean<RoomTypeBean> data) {
                        List<RoomTypeBean> roomTypeBeans = data.getList();

                        if (roomTypeBeans != null && roomTypeBeans.size() > 0) {
                            BottomListDialog.newInstance(roomTypeBeans, new BottomListDialog.Convert<RoomTypeBean>() {
                                @Override
                                public String convert(RoomTypeBean data) {
                                    return data.getDESC_CHINA();
                                }
                            }).setCallback(new BottomListDialog.Callback<RoomTypeBean>() {
                                @Override
                                public boolean onSelected(RoomTypeBean data, int position) {
                                    tvRoomType.setText(data.getDESC_CHINA());
                                    roomTypeIdStr = data.getSERIAL_NO();
                                    return true;
                                }
                            }).show(getSupportFragmentManager(), "roomType");
                        } else {
                            ToastUtils.showToast("没有数据");
                        }
                    }
                });
    }


    @SuppressLint("CheckResult")
    private void startLocation() {
        tvLocation.setText("定位中...");
        new RxPermissions(this)
                .request(Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            stopLocation();
                            locationManager = new LocationManager(JuZhanAddActivity.this,
                                    JuZhanAddActivity.this);
                            locationManager.start();
                        } else {
                            ToastUtils.showToast("请开启定位权限");
                            tvLocation.setText("定位失败，点击重新定位");
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocation();
    }

    private void stopLocation() {
        if (locationManager != null) {
            locationManager.stop();
            locationManager = null;
        }
    }


    private LocationManager.LocationBean locationBean;

    @Override
    public void onSuccess(LocationManager.LocationBean bean) {
        tvLocation.setText(bean.addrss);
        this.locationBean = bean;
    }

    @Override
    public void onError(int code, String msg) {
        tvLocation.setText("定位失败，点击重新定位");
    }
}
