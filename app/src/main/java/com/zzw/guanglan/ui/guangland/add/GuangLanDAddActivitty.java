package com.zzw.guanglan.ui.guangland.add;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.zzw.guanglan.R;
import com.zzw.guanglan.base.BaseActivity;
import com.zzw.guanglan.bean.AreaBean;
import com.zzw.guanglan.bean.BseRoomBean;
import com.zzw.guanglan.bean.GuangLanItemBean;
import com.zzw.guanglan.bean.ListDataBean;
import com.zzw.guanglan.bean.StationBean;
import com.zzw.guanglan.bean.StatusInfoBean;
import com.zzw.guanglan.bean.TeamInfoBean;
import com.zzw.guanglan.dialogs.BottomListDialog;
import com.zzw.guanglan.dialogs.area.AreaDialog;
import com.zzw.guanglan.dialogs.multilevel.OnConfirmCallback;
import com.zzw.guanglan.http.Api;
import com.zzw.guanglan.http.retrofit.RetrofitHttpEngine;
import com.zzw.guanglan.manager.UserManager;
import com.zzw.guanglan.rx.ErrorObserver;
import com.zzw.guanglan.rx.LifeObservableTransformer;
import com.zzw.guanglan.rx.ResultBooleanFunction;
import com.zzw.guanglan.utils.RequestBodyUtils;
import com.zzw.guanglan.utils.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Function;

/**
 * Created by zzw on 2018/10/4.
 * 描述:
 */
public class GuangLanDAddActivitty extends BaseActivity {
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


    public static void open(Context context) {
        context.startActivity(new Intent(context, GuangLanDAddActivitty.class));
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

        RetrofitHttpEngine.obtainRetrofitService(Api.class)
                .duanAppAdd(RequestBodyUtils.generateRequestBody(new HashMap<String, String>() {
                    {
                        put("userId", UserManager.getInstance().getUserId());
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

    private void cableId() {
        RetrofitHttpEngine.obtainRetrofitService(Api.class)
                .getGuangLanByPage(RequestBodyUtils.generateRequestBody(new HashMap<String, String>() {
                    {
                        put("model.cableNo", "");
                        put("model.cableName", "");
                    }
                }))
                .compose(LifeObservableTransformer.<ListDataBean<GuangLanItemBean>>create(this))
                .subscribe(new ErrorObserver<ListDataBean<GuangLanItemBean>>(this) {
                    @Override
                    public void onNext(ListDataBean<GuangLanItemBean> guanLanItemBeans) {
                        if (guanLanItemBeans != null && guanLanItemBeans.getList() != null) {
                            BottomListDialog.newInstance(guanLanItemBeans.getList(), new BottomListDialog.Convert<GuangLanItemBean>() {
                                @Override
                                public String convert(GuangLanItemBean data) {
                                    return data.getCableName();
                                }
                            }).setCallback(new BottomListDialog.Callback<GuangLanItemBean>() {
                                @Override
                                public boolean onSelected(GuangLanItemBean data, int position) {
                                    paCableId.setText(data.getCableName());
                                    guanglanIdS = data.getCableId();
                                    return true;
                                }
                            }).show(getSupportFragmentManager(), "cableId");
                        } else {
                            ToastUtils.showToast("没有查到光缆信息");
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

    @OnClick({R.id.add, R.id.area_id, R.id.stat_id, R.id.room_id, R.id.org_id, R.id.state, R.id.pa_cable_id})
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
                cableId();
                break;
            case R.id.add:
                submit();
                break;
        }
    }


}
