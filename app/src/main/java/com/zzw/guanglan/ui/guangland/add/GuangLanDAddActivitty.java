package com.zzw.guanglan.ui.guangland.add;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zzw.guanglan.R;
import com.zzw.guanglan.base.BaseActivity;
import com.zzw.guanglan.bean.AreaBean;
import com.zzw.guanglan.bean.BseRoomBean;
import com.zzw.guanglan.bean.GuangLanItemBean;
import com.zzw.guanglan.bean.GuangLanParamBean;
import com.zzw.guanglan.bean.ListDataBean;
import com.zzw.guanglan.bean.StationBean;
import com.zzw.guanglan.bean.StatusInfoBean;
import com.zzw.guanglan.bean.TeamInfoBean;
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
import com.zzw.guanglan.ui.MainActivity;
import com.zzw.guanglan.ui.guangland.param.GuangLanParamActivity;
import com.zzw.guanglan.utils.RequestBodyUtils;
import com.zzw.guanglan.utils.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by zzw on 2018/10/4.
 * 描述:
 */
public class GuangLanDAddActivitty extends BaseActivity implements LocationManager.OnLocationListener {
    @BindView(R.id.cabel_op_name)
    EditText cabelOpName;
    @BindView(R.id.cabel_op_code)
    EditText cabelOpCode;
    @BindView(R.id.area_id)
    TextView areaId;
    @BindView(R.id.stat_id)
    TextView statId;
    @BindView(R.id.room_id)
    TextView roomId;
    @BindView(R.id.capaticy)
    EditText capaticy;
    @BindView(R.id.op_long)
    EditText opLong;
    @BindView(R.id.org_id)
    TextView orgId;
    @BindView(R.id.org_user_name)
    EditText orgUserName;
    @BindView(R.id.op_start_time)
    EditText opStartTime;
    @BindView(R.id.last_time)
    EditText lastTime;
    @BindView(R.id.pa_cable_id)
    TextView paCableId;
    @BindView(R.id.remark)
    EditText remark;
    @BindView(R.id.state)
    TextView state;
    @BindView(R.id.location)
    TextView location;


    private LocationManager locationManager;

    public static void open(Context context) {
        context.startActivity(new Intent(context, GuangLanDAddActivitty.class));
    }

    @Override
    protected void initData() {
        super.initData();
        startLocation();
    }

    @Override
    protected int initLayoutId() {
        return R.layout.activity_guang_lan_d_add;
    }


    private String areaIdStr;
    private String roomIdStr;
    private String stationIdStr;
    private String orgIdStr;
    private String stateIdS;
    private String guanglanIdS;

    public void submit() {


        final String cabelOpNameS = cabelOpName.getText().toString().trim();
        final String cabelOpCodeS = cabelOpCode.getText().toString().trim();
        final String capaticyS = capaticy.getText().toString().trim();
        final String opLongS = opLong.getText().toString().trim();
        final String orgUserNameS = orgUserName.getText().toString().trim();
        final String opStartTimeS = opStartTime.getText().toString().trim();
        final String lastTimeS = lastTime.getText().toString().trim();
        final String remarkS = remark.getText().toString().trim();

        if (TextUtils.isEmpty(cabelOpNameS)) {
            ToastUtils.showToast("请填取光缆段名称!");
            return;
        }

        if (TextUtils.isEmpty(capaticyS)) {
            ToastUtils.showToast("请填取容量!");
            return;
        }
        if (TextUtils.isEmpty(opLongS)) {
            ToastUtils.showToast("请填取长度!");
            return;
        }

        if (TextUtils.isEmpty(areaIdStr)) {
            ToastUtils.showToast("请选择地区!");
            return;
        }

        if (TextUtils.isEmpty(stationIdStr)) {
            ToastUtils.showToast("请选择局站!");
            return;
        }

        RetrofitHttpEngine.obtainRetrofitService(Api.class)
                .duanAppAdd(RequestBodyUtils.generateRequestBody(new HashMap<String, String>() {
                    {
                        put("userId", UserManager.getInstance().getUserId());

                        put("ageoy", String.valueOf(locationBean == null ? "" : locationBean.latitude));
                        put("ageox", String.valueOf(locationBean == null ? "" : locationBean.longitude));

                        put("cabelOpName", cabelOpNameS);
                        put("cabelOpCode", cabelOpCodeS);
                        put("areaId", areaIdStr);
                        put("statId", stationIdStr);
                        put("roomId", roomIdStr);
                        put("capaticy", capaticyS);
                        put("oplong", opLongS);
                        put("orgId", orgIdStr);
                        put("orgUserName", orgUserNameS);
                        put("opStartTime", opStartTimeS);
                        put("lastTime", lastTimeS);
                        put("paCableId", guanglanIdS);
                        put("remark", remarkS);
                        put("state", stateIdS);
                    }
                }))
                .map(ResultBooleanFunction.create())
                .compose(LifeObservableTransformer.<Boolean>create(this))
                .subscribe(new ErrorObserver<Boolean>(this) {
                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            ToastUtils.showToast("新增成功");
                            finish();
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
                    areaId.setText(bean.getText());
                    areaIdStr = bean.getId();
                }
            }
        }).show(getSupportFragmentManager(), "area");
    }


    private void room() {
        RetrofitHttpEngine.obtainRetrofitService(Api.class)
                .getBseRoomListByArea(areaIdStr)
                .compose(LifeObservableTransformer.<List<BseRoomBean>>create(this))
                .subscribe(new ErrorObserver<List<BseRoomBean>>(this) {
                    @Override
                    public void onNext(List<BseRoomBean> data) {
                        if (data != null && data.size() > 0) {
                            BottomListDialog.newInstance(data, new BottomListDialog.Convert<BseRoomBean>() {
                                @Override
                                public String convert(BseRoomBean data) {
                                    return data.getName();
                                }
                            }).setCallback(new BottomListDialog.Callback<BseRoomBean>() {
                                @Override
                                public boolean onSelected(BseRoomBean data, int position) {
                                    roomId.setText(data.getName());
                                    roomIdStr = data.getStationId();
                                    return true;
                                }
                            }).show(getSupportFragmentManager(), "room");
                        } else {
                            ToastUtils.showToast("当前地区无机房");
                        }
                    }
                });
    }

    private void station() {
        RetrofitHttpEngine.obtainRetrofitService(Api.class)
                .getAllStation(areaIdStr)
                .compose(LifeObservableTransformer.<List<StationBean>>create(this))
                .subscribe(new ErrorObserver<List<StationBean>>(this) {
                    @Override
                    public void onNext(List<StationBean> data) {
                        if (data != null && data.size() > 0) {
                            BottomListDialog.newInstance(data, new BottomListDialog.Convert<StationBean>() {
                                @Override
                                public String convert(StationBean data) {
                                    return data.getName();
                                }
                            }).setCallback(new BottomListDialog.Callback<StationBean>() {
                                @Override
                                public boolean onSelected(StationBean data, int position) {
                                    statId.setText(data.getName());
                                    stationIdStr = data.getStationId();
                                    return true;
                                }
                            }).show(getSupportFragmentManager(), "station");
                        } else {
                            ToastUtils.showToast("当前地区无局站");
                        }
                    }
                });
    }


    private void team() {
        RetrofitHttpEngine.obtainRetrofitService(Api.class)
                .getAppConstructionTeamInfo()
                .map(new Function<ListDataBean<TeamInfoBean>, List<TeamInfoBean>>() {
                    @Override
                    public List<TeamInfoBean> apply(ListDataBean<TeamInfoBean> teamInfoBeanListDataBean) throws Exception {
                        if (teamInfoBeanListDataBean.getList() == null) {
                            teamInfoBeanListDataBean.setList(new ArrayList<TeamInfoBean>());
                        }
                        return teamInfoBeanListDataBean.getList();
                    }
                })
                .compose(LifeObservableTransformer.<List<TeamInfoBean>>create(this))
                .subscribe(new ErrorObserver<List<TeamInfoBean>>(this) {
                    @Override
                    public void onNext(List<TeamInfoBean> data) {
                        if (data != null && data.size() > 0) {
                            BottomListDialog.newInstance(data, new BottomListDialog.Convert<TeamInfoBean>() {
                                @Override
                                public String convert(TeamInfoBean data) {
                                    return data.getOrgName();
                                }
                            }).setCallback(new BottomListDialog.Callback<TeamInfoBean>() {
                                @Override
                                public boolean onSelected(TeamInfoBean data, int position) {
                                    orgId.setText(data.getOrgName());
                                    orgIdStr = data.getOrgId();
                                    return true;
                                }
                            }).show(getSupportFragmentManager(), "team");
                        } else {
                            ToastUtils.showToast("没有查到维护班组信息");
                        }
                    }
                });
    }


    private void state() {
        RetrofitHttpEngine.obtainRetrofitService(Api.class)
                .quertstatuslistinfo()
                .compose(LifeObservableTransformer.<List<StatusInfoBean>>create(this))
                .subscribe(new ErrorObserver<List<StatusInfoBean>>(this) {
                    @Override
                    public void onNext(List<StatusInfoBean> data) {
                        if (data != null && data.size() > 0) {
                            BottomListDialog.newInstance(data, new BottomListDialog.Convert<StatusInfoBean>() {
                                @Override
                                public String convert(StatusInfoBean data) {
                                    return data.getName();
                                }
                            }).setCallback(new BottomListDialog.Callback<StatusInfoBean>() {
                                @Override
                                public boolean onSelected(StatusInfoBean data, int position) {
                                    state.setText(data.getName());
                                    stateIdS = data.getStateId();
                                    return true;
                                }
                            }).show(getSupportFragmentManager(), "state");
                        } else {
                            ToastUtils.showToast("没有查到状态信息");
                        }
                    }
                });
    }

    @OnClick({R.id.add, R.id.area_id, R.id.location, R.id.stat_id, R.id.room_id, R.id.org_id, R.id.state, R.id.pa_cable_id})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.area_id:
                area();
                break;
            case R.id.stat_id:
                if (TextUtils.isEmpty(areaIdStr)) {
                    ToastUtils.showToast("请先选择地区");
                    return;
                }
                station();
                break;
            case R.id.room_id:
                if (TextUtils.isEmpty(areaIdStr)) {
                    ToastUtils.showToast("请先选择地区");
                    return;
                }
                room();
                break;
            case R.id.org_id:
                team();
                break;
            case R.id.state:
                state();
                break;
            case R.id.pa_cable_id:
                GuangLanParamActivity.open(this, 5);
                break;
            case R.id.add:
                submit();
                break;
            case R.id.location:
                startLocation();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5 && resultCode == Activity.RESULT_OK) {
            GuangLanItemBean bean = (GuangLanItemBean) data.getSerializableExtra("bean");
            if (bean != null) {
                guanglanIdS = bean.getCableId();
                paCableId.setText(bean.getCableName());
            }
        }
    }

    @SuppressLint("CheckResult")
    private void startLocation() {
        location.setText("定位中...");
        new RxPermissions(this)
                .request(Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            stopLocation();
                            locationManager = new LocationManager(GuangLanDAddActivitty.this,
                                    GuangLanDAddActivitty.this);
                            locationManager.start();
                        } else {
                            ToastUtils.showToast("请开启定位权限");
                            location.setText("定位失败，点击重新定位");
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
        location.setText(bean.addrss);
        this.locationBean = bean;
    }

    @Override
    public void onError(int code, String msg) {
        location.setText("定位失败，点击重新定位");
    }
}
